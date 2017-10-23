package org.commonmark.ext.replacement.internal;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.PostProcessor;

import java.util.Map;

public class ReplacementPostProcessor implements PostProcessor {
    private final Map<String, String> mReplacementMap;

    public ReplacementPostProcessor(Map<String, String> replacementMap) {
        mReplacementMap = replacementMap;
    }

    @Override
    public Node process(Node node) {
        ReplacementVisitor replacementVisitor = new ReplacementVisitor();
        node.accept(replacementVisitor);
        return node;
    }

    private void replace(Text text) {
        String literal = text.getLiteral();

        boolean search = true;
        Node lastNode = text;
        int last = 0;
        for (int i = 0; i < literal.length(); i++) {
            if (search) {
                for (Map.Entry<String, String> entry : mReplacementMap.entrySet()) {
                    String key = entry.getKey();
                    if (literal.substring(i).startsWith(key) && isWhitespaceOrEndOfLine(literal, i + key.length())) {
                        String value = entry.getValue();
                        if (i != 0) {
                            lastNode = insertNode(new Text(literal.substring(last, i)), lastNode);
                        }
                        lastNode = insertNode(new Text(value), lastNode);
                        last = i = i + key.length();
                        break;
                    }
                }
            }
            search = isWhitespaceOrEndOfLine(literal, i);
        }

        if (last != literal.length()) {
            insertNode(new Text(literal.substring(last)), lastNode);
        }

        text.unlink();
    }

    private boolean isWhitespaceOrEndOfLine(String string, int position) {
        return position >= string.length() || Character.isWhitespace(string.charAt(position));
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
