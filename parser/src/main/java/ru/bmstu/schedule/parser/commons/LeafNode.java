package ru.bmstu.schedule.parser.commons;

import java.util.List;

public abstract class LeafNode<P extends Node> implements Node<P,Node> {
    private P parent;

    @Override
    public P getParent() {
        return parent;
    }

    public void setParent(P parent) {
        this.parent = parent;
    }


    @Override
    public List<Node> getChildren() {
        return null;
    }
}
