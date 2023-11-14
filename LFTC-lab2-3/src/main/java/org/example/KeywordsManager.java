package org.example;

import java.util.HashMap;
import java.util.Map;

public class KeywordsManager {
    private Map<String, Integer> keywords;

    public KeywordsManager() {
        keywords = new HashMap<>();
        initializeKeywords();
    }

    private void initializeKeywords() {
        keywords.put("ID", 0);
        keywords.put("CONST", 1);
        keywords.put("#include", 2);
        keywords.put("iostream", 3);
        keywords.put("using", 4);
        keywords.put("namespace", 5);
        keywords.put("std", 6);
        keywords.put(";", 7);
        keywords.put("(", 8);
        keywords.put(")", 9);
        keywords.put("{", 10);
        keywords.put("}", 11);
        keywords.put(",", 12);
        keywords.put("int", 13);
        keywords.put("float", 14);
        keywords.put("cerc", 15);
        keywords.put("=", 16);
        keywords.put("==", 17);
        keywords.put("+", 18);
        keywords.put("-", 19);
        keywords.put("*", 20);
        keywords.put("/", 21);
        keywords.put("%", 22);
        keywords.put("cin", 23);
        keywords.put("cout", 24);
        keywords.put("<<", 25);
        keywords.put(">>", 26);
        keywords.put("if", 27);
        keywords.put("else", 28);
        keywords.put("for", 29);
        keywords.put("while", 30);
        keywords.put("return", 31);
        keywords.put("Cerc", 32);
        keywords.put("<", 33);
        keywords.put(">", 34);
        keywords.put("<=", 35);
        keywords.put(">=", 36);
        keywords.put("!=", 37);
        keywords.put("||", 38);
        keywords.put("&&", 39);
        keywords.put("!", 40);
        keywords.put("endl", 41);
        keywords.put("void", 42);
    }

    public Map<String, Integer> getKeywords() {
        return keywords;
    }
}
