package org.thereeds.utf7;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A front end to {@link Utf7Reader} and {@link Utf7Writer} with
 * methods for operating on Strings and byte arrays instead of I/O streams.
 *
 * @author Mark J. Reed
 * @version 1.0
 * @see org.thereeds.utf7.Utf7Reader
 * @see org.thereeds.utf7.Utf7Writer
 */
public class Utf7Converter
{
    /**
     * Decode a byte array containing UTF-7-encoded text into a String.
     *
     * @param utf7Bytes Array of bytes containing the UTF-7-encoded text.
     * @return A String containing the decoded text.
     *
     */
    public static String decode(byte[] utf7Bytes)
    {
        return decode(utf7Bytes, 0, utf7Bytes.length);
    }

    /**
     * Decode a portion of a byte array containing UTF-7-encoded text
     * into a String. Note: no context is maintained across calls for
     * the same array, so the delimited section must be complete unto 
     * itself - that is, it must not start or end in the middle of a
     * UTF-7-encoded sequence.
     *
     * @param utf7Bytes Array of bytes containing the UTF-7-encoded text.
     * @param start position in the array at which to start decoding
     * @param len   number of bytes to decode
     * @return A String containing the decoded text.
     *
     */
    public static String decode(byte[] utf7Bytes, int start, int len)
    {
        ByteArrayInputStream is = 
            new ByteArrayInputStream(utf7Bytes, start, len);
        Utf7Reader reader = new Utf7Reader(is);

        StringBuffer buf = new StringBuffer();
        
        int next;
        try
        {
            while (0 <= (next = reader.read()))
            {
                buf.append((char)next);
            }
            reader.close();
        }
        catch (IOException e)
        {
            return null;
        }
        return buf.toString();
    }
             
    /**
     * Create a byte array containing UTF-7-encoded text from a String.
     *
     * @param string String containing the text to encode.
     * @return A byte array containing the encoded text.
     *
     */
    public static byte[] encode(String string)
    {
        ByteArrayOutputStream os = 
            new ByteArrayOutputStream();
        Utf7Writer writer = new Utf7Writer(os);

        try
        {
            writer.write(string);
            writer.close();
        }
        catch (IOException e)
        {
            return null;
        }
        return os.toByteArray();
    }
}
