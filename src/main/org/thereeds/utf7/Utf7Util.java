package org.thereeds.utf7;

/**
 * Utilities for use by the UTF-7 conversion classes.
 *
 * @author Mark J. Reed
 * @version 1.0
 * @see org.thereeds.utf7.Utf7Converter
 * @see org.thereeds.utf7.Utf7Reader
 * @see org.thereeds.utf7.Utf7Writer
 */
public class Utf7Util 
{
    /**
     * Internal storage of the values returned by {@link #symbolFor}.
     */
    private static char[] symbols =
    { 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
      'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
      'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
      'w', 'x', 'y', 'z', '0', '1', '2', '3',
      '4', '5', '6', '7', '8', '9', '+', '/'
    };

    /**
     * Internal storage of the values returned by {@link #valueOf}.
     */
    private static int[] values = null;

    /**
     * Return the one-byte character value used to repersent a given
     * the base-64 "digit" in UTF-7.
     *
     * @param value The value to represent, 0 &lt;= value &lt; 64.
     * @return The byte representing that value, or the byte equivalent of
     *         the character '?' if the value is outside the legal range.
     */
    public static byte symbolFor(int value)
    {
        if (value < 0 || value > symbols.length)
            return (byte)'?';
        else
            return (byte)symbols[value];
    }

    /**
     * Return the one-base64-"digit" value of a given byte inside a
     * UTF-7 sequence.  
     * @param inByte the byte of which to find the value.
     * @return The value of that byte, or -1 if the byte is not a legal
     *         UTF-7 digit.
     */
    public static int valueOf(byte inByte)
    {
        if (null == values)
        {
            values = new int[256];

            for (int i=0; i<256; ++i)
            {
                values[i] = -1;
            }

            for (int i=0; i<symbols.length; ++i)
            {
                values[symbols[i]] = i;
            }
        }
        int index = inByte;
        if (index < 0)
            index = 256 + index;
        return values[index];
    }
}
