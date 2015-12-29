package scheme.base;

import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by wangzhiyuan on 2015/10/19.
 */
public class IntentUtils {

    /**
     * urlDecode的时候会把'+'转换成' '
     *
     * @param uri
     * @return
     */
    public static HashMap<String, String> splitParams2(Uri uri) {
        if (uri == null) {
            return new HashMap<String, String>();
        }
        Set<String> keys = getQueryParameterNames(uri);
        HashMap<String, String> map = new HashMap<String, String>(keys.size());

        for (String key : keys) {
            map.put(key, getQueryParameter(uri, key));
        }
        return map;
    }

    /**
     * urlDecode时 <br />
     * 4.0版本以后 '+' ->' ' <br />
     * 2.3版本之前 '+' -> '+'<br />
     *
     * @param uri
     * @return
     */
    public static HashMap<String, String> splitParams1(Uri uri) {
        if (uri == null) {
            return new HashMap<String, String>();
        }
        Set<String> keys = getQueryParameterNames(uri);
        HashMap<String, String> map = new HashMap<String, String>(keys.size());

        for (String key : keys) {
            map.put(key, uri.getQueryParameter(key));
        }
        return map;
    }

    /**
     * Searches the query string for the first value with the given key.
     *
     * <p>
     * <strong>Warning:</strong> this decoded the '+' character as ' '.
     *
     * @param key which will be encoded
     * @throws UnsupportedOperationException if this isn't a hierarchical URI
     * @throws NullPointerException if key is null
     * @return the decoded value or null if no parameter is found
     */
    public static String getQueryParameter(Uri uri, String key) {
        if (uri.isOpaque()) {
            throw new UnsupportedOperationException("This isn't a hierarchical URI.");
        }
        if (key == null) {
            throw new NullPointerException("key");
        }

        final String query = uri.getEncodedQuery();
        if (query == null) {
            return null;
        }

        final String encodedKey = Uri.encode(key, null);
        final int length = query.length();
        int start = 0;
        do {
            int nextAmpersand = query.indexOf('&', start);
            int end = nextAmpersand != -1 ? nextAmpersand : length;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            if (separator - start == encodedKey.length()
                    && query.regionMatches(start, encodedKey, 0, encodedKey.length())) {
                if (separator == end) {
                    return "";
                } else {
                    String encodedValue = query.substring(separator + 1, end);
                    return UriCodec.decode(encodedValue, true, UriCodec.UTF_8, false);
                }
            }

            // Move start to end of name.
            if (nextAmpersand != -1) {
                start = nextAmpersand + 1;
            } else {
                break;
            }
        } while (true);
        return null;
    }

    public static Set<String> getQueryParameterNames(Uri uri) {
        if (uri.isOpaque()) {
            throw new UnsupportedOperationException("This isn't a hierarchical URI.");
        }

        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }

        Set<String> names = new LinkedHashSet<String>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = next == -1 ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(Uri.decode(name));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }

    /**
     * Encodes and decodes {@code application/x-www-form-urlencoded} content. Subclasses define exactly which characters
     * are legal.
     *
     * <p>
     * By default, UTF-8 is used to encode escaped characters. A single input character like "\u0080" may be encoded to
     * multiple octets like %C2%80.
     */
    abstract static class UriCodec {

        /**
         * Returns true if {@code c} does not need to be escaped.
         */
        protected abstract boolean isRetained(char c);

        /**
         * Throws if {@code s} is invalid according to this encoder.
         */
        public final String validate(String uri, int start, int end, String name) throws URISyntaxException {
            for (int i = start; i < end;) {
                char ch = uri.charAt(i);
                if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || isRetained(ch)) {
                    i++;
                } else if (ch == '%') {
                    if (i + 2 >= end) {
                        throw new URISyntaxException(uri, "Incomplete % sequence in " + name, i);
                    }
                    int d1 = hexToInt(uri.charAt(i + 1));
                    int d2 = hexToInt(uri.charAt(i + 2));
                    if (d1 == -1 || d2 == -1) {
                        throw new URISyntaxException(uri, "Invalid % sequence: " + uri.substring(i, i + 3) + " in "
                                + name, i);
                    }
                    i += 3;
                } else {
                    throw new URISyntaxException(uri, "Illegal character in " + name, i);
                }
            }
            return uri.substring(start, end);
        }

        /**
         * Throws if {@code s} contains characters that are not letters, digits or in {@code legal}.
         */
        public static void validateSimple(String s, String legal) throws URISyntaxException {
            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                if (!(ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || legal.indexOf(ch) > -1)) {
                    throw new URISyntaxException(s, "Illegal character", i);
                }
            }
        }

        /**
         * Encodes {@code s} and appends the result to {@code builder}.
         *
         * @param isPartiallyEncoded true to fix input that has already been partially or fully encoded. For example,
         *            input of "hello%20world" is unchanged with isPartiallyEncoded=true but would be double-escaped to
         *            "hello%2520world" otherwise.
         * @throws UnsupportedEncodingException
         */
        private void appendEncoded(StringBuilder builder, String s, Charset charset, boolean isPartiallyEncoded)
                throws UnsupportedEncodingException {
            if (s == null) {
                throw new NullPointerException();
            }

            int escapeStart = -1;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || isRetained(c) || c == '%'
                        && isPartiallyEncoded) {
                    if (escapeStart != -1) {
                        appendHex(builder, s.substring(escapeStart, i), charset);
                        escapeStart = -1;
                    }
                    if (c == '%' && isPartiallyEncoded) {
                        // this is an encoded 3-character sequence like "%20"
                        builder.append(s, i, i + 3);
                        i += 2;
                    } else if (c == ' ') {
                        builder.append('+');
                    } else {
                        builder.append(c);
                    }
                } else if (escapeStart == -1) {
                    escapeStart = i;
                }
            }
            if (escapeStart != -1) {
                appendHex(builder, s.substring(escapeStart, s.length()), charset);
            }
        }

        public final String encode(String s, Charset charset) throws UnsupportedEncodingException {
            // Guess a bit larger for encoded form
            StringBuilder builder = new StringBuilder(s.length() + 16);
            appendEncoded(builder, s, charset, false);
            return builder.toString();
        }

        static final Charset UTF_8 = Charset.forName("UTF-8");

        public final void appendEncoded(StringBuilder builder, String s) throws UnsupportedEncodingException {
            appendEncoded(builder, s, UTF_8, false);
        }

        public final void appendPartiallyEncoded(StringBuilder builder, String s) throws UnsupportedEncodingException {
            appendEncoded(builder, s, UTF_8, true);
        }

        /**
         * @param convertPlus true to convert '+' to ' '.
         * @param throwOnFailure true to throw an IllegalArgumentException on invalid escape sequences; false to replace
         *            them with the replacement character (U+fffd).
         * @throws UnsupportedEncodingException
         */
        public static String decode(String s, boolean convertPlus, Charset charset, boolean throwOnFailure) {
            if (s.indexOf('%') == -1 && (!convertPlus || s.indexOf('+') == -1)) {
                return s;
            }

            StringBuilder result = new StringBuilder(s.length());
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                for (int i = 0; i < s.length();) {
                    char c = s.charAt(i);
                    if (c == '%') {
                        do {
                            int d1, d2;
                            if (i + 2 < s.length() && (d1 = hexToInt(s.charAt(i + 1))) != -1
                                    && (d2 = hexToInt(s.charAt(i + 2))) != -1) {
                                out.write((byte) ((d1 << 4) + d2));
                            } else if (throwOnFailure) {
                                throw new IllegalArgumentException("Invalid % sequence at " + i + ": " + s);
                            } else {
                                byte[] replacement = "\ufffd".getBytes(charset.name());
                                out.write(replacement, 0, replacement.length);
                            }
                            i += 3;
                        } while (i < s.length() && s.charAt(i) == '%');
                        result.append(new String(out.toByteArray(), charset.name()));
                        out.reset();
                    } else {
                        if (convertPlus && c == '+') {
                            c = ' ';
                        }
                        result.append(c);
                        i++;
                    }
                }
            } catch (UnsupportedEncodingException e) {
            }
            return result.toString();
        }

        /**
         * Like {@link Character#digit}, but without support for non-ASCII characters.
         */
        private static int hexToInt(char c) {
            if ('0' <= c && c <= '9') {
                return c - '0';
            } else if ('a' <= c && c <= 'f') {
                return 10 + c - 'a';
            } else if ('A' <= c && c <= 'F') {
                return 10 + c - 'A';
            } else {
                return -1;
            }
        }

        public static String decode(String s) throws UnsupportedEncodingException {
            return decode(s, false, UTF_8, true);
        }

        private static void appendHex(StringBuilder builder, String s, Charset charset)
                throws UnsupportedEncodingException {
            for (byte b : s.getBytes(charset.name())) {
                appendHex(builder, b);
            }
        }

        private static void appendHex(StringBuilder sb, byte b) {
            sb.append('%');
            sb.append(byteToHexString(b, true));
        }

        /**
         * The digits for every supported radix.
         */
        private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                'y', 'z' };

        private static final char[] UPPER_CASE_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
                'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                'W', 'X', 'Y', 'Z' };

        public static String byteToHexString(byte b, boolean upperCase) {
            char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
            char[] buf = new char[2]; // We always want two digits.
            buf[0] = digits[b >> 4 & 0xf];
            buf[1] = digits[b & 0xf];
            return new String(buf, 0, 2);
        }

    }

}
