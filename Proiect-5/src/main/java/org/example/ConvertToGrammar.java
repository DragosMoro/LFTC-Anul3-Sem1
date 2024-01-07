package org.example;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConvertToGrammar {
    private static Map<String, Integer> terminals = new HashMap<>();
    private static Map<String, String> nonterminals = new HashMap<>();

    public ConvertToGrammar() {
    }

    private void populateTerminals() {
        terminals.put("INCLUDE", 2);
        terminals.put("IOSTREAM", 3);
        terminals.put("USING", 4);
        terminals.put("NAMESPACE", 5);
        terminals.put("STD", 6);
        terminals.put("SEMICOLON", 7);
        terminals.put("OPEN_PAREN", 8);
        terminals.put("CLOSE_PAREN", 9);
        terminals.put("OPEN_BRACE", 10);
        terminals.put("CLOSE_BRACE", 11);
        terminals.put("COMMA", 12);
        terminals.put("INT_TYPE", 13);
        terminals.put("FLOAT_TYPE", 14);
        terminals.put("CERC", 15);
        terminals.put("ASSIGN", 16);
        terminals.put("EQUAL", 17);
        terminals.put("PLUS", 18);
        terminals.put("MINUS", 19);
        terminals.put("MULTIPLY", 20);
        terminals.put("DIVIDE", 21);
        terminals.put("MODULO", 22);
        terminals.put("CIN", 23);
        terminals.put("COUT", 24);
        terminals.put("OUTPUT_OP", 25);
        terminals.put("INPUT_OP", 26);
        terminals.put("IF", 27);
        terminals.put("ELSE", 28);
        terminals.put("FOR", 29);
        terminals.put("WHILE", 30);
        terminals.put("RETURN", 31);
        terminals.put("LESS_THAN", 33);
        terminals.put("GREATER_THAN", 34);
        terminals.put("LESS_THAN_EQUAL", 35);
        terminals.put("GREATER_THAN_EQUAL", 36);
        terminals.put("NOT_EQUAL", 37);
        terminals.put("LOGICAL_OR", 38);
        terminals.put("LOGICAL_AND", 39);
        terminals.put("LOGICAL_NOT", 40);
        terminals.put("ENDL", 41);
        terminals.put("VOID", 42);
        terminals.put("IDENTIFIER", 0);
        terminals.put("INT_CONSTANT", 1);
        terminals.put("FLOAT_CONSTANT", 1);
        terminals.put("CUSTOM_TYPE", 32);
    }

    private void populateNonterminals() {
        nonterminals.put("program", "A");
        nonterminals.put("declarare_functie", "B");
        nonterminals.put("antet", "C");
        nonterminals.put("functie", "D");
        nonterminals.put("apelare_functie", "E");
        nonterminals.put("corp_functie_apelare", "F");
        nonterminals.put("parametri_opt_apelare", "G");
        nonterminals.put("lista_parametri_apelare", "H");
        nonterminals.put("tip_parametri_apelare", "I");
        nonterminals.put("tip_return", "J");
        nonterminals.put("corp_functie", "K");
        nonterminals.put("parametri_opt", "L");
        nonterminals.put("lista_parametri", "M");
        nonterminals.put("parametru", "N");
        nonterminals.put("tip_de_date", "O");
        nonterminals.put("cerc", "P");
        nonterminals.put("atribuire_int", "Q");
        nonterminals.put("atribuire_float", "R");
        nonterminals.put("atribuire_cerc", "S");
        nonterminals.put("atribuire_identificator", "T");
        nonterminals.put("atribuire_apelare", "U");
        nonterminals.put("atribuire_variabile", "V");
        nonterminals.put("operatii_numere_int", "W");
        nonterminals.put("operatii_numere_float", "X");
        nonterminals.put("operatii_variabile", "Y");
        nonterminals.put("operatii_int", "Z");
        nonterminals.put("operatii_float", "AA");
        nonterminals.put("intrare", "AB");
        nonterminals.put("elemente_intrare", "AC");
        nonterminals.put("iesire", "AD");
        nonterminals.put("elemente_iesire", "AE");
        nonterminals.put("tip_el_iesire", "AF");
        nonterminals.put("selectie_conditionala", "AG");
        nonterminals.put("selectie_conditionala_prima_parte", "AH");
        nonterminals.put("conditie", "AI");
        nonterminals.put("operator_conditional", "AJ");
        nonterminals.put("instructiune_de_ciclare", "AK");
        nonterminals.put("instructiuni", "AL");
        nonterminals.put("tip_de_instructiune", "AM");
        nonterminals.put("return_el", "AN");
        nonterminals.put("return", "AO");
    }

    public void convertToGrammar() {
        try {
            populateTerminals();
            populateNonterminals();
            String inputFilePath = "E:\\LFTC-Anul3-Sem1\\Proiect-5\\src\\main\\resources\\bnf.txt";
            String outputFilePath = "E:\\LFTC-Anul3-Sem1\\Proiect-5\\src\\main\\resources\\gramatica_limbaj.txt";

            readAndWriteToFile(inputFilePath, outputFilePath);
            System.out.println("Gramatica a fost exportată în " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void readAndWriteToFile(String filePath, String outputFilePath) throws IOException {
        Set<String> uniqueProductions = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] wordArray = line.split(":");
                String leftHandSide = wordArray[0].trim();
                leftHandSide = nonterminals.get(leftHandSide);
                String[] rightHandSide = wordArray[1].trim().split("\\|");
                if (rightHandSide.length > 0 && rightHandSide[0].trim().isEmpty()) {
                    continue;
                }

                for (String right : rightHandSide) {
                    int index = 0;
                    String[] words = right.split(" ");
                    StringBuilder productionStringBuilder = new StringBuilder();
                    productionStringBuilder.append(leftHandSide).append(" -> ");

                    for (String word : words) {
                        if (word.trim().isEmpty()) {
                            continue;
                        }
                        if (word.equals("*")) {
                            productionStringBuilder.append("EPSILON ");
                            continue;
                        }

                        String replacement = null;
                        if (terminals.containsKey(word)) {
                            replacement = String.valueOf(terminals.get(word));
                        } else if (nonterminals.containsKey(word)) {
                            replacement = nonterminals.get(word);
                        } else {
                            System.out.println("Eroare la linia " + lineNumber + ": " + word + " nu este un terminal sau neterminal!");
                            return;
                        }

                        productionStringBuilder.append(replacement).append(" ");
                    }

                    String production = productionStringBuilder.toString().trim();

                    // Verificați dacă producția există deja în set
                    if (uniqueProductions.add(production)) {
                        // Dacă nu există, adăugați-o și scrieți în fișier
                        writeProductionToFile(outputFilePath, production);
                    }
                }
                lineNumber++;
            }
        }
    }


    private void writeProductionToFile(String filePath, String production) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(production + "\n");
        }
    }
}

