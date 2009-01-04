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
 * This extension of JTextField will load various textual properties of the
 * component from the specified ResourceBundle using the specified prefix.
 * <p/>
 * <code>
 * Text/Name: <prefix>.label
 * Mnemonic:  <prefix>.mnemonic
 * Tool Tip:  <prefix>.desc
 * </code>
 * <p/>
 * In addition, a JLabel associated with this component (using the
 * <prefix>.label property) will be created and can be retrieved with the
 * <code>getDefaultLabel</code> method.
 *
 * @author David C. Gibbons
 */
public class JTextFieldHelper extends JTextField {
    private JLabel defaultLabel;

    public JTextFieldHelper(ResourceBundle resources, String resourcePrefix) {
        super();
        init(resources, resourcePrefix);
    }

    public JTextFieldHelper(ResourceBundle resources, String resourcePrefix, int columns) {
        super(columns);
        init(resources, resourcePrefix);
    }

    public JTextFieldHelper(ResourceBundle resources, String resourcePrefix, String text) {
        super(text);
        init(resources, resourcePrefix);
    }

    /**
     * Retrieves a JLabel associated with this component. The text property
     * will already be set, and <code>setLabelFor</code> has already been
     * called to associate the label with this component
     *
     * @return the JLabel specific to this component
     */
    public JLabel getDefaultLabel() {
        return defaultLabel;
    }

    protected void init(ResourceBundle resources, String resourcePrefix) {
        String name = getResource(resources, resourcePrefix + ".label");
        if (name != null) {
            defaultLabel = new JLabel(name);
            defaultLabel.setLabelFor(this); // sets this' AccessibleName property
        }

        String mnemonic = getResource(resources, resourcePrefix + ".mnemonic");
        if (mnemonic != null) {
            defaultLabel.setDisplayedMnemonic(mnemonic.charAt(0));
        }

        String shortDesc = getResource(resources, resourcePrefix + ".desc");
        if (shortDesc != null) {
            setToolTipText(shortDesc); // sets this' AccessibleDescription property
        }
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
