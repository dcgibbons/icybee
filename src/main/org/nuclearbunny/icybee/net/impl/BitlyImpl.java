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

package org.nuclearbunny.icybee.net.impl;

import org.nuclearbunny.icybee.net.URLShrinker;
import org.json.JSONTokener;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class BitlyImpl implements URLShrinker {
    /**
     * The <code>BITLY_MIN_URL_LENGTH</code> constant specifies the minimum
     * length of a URL before the TinyURL service would be useful in
     * providing in translation.
     */
    private static final int BITLY_MIN_URL_LENGTH = 24;

    private static final String BITLY_URL = "http://api.bit.ly/shorten";
    private static final String BITLY_API_LOGIN = "icybee";
    private static final String BITLY_API_KEY = "R_97db5ec116eaf6bd5c7b7a15c8e179ec";

    public int getMinURLLen() {
        return BITLY_MIN_URL_LENGTH;
    }

    public String shrinkURL(String url) throws IOException {
        /**
         * bit.ly provides an elegant REST API that returns results in either JSON
         * or XML format. See http://bitly.com/app/developers for more information.
         */
        StringBuilder buffer = new StringBuilder(BITLY_URL);
        buffer.append("?version=2.0.1")
              .append("&format=json")
              .append("&login=").append(BITLY_API_LOGIN)
              .append("&apiKey=").append(BITLY_API_KEY)
              .append("&longUrl=").append(URLEncoder.encode(url, "UTF-8"));

        URL bitlyURL = new URL(buffer.toString());
        URLConnection connection = bitlyURL.openConnection();

        String newURL = url;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JSONObject jsonObject = new JSONObject(new JSONTokener(in));
            if ("OK".equals(jsonObject.opt("statusCode"))) {
                JSONObject results = (JSONObject) jsonObject.get("results");
                results = (JSONObject) results.get(url);
                newURL = results.get("shortUrl").toString();
            }
        } catch (JSONException e) {
            e.printStackTrace(System.err);
            throw new IOException(e.getMessage());
        }

        return newURL;
    }
}
