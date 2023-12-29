package org.example;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        try {
            Grammar grammar = Grammar.readGrammarFromFile("src/main/resources/gramatica.txt");

            // Build LR1 Automaton
            Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton = grammar.buildLR1Automaton();

            // Print LR1 Automaton
            System.out.println("LR(1) Automaton:");
            grammar.printLR1Automaton(lr1Automaton);

            // Print Action and Goto Tables
            System.out.println("Action and Goto Tables:");
            grammar.printActionGotoTables(lr1Automaton);

            //public void parse(List<Symbol> input, Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {

//            String inputString = "ccd$";
//
//
//            grammar.parse(inputString, lr1Automaton);

            //aaab
            List<Symbol> inputSymbols = Arrays.asList(new Symbol("a"), new Symbol("b"));
            grammar.parseInput(inputSymbols);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
