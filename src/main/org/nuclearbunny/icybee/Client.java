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

import java.io.IOException;

// TODO: document the interface
public interface Client {
    boolean isConnected();
    void connect(String server) throws IllegalStateException, IOException;
    void disconnect() throws IllegalStateException;

    boolean isLoggingEnabled();
    void startLogging() throws IllegalStateException, IOException;
    void startLogging(String logFileName) throws IllegalStateException, IOException;
    void stopLogging() throws IllegalStateException;

    void printMessage(int msgType, String msg);

    void sendCommand(String cmd);

    void sendOpenMessage(String msg);
    void sendPersonalMessage(String nick, String origMsg);
    void sendWriteMessage(String nick, String origMsg);
    void sendCommandMessage(String command, String msg);

    void addUserHistory(String nick);
    String getNextUserFromHistory(boolean getLastUsed);
    void removeUserFromHistory(String nick);
    void clearHistory();
}
