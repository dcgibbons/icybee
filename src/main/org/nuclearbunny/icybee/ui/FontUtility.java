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

package org.nuclearbunny.icybee.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class FontUtility {
    public static Font getFont(AttributeSet a) {
        // build a new font encoding in the form of "fontfamily-style-size"
        StringBuffer buffer = new StringBuffer(StyleConstants.getFontFamily(a));
        buffer.append('-');
        if (StyleConstants.isBold(a)) {
            if (StyleConstants.isItalic(a)) {
                buffer.append("BOLDITALIC");
            } else {
                buffer.append("BOLD");
            }
        } else if (StyleConstants.isItalic(a)) {
            buffer.append("ITALIC");
        } else {
            buffer.append("PLAIN");
        }
        buffer.append('-').append(StyleConstants.getFontSize(a));

        Font f = Font.decode(buffer.toString());
        return f;
    }
}