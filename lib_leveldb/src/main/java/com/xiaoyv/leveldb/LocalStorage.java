package com.xiaoyv.leveldb;//package com.xiaoyv.floater.widget.x5.helper;

import android.content.Context;

import androidx.annotation.NonNull;

import com.xiaoyv.leveldb.impl.Iq80DBFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Reads the Local Storage database created by WebView in the app's profile directory, to recover
 * from the incomplete profile migration in https://crbug.com/1033655.
 * <p>
 * See https://source.chromium.org/chromium/chromium/src/+/master:components/services/storage/dom_storage/local_storage_impl.cc
 * for the description of the DB schema this is based on.
 */
public class LocalStorage implements AutoCloseable {
    private static final byte[] META_PREFIX = {'M', 'E', 'T', 'A', ':'};
    private static final byte FORMAT_UTF16 = 0;
    private static final byte FORMAT_LATIN1 = 1;

    public DB db;

    /**
     * An iterator that should be closed once it's no longer needed.
     */
    public interface CloseableIterator<T> extends Iterator<T>, Closeable {
    }

    /**
     * Create an instance of LocalStorage which will read the data from the backup created during
     * the fix for https://crbug.com/1033655.
     *
     * @param ctx Application context
     */
    public LocalStorage(@NonNull Context ctx) throws IOException {
        this(new File(ctx.getDir("webview", Context.MODE_PRIVATE), "Default/Local Storage"));
    }

    /**
     * Create an instance of LocalStorage which will read the data from an arbitrary Local Storage
     * directory. Do not use this to access a running WebView instance's LocalStorage - this is not safe
     * for concurrent access.
     *
     * @param localStorageDir Path to a "Local Storage" directory from a WebView profile.
     */
    public LocalStorage(@NonNull File localStorageDir) throws IOException {
        File dbPath = new File(localStorageDir, "leveldb");
        if (!dbPath.isDirectory()) {
            throw new IllegalArgumentException("Path " + localStorageDir + " is not a Local Storage database");
        }
        Options options = new Options();
        options.createIfMissing(false);
        db = Iq80DBFactory.factory.open(dbPath, options);
    }

    public void put(String key, String value) {
        db.put(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Iterate over all of the origins which have data stored in this Local Storage database.
     *
     * @return Iterator returning each origin as a string.
     */
    @NonNull
    public CloseableIterator<String> origins() {
        return new KeyPrefixIterator<String>(db.iterator(), META_PREFIX) {
            @Override
            public String next() {
                byte[] key = iterator_.next().getKey();
                return decodeWithoutPrefix(key, META_PREFIX.length);
            }
        };
    }

    /**
     * Iterate over all the localStorage data for the given origin.
     *
     * @param origin The origin whose data should be retrieved.
     * @return Iterator returning the key/value pairs as strings.
     */
    @NonNull
    public CloseableIterator<Map.Entry<String, String>> dataForOrigin(@NonNull String origin) {
        byte[] encodedOrigin = origin.getBytes(StandardCharsets.UTF_8);
        final int prefixLen = encodedOrigin.length + 2;
        byte[] prefix = new byte[prefixLen];
        System.arraycopy(encodedOrigin, 0, prefix, 1, encodedOrigin.length);
        prefix[0] = '_';
        prefix[prefix.length - 1] = 0;
        return new KeyPrefixIterator<Map.Entry<String, String>>(db.iterator(), prefix) {
            @Override
            public Map.Entry<String, String> next() {
                Map.Entry<byte[], byte[]> entry = iterator_.next();
                return new AbstractMap.SimpleImmutableEntry<>(
                        detectAndDecodeWithoutPrefix(entry.getKey(), prefixLen),
                        detectAndDecodeWithoutPrefix(entry.getValue(), 0));
            }
        };
    }

    /**
     * Closes the database.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    private static String detectAndDecodeWithoutPrefix(byte[] in, int prefixLen) {
        if (in.length == prefixLen) {
            return "";
        }
        byte format = in[prefixLen];
        int dataLength = in.length - prefixLen - 1;
        if (format == FORMAT_UTF16) {
            return new String(in, prefixLen + 1, dataLength, StandardCharsets.UTF_16LE);
        } else if (format == FORMAT_LATIN1) {
            return new String(in, prefixLen + 1, dataLength, StandardCharsets.ISO_8859_1);
        } else {
            throw new RuntimeException("Unknown string format " + format);
        }
    }

    private static String decodeWithoutPrefix(byte[] in, int prefixLen) {
        return new String(in, prefixLen, in.length - prefixLen, StandardCharsets.UTF_8);
    }

    private static abstract class KeyPrefixIterator<T> implements CloseableIterator<T> {
        protected DBIterator iterator_;
        private final byte[] keyPrefix_;

        KeyPrefixIterator(DBIterator iterator, byte[] keyPrefix) {
            iterator_ = iterator;
            keyPrefix_ = keyPrefix;
            iterator_.seek(keyPrefix);
        }

        @Override
        public void close() throws IOException {
            if (iterator_ != null) {
                iterator_.close();
                iterator_ = null;
            }
        }

        @Override
        public boolean hasNext() {
            if (iterator_ == null || !iterator_.hasNext()) {
                return false;
            }
            byte[] nextKey = iterator_.peekNext().getKey();
            if (nextKey.length < keyPrefix_.length) {
                return false;
            }
            for (int i = 0; i < keyPrefix_.length; ++i) {
                if (nextKey[i] != keyPrefix_[i]) {
                    return false;
                }
            }
            return true;
        }
    }
}