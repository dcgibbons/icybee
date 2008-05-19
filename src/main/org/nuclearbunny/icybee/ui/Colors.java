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

package org.nuclearbunny.icybee.ui;

import java.awt.*;
import java.util.*;

public class Colors {
    public final String name;
    public final Color defaultColor;
    public Color color;

    private Colors(String name, Color defaultColor) { 
        this.name = name;
        this.defaultColor = defaultColor;
        color = defaultColor;
    }

    public String toString() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public static final Colors BACKGROUND = new Colors("Background", Color.white);
    public static final Colors OPEN_TEXT = new Colors("Open Message Text", Color.black);
    public static final Colors OPEN_NICK = new Colors("Open Message Nick", Color.blue);
    public static final Colors PERSONAL_TEXT = new Colors("Personal Message Text", Color.darkGray);
    public static final Colors PERSONAL_NICK = new Colors("Personal Message Nick", Color.lightGray);
    public static final Colors COMMAND_TEXT = new Colors("Command Output Text", Color.black);
    public static final Colors ERROR_TEXT = new Colors("Error Text", Color.red);
    public static final Colors STATUS_HEADER = new Colors("Status Header", Color.orange);
    public static final Colors STATUS_TEXT = new Colors("Status Text", Color.black);

    private static final Colors[] PRIVATE_VALUES = {
        BACKGROUND, OPEN_TEXT, OPEN_NICK, PERSONAL_TEXT, PERSONAL_NICK, COMMAND_TEXT, ERROR_TEXT, STATUS_HEADER, STATUS_TEXT
    };

    public static final java.util.List VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));
}
