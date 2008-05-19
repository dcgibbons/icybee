/*
 * $Id: ActionHelper.java,v 1.2 2004/09/19 16:28:17 dcgibbons Exp $
 *
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright © 2000-2004 David C. Gibbons, dcg@nuclearbunny.org
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

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This extension of AbstractAction will load various textual properties of
 * the Action from the specified ResourceBundle using the specified prefix.
 *
 * @see Action
 * @author David C. Gibbons
 */
public abstract class ActionHelper extends AbstractAction {
    public ActionHelper(ResourceBundle resources, String resourcePrefix) {
        String acceleratorKey = getResource(resources, resourcePrefix + ".accelerator");
        if (acceleratorKey != null) {
            putValue(Action.ACCELERATOR_KEY, getKeyCode(acceleratorKey.charAt(0)));
        }

        String actionCommandKey = getResource(resources, resourcePrefix + ".command");
        if (actionCommandKey != null) {
            putValue(Action.ACTION_COMMAND_KEY, getKeyCode(actionCommandKey.charAt(0)));
        }

        String longDescription = getResource(resources, resourcePrefix + ".long.desc");
        if (longDescription != null) {
            putValue(Action.LONG_DESCRIPTION, longDescription);
        }

        String mnemonicKey = getResource(resources, resourcePrefix + ".mnemonic");
        if (mnemonicKey != null) {
            putValue(Action.MNEMONIC_KEY, getKeyCode(mnemonicKey.charAt(0)));
        }

        String name = getResource(resources, resourcePrefix + ".name");
        if (name != null) {
            putValue(Action.NAME, name);
        }

        String shortDescription = getResource(resources, resourcePrefix + ".desc");
        if (shortDescription != null) {
            putValue(Action.SHORT_DESCRIPTION, shortDescription);
        }
    }

    private Integer getKeyCode(char aChar) {
        int vk = (int) aChar;
        if(vk >= 'a' && vk <='z')
            vk -= ('a' - 'A');
        return new Integer(vk);
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