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

package org.nuclearbunny.icybee.net;

import java.io.IOException;

/**
 * The <code>URLShrinker</code> interface specifies methods that allow the
 * caller to translate URLs into shorter forms. Implementation classes are
 * assumed to provide this functionality using a variety of available
 * URL shrinking services.
 *
 * @author David C. Gibbons
 * @since 0.92
 */
public interface URLShrinker {
    /**
     * This method returns the minimum length a URL should be before this
     * URL shrinking service is used.
     * @return the minimum URL length that should be sent to this
     *         implementation
     */
    public int getMinURLLen();

    /**
     * This method attempts to translate a URL into a shorter form using the
     * implemented URL shrinking service.
     *
     * @param url the original URL text
     * @return the translated URL
     * @throws IOException if any error occurs while communicating to the URL
     *         shrinking service
     */
    public String shrinkURL(String url) throws IOException;
}
