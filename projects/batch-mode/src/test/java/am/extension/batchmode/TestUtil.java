package am.extension.batchmode;

import com.google.common.io.ByteStreams;
import junit.framework.ComparisonFailure;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class TestUtil {
    public static InputStream openResource(String file) {
        final InputStream s = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(file);
        assert s != null;
        return s;
    }

    public static void assertEqualStreams(InputStream expected, InputStream actual) throws IOException {
        byte[] expectedBytes = ByteStreams.toByteArray(expected);
        byte[] actualBytes = ByteStreams.toByteArray(actual);

        if(!Arrays.equals(expectedBytes, actualBytes)) {
            throw new ComparisonFailure("Streams differ.", new String(expectedBytes), new String(actualBytes));
        }
    }
}
