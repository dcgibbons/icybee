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

package org.nuclearbunny.icybee.ui.macosx;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;
import org.nuclearbunny.icybee.ui.UIMessages;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Provides Mac OS X specific integration functionality.
 *
 * @see http://developer.apple.com/documentation/Java/Conceptual/Java14Development/04-JavaUIToolkits/JavaUIToolkits.html#//apple_ref/doc/uid/TP40001901
 */
public class MacOSXIntegratorImpl implements ApplicationListener {
    private Application application = Application.getApplication();
    private Action aboutAction;
    private Action prefsAction;
    private Action quitAction;

    protected MacOSXIntegratorImpl() {
        String appName = UIMessages.messages.getString(UIMessages.ICYBEE_APP_NAME);
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        application.setEnabledAboutMenu(true);
        application.setEnabledPreferencesMenu(true);
        application.addApplicationListener(this);
    }

    protected void setAboutAction(Action aboutAction) {
        this.aboutAction = aboutAction;
    }

    protected void setPreferencesAction(Action prefsAction) {
        this.prefsAction = prefsAction;
    }

    protected void setQuitAction(Action quitAction) {
        this.quitAction = quitAction;
    }

    public void handleAbout(ApplicationEvent event) {
        if (aboutAction != null) {
            aboutAction.actionPerformed(new ActionEvent(this, 0, "handleAbout"));
            event.setHandled(true);
        }
    }

    public void handleOpenApplication(ApplicationEvent event) {
    }

    public void handleOpenFile(ApplicationEvent event) {
    }

    public void handlePreferences(ApplicationEvent event) {
        if (prefsAction != null) {
            prefsAction.actionPerformed(new ActionEvent(this, 0, "handlePreferences"));
            event.setHandled(true);
        }
    }

    public void handlePrintFile(ApplicationEvent event) {
    }

    public void handleQuit(ApplicationEvent event) {
        if (quitAction != null) {
            quitAction.actionPerformed(new ActionEvent(this, 0, "handleQuit"));
            event.setHandled(true);
        }
    }

    public void handleReOpenApplication(ApplicationEvent event) {
    }
}
