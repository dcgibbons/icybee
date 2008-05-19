package org.nuclearbunny.icybee.ui.macosx;

import javax.swing.*;

public class MacOSXIntegrator {
    private static MacOSXIntegrator thisInstance;
    private MacOSXIntegratorImpl impl;

    private MacOSXIntegrator() {
        if (isMacOSX()) {
            impl = new MacOSXIntegratorImpl();
        }
    }

    public static boolean isMacOSX() {
        return (System.getProperty("os.name").indexOf("Mac OS X") != -1);
    }

    public static MacOSXIntegrator getInstance() {
        if (isMacOSX() && thisInstance == null) {
            thisInstance = new MacOSXIntegrator();
        }

        return thisInstance;
    }

    public void setAboutAction(Action aboutAction) {
        if (impl != null) {
            impl.setAboutAction(aboutAction);
        }
    }

    public void setPreferencesAction(Action preferencesAction) {
        if (impl != null) {
            impl.setPreferencesAction(preferencesAction);
        }
    }

    public void setQuitAction(Action quitAction) {
        if (impl != null) {
            impl.setQuitAction(quitAction);
        }
    }
}
