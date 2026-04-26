package app.kareem.extension.threads.patches;

import java.net.URI;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class ThreadsShareLinksPatch {
    private static final String CUSTOM_HOST = "shoelace.kareem.one";

    private static final Pattern THREADS_URL_PATTERN = Pattern.compile(
        "(?i)\\b(?:https?://)?(?:l\\.|www\\.)?threads\\.(?:com|net)(?:/[^\\s]*)?"
    );

    private ThreadsShareLinksPatch() {
    }

    public static String rewriteShareText(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        Matcher matcher = THREADS_URL_PATTERN.matcher(text);
        StringBuffer rewrittenText = new StringBuffer(text.length());
        while (matcher.find()) {
            String matchedUrl = matcher.group();
            String trailingPunctuation = trailingPunctuation(matchedUrl);
            String urlWithoutTrailingPunctuation = matchedUrl.substring(
                0,
                matchedUrl.length() - trailingPunctuation.length()
            );

            String rewrittenUrl = rewriteShareUrl(urlWithoutTrailingPunctuation);
            matcher.appendReplacement(
                rewrittenText,
                Matcher.quoteReplacement(rewrittenUrl + trailingPunctuation)
            );
        }
        matcher.appendTail(rewrittenText);

        return rewrittenText.toString();
    }

    public static String rewriteShareUrl(String url) {
        if (url == null || url.isBlank()) {
            return url;
        }

        try {
            String parseableUrl = hasScheme(url) ? url : "https://" + url;
            URI uri = new URI(parseableUrl);
            String host = uri.getHost();
            if (host == null || !isThreadsHost(host)) {
                return url;
            }

            return new URI(
                "https",
                uri.getUserInfo(),
                CUSTOM_HOST,
                uri.getPort(),
                uri.getPath(),
                null,
                uri.getFragment()
            ).toString();
        } catch (Exception ignored) {
            return url;
        }
    }

    private static boolean hasScheme(String url) {
        return url.regionMatches(true, 0, "http://", 0, 7) ||
            url.regionMatches(true, 0, "https://", 0, 8);
    }

    private static boolean isThreadsHost(String host) {
        String normalizedHost = host.toLowerCase(Locale.ROOT);
        return normalizedHost.equals("threads.com") ||
            normalizedHost.equals("www.threads.com") ||
            normalizedHost.equals("l.threads.com") ||
            normalizedHost.equals("threads.net") ||
            normalizedHost.equals("www.threads.net");
    }

    private static String trailingPunctuation(String url) {
        int end = url.length();
        while (end > 0) {
            char character = url.charAt(end - 1);
            if (character == '.' || character == ',' || character == '!' || character == '?' ||
                character == ';' || character == ':' || character == ')' || character == ']' ||
                character == '}') {
                end--;
            } else {
                break;
            }
        }
        return url.substring(end);
    }
}
