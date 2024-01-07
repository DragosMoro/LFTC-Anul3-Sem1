package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try {
            String choice = "-1";
            while (choice != "0") {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Please enter your choice:");
                System.out.println("1. Read grammar from 'gramatica.txt' and input from user");
                System.out.println("2. Read grammar from 'gramatica_limbaj.txt' and input from 'fip.txt'");
                System.out.println("0. Exit");
                choice = scanner.next();
                switch (choice) {
                    case "1":
                        Grammar grammar = GrammarReader.readGrammarFromFile("src/main/resources/gramatica.txt");
                        FirstSetCalculator firstSetCalculator = new FirstSetCalculator(grammar);
                        LR1AutomatonBuilder lr1AutomatonBuilder = new LR1AutomatonBuilder(grammar, firstSetCalculator);
                        TableGenerator tableGenerator = new TableGenerator(grammar, firstSetCalculator, lr1AutomatonBuilder);

                        Printer printer = new Printer(grammar, tableGenerator, lr1AutomatonBuilder, firstSetCalculator);
                        Parser parser = new Parser(grammar, tableGenerator, lr1AutomatonBuilder);

                        firstSetCalculator.calculateFirstSets();

                        Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton = lr1AutomatonBuilder.buildLR1Automaton();

                        System.out.println("Please enter your choice for case 1:");
                        System.out.println("1. Print LR1 Automaton");
                        System.out.println("2. Print Action and Goto Tables");
                        System.out.println("3. Parse Input");
                        String choice1 = scanner.next();

                        switch (choice1) {
                            case "1":
                                printer.printLR1Automaton(lr1Automaton);
                                break;
                            case "2":
                                printer.printActionGotoTables(lr1Automaton);
                                break;
                            case "3":
                                System.out.println("Please enter the input string: ");
                                String input = scanner.next();
                                List<Symbol> inputSymbols = new ArrayList<>();
                                for (int i = 0; i < input.length(); i++) {
                                    inputSymbols.add(new Symbol(String.valueOf(input.charAt(i))));
                                }
                                parser.parseInput(inputSymbols);
                                break;
                            default:
                                System.out.println("Invalid choice. Please enter 1, 2 or 3.");
                                break;
                        }
                        break;
                    case "2":
                        Grammar grammar1 = GrammarReader.readGrammarFromFile("src/main/resources/gramatica_limbaj.txt");
                        FirstSetCalculator firstSetCalculator1 = new FirstSetCalculator(grammar1);
                        LR1AutomatonBuilder lr1AutomatonBuilder1 = new LR1AutomatonBuilder(grammar1, firstSetCalculator1);
                        TableGenerator tableGenerator1 = new TableGenerator(grammar1, firstSetCalculator1, lr1AutomatonBuilder1);

                        Printer printer1 = new Printer(grammar1, tableGenerator1, lr1AutomatonBuilder1, firstSetCalculator1);
                        Parser parser1 = new Parser(grammar1, tableGenerator1, lr1AutomatonBuilder1);

                        firstSetCalculator1.calculateFirstSets();

                        Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton1 = lr1AutomatonBuilder1.buildLR1Automaton();

                        System.out.println("Please enter your choice for case 2:");
                        System.out.println("1. Print LR1 Automaton");
                        System.out.println("2. Print Action and Goto Tables");
                        System.out.println("3. Parse Input from 'fip.txt'");
                        String choice2 = scanner.next();

                        switch (choice2) {
                            case "1":
                                printer1.printLR1Automaton(lr1Automaton1);
                                break;
                            case "2":
                                printer1.printActionGotoTables(lr1Automaton1);
                                break;
                            case "3":
                                FileReader fileReader = new FileReader("src/main/resources/fip.txt");
                                BufferedReader bufferedReader = new BufferedReader(fileReader);
                                String line = bufferedReader.readLine();
                                String[] elems = line.split(" ");
                                List<Symbol> fip = new ArrayList<>();
                                for (String elem : elems) {
                                    fip.add(new Symbol(elem));
                                }
                                bufferedReader.close();
                                fileReader.close();
                                parser1.parseInput(fip);
                                break;
                            default:
                                System.out.println("Invalid choice. Please enter 1, 2 or 3.");
                                break;
                        }

                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1 or 2.");
                        break;

                    case "0":
                        System.out.println("Exiting...");
                        return;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}