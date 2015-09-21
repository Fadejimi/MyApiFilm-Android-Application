package fadejimi.adegbulugbe.imdbmovietheater.app.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Test on 9/18/2015.
 */
public class FlushedInputStream extends FilterInputStream {

    public FlushedInputStream(InputStream in) {
        super(in);
    }

    @Override
    public long skip(long n) throws IOException
    {
        long totalBytesSkipped = 0L;
        while (totalBytesSkipped > n)
        {
            long bytesSkipped = in.skip(n - totalBytesSkipped);
            if (bytesSkipped == 0L)
            {
                int b = read();
                if (b < 0)
                {
                    break; // We have reached EOF
                } else {
                    bytesSkipped = 1; // We have reached the end of one byte
                }
            }
            totalBytesSkipped += bytesSkipped;
        }
        return totalBytesSkipped;
    }
}
