/*
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright (C) 2000-2008 David C. Gibbons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.nuclearbunny.icybee;

import org.nuclearbunny.icybee.protocol.*;
import org.nuclearbunny.icybee.ui.OutputLogger;
import org.nuclearbunny.util.*;
import org.thereeds.utf7.*;
import tcl.lang.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class ICBClient implements Client {
    private static final int READ_BUFFER_SIZE = 10240;

    private List usageList = new LinkedList();
    private ICBProperties clientProperties = new ICBProperties();
    private TclUtil tclManager = new TclUtil(this);
    private LinkedList userHistory = new LinkedList();
    private int currentHistory = 0;

    private OutputLogger logger = null;
    private Timer timer = null;

    private boolean outputPaused = false;

    private int reconnectNeeded = 0;

    private ConnectionState state = ConnectionState.DISCONNECTED;
    private List statusListeners = new LinkedList();
    private List messageListeners = new LinkedList();
    private Socket socket = null;
    private BufferedOutputStream out = null;
    private Thread readerThread = null;
    private long lastMessageSentAt = 0;

    public ICBClient() {
        addMessageListener(new TclTriggerListener(this));
    }

    /**
     * Checks ICB client options for any parameters that affect startup. If
     * specified, an automatic connection to the default ICB server will be
     * attempted.
     */
    public void checkStartupOptions() {
        // TODO: see if this is the initial run for this user, if so
        // then bring up the license agreement and the initial
        // properties.
        if (!clientProperties.available()) {
            if (IcyBee.isDebugEnabled()) {
                System.out.println("Client properties were not available.");
            }
        }

        // check for auto-connect option
        if (clientProperties.getAutoConnect()) {
            try {
                String server = clientProperties.getDefaultServer();
                connect(server);
            } catch (IOException ex) {
                // XXX deal with this another way
                System.err.println("Unable to automatically connect to server");
                ex.printStackTrace(System.err);
            }
        }
    }

    public void printMessage(int msgType, String msg) {
        PrintPacket p = new PrintPacket(msgType, msg);
        processPacket(p);
    }

    public ICBProperties getProperties() {
        return clientProperties;
    }

    public boolean isLoggingEnabled() {
        synchronized (this) {
            return (logger != null);
        }
    }

    public void startLogging() throws IllegalStateException, IOException {
        String logFileName = clientProperties.getLogFileName();
        startLogging(logFileName);
    }

    public void startLogging(String logFileName) throws IllegalStateException, IOException {
        boolean append = clientProperties.getLogAppend();
        synchronized (this) {
            if (logger == null) {
                logger = new OutputLogger(this, logFileName, append);
                addMessageListener(logger);
                fireStatusLoggingStarted();
            }
        }
    }

    public void stopLogging() throws IllegalStateException {
        synchronized (this) {
            if (logger != null) {
                removeMessageListener(logger);
                logger.close();
                logger = null;
                fireStatusLoggingStopped();
            }
        }
    }

    public synchronized void toggleOutputPause() {
        outputPaused = !outputPaused;
        if (outputPaused) {
            fireStatusOutputPaused();
        } else {
            fireStatusOutputUnpaused();
        }
    }

    public synchronized void pauseOutput(boolean paused) {
        outputPaused = paused;
        if (outputPaused) {
            fireStatusOutputPaused();
        } else {
            fireStatusOutputUnpaused();
        }
    }

    public synchronized boolean isOutputPaused() {
        return outputPaused;
    }

    public TclUtil getTclManager() {
        return tclManager;
    }

    public void loadTclScript(String scriptName) {
        try {
            tclManager.evalFile(scriptName);
        } catch (TclException ex) {
            printMessage(PrintPacket.MSG_TYPE_ERROR, "Unable to evaluate Tcl script " + scriptName);
            printMessage(PrintPacket.MSG_TYPE_ERROR, ex.getMessage());
        }
    }

    public void sendCommand(String cmd) {
        int cmdLength = cmd.length();
        if (cmd == null || cmdLength == 0) {
            return;
        }

        char commandPrefix = clientProperties.getCommandPrefix().charAt(0);

        // if the command does not begin with the command character, go ahead
        // and send it as an open message
        if (cmd.charAt(0) != commandPrefix) {
            sendOpenMessage(cmd);

            // if the command does begin with the command character, but it is
            // escaped by another command character, send it as an open message
        } else if (cmdLength > 1 && cmd.charAt(1) == commandPrefix) {
            sendOpenMessage(cmd.substring(1));

            // otherwise, go ahead and send the command to the Tcl interperter
        } else {
            String tclCmd = cmd.substring(1);
            // if the command fails to execute, pass it to the server as a personal
            // message so it can take over any unknown commands
            if (!tclManager.executeCommand(tclCmd)) {
                sendPersonalMessage("server", tclCmd); // TODO XXX don't hardcode this here
            }
        }
    }

    public long getLastMessageSentAt() {
        return lastMessageSentAt;
    }

    /**
     * Sends the provided text string as an open message to the
     * user's current group.
     */
    public void sendOpenMessage(String msg) {
        try {
            msg = removeControlCharacters(msg);

            if (IcyBee.isDebugEnabled()) {
                System.out.println("Sending Open Message: ");
                HexDump.dump(System.out, msg.toString().getBytes());
            }

            /* send the message in maximum sized chunks */
            String currentMsg;
            String remaining = msg;
            int n;
            String pkt;
            do {
                if (remaining.length() > ICBProtocol.MAX_OPEN_MESSAGE_SIZE) {
                    currentMsg = remaining.substring(0, ICBProtocol.MAX_OPEN_MESSAGE_SIZE);
                    n = currentMsg.lastIndexOf(' ');
                    if (n > 0) {
                        currentMsg = currentMsg.substring(0, n + 1);
                    }
                    remaining = remaining.substring(currentMsg.length());
                } else {
                    currentMsg = remaining;
                    remaining = "";
                }

                pkt = new OpenPacket(Packet.CLIENT, currentMsg).toString();
                sendPacket(pkt);

            } while (remaining.length() > 0);

        } catch (ProtocolException ex) {
            // XXX fix more appropriately
            System.err.println("Exception sending open message, " + ex);
            ex.printStackTrace(System.err);
            System.err.println("Attempting to continue...");
        }
    }

    /**
     * Sends the provided text string as a personal message to the
     * specified user.
     */
    public void sendPersonalMessage(String nick, String origMsg) {
        String msg = removeControlCharacters(origMsg);

        // send the message in chunks
        String currentMsg;
        String remaining = msg;
        int n;
        do {
            if (remaining.length() > ICBProtocol.MAX_PERSONAL_MESSAGE_SIZE) {
                currentMsg = remaining.substring(0, ICBProtocol.MAX_PERSONAL_MESSAGE_SIZE);
                n = currentMsg.lastIndexOf(' ');
                if (n > 0) {
                    currentMsg = currentMsg.substring(0, n + 1);
                }
                remaining = remaining.substring(currentMsg.length());
            } else {
                currentMsg = remaining;
                remaining = "";
            }

            StringBuffer buf = new StringBuffer(nick.length() + 1 + currentMsg.length());
            buf.append(nick).append(' ').append(currentMsg);
            sendCommandMessage("m", buf.toString());

        } while (remaining.length() > 0);

        if (clientProperties.isAddOutgoingToHistoryEnabled()) {
            addUserHistory(nick);
        }
    }

    public void sendWriteMessage(String nick, String origMsg) {
        String msg = removeControlCharacters(origMsg);

        String currentMsg;
        String remaining = msg;
        int n;
        do {
            if (remaining.length() > ICBProtocol.MAX_WRITE_MESSAGE_SIZE) {
                currentMsg = remaining.substring(0, ICBProtocol.MAX_WRITE_MESSAGE_SIZE);
                n = currentMsg.lastIndexOf(' ');
                if (n > 0) {
                    currentMsg = currentMsg.substring(0, n + 1);
                }
                remaining = remaining.substring(currentMsg.length());
            } else {
                currentMsg = remaining;
                remaining = "";
            }

            StringBuffer buf = new StringBuffer(nick.length() + 15 + currentMsg.length()); // 7 = "server write  "
            buf.append("server write ").append(nick).append(' ').append(currentMsg);
            sendCommandMessage("m", buf.toString());
        } while (remaining.length() > 0);
    }

    /**
     * Sends the specified command and argument text to the server.
     */
    public void sendCommandMessage(String command, String msg) {
        try {
            // XXX validate packet length from user
            String pkt = new CommandPacket(command, msg).toString();
            sendPacket(pkt);

        } catch (ProtocolException ex) {
            System.err.println("Exception sending command message, " + ex);
            ex.printStackTrace(System.err);
            System.err.println("Attempting to continue...");
            // XXX fix more appropriately
        }
    }

    public void clearHistory() {
        userHistory.clear();
        currentHistory = 0;
    }

    public void removeUserFromHistory(String nick) {
        int n = userHistory.indexOf(nick);
        if (n != -1) {
            if (currentHistory == n) {
                currentHistory++;
                if (currentHistory == userHistory.size()) {
                    currentHistory = 0;
                }
            }
            userHistory.remove(n);
        }
    }

    public void addUserHistory(String nick) {
        Iterator i = userHistory.iterator();
        while (i.hasNext()) {
            String n = (String) i.next();
            if (n.equalsIgnoreCase(nick)) {
                i.remove();
                userHistory.addFirst(n);
                currentHistory = 0;
                return;
            }
        }

        userHistory.addFirst(nick);
        currentHistory = 0;
    }

    public String getNextUserFromHistory(boolean getLastUsed) {
        String nick = null;
        try {
            nick = userHistory.get(currentHistory).toString();
            currentHistory++;
            if (currentHistory == userHistory.size()) {
                currentHistory = 0;
            }
        } catch (IndexOutOfBoundsException ex) {
            currentHistory = 0;
        }
        return nick;
    }

    private boolean processPacket(Packet p) {
        boolean continueProcessing = true;

        if (IcyBee.isDebugEnabled()) {
            System.out.println("Packet received:");
            HexDump.dump(System.out, p.toString().getBytes());
        }

        MessageEvent e = new MessageEvent(this, p);
        fireMessageReceived(e);

        if (p instanceof ExitPacket) {
            continueProcessing = false;
        } else if (p instanceof PingPacket) {
            handlePingPacket((PingPacket) p);
        } else if (p instanceof ProtocolPacket) {
            handleProtocolPacket((ProtocolPacket) p);
        } else if (p instanceof ErrorPacket) {
            handleErrorPacket((ErrorPacket) p);
        } else if (p instanceof LoginPacket) {
            handleLoginPacket((LoginPacket) p);
        }

        return continueProcessing;
    }

    /**
     * Indicates that user's login was successful.
     */
    private void handleLoginPacket(LoginPacket p) {
        reconnectNeeded = 0;
    }

    private void handleErrorPacket(ErrorPacket p) {
        String errorText = p.getErrorText();
        if (errorText.equalsIgnoreCase("Nickname already in use.")) {
            reconnectNeeded++;
        }
    }

    private void handlePingPacket(PingPacket p) {
        PongPacket pp = new PongPacket();
        sendPacket(pp.toString());
    }

    private void handleProtocolPacket(ProtocolPacket p) {
        String id = clientProperties.getUserID();
        String nick = clientProperties.getUserNick();
        String alternativeNick = clientProperties.getUserAlternativeNick();
        String group = clientProperties.getUserGroup();
        String command = "login";
        String passwd = clientProperties.getUserPassword();

        if (reconnectNeeded > 0) {
            nick = alternativeNick;
        }

        LoginPacket lp = new LoginPacket(id, nick, group, command, passwd);
        sendPacket(lp.toString());

        /* set the appropriate echoback state */
        int echoback = clientProperties.getEchoback();
        switch (echoback) {
            case Echoback.ECHOBACK_ON:
                sendCommandMessage("echoback", "on");
                break;
            case Echoback.ECHOBACK_VERBOSE_SERVER:
                sendCommandMessage("echoback", "verbose");
                break;
            default:
                break;
        }

        executeInitScript();
    }


    static class Usage {
        String name;
        String type;
        String args;
        String usage;
    }

    public void addUsage(String name, String type, String args, String usage) {
        Usage u = new Usage();
        u.name = name;
        u.type = type;
        u.args = args;
        u.usage = usage;
        synchronized (usageList) {
            usageList.add(u);
        }
    }

    public void removeUsage(String name) {
        synchronized (usageList) {
            ListIterator i = usageList.listIterator();
            Usage u = null;
            while (i.hasNext()) {
                u = (Usage) i.next();
                if (u.name.compareTo(name) == 0) {
                    i.remove();
                    break;
                }
            }
        }
    }

    // XXX fix this crap
    public void listUsage(String name) {
        printMessage(PrintPacket.MSG_TYPE_NORMAL, "[=Commands=]");
        synchronized (usageList) {
            ListIterator i = usageList.listIterator();
            Usage u = null;
            while (i.hasNext()) {
                u = (Usage) i.next();
                printMessage(PrintPacket.MSG_TYPE_NORMAL, u.name + " " + u.type + " " + u.args + " " + u.usage);
            }
        }
    }

    private String removeControlCharacters(String s) {
        StringBuffer buf = new StringBuffer(s.length());
        char c;
        for (int i = 0, n = s.length(); i < n; i++) {
            c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                buf.append(' ');
            } else if (!Character.isISOControl(c)) {
                buf.append((c == '\n') ? ' ' : c);
            }
        }
        return buf.toString();
    }


    /**
     * Attempts to the connect to the specified ICB server.
     *
     * @param server The icb server, in hostname:portnumber format.
     * @throws IllegalStateException if already connected
     * @throws IOException           if any I/O error occurs
     */
    public void connect(String server) throws IllegalStateException, IOException {
        synchronized (this) {
            if (isConnected()) {
                throw new IllegalStateException("already connected");
            } else {
                // TODO: handle parsing errors here
                int separator = server.indexOf(':');
                String host = server.substring(0, separator);
                String port = server.substring(separator + 1);
                new Thread(new Connector(host, Integer.parseInt(port))).start();
            }
        }
    }

    /**
     * Disconnects from the currently connected ICB server.
     *
     * @throws IllegalStateException if not currently connected
     */
    public void disconnect() throws IllegalStateException {
        synchronized (this) {
            if (!isConnected()) {
                throw new IllegalStateException("not connected");
            } else {
                new Thread(new Disconnector()).start();
            }
        }
    }

    /**
     * Determines if the client is currently connected or not.
     */
    public boolean isConnected() {
        synchronized (this) {
            return (state == ConnectionState.CONNECTED);
        }
    }

    /**
     * Sends a single ICB packet to the server.
     */
    public void sendPacket(String msg) {
        try {
            if (msg.charAt(0) != ICBProtocol.PKT_NOOP.getPacketType()) {
                lastMessageSentAt = System.currentTimeMillis();
            }

            byte[] buf = null;
            String encoding = clientProperties.getTextEncoding();
            if (encoding.equalsIgnoreCase("UTF7")) {
                buf = Utf7Converter.encode(msg);
            } else {
                buf = msg.getBytes(encoding);
            }

            if (IcyBee.isDebugEnabled()) {
                System.out.println("Sending packet:");
                HexDump.dump(System.out, buf);
            }

            if (buf.length > ICBProtocol.MAX_PACKET_SIZE) {
                System.err.println("Assertion failure, packet length > MAX_PACKET_SIZE");
            }

            synchronized (out) {
                out.write(buf.length);
                out.write(buf);
                out.flush();
            }
        } catch (UnsupportedEncodingException ex) {
            // "can't happen" -- US-ASCII is required by Java platform
            System.err.println("*** FATAL ERROR ***");
            System.err.println("US-ASCII character encoding required");
            System.err.println("Aborting...");
            System.exit(1);
        } catch (IOException ex) {
            // bad news! we'll assume that the reader thread will run
            // into an exception also and shut us down.
        }
    }

    /**
     * Adds a new MessageListener object to the list of objects listening
     * for MessageEvent notifications.
     */
    public void addMessageListener(MessageListener l) {
        synchronized (this) {
            messageListeners.add(l);
        }
    }

    /**
     * Removes an existing MessageListener object from the list of objects
     * listening for MessageEvent notifications.
     */
    public void removeMessageListener(MessageListener l) {
        synchronized (this) {
            int n = messageListeners.indexOf(l);
            if (n >= 0) {
                messageListeners.remove(n);
            }
        }
    }

    /**
     * Adds a new StatusListener object to the list of objects listening
     * for StatusEvent notifications.
     */
    public void addStatusListener(StatusListener l) {
        synchronized (this) {
            statusListeners.add(l);
        }
    }

    /**
     * Removes an existing StatusListener object from the list of objects
     * listening for StatusEvent notifications.
     */
    public void removeStatusListener(StatusListener l) {
        synchronized (this) {
            int n = statusListeners.indexOf(l);
            if (n >= 0) {
                statusListeners.remove(n);
            }
        }
    }

    public void executeInitScript() {
        String initScriptName = clientProperties.getInitScript();
        String folder = System.getProperty("user.home");
        String filesep = System.getProperty("file.separator");
        String subdir = Properties.SUBDIRECTORY;

        File subdirFile = new File(folder + filesep + subdir);
        File f = new File(subdirFile, initScriptName);
        if (!f.exists()) {
            f = new File(initScriptName);
        }

        if (f.exists()) {
            loadTclScript(f.getAbsolutePath());
        }
    }

    private void setConnectingState() {
        synchronized (this) {
            state = ConnectionState.CONNECTING;
            fireStatusConnecting();
        }
    }

    private void setConnectedState() {
        synchronized (this) {
            lastMessageSentAt = System.currentTimeMillis();

            if (clientProperties.getLogAutomatically()) {
                try {
                    startLogging();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (clientProperties.isKeepConnectionAliveEnabled()) {
                int interval = clientProperties.getKeepConnectionAliveInterval();
                if (interval > 0) {
                    TimerTask timerTask = new TimerTask() {
                        public void run() {
                            sendPacket(ICBProtocol.PKT_NOOP + "\000");
                        }
                    };
                    timer = new Timer(true);
                    timer.schedule(timerTask, interval * 1000, interval * 1000);
                }
            }

            state = ConnectionState.CONNECTED;
            fireStatusConnected();
        }
    }

    private void setDisconnectingState() {
        synchronized (this) {
            state = ConnectionState.DISCONNECTING;
            fireStatusDisconnecting();
        }
    }

    private void setDisconnectedState() {
        synchronized (this) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            state = ConnectionState.DISCONNECTED;
            fireStatusDisconnected();

            if (reconnectNeeded == 1) {
                printMessage(PrintPacket.MSG_TYPE_NORMAL, "Attempting to reconnect using alternative nickname.");
                try {
                    String server = clientProperties.getDefaultServer();
                    connect(server);
                } catch (IOException ex) {
                    // XXX deal with this another way
                    System.err.println("Unable to automatically connect to server");
                    ex.printStackTrace(System.err);
                }
            } else {
                reconnectNeeded = 0;
            }
        }
    }

    private void cleanupSocket() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            // XXX: log an error?
        } finally {
            socket = null;
            out = null;
        }

        setDisconnectedState();
    }

    private void fireStatusConnecting() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            ListIterator i = statusListeners.listIterator();
            while (i.hasNext()) {
                StatusListener l = (StatusListener) i.next();
                l.statusConnecting(e);
            }
        }
    }

    private void fireMessageReceived(MessageEvent e) {
        synchronized (this) {
            ListIterator i = messageListeners.listIterator();
            while (i.hasNext()) {
                MessageListener l = (MessageListener) i.next();
                l.messageReceived(e);
            }
        }
    }

    private void fireStatusConnected() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            ListIterator i = statusListeners.listIterator();
            while (i.hasNext()) {
                StatusListener l = (StatusListener) i.next();
                l.statusConnected(e);
            }
        }
    }

    private void fireStatusDisconnecting() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            ListIterator i = statusListeners.listIterator();
            while (i.hasNext()) {
                StatusListener l = (StatusListener) i.next();
                l.statusDisconnecting(e);
            }
        }
    }

    private void fireStatusDisconnected() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            ListIterator i = statusListeners.listIterator();
            while (i.hasNext()) {
                StatusListener l = (StatusListener) i.next();
                l.statusDisconnected(e);
            }
        }
    }

    private void fireStatusLoggingStarted() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            ListIterator i = statusListeners.listIterator();
            while (i.hasNext()) {
                StatusListener l = (StatusListener) i.next();
                l.statusLoggingStarted(e);
            }
        }
    }

    private void fireStatusLoggingStopped() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            ListIterator i = statusListeners.listIterator();
            while (i.hasNext()) {
                StatusListener l = (StatusListener) i.next();
                l.statusLoggingStopped(e);
            }
        }
    }

    private void fireStatusOutputPaused() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            ListIterator i = statusListeners.listIterator();
            while (i.hasNext()) {
                StatusListener l = (StatusListener) i.next();
                l.statusOutputPaused(e);
            }
        }
    }

    private void fireStatusOutputUnpaused() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            ListIterator i = statusListeners.listIterator();
            while (i.hasNext()) {
                StatusListener l = (StatusListener) i.next();
                l.statusOutputUnpaused(e);
            }
        }

    }

    class ConnectionListener implements Runnable {
        public void run() {
            try {
                byte[] buffer = new byte[256];

                DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream(),
                        ICBClient.READ_BUFFER_SIZE));

                int len;
                String msg;
                Packet p;
                boolean continueProcessing = true;
                while (continueProcessing) {
                    len = in.readUnsignedByte();
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    assert(len >= 0 && len <= 256);

                    in.readFully(buffer, 0, len);
                    String encoding = clientProperties.getTextEncoding();
                    if (encoding.equalsIgnoreCase("UTF7")) {
                        msg = Utf7Converter.decode(buffer, 0, len);
                    } else {
                        msg = new String(buffer, 0, len, encoding);
                    }

                    try {
                        p = Packet.getInstance(msg);
                        continueProcessing = processPacket(p);
                    } catch (ProtocolException ex) {
                        printMessage(PrintPacket.MSG_TYPE_ERROR, "Unable to process an ICB packet, " + ex.getMessage());
                        System.err.println("A ProtocolException was encountered while sending a packet");
                        ex.printStackTrace(System.err);
                    }
                }
            } catch (IOException ex) {
                printMessage(PrintPacket.MSG_TYPE_ERROR, "An Input/Output operation failed, " + ex.getMessage());
                System.err.println("An IOException was encountered while sending a packet");
                ex.printStackTrace(System.err);
            } finally {
                cleanupSocket();
            }
        }
    }

    class Connector implements Runnable {
        private String host;
        private int port;

        public Connector(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public void run() {
            setConnectingState();

            try {
                socket = new Socket(host, port);
                socket.setKeepAlive(true);
                out = new BufferedOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                printMessage(PrintPacket.MSG_TYPE_ERROR, "Unable to connect to the ICB server, " + ex.getMessage());
                System.err.println("An IOException was encountered while attempting to connect");
                ex.printStackTrace(System.err);
                setDisconnectedState();
                return;
            }

            setConnectedState();

            readerThread = new Thread(new ConnectionListener());
            readerThread.setDaemon(true);
            readerThread.start();
        }
    }

    class Disconnector implements Runnable {
        public void run() {
            setDisconnectingState();

            sendPacket(ICBProtocol.PKT_EXIT + "\000");

            // interrupt the reader thread and wait for it to exit
            readerThread.interrupt();
            try {
                readerThread.join();
            } catch (InterruptedException ex) {
            } finally {
                readerThread = null;
            }
        }
    }
}
