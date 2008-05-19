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

import java.io.*;

public abstract class Properties {
    public static final String SUBDIRECTORY = ".icybee";

    protected java.util.Properties properties = null;

    private String propertiesFilename;
    private String propertiesDescription;
    private boolean propertiesAvailable = false;

    public void getParameters() {
        java.util.Properties defaults = new java.util.Properties();
        FileInputStream in = null;

        setDefaults(defaults);

        properties = new java.util.Properties(defaults);

        try {
            String folder = System.getProperty("user.home");
            String filesep = System.getProperty("file.separator");
            String subdir = Properties.SUBDIRECTORY;
            File subdirFile = new File(folder + filesep + subdir);
            in = new FileInputStream(new File(subdirFile, propertiesFilename));
            properties.load(in);
        } catch (java.io.FileNotFoundException ex) {
            in = null;
        } catch (java.io.IOException ex) {
        } finally {
            if (in != null) {
                try { 
                    in.close(); 
                } catch (java.io.IOException ex) {
                }
                in = null;
            }
        }

        propertiesAvailable = (in != null);

        updateSettingsFromProperties();
    }

    public void saveParameters() {
        updatePropertiesFromSettings();

        FileOutputStream out = null;

        try {
            String folder = System.getProperty("user.home");
            String filesep = System.getProperty("file.separator");
            String subdir = Properties.SUBDIRECTORY;
            File subdirFile = new File(folder + filesep + subdir);
            if (!subdirFile.exists()) {
                subdirFile.mkdirs();
            }
            File f = new File(subdirFile, propertiesFilename);
            if (IcyBee.isDebugEnabled()) {
                System.out.println("Storing user settings in " + f.getAbsolutePath());
            }
            out = new FileOutputStream(f);
            properties.store(out, propertiesDescription);
        } catch (IOException ex) {
        } finally {
            if (out != null) {
                try { 
                     out.close(); 
                } catch (java.io.IOException e) {
                }
                out = null;
            }
        }
    }

    public boolean available() {
        return propertiesAvailable;
    }

    protected Properties(String fname, String desc) {
        this.propertiesFilename = fname;
        this.propertiesDescription = desc;
    }

    abstract protected void setDefaults(java.util.Properties defaults);
    abstract protected void updatePropertiesFromSettings();
    abstract protected void updateSettingsFromProperties();
}
