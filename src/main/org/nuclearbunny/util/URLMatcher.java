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

package org.nuclearbunny.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * The <code>URLMatcher</code> class provides utilities for finding URLs
 * contained within String buffers and for determining if a string is
 * a valid URL.
 *
 * @author David C. Gibbons
 * @since 0.84
 */
public class URLMatcher {
    /**
     * This <code>Pattern</code> object can be used to match for URL strings
     * within a larger string buffer.
     */
    public static final Pattern URL_PATTERN = Pattern.compile("(?s)((?:\\w+://|\\bwww\\.[^.])\\S+)");

    /**
     * Attempts to convert the specified string into a valid URL object.
     *
     * @param candidate
     * @return a valid URL, or <code>null</code> if the candidate was not valid
     */
    public static URL getURL(String candidate) {
        URL url = null;
        try {
            url = new URL(candidate);
        } catch (MalformedURLException ex) {
            url = null;
        }

        try {
            if (url == null) {
                url = new URL("http://" + candidate);
            }
        } catch (MalformedURLException ex) {
            url = null;
        }

        return url;
    }
}
