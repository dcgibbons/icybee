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

import java.text.NumberFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {
    public static String formatElapsedTime(long elapsedTime) {
        StringBuilder buffer = new StringBuilder();

        if (elapsedTime < 60) {
            buffer.append("       - ");
        } else {
            long hours = elapsedTime / 3600;
            if (hours > 0) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                FieldPosition fp = new FieldPosition(NumberFormat.INTEGER_FIELD);
                nf.setMaximumIntegerDigits(3);
                String h = nf.format(hours, new StringBuffer(), fp).toString();
                buffer.append(StringUtils.repeatString(" ", 3 - fp.getEndIndex()))
                      .append(h)
                      .append("h ");
                elapsedTime -= 3600 * hours;
            } else {
                buffer.append("     ");
            }

            long minutes = elapsedTime / 60;
            if (minutes < 10) {
                buffer.append(' ');
            }
            buffer.append(minutes).append("m");
        }

        return buffer.toString();
    }

    public static String formatEventTime(final Date eventTime) {
        StringBuilder buffer = new StringBuilder();
        long now = System.currentTimeMillis();
        long days = (now - eventTime.getTime()) / 86400000L; // milliseconds in a day
        if (days > 0) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            FieldPosition fp = new FieldPosition(NumberFormat.INTEGER_FIELD);
            nf.setMaximumIntegerDigits(3);
            String ds = nf.format(days, new StringBuffer(), fp).toString();
            buffer.append(StringUtils.repeatString(" ", 3 - fp.getEndIndex())).append(ds).append('+');
        } else {
            buffer.append("    ");
        }

        SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
        df.setTimeZone(TimeZone.getDefault());
        buffer.append(df.format(eventTime));

        return buffer.toString();
    }
}
