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

import java.awt.*;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class AboutDialog extends HtmlDialog {
    private static final ResourceBundle verInfo = ResourceBundle.getBundle("version");

    public AboutDialog(Frame owner) {
        super(owner);
        initDialog();
    }

    private void initDialog() {
        setTitle(UIMessages.messages.getString("about.dialog.title"));

        double release = Double.parseDouble(verInfo.getString("Release"));
        String releaseType = verInfo.getString("ReleaseType");

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        StringBuffer aboutText = new StringBuffer();
        aboutText.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        aboutText.append("<html><body bgcolor=\"");
        convertColorToHtml(aboutText, getBackground());
        aboutText.append("\" text=\"");
        convertColorToHtml(aboutText, getForeground());
        aboutText.append("\">");

        aboutText.append("<center>");
        aboutText.append("<font size=+2>")
                .append(UIMessages.messages.getString("about.dialog.app.name"))
                .append("</font>");
        aboutText.append("<br>").append(UIMessages.messages.getString("about.dialog.version"))
                .append(" ").append(nf.format(release)).append(" ").append(releaseType);
        aboutText.append("<br>").append(UIMessages.messages.getString("about.dialog.copyright.prefix"))
                .append(" ").append(verInfo.getString("ReleaseCopyrightYear"))
                .append(" ").append(UIMessages.messages.getString("about.dialog.copyright.postfix"));
        aboutText.append("<br>");
        aboutText.append("<a href=\"").append(UIMessages.messages.getString("about.dialog.icybee.url")).append("\">")
                .append(UIMessages.messages.getString("about.dialog.icybee.url")).append("</a>");

        aboutText.append("<br><table border=0 cellspacing=0 cellpadding=1>");
        aboutText.append("<tr>")
                .append("<td>").append(UIMessages.messages.getString("about.dialog.java.version")).append("</td>")
                .append("<td>").append(System.getProperty("java.version")).append("</td>")
                .append("</tr>");
        aboutText.append("<tr>")
                .append("<td>").append(UIMessages.messages.getString("about.dialog.java.vendor")).append("</td>")
                .append("<td>").append(System.getProperty("java.vendor")).append("</td>")
                .append("</tr>");
        aboutText.append("<tr>")
                .append("<td>").append(UIMessages.messages.getString("about.dialog.java.vendor.url")).append("</td>")
                .append("<td><a href=\"").append(System.getProperty("java.vendor.url")).append("\">")
                .append(System.getProperty("java.vendor.url")).append("</a").append("</td>")
                .append("</tr>");
        aboutText.append("<tr>")
                .append("<td>").append(UIMessages.messages.getString("about.dialog.java.vm.spec.version"))
                .append("</td>")
                .append("<td>").append(System.getProperty("java.vm.specification.version")).append("</td>")
                .append("</tr>");
        aboutText.append("<tr>")
                .append("<td>").append(UIMessages.messages.getString("about.dialog.java.vm.version")).append("</td>")
                .append("<td>").append(System.getProperty("java.vm.version")).append("</td>")
                .append("</tr>");
        aboutText.append("<tr>")
                .append("<td>").append(UIMessages.messages.getString("about.dialog.java.vm.vendor")).append("</td>")
                .append("<td>").append(System.getProperty("java.vm.vendor")).append("</td>")
                .append("</tr>");
        aboutText.append("<tr>")
                .append("<td>").append(UIMessages.messages.getString("about.dialog.java.home")).append("</td>")
                .append("<td>").append(System.getProperty("java.home")).append("</td>")
                .append("</tr>");
        aboutText.append("<tr>")
                .append("<td>").append(UIMessages.messages.getString("about.dialog.os.name")).append("</td>")
                .append("<td>").append(System.getProperty("os.name")).append("</td>")
                .append("</tr>");
        aboutText.append("<tr>")
                .append("<td>").append(UIMessages.messages.getString("about.dialog.os.arch")).append("</td>")
                .append("<td>").append(System.getProperty("os.arch")).append("</td>")
                .append("</tr>");
        aboutText.append("<tr>")
                .append("<td>").append(UIMessages.messages.getString("about.dialog.os.version")).append("</td>")
                .append("<td>").append(System.getProperty("os.version")).append("</td>")
                .append("</tr>");
        aboutText.append("</table>");
        aboutText.append("</center>");
        aboutText.append("</body></html>");

        setText(aboutText.toString());
    }

    private void convertColorToHtml(StringBuffer buffer, Color c) {
        buffer.append("#");

        String red = Integer.toHexString(c.getRed()).toUpperCase();
        if (red.length() == 1) {
            buffer.append("0");
        }
        buffer.append(red);

        String green = Integer.toHexString(c.getGreen()).toUpperCase();
        if (green.length() == 1) {
            buffer.append("0");
        }
        buffer.append(green);

        String blue = Integer.toHexString(c.getBlue()).toUpperCase();
        if (blue.length() == 1) {
            buffer.append("0");
        }
        buffer.append(blue);
    }
}
