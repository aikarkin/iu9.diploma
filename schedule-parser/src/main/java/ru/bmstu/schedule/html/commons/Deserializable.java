package ru.bmstu.schedule.html.commons;

public interface Deserializable<T extends Node> {
    T deserialize();
}
