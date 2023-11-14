package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OutputFileWriter {
    private String fileName;

    public OutputFileWriter(String fileName) {
        this.fileName = fileName;
    }

    public void exportToFile(List<WordPosition> elements, List<String> errors, Map<String, Integer> symbolMapping) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Forma Internă a Programului");
            writer.newLine();
            writer.newLine();
            for (int i = 0; i < elements.size(); i++) {
                String line = "";
                if (elements.get(i).getLine() == 0 || elements.get(i).getLine() == 1) {
                    line = elements.get(i).getWord() + " -> " + elements.get(i).getLine() + " -> " + symbolMapping.get(elements.get(i).getWord());
                } else {
                    line = elements.get(i).getWord() + " -> " + elements.get(i).getLine() + " -> NA";
                }
                writer.write(line);
                writer.newLine();
            }
            writer.newLine();
            writer.write("Tabela de Simboluri");
            writer.newLine();
            writer.newLine();

            for (String symbol : symbolMapping.keySet()) {
                writer.write(symbol + " -> " + symbolMapping.get(symbol));
                writer.newLine();
            }
            writer.newLine();
            writer.write("Lista de erori");
            writer.newLine();
            writer.newLine();
            for (String error : errors) {
                writer.write(error);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Eroare la scrierea în fișier: " + e.getMessage());
        }
    }
}