package org.commonmark.ext.replacement;

import org.commonmark.Extension;
import org.commonmark.ext.replacement.internal.ReplacementPostProcessor;
import org.commonmark.parser.Parser;

import java.util.Map;

/**
 * Extension for replacement chars using provided map.
 * <p>
 * Create it with {@link ReplacementExtension#create(Map)} and then configure it on the builder
 * ({@link org.commonmark.parser.Parser.Builder#extensions(Iterable)}).
 * </p>
 */
public class ReplacementExtension implements Parser.ParserExtension {
    private final Map<String, String> mReplacementMap;

    private ReplacementExtension(Map<String, String> replacementMap) {
        mReplacementMap = replacementMap;
    }

    public static Extension create(Map<String, String> replacementMap) {
        return new ReplacementExtension(replacementMap);
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessor(new ReplacementPostProcessor(mReplacementMap));
    }
}
