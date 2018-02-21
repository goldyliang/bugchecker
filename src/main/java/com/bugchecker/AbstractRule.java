package com.bugchecker;

import com.github.javaparser.ast.Node;

/**
 * Created by elnggng on 2/18/18.
 */
public abstract class AbstractRule <T extends Node> {

    private Class<? extends Node> nodeClass;

    private String description;
    private String name;

    public AbstractRule(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {return name;}
    public String getDescription() {return description;}

    public void setNodeClass (Class<? extends Node> nodeClass) {
        this.nodeClass = nodeClass;
    }

    public boolean isInterested (Node node) {
        return nodeClass.isInstance(node);
    }

    public String toString () {
        return name;
    }
    public abstract Bug match (T node);
}
