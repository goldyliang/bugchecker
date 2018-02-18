package com.bugchecker;

import com.github.javaparser.ast.Node;

/**
 * Created by elnggng on 2/18/18.
 */
public class Bug {
    private Node node;
    private AbstractRule matcher;

    public Bug(Node node, AbstractRule matcher) {
        this.node = node;
        this.matcher = matcher;
    }

    @Override
    public String toString() {
        return "Node: " + node.toString() + "Rule: " + matcher.toString();
    }
}
