package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Printer {
    private Grammar grammar;

    private TableGenerator tableGenerator;
    private LR1AutomatonBuilder lr1AutomatonBuilder;

    private FirstSetCalculator firstSetCalculator;

    public Printer(Grammar grammar, TableGenerator tableGenerator, LR1AutomatonBuilder lr1AutomatonBuilder, FirstSetCalculator firstSetCalculator) {
        this.grammar = grammar;
        this.tableGenerator = tableGenerator;
        this.lr1AutomatonBuilder = lr1AutomatonBuilder;
        this.firstSetCalculator = firstSetCalculator;

    }

    public void printLR1Automaton(Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
        int maxGotoLength = 0;
        int maxKernelLength = 0;
        int maxStateLength = 0;
        int maxClosureLength = 0;

        // Calculate the maximum length of each column
        for (Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> entry : lr1Automaton.entrySet()) {
            int state = lr1AutomatonBuilder.getSetState(entry.getKey());

            for (Map.Entry<Symbol, Set<LR1Item>> transition : entry.getValue().entrySet()) {
                Symbol symbol = transition.getKey();
                Set<LR1Item> nextState = transition.getValue();
                int nextStateId = lr1AutomatonBuilder.getSetState(nextState);

                LR1Item kernelItem = entry.getKey().stream()
                        .filter(item -> item.getNextSymbol() != null && item.getNextSymbol().equals(symbol))
                        .findFirst()
                        .orElse(null);

                String gotoString = "goto(" + state + ", " + symbol + ")";
                String kernelString = (kernelItem != null ? "{" + kernelItem + "}" : "{}");
                String stateString = String.valueOf(nextStateId);
                String closureString = nextState.stream().map(LR1Item::toString).collect(Collectors.joining("; "));

                maxGotoLength = Math.max(maxGotoLength, gotoString.length());
                maxKernelLength = Math.max(maxKernelLength, kernelString.length());
                maxStateLength = Math.max(maxStateLength, stateString.length());
                maxClosureLength = Math.max(maxClosureLength, closureString.length());
            }
        }
        String format = "| %-" + maxGotoLength + "s | %-" + maxKernelLength + "s | %-" + maxStateLength + "s | %-" + maxClosureLength + "s |\n";
        String separator = "+-" + "-".repeat(maxGotoLength) + "-+-" + "-".repeat(maxKernelLength) + "-+-" + "-".repeat(maxStateLength) + "-+-" + "-".repeat(maxClosureLength) + "-+\n";

        System.out.println("LR(1) closure table");
        System.out.println(separator);

        System.out.printf(format, "Goto", "Kernel", "State", "Closure");
        System.out.println(separator);

        List<Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>>> sortedAutomaton = new ArrayList<>(lr1Automaton.entrySet());
        sortedAutomaton.sort(Comparator.comparingInt(entry -> lr1AutomatonBuilder.getSetState(entry.getKey())));

        Set<Integer> printedStates = new HashSet<>();

        for (Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> entry : sortedAutomaton) {
            int state = lr1AutomatonBuilder.getSetState(entry.getKey());

            if (!printedStates.contains(state)) {
                printedStates.add(state);

                List<Map.Entry<Symbol, Set<LR1Item>>> sortedTransitions = new ArrayList<>(entry.getValue().entrySet());
                sortedTransitions.sort(Comparator.comparing(entry2 -> entry2.getKey().getValue()));
                int i = sortedTransitions.size();
                for (Map.Entry<Symbol, Set<LR1Item>> transition : sortedTransitions) {
                    Symbol symbol = transition.getKey();
                    Set<LR1Item> nextState = transition.getValue();
                    int nextStateId = lr1AutomatonBuilder.getSetState(nextState);

                    LR1Item kernelItem = entry.getKey().stream()
                            .filter(item -> item.getNextSymbol() != null && item.getNextSymbol().equals(symbol))
                            .findFirst()
                            .orElse(null);

                    System.out.printf(format, "goto(" + state + ", " + symbol + ")", (kernelItem != null ? "{" + kernelItem + "}" : "{}"), nextStateId, nextState.stream().map(LR1Item::toString).collect(Collectors.joining("; ")));
                }
                if (i != 0) {
                    System.out.println(separator);
                }

            }
        }
    }

    public void printActionGotoTables(Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
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
        terminals.add(Symbol.EOF);

        // Print header
        System.out.printf("%-10s", "State");
        for (Symbol terminal : terminals) {
            System.out.printf("%-10s", terminal);
        }
        for (Symbol nonTerminal : nonTerminals) {
            System.out.printf("%-10s", nonTerminal);
        }
        System.out.println();

        // Print rows
        for (int state = 0; state < lr1Automaton.size(); state++) {
            Map<Symbol, Action> actions = actionTable.getOrDefault(state, Collections.emptyMap());
            Map<Symbol, Integer> gotos = gotoTable.getOrDefault(state, Collections.emptyMap());

            System.out.printf("%-10s", state);
            for (Symbol terminal : terminals) {
                Action action = actions.get(terminal);
                System.out.printf("%-10s", action != null ? action : "");
            }
            for (Symbol nonTerminal : nonTerminals) {
                Integer nextState = gotos.get(nonTerminal);
                System.out.printf("%-10s", nextState != null ? nextState : "");
            }
            System.out.println();
        }
    }

}