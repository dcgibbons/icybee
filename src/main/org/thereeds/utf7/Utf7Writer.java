package org.thereeds.utf7;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * A Reader to put characters onto a UTF-7-encoded byte stream.
 *
 * @author Mark J. Reed
 * @version 1.0
 * @see org.thereeds.utf7.Utf7Reader
 */
public class Utf7Writer extends Writer
{
    /**
     * The {@link java.io.InputStream} from which to read the UTF-7-encoded
     * bytes.
     */
    private OutputStream os = null;

    /**
     * Flag indicating if we're currently in normal or encoded mode
     * (outside or inside a '+' sequence).
     */
    private boolean switched = false;

    /**
     * Bits representing encoded characters
     */
    private int bits = 0;

    /**
     * Count of valid bits inside {@link #bits}.
     */
    private int bitCount = 0;

    /**
     * Create a Utf7Writer chained onto the given OutputStream.
     *
     * @param os The OutputStream onto which to write the encoded bytes.
     */
    public Utf7Writer(OutputStream os)
    {
        super();
        this.os = os;
    }

    /**
     * Encode characters from an array and write them onto the string.
     *
     * @param cbuf The character array from which to read the characters
     * @param off The offset into the array at which to start reading
     *            characters.
     * @param len The number of characters to read.
     * @throws java.io.IOException propagated from the enclosed OutputStream
     */
    public void write(char[] buf, int off, int len)
        throws IOException
    {
        for (int i=0; i<len; ++i)
        {
            char next = buf[off+i];
            if (next < 0x80)
            {
                if (switched)
                {
                    if (0 != bits)
                    {
                        int under = 6 - bitCount;
                        int value = (char) bits << under;
                        os.write(Utf7Util.symbolFor(value));
                    }
                    os.write((byte)'-');
                    switched = false;
                }
                os.write((byte)next);
                if ('+' == next)
                    os.write((byte)'-');
            }
            else
            {
                if (!switched)
                {
                    os.write((byte)'+');
                    switched = true;
                    bits = 0;
                    bitCount = 0;
                }
                bits = (bits << 16) | next;
                bitCount += 16;
                while (bitCount >= 6)
                {
                    int over = bitCount - 6;
                    int mask = 0x3f << over;
                    int value = (bits & mask)>>over;
                    os.write(Utf7Util.symbolFor(value));
                    bits &= ~mask;
                    bitCount = over;
                }
            }
        }
    }

    /**
     * Flush the stream.  Write out bytes representing any leftover bits.
     *
     * @throws java.io.IOException propagated from the enclosed OutputStream
     */
    public void flush() throws IOException
    {
        if (switched)
        {
            if (0 != bits)
            {
                int under = 6 - bitCount;
                int value = (char) bits << under;
                os.write(Utf7Util.symbolFor(value));
            }
            os.write((byte)'-');
        }
        os.flush();
    }

    /**
     * Close the stream.
     *
     * @throws java.io.IOException propagated from the enclosed OutputStream
     */
    public void close() throws IOException
    {
        flush();
        os.close();
    }
}
