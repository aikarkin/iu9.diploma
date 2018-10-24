package ru.bmstu.schedule.parser.commons;

public interface Deserializable<T extends Node> {
    T deserialize();
}
