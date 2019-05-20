package ru.bmstu.schedule.html.commons;

public abstract class RootNode<C extends Node> extends BasicNode<Node,C> {
    @Override
    public BasicNode getParent() {
        return null;
    }
}
