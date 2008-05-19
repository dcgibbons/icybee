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

package org.nuclearbunny.icybee.net.impl;

import org.nuclearbunny.icybee.net.URLShrinker;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * The <code>TinyURLImpl</code> class provides URL shrinking functionality
 * using the TinyURL service found at
 * <a href="http://www.tinyurl.com">http://www.tinyurl.com/</a>
 *
 * Since TinyURL does not provide an API, this implementation uses HTML
 * "scraping" in order to retrieve the translated URL. This implementation
 * is likely to break anytime the TinyURL service changes the way it generates
 * web pages.
 *
 * @author David C. Gibbons
 * @since 0.92
 */
public class TinyURLImpl implements URLShrinker {
    /**
     * The <code>TINYURL_MIN_URL_LENGTH</code> constant specifies the minimum
     * length of a URL before the TinyURL service would be useful in
     * providing in translation.
     */
    private static final int TINYURL_MIN_URL_LENGTH = 24;

    private static final String TINYURL_URL = "http://tinyurl.com/create.php";
    private static final String TINYURL_TOKEN = "name=tinyurl value=";

    public int getMinURLLen() {
        return TINYURL_MIN_URL_LENGTH;
    }

    public String shrinkURL(String url) throws IOException {
        URL tinyURL = new URL(TINYURL_URL);
        URLConnection connection = tinyURL.openConnection();
        connection.setDoOutput(true);

        PrintWriter out = new PrintWriter(connection.getOutputStream());
        out.println("url=" + url);
        out.close();

        String newURL = url;

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            int n = inputLine.indexOf(TINYURL_TOKEN);
            if (n >= 0) {
                newURL = inputLine.substring(n + TINYURL_TOKEN.length());
                int start, end;
                n = newURL.length();
                for (start = 0; start < n; start++) {
                    if (newURL.charAt(start) == '"')
                        break;
                }
                for (end = start+1; end < n; end++) {
                    if (newURL.charAt(end) == '"')
                        break;
                }
                newURL = newURL.substring(start+1, end-start);
                break;
            }
        }
        in.close();

        return newURL;
    }
}
