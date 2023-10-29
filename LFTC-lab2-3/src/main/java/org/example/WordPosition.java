package org.example;

public class WordPosition {

    private String word;
    private int line;

    public WordPosition(String word, int line) {
        this.word = word;
        this.line = line;
    }

    public String getWord() {
        return word;
    }

    public int getLine() {
        return line;
    }
}
