package ru.bmstu.schedule.html.commons;

import java.util.List;

public interface Node<P extends Node, C extends Node> {
    List<C> getChildren();
    P getParent();
}
