package ru.bmstu.schedule.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class RunParser {
    public static void main(String[] args) {
        try {
            Document doc = Jsoup.connect("https://student.sbmstu.ru/schedule/list").get();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
