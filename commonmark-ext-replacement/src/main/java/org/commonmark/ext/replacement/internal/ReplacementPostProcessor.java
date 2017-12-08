package org.commonmark.ext.replacement.internal;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.PostProcessor;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplacementPostProcessor implements PostProcessor {
    private final Map<String, String> mReplacementMap;
    private final Matcher mKeysMatcher;

    public ReplacementPostProcessor(Map<String, String> replacementMap, Pattern keysPattern) {
        mReplacementMap = replacementMap;
        mKeysMatcher = keysPattern.matcher("");
    }

    @Override
    public Node process(Node node) {
        ReplacementVisitor replacementVisitor = new ReplacementVisitor();
        node.accept(replacementVisitor);
        return node;
    }

    private void replace(Text text) {
        String literal = text.getLiteral();

        Matcher matcher = mKeysMatcher.reset(literal);

        Node lastNode = text;
        int last = 0;
        while (matcher.find()) {
            String shortcut = matcher.group();
            if (matcher.start() != last) {
                lastNode = insertNode(new Text(literal.substring(last, matcher.start())), lastNode);
            }
            Text replacementNode = new Text(mReplacementMap.get(shortcut));
            lastNode = insertNode(replacementNode, lastNode);
            last = matcher.end();
        }

        if (last != literal.length()) {
            insertNode(new Text(literal.substring(last)), lastNode);
        }

        text.unlink();
    }

    private Node insertNode(Node node, Node insertAfterNode) {
        insertAfterNode.insertAfter(node);
        return node;
    }

    private class ReplacementVisitor extends AbstractVisitor {
        @Override
        public void visit(Text text) {
            replace(text);
        }
    }
}