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

        try {
            int choice = Integer.parseInt(reader.readLine());

            switch (choice) {
                case 1:
                    dfa.readDeterministicFiniteAutomaton(FILE_NAME_DFA);
                    break;
                case 2:
                    dfa.readDeterministicFiniteAutomatonFromConsole(reader);
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
                    return; // Ieșire din program în cazul unei alegeri invalide
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
                        dfa.readAndVerifySequenceFromConsole(reader);
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


//    public static void main(String[] args) {
//
//        InputFileReader inputFileReader = new InputFileReader(FILE_NAME);
//        List<WordPosition> words = inputFileReader.readFromFile();
//        KeywordsManager keywordsManager = new KeywordsManager();
//        Map<String, Integer> keywords = keywordsManager.getKeywords();
//
//        List<WordPosition> elements = new ArrayList<>();
//
//        List<WordPosition> errors = new ArrayList<>();
//        Map<String, Integer> symbolMapping = new LinkedHashMap<>();
//        for (WordPosition word : words) {
//            if (keywords.containsKey(word.getWord())) {
//                elements.add(new WordPosition(word.getWord(), keywords.get(word.getWord())));
//            } else if (word.getWord().matches(REGEX_STRING)) {
//                elements.add(new WordPosition(word.getWord(), 0));
//                symbolsList.add(word.getWord());
//            } else if (Pattern.matches(REGEX_FLOAT, word.getWord()) || Pattern.matches(REGEX_NUMBER, word.getWord())) {
//                elements.add(new WordPosition(word.getWord(), 1));
//                symbolsList.add(word.getWord());
//            } else {
//                errors.add(word);
//            }
//        }
//        for (int i = 0; i < symbolsList.size(); i++) {
//            symbolMapping.put(symbolsList.get(i), i);
//            System.out.println(symbolsList.get(i) + " -> " + i);
//        }
//
//
//        OutputFileWriter outputFileWriter = new OutputFileWriter(OUTPUT_FILE_NAME);
//        outputFileWriter.exportToFile(elements, errors, symbolMapping);
//
//
//    }
}