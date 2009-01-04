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

package org.nuclearbunny.icybee.ui.util;

import javax.swing.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This extension of JRadioButton will load various textual properties of the
 * component from the specified ResourceBundle using the specified prefix.
 * <p/>
 * <code>
 * Text/Name: <prefix>.label
 * Mnemonic:  <prefix>.mnemonic
 * Tool Tip:  <prefix>.desc
 * </code>
 *
 * @author David C. Gibbons
 */
public class JRadioButtonHelper extends JRadioButton {
    public JRadioButtonHelper(ResourceBundle resources, String resourcePrefix) {
        super();
        init(resources, resourcePrefix);
    }

    protected void init(ResourceBundle resources, String resourcePrefix) {
        setText(getResource(resources, resourcePrefix + ".label")); // sets this' AccessibleName property
        setMnemonic(getResource(resources, resourcePrefix + ".mnemonic").charAt(0));
        setToolTipText(getResource(resources, resourcePrefix + ".desc")); // sets this' AccessibleDescription property
    }

    private String getResource(ResourceBundle resources, String resource) {
        String value;
        try {
            value = resources.getString(resource);
        } catch (MissingResourceException ex) {
            value = null;
        }
        return value;
    }
}
