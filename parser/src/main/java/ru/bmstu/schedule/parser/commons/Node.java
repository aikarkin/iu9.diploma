package ru.bmstu.schedule.parser.commons;

import java.util.List;

public interface Node<P extends Node, C extends Node> {
    List<C> getChildren();
    P getParent();
}
