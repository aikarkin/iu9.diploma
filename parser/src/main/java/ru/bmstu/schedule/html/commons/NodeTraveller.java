package ru.bmstu.schedule.html.commons;

import java.util.*;
import java.util.function.Predicate;

public class NodeTraveller {
    Node root;

    public NodeTraveller(Node node) {
        this.root = node;
    }

    public <T extends Node> Iterable<T> filter(Class<? extends T> clazz, Predicate<T> predicate) {
        Iterator<Node> allNodesVisitor = new Iterator<Node>() {
            private Queue<Node> queue = new LinkedList<>(Collections.singletonList(root));

            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            @Override
            public Node next() {
                if(!hasNext())
                    throw new NoSuchElementException();
                Node top = queue.poll();
                if(!isLeaf(top)) {
                    queue.addAll(top.getChildren());
                }
                return top;
            }
        };

        return () -> new Iterator<T>() {
            private Node curNode = root;

            @Override
            public boolean hasNext() {
                if(allNodesVisitor.hasNext() && curNode != root)
                    curNode = allNodesVisitor.next();
                while (allNodesVisitor.hasNext() && (curNode.getClass() != clazz || !predicate.test((T) curNode))) {
                    try {
                        curNode = allNodesVisitor.next();
                    } catch (NoSuchElementException ignored) {
                        if(allNodesVisitor.hasNext())
                            curNode = allNodesVisitor.next();
                        else {
                            curNode = null;
                            break;
                        }
                    }
                }

                return allNodesVisitor.hasNext() && curNode != null;
            }

            @Override
            public T next() {
                return (T) curNode;
            }
        };
    }

    public <T extends Node> Optional<T> findFirst(Class<? extends T> clazz, Predicate<T> predicate) {
        Iterator<T> iter = filter(clazz, predicate).iterator();
        return iter.hasNext() ? Optional.ofNullable(iter.next()) : Optional.empty();
    }

    public <T extends Node> Iterable<T> entitiesListOf(Class<? extends T> clazz) {
        return this.filter(clazz, (e) -> true);
    }

    private static boolean isLeaf(Node node) {
        return node instanceof LeafNode;
    }

    private static boolean isRoot(Node node) {
        return node instanceof RootNode;
    }
}
