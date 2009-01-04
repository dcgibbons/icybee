/*
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright (C) 2000-2009 David C. Gibbons
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
import org.nuclearbunny.util.HexDump;
import org.thereeds.utf7.Utf7Converter;
import tcl.lang.TclException;

import java.io.*;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.*;

public class ICBClient implements Client {
    private static final int READ_BUFFER_SIZE = 10240;

    private List<Usage> usageList = new LinkedList<Usage>();
    private ICBProperties clientProperties = new ICBProperties();
    private TclUtil tclManager = new TclUtil(this);
    private LinkedList<String> userHistory = new LinkedList<String>();
    private int currentHistory = 0;

    private OutputLogger logger = null;
    private Timer timer = null;

    private boolean outputPaused = false;

    private int reconnectNeeded = 0;

    private ConnectionState state = ConnectionState.DISCONNECTED;
    private List<StatusListener> statusListeners = new LinkedList<StatusListener>();
    private List<MessageListener> messageListeners = new LinkedList<MessageListener>();
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
        msg = removeControlCharacters(msg);

        if (IcyBee.isDebugEnabled()) {
            System.out.println("Sending Open Message: ");
            HexDump.dump(System.out, msg.getBytes());
        }

        /* send the message in maximum sized chunks */
        String currentMsg;
        String remaining = msg;
        int n;
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

            OpenPacket p = new OpenPacket();
            p.setText(currentMsg);
            sendPacket(p.toString());

        } while (remaining.length() > 0);
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
    public void sendCommandMessage(final String command, final String msg) {
        // XXX validate packet length from user
        sendPacket(new CommandPacket(command, msg).toString());
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
            nick = userHistory.get(currentHistory);
            currentHistory++;
            if (currentHistory == userHistory.size()) {
                currentHistory = 0;
            }
        } catch (IndexOutOfBoundsException ex) {
            currentHistory = 0;
        }
        return nick;
    }

    static class Usage {
        String name;
        String type;
        String args;
        String usage;
    }

    /**
     * {@inheritDoc}
     */
    public void addUsage(String name, String type, String args, String usage) {
        Usage u = new Usage();
        u.name = name;
        u.type = type;
        u.args = args;
        u.usage = usage;
        usageList.add(u);
    }

    /**
     * {@inheritDoc}
     */
    public void removeUsage(String name) {
        ListIterator<Usage> i = usageList.listIterator();
        while (i.hasNext()) {
            Usage u = i.next();
            if (u.name.compareTo(name) == 0) {
                i.remove();
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unusedArgument")
    public void listUsage(String name) {
        printMessage(PrintPacket.MSG_TYPE_NORMAL, "[=Commands=]");
        for (Usage u : usageList) {
            printMessage(PrintPacket.MSG_TYPE_NORMAL, u.name + " " + u.type + " " + u.args + " " + u.usage);
        }
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public boolean isConnected() {
        synchronized (this) {
            return (state == ConnectionState.CONNECTED);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sendPacket(String msg) {
        try {
            if (msg.charAt(0) != ICBProtocol.PKT_NOOP.getPacketType()) {
                lastMessageSentAt = System.currentTimeMillis();
            }

            byte[] buf;
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

            synchronized (this) {
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
     *
     * @param listener the new listener to add
     */
    public void addMessageListener(MessageListener listener) {
        synchronized (this) {
            messageListeners.add(listener);
        }
    }

    /**
     * Removes an existing MessageListener object from the list of objects
     * listening for MessageEvent notifications.
     *
     * @param listener the listener to remove
     */
    public void removeMessageListener(MessageListener listener) {
        synchronized (this) {
            int n = messageListeners.indexOf(listener);
            if (n >= 0) {
                messageListeners.remove(n);
            }
        }
    }

    /**
     * Adds a new StatusListener object to the list of objects listening
     * for StatusEvent notifications.
     *
     * @param listener the new listener to add
     */
    public void addStatusListener(StatusListener listener) {
        synchronized (this) {
            statusListeners.add(listener);
        }
    }

    /**
     * Removes an existing StatusListener object from the list of objects
     * listening for StatusEvent notifications.
     *
     * @param listener the new listener to remove
     */
    public void removeStatusListener(StatusListener listener) {
        synchronized (this) {
            int n = statusListeners.indexOf(listener);
            if (n >= 0) {
                statusListeners.remove(n);
            }
        }
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

    private void executeInitScript() {
        final String initScriptName = clientProperties.getInitScript();
        final String folder = System.getProperty("user.home");
        final String subdir = Properties.SUBDIRECTORY;

        File subdirFile = new File(folder, subdir);
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
            for (StatusListener listener : statusListeners) {
                listener.statusConnecting(e);
            }
        }
    }

    private void fireMessageReceived(MessageEvent e) {
        synchronized (this) {
            for (MessageListener listener : messageListeners) {
                listener.messageReceived(e);
            }
        }
    }

    private void fireStatusConnected() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            for (StatusListener listener : statusListeners) {
                listener.statusConnected(e);
            }
        }
    }

    private void fireStatusDisconnecting() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            for (StatusListener listener : statusListeners) {
                listener.statusDisconnecting(e);
            }
        }
    }

    private void fireStatusDisconnected() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            for (StatusListener listener : statusListeners) {
                listener.statusDisconnected(e);
            }
        }
    }

    private void fireStatusLoggingStarted() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            for (StatusListener listener : statusListeners) {
                listener.statusLoggingStarted(e);
            }
        }
    }

    private void fireStatusLoggingStopped() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            for (StatusListener listener : statusListeners) {
                listener.statusLoggingStopped(e);
            }
        }
    }

    private void fireStatusOutputPaused() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            for (StatusListener listener : statusListeners) {
                listener.statusOutputPaused(e);
            }
        }
    }

    private void fireStatusOutputUnpaused() {
        synchronized (this) {
            EventObject e = new EventObject(this);
            for (StatusListener listener : statusListeners) {
                listener.statusOutputUnpaused(e);
            }
        }

    }

    class ConnectionListener implements Runnable {
        public void run() {
            try {
                final byte[] buffer = new byte[256];
                final DataInputStream in = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream(),
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
                    assert (len >= 0 && len <= 256);

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
                // NO-OP
            } finally {
                readerThread = null;
            }
        }
    }
}
