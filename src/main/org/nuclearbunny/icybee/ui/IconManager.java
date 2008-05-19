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

package org.nuclearbunny.icybee.ui;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;

public class IconManager {
    private static IconManager theInstance = null;
    private Pattern emoticonPattern;
    private LinkedList emoticonList;
    private HashMap emoticonMap;

    /**
     * Retrieves an reference to the global <code>IconManager</code> object.
     */
    public static synchronized IconManager getInstance() {
        if (theInstance == null) {
            theInstance = new IconManager();
        }
        return theInstance;
    }

    /**
     * The <code>IconManager</code> class manages icons that represent
     * <i>emoticons</i>. Emoticons are graphical replacements for common
     * textual symbols that represent expressions or emotions. Common
     * emoticons are :) for a smile, :D for a big grin, :P for a razz, ;)
     * for a wink, etc.
     */
    private IconManager() {
        emoticonList = new LinkedList();
        emoticonMap = new HashMap();

        addEmoticon("}:)", "Devil", "images/devil.gif");
        addEmoticon("O:)", "Angel", "images/angel.gif");
        addEmoticon(":)", "Smile", "images/smile.gif");
        addEmoticon(":(", "Frown", "images/frown.gif");
        addEmoticon(":P", "Razz", "images/razz.gif");
        addEmoticon(":D", "Big Grin", "images/biggrin.gif");
        addEmoticon(";)", "Wink", "images/wink.gif");

        StringBuffer patternBuffer = new StringBuffer("(");
        boolean previousEmoticon = false;
        Iterator emoticonIterator = emoticonList.iterator();
        while (emoticonIterator.hasNext()) {
            Emoticon emoticon = (Emoticon) emoticonIterator.next();

            if (previousEmoticon) {
                patternBuffer.append('|');
            }

            String escapedPattern = escapeString(emoticon.emoticonText);
            patternBuffer.append(escapedPattern);

            previousEmoticon = true;
        }
        patternBuffer.append(")");
        String emoticonPatternText = patternBuffer.toString();

        try {
            emoticonPattern = Pattern.compile(emoticonPatternText, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException ex) {
            // XXX TODO
            System.out.println("MalformedPattern: " + ex.getMessage());
        }
    }

    /**
     * Returns a Perl5 compatible <code>Pattern</code> that allows searching
     * for emoticons in a text string.
     *
     * @see Pattern
     */
    public Pattern getPattern() {
        return emoticonPattern;
    }

    /**
     * Returns a <code>List</code> containing all of the <code>Emoticons
     * configured.
     *
     * @see IconManager.Emoticon
     * @see java.util.List
     */
    public List getEmoticonList() {
        return Collections.unmodifiableList(emoticonList);
    }

    /**
     * Adds a new emoticon to the IconManager.
     *
     * @param emoticonText the text string representing the emoticon, i.e.
     *                     :) or :-)
     * @param description  a short description that describes the emoticon
     * @param imagePath    a file path within the application's class loader
     *                     indicating the image to use for this emoticon
     */
    public void addEmoticon(String emoticonText, String description, String imagePath) {
        ClassLoader loader = this.getClass().getClassLoader();

        Emoticon emoticon = new Emoticon();
        emoticon.emoticonText = emoticonText;
        emoticon.description = description;
        emoticon.imagePath = imagePath;
        emoticon.imageIcon = new ImageIcon(loader.getResource(imagePath));

        emoticonList.addLast(emoticon);
        emoticonMap.put(emoticonText, emoticon);
    }

    /**
     * Retrieves the <code>ImageIcon</code> object for a given emoticon text
     * string. If the emoticon is not available, the return value is
     * <code>null</code>
     */
    public ImageIcon getIcon(String emoticonText) {
        Emoticon emoticon = (Emoticon) emoticonMap.get(emoticonText);
        return (emoticon != null) ? emoticon.imageIcon : null;
    }

    /**
     * Convert an input string to one escaped to work with the
     * regular expression compiler.
     */
    private String escapeString(String source) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, n = source.length(); i < n; i++) {
            char c = source.charAt(i);
            // XXX this is an incomplete escape list
            switch (c) {
                case '(':
                    buffer.append("\\(");
                    break;
                case ')':
                    buffer.append("\\)");
                    break;
                case '\\':
                    buffer.append("\\\\");
                    break;
                case '[':
                    buffer.append("\\[");
                    break;
                case ']':
                    buffer.append("\\]");
                    break;
                default:
                    buffer.append(c);
                    break;
            }
        }
        return buffer.toString();
    }

    /**
     * A simple helper class to hold Emoticon information.
     */
    public static class Emoticon {
        public String emoticonText;
        public String description;
        public String imagePath;
        public ImageIcon imageIcon;
    }
}
