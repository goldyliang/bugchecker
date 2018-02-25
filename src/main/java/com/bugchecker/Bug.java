package com.bugchecker;

import com.github.javaparser.ast.Node;

/**
 * Created by goldyliang on 2/18/18.
 */
public class Bug {
    private Node node;
    private AbstractRule rule;

    public Bug(Node node, AbstractRule rule) {
        this.node = node;
        this.rule = rule;
    }

    public Node getNode() {return node;}
    public AbstractRule getRule() {return rule;}

    @Override
    public String toString() {
        return "Node: " + node.toString() + "Rule: " + rule.toString();
    }
}
