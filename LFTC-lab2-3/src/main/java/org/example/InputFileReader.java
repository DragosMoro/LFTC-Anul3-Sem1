package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputFileReader {
    private String fileName;

    public InputFileReader(String fileName) {
        this.fileName = fileName;
    }

    public List<WordPosition> readFromFile() {
        List<WordPosition> words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {
            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                String[] wordArray = line.split("[\\s]+");
                for (String word : wordArray) {
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
}
