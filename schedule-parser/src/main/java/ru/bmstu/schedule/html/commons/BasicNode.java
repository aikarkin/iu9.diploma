package ru.bmstu.schedule.html.commons;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class BasicNode<P extends Node, C extends Node> implements Node<P, C> {
    private List<C> children;
    private P parent;

    public List<C> getChildren() {
        if(children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public P getParent() {
        return parent;
    }

    public void setParent(P parent) {
        this.parent = parent;
    }

    public boolean isLeaf() {
        return getChildren() == null;
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    @SuppressWarnings("unchecked")
    protected void parseChildren(Class<? extends ElementDeserializer> parserClass, Elements elements) {
        children = elements
                .stream()
                .map(elem -> {
                    C child = null;
                    try {
                        ElementDeserializer<C> parser = parserClass.getConstructor(Element.class).newInstance(elem);
                        child = parser.deserialize();
                        if(child instanceof BasicNode) {
                            ((BasicNode) child).setParent(this);
                        } else if(child instanceof LeafNode) {
                            ((LeafNode) child).setParent(this);
                        }
                    } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    return child;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public abstract void parseChildren(Elements elements);
}
