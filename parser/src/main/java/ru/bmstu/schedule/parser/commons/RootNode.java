package ru.bmstu.schedule.parser.commons;

public abstract class RootNode<C extends Node> extends BasicNode<Node,C> {
    @Override
    public BasicNode getParent() {
        return null;
    }
}
