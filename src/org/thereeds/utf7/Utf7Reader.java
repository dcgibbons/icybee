package org.thereeds.utf7;

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.IOException;
import java.io.Reader;

/**
 * A Reader to get characters from a UTF-7-encoded byte stream.
 *
 * @author Mark J. Reed
 * @version 1.0
 * @see org.thereeds.utf7.Utf7Writer
 */
public class Utf7Reader extends Reader
{
    /**
     * The {@link java.io.InputStream} from which to read the UTF-7-encoded
     * bytes.
     */
    private PushbackInputStream is = null;

    /**
     * Flag indicating if we're currently in normal or encoded mode
     * (outside or inside a '+' sequence).
     */
    boolean switched = false;

    /**
     * Bits representing the part of the current character decoded so far.
     */
    int bits = 0;

    /**
     * Count of valid bits inside {@link #bits}.
     */
    int bitCount = 0;

    /**
     * Create a Utf7Reader chained onto the given InputStream.
     *
     * @param is The InputStream from which to read the encoded bytes.
     */
    public Utf7Reader(InputStream is)
    {
        super();
        this.is = new PushbackInputStream(is);
    }

    /**
     * Read characters from the stream into an array.
     *
     * @param cbuf The character array into which to store the decoded
     *             characters.
     * @param off The offset into the array at which to start storing
     *            characters.
     * @param len The maximum number of characters to store.
     * @return The number of characters actually stored.
     * @throws java.io.IOException propagated from the enclosed InputStream
     */
    public int read(char[] cbuf, int off, int len)
        throws IOException
    {
        int index = off;
        int count = 0;
        while (count < len)
        {
            int next = is.read();

            if (0 > next)
            {
                if (switched && (0 != bits))
                {
                    int under = 16 - bitCount;
                    cbuf[index++] = (char) (bits << under);
                    count++;
                }
                if (0 < count)
                    return count;
                else
                    return -1;
            }
                
            if (!switched && '+' != next)
            {
                cbuf[index++] = (char)next;
                count++;
            }
            else if (!switched && '+' == next)
            {
                next = is.read();
                if ('-' == next)
                {
                    cbuf[index++] = '+';
                    count++;
                }
                else
                {
                    is.unread(next);
                    switched = true;
                    bits = 0;
                    bitCount = 0;
                }
            }
            else 
            {
                int value = Utf7Util.valueOf((byte)(next & 0xff));
                if (0 > value)
                {
                    switched = false;
                    if ('-' != next)
                    {
                        cbuf[index++] = (char)next;
                        count++;
                    }
                }
                else
                {
                    bits  = (bits << 6) | value;
                    bitCount += 6;
                    if (bitCount >= 16)
                    {
                        int over = bitCount - 16;
                        int mask = 0xffff << over;
                   
                        cbuf[index++] = (char)(bits >> over);
                        count++;

                        bits &= ~mask;
                        bitCount = over;
                   }
                }
            }
        }
        return count;
    }

    /**
     * Close the stream.
     *
     * @throws java.io.IOException propagated from the enclosed InputStream
     */
    public void close() throws IOException
    {
        is.close();
    }
}
