package ru.bmstu.schedule.smtgen;

import java.util.Objects;

public class PairLessonItem implements LessonItem {
    private Lesson numerator;
    private Lesson denominator;
    private int index;

    public PairLessonItem() {
        index = 0;
    }

    public PairLessonItem(int index, Lesson numerator, Lesson denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.index = index;
    }

    public Lesson getNumerator() {
        return numerator;
    }

    public void setNumerator(Lesson numerator) {
        this.numerator = numerator;
    }

    public Lesson getDenominator() {
        return denominator;
    }

    public void setDenominator(Lesson denominator) {
        this.denominator = denominator;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PairLessonItem that = (PairLessonItem) o;
        return index == that.index &&
                Objects.equals(numerator, that.numerator) &&
                Objects.equals(denominator, that.denominator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator, index);
    }

    @Override
    public String toString() {
        return String.format("%d: [ %s ] | [ %s ]",
                getIndex(),
                numerator == null ? "---" : numerator,
                denominator == null ? "---" : denominator
        );
    }

}
