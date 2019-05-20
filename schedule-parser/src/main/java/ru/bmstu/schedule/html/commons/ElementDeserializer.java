package ru.bmstu.schedule.html.commons;


import org.jsoup.nodes.Element;

public abstract class ElementDeserializer<T extends Node> implements Deserializable<T> {
    private ElementHolder holder;

    public abstract T deserialize();

    public ElementDeserializer(Element element) {
        this.holder = new ElementHolder(element);
    }

    protected ElementHolder elementHolder() {
        return holder;
    }
}
