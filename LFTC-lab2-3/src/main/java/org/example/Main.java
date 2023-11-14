package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import static org.example.Utils.*;

public class Main {

    public static void main(String[] args) throws IOException {
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean exit = false;
        System.out.println("Choose how you want to read the DFA:");
        System.out.println("1. Read DFA from file");
        System.out.println("2. Read DFA from console");
        System.out.println("3. Verify code from file");
        try {
            int choice = Integer.parseInt(reader.readLine());

            switch (choice) {
                case 1:
                    dfa.readDeterministicFiniteAutomaton(FILE_NAME_DFA);
                    break;
                case 2:
                    dfa.readDeterministicFiniteAutomatonFromConsole(reader);
                    break;
                case 3:
                    lexerAnalasys();
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
                    return;
            }
        } catch (IOException e) {
            System.err.println("Error while reading from console: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Please select a valid option.");
        }

        while (!exit) {
            System.out.println("Choose an option:");
            System.out.println("1. Display states");
            System.out.println("2. Display alphabet");
            System.out.println("3. Display transitions");
            System.out.println("4. Display final states");
            System.out.println("5. Read and verify a sequence from console");
            System.out.println("6. Exit");

            try {
                int choice = Integer.parseInt(reader.readLine());

                switch (choice) {
                    case 1:
                        dfa.displayStates();
                        break;
                    case 2:
                        dfa.displayAlphabet();
                        break;
                    case 3:
                        dfa.displayTransitions();
                        break;
                    case 4:
                        dfa.displayFinalStates();
                        break;
                    case 5:
                        if (dfa.isDeterministic()) {
                            dfa.readAndVerifySequenceFromConsole(reader);
                        } else {
                            System.out.println("The DFA is not deterministic. Please choose another option.");
                        }
                        break;
                    case 6:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                        break;
                }
            } catch (IOException e) {
                System.err.println("Error while reading from console: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please select a valid option.");
            }
        }

    }


    public static void lexerAnalasys() {
        DeterministicFiniteAutomaton dfaIdentifier = new DeterministicFiniteAutomaton();
        DeterministicFiniteAutomaton dfaIntConstant = new DeterministicFiniteAutomaton();
        DeterministicFiniteAutomaton dfaFloatConstant = new DeterministicFiniteAutomaton();
        dfaIntConstant.readDeterministicFiniteAutomaton(FILE_NAME_DFA_INT_CONSTANT);
        dfaFloatConstant.readDeterministicFiniteAutomaton(FILE_NAME_DFA_FLOAT_CONSTANT);
        dfaIdentifier.readDeterministicFiniteAutomaton(FILE_NAME_DFA_IDENTIFIER);
        InputFileReader inputFileReader = new InputFileReader(FILE_NAME);
        List<WordPosition> lines = inputFileReader.readLinesFromFile();
        KeywordsManager keywordsManager = new KeywordsManager();
        Map<String, Integer> keywords = keywordsManager.getKeywords();

        List<WordPosition> elements = new ArrayList<>();

        Map<String, Integer> symbolMapping = new LinkedHashMap<>();
        List<String> errorMsgs = new ArrayList<>();
        for (WordPosition line : lines) {
            String err = verifySequence(line.getWord(), dfaIdentifier, dfaIntConstant, dfaFloatConstant, elements, line.getLine(), keywords);
            if (err != "") {
                errorMsgs.add(err);
                break;
            }

        }
        for (int i = 0; i < symbolsList.size(); i++) {
            symbolMapping.put(symbolsList.get(i), i);
        }


        OutputFileWriter outputFileWriter = new OutputFileWriter(OUTPUT_FILE_NAME);
        outputFileWriter.exportToFile(elements, errorMsgs, symbolMapping);


    }

    public static String longestPrefix(String[] arr) {
        String longest = "";

        for (String el : arr) {
            if (el.length() > longest.length()) {
                longest = el;
            }
        }
        return longest;
    }

    public static String verifySequence(String seq, DeterministicFiniteAutomaton dfaIdentifier, DeterministicFiniteAutomaton dfaIntConstant, DeterministicFiniteAutomaton dfaFloatConstant, List<WordPosition> elements, int lineNumber, Map<String, Integer> keywords) {


        while (!seq.isEmpty() && !seq.isBlank()) {
            while (seq.startsWith(" ") || seq.startsWith("\t")) {
                seq = seq.substring(1);
            }
            String longestInt = dfaIntConstant.findLongestAcceptedPrefix(seq);
            String longestFloat = dfaFloatConstant.findLongestAcceptedPrefix(seq);
            String longestIdentifier = dfaIdentifier.findLongestAcceptedPrefix(seq);
            String longestAcceptedPrefix = longestPrefix(new String[]{longestIdentifier, longestInt, longestFloat});
            if (longestAcceptedPrefix.length() == 0) {
                int i = 0;
                StringBuilder sb = new StringBuilder();
                boolean theEnd = false;
                while (!keywords.containsKey(sb.toString())) {
                    if (i < seq.length()) {
                        if (seq.charAt(i) != ' ') {
                            sb.append(seq.charAt(i));
                        }
                        i++;
                    } else {
                        theEnd = true;
                        break;
                    }

                }
                if (!theEnd) {
                    longestAcceptedPrefix = sb.toString();
                }

            }
            if (longestAcceptedPrefix.length() > 0 && longestAcceptedPrefix != " " && longestAcceptedPrefix != "\r") {
                int code = keywords.getOrDefault(longestAcceptedPrefix, -1);
                if (code != -1) {
                    elements.add(new WordPosition(longestAcceptedPrefix, code));

                } else if (longestAcceptedPrefix.length() < 8) {
                    boolean isInteger = dfaIntConstant.verifySequence(longestAcceptedPrefix);
                    boolean isFloat = dfaFloatConstant.verifySequence(longestAcceptedPrefix);
                    if (isInteger || isFloat) {
                        elements.add(new WordPosition(longestAcceptedPrefix, 1));
                    } else {
                        if (!dfaIdentifier.verifySequence(longestAcceptedPrefix) && longestAcceptedPrefix.length() == 1) {
                            return "Eroare la linia " + lineNumber;
                        } else {
                            elements.add(new WordPosition(longestAcceptedPrefix, 0));

                        }
                    }
                    symbolsList.add(longestAcceptedPrefix);
                } else {
                    return "Eroare la linia " + lineNumber + " din cauza lungimii cuvantului";
                }

            } else {
                return "Eroare la linia " + lineNumber + " din cauza faptului ca nu a fost gasit niciun cuvant acceptat";
            }
            seq = seq.substring(longestAcceptedPrefix.length());
        }
        return "";
    }
}