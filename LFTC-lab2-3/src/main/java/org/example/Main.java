package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    private static final String FILE_NAME = "src/main/resources/input.txt";
    private static final String OUTPUT_FILE_NAME = "src/main/resources/output.txt";
    public static class WordPosition {
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
    public static List<String> symbolsList = new ArrayList<>() {
        public boolean add(String elem) {
            if (!contains(elem)) {
                int index = Collections.binarySearch(this, elem);
                if (index < 0) {
                    index = ~index;
                }
                super.add(index, elem);
                return true;
            }
            return false;
        }
    };

    private static List<WordPosition> readFromFile() {
        List<WordPosition> words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(Main.FILE_NAME))) {
            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                String[] wordArray = line.split("[\\s]+");
                for (String word : wordArray) {
                    // Verificăm dacă cuvântul nu este gol
                    if (!word.isEmpty()) {
                        words.add(new WordPosition(word, lineNumber));
                    }
                }

                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Eroare la citirea din fișier: " + e.getMessage());
        }

        return words;
    }

    private static void exportToFile(List<WordPosition> elems, List<WordPosition> errors, Map<String, Integer> symbolMapping, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Forma Internă a Programului");
            writer.newLine();
            writer.newLine();
            for (int i = 0; i < elems.size(); i++) {
                String line = "";
                if(elems.get(i).getLine() == 0 || elems.get(i).getLine() == 1)
                {
                    line = elems.get(i).getWord() + " -> " + elems.get(i).getLine() + " -> " + symbolMapping.get(elems.get(i).getWord());
                }
                else{
                    line = elems.get(i).getWord() + " -> " + elems.get(i).getLine() + " -> NA" ;
                }
                writer.write(line);
                writer.newLine();
            }
            writer.newLine();
            writer.write("Tabela de Simboluri");
            writer.newLine();
            writer.newLine();

            for(String symbol : symbolMapping.keySet())
            {
                writer.write(symbol + " -> " + symbolMapping.get(symbol));
                writer.newLine();
            }
            writer.newLine();
            writer.write("Lista de erori");
            writer.newLine();
            writer.newLine();
            for (WordPosition error : errors) {
                String line = error.getWord() + " -> " + error.getLine();
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Eroare la scrierea în fișier: " + e.getMessage());
        }
    }
    private static Map<String, Integer> addConstantsToMap()
    {
        Map<String, Integer> keywords = new HashMap<>();
        keywords.put("ID", 0);
        keywords.put("CONST", 1);
        keywords.put("#include", 2);
        keywords.put("<iostream>", 3);
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
        return keywords;
    }

    public static void main(String[] args) {
        Map<String, Integer> keywords = addConstantsToMap();
        List<WordPosition> elements = new ArrayList<>();
        String regexString = "^[A-Za-z][A-Za-z0-9]{0,7}$";
        String regexNumber = "^([+-]?[1-9]\\d*|0)$";
        String regexFloat = "^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$";
        List<WordPosition> words = readFromFile();
        List<WordPosition> errors = new ArrayList<>();
        Map<String, Integer> symbolMapping = new LinkedHashMap<>();
        for(WordPosition word : words)
        {
           if(keywords.containsKey(word.getWord()))
           {
               elements.add(new WordPosition(word.getWord(), keywords.get(word.getWord())));
           }

           else if(word.getWord().matches(regexString))
           {
               elements.add(new WordPosition(word.getWord(), 0));
               symbolsList.add(word.getWord());
           }
           else if(Pattern.matches(regexFloat, word.getWord()) || Pattern.matches(regexNumber, word.getWord()))
           {
               elements.add(new WordPosition(word.getWord(), 1));
               symbolsList.add(word.getWord());
           }
           else{
                errors.add(word);
           }
        }
        for (int i=0; i<symbolsList.size(); i++) {
            symbolMapping.put(symbolsList.get(i), i);
            System.out.println(symbolsList.get(i) + " -> " + i);
        }


        exportToFile(elements, errors, symbolMapping, OUTPUT_FILE_NAME);

    }
}