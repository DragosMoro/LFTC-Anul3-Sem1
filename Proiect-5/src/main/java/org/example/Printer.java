package org.example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for printing the LR(1) automaton and the action and goto tables.
 */
public class Printer {
    private Grammar grammar; // The grammar used for parsing
    private TableGenerator tableGenerator; // The table generator used to generate the action and goto tables
    private LR1AutomatonBuilder lr1AutomatonBuilder; // The LR(1) automaton builder used to build the LR(1) automaton
    private FirstSetCalculator firstSetCalculator; // The first set calculator used to calculate the first sets of the grammar

    /**
     * Constructor for the Printer class.
     *
     * @param grammar             The grammar used for parsing
     * @param tableGenerator      The table generator used to generate the action and goto tables
     * @param lr1AutomatonBuilder The LR(1) automaton builder used to build the LR(1) automaton
     * @param firstSetCalculator  The first set calculator used to calculate the first sets of the grammar
     */
    public Printer(Grammar grammar, TableGenerator tableGenerator, LR1AutomatonBuilder lr1AutomatonBuilder, FirstSetCalculator firstSetCalculator) {
        this.grammar = grammar;
        this.tableGenerator = tableGenerator;
        this.lr1AutomatonBuilder = lr1AutomatonBuilder;
        this.firstSetCalculator = firstSetCalculator;
    }

    /**
     * This method prints the LR(1) automaton.
     *
     * @param lr1Automaton The LR(1) automaton to be printed
     */
    public void printLR1Automaton(Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
        // Initialize the maximum lengths of each column
        int maxGotoLength = 0;
        int maxKernelLength = 0;
        int maxStateLength = 0;
        int maxClosureLength = 0;

        // Calculate the maximum length of each column
        for (Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> entry : lr1Automaton.entrySet()) {
            // Get the state ID of the current set of LR(1) items
            int state = lr1AutomatonBuilder.getSetState(entry.getKey());

            // For each transition from the current set of LR(1) items
            for (Map.Entry<Symbol, Set<LR1Item>> transition : entry.getValue().entrySet()) {
                // Get the symbol and the next state of the transition
                Symbol symbol = transition.getKey();
                Set<LR1Item> nextState = transition.getValue();
                int nextStateId = lr1AutomatonBuilder.getSetState(nextState);

                // Get the kernel item of the transition
                LR1Item kernelItem = entry.getKey().stream()
                        .filter(item -> item.getNextSymbol() != null && item.getNextSymbol().equals(symbol))
                        .findFirst()
                        .orElse(null);

                // Create the strings for the goto, kernel, state, and closure columns
                String gotoString = "goto(" + state + ", " + symbol + ")";
                String kernelString = (kernelItem != null ? "{" + kernelItem + "}" : "{}");
                String stateString = String.valueOf(nextStateId);
                String closureString = nextState.stream().map(LR1Item::toString).collect(Collectors.joining("; "));

                // Update the maximum lengths of the columns
                maxGotoLength = Math.max(maxGotoLength, gotoString.length());
                maxKernelLength = Math.max(maxKernelLength, kernelString.length());
                maxStateLength = Math.max(maxStateLength, stateString.length());
                maxClosureLength = Math.max(maxClosureLength, closureString.length());
            }
        }

        // Create the format string and the separator string for the table
        String format = "| %-" + maxGotoLength + "s | %-" + maxKernelLength + "s | %-" + maxStateLength + "s | %-" + maxClosureLength + "s |\n";
        String separator = "+-" + "-".repeat(maxGotoLength) + "-+-" + "-".repeat(maxKernelLength) + "-+-" + "-".repeat(maxStateLength) + "-+-" + "-".repeat(maxClosureLength) + "-+\n";

        // Print the header of the table
        System.out.println("LR(1) closure table");
        System.out.println(separator);
        System.out.printf(format, "Goto", "Kernel", "State", "Closure");
        System.out.println(separator);

        // Sort the sets of LR(1) items in the automaton by their state IDs
        List<Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>>> sortedAutomaton = new ArrayList<>(lr1Automaton.entrySet());
        sortedAutomaton.sort(Comparator.comparingInt(entry -> lr1AutomatonBuilder.getSetState(entry.getKey())));

        // Initialize the set of printed states
        Set<Integer> printedStates = new HashSet<>();

        // For each set of LR(1) items in the sorted automaton
        for (Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> entry : sortedAutomaton) {
            // Get the state ID of the current set of LR(1) items
            int state = lr1AutomatonBuilder.getSetState(entry.getKey());

            // If the state has not been printed yet
            if (!printedStates.contains(state)) {
                // Add the state to the set of printed states
                printedStates.add(state);

                // Sort the transitions from the current set of LR(1) items by their symbols
                List<Map.Entry<Symbol, Set<LR1Item>>> sortedTransitions = new ArrayList<>(entry.getValue().entrySet());
                sortedTransitions.sort(Comparator.comparing(entry2 -> entry2.getKey().getValue()));

                // For each transition from the current set of LR(1) items
                for (Map.Entry<Symbol, Set<LR1Item>> transition : sortedTransitions) {
                    // Get the symbol and the next state of the transition
                    Symbol symbol = transition.getKey();
                    Set<LR1Item> nextState = transition.getValue();
                    int nextStateId = lr1AutomatonBuilder.getSetState(nextState);

                    // Get the kernel item of the transition
                    LR1Item kernelItem = entry.getKey().stream()
                            .filter(item -> item.getNextSymbol() != null && item.getNextSymbol().equals(symbol))
                            .findFirst()
                            .orElse(null);

                    // Print the row of the table for the transition
                    System.out.printf(format, "goto(" + state + ", " + symbol + ")", (kernelItem != null ? "{" + kernelItem + "}" : "{}"), nextStateId, nextState.stream().map(LR1Item::toString).collect(Collectors.joining("; ")));
                }

                // Print the separator after the rows for the current state
                System.out.println(separator);
            }
        }
    }

    /**
     * This method prints the action and goto tables.
     *
     * @param lr1Automaton The LR(1) automaton used to generate the action and goto tables
     */
    public void printActionGotoTables(Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
        // Generate the action and goto tables from the LR(1) automaton
        Map<Integer, Map<Symbol, Action>> actionTable = tableGenerator.generateActionTable(lr1Automaton);
        Map<Integer, Map<Symbol, Integer>> gotoTable = tableGenerator.generateGotoTable(lr1Automaton);

        // Get the terminal and non-terminal symbols from the grammar
        Set<Symbol> terminals = new HashSet<>();
        Set<Symbol> nonTerminals = new HashSet<>();
        for (ProductionRule rule : grammar.getProductionRules()) {
            nonTerminals.add(rule.getLeftHandSide());
            for (Symbol symbol : rule.getRightHandSide()) {
                if (firstSetCalculator.isNonTerminal(symbol)) {
                    nonTerminals.add(symbol);
                } else {
                    terminals.add(symbol);
                }
            }
        }
        // Add the end of file symbol to the terminals
        terminals.add(Symbol.EOF);

        // Print the header of the tables
        System.out.printf("%-10s", "State");
        for (Symbol terminal : terminals) {
            System.out.printf("%-10s", terminal);
        }
        for (Symbol nonTerminal : nonTerminals) {
            System.out.printf("%-10s", nonTerminal);
        }
        System.out.println();

        // Print the rows of the tables
        for (int state = 0; state < lr1Automaton.size(); state++) {
            // Get the actions and gotos for the current state
            Map<Symbol, Action> actions = actionTable.getOrDefault(state, Collections.emptyMap());
            Map<Symbol, Integer> gotos = gotoTable.getOrDefault(state, Collections.emptyMap());

            // Print the state
            System.out.printf("%-10s", state);
            // Print the actions for each terminal
            for (Symbol terminal : terminals) {
                Action action = actions.get(terminal);
                System.out.printf("%-10s", action != null ? action : "");
            }
            // Print the gotos for each non-terminal
            for (Symbol nonTerminal : nonTerminals) {
                Integer nextState = gotos.get(nonTerminal);
                System.out.printf("%-10s", nextState != null ? nextState : "");
            }
            System.out.println();
        }
    }

}