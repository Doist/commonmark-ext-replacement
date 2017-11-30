package org.commonmark.ext.replacement;

import org.commonmark.Extension;
import org.commonmark.ext.replacement.internal.ReplacementPostProcessor;
import org.commonmark.parser.Parser;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Extension for replacement chars using provided map.
 * <p>
 * Create it with {@link ReplacementExtension#create(Map)} and then configure it on the builder
 * ({@link org.commonmark.parser.Parser.Builder#extensions(Iterable)}).
 * </p>
 */
public class ReplacementExtension implements Parser.ParserExtension {
    private final Map<String, String> mReplacementMap;
    private final Pattern mKeysPattern;

    private ReplacementExtension(Map<String, String> replacementMap, Pattern keysPattern) {
        mReplacementMap = replacementMap;
        mKeysPattern = keysPattern;
    }

    public static Extension create(Map<String, String> replacementMap) {
        return new ReplacementExtension(replacementMap, getKeysPattern(replacementMap.keySet()));
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessor(new ReplacementPostProcessor(mReplacementMap, mKeysPattern));
    }

    private static Pattern getKeysPattern(Set<String> keys) {
        StringBuilder sb = new StringBuilder();

        for (String key : keys) {
            if (sb.length() == 0) {
                sb.append("(?<=(^|\\s))(");
            } else {
                sb.append('|');
            }
            sb.append(Pattern.quote(key));
        }

        sb.append(")(?=($|\\s))");

        return Pattern.compile(sb.toString());
    }
}
