package org.example;

import java.util.*;

/**
 * This class is responsible for calculating the first sets of a given grammar.
 * The first set of a symbol is the set of terminals that begin the strings derived from that symbol.
 */
public class FirstSetCalculator {
    private Grammar grammar; // The grammar for which the first sets are calculated
    private Map<Symbol, Set<Symbol>> firstSets; // The map storing the first sets for each symbol

    /**
     * Constructor for the FirstSetCalculator class.
     *
     * @param grammar The grammar for which the first sets are calculated
     */
    public FirstSetCalculator(Grammar grammar) {
        this.grammar = grammar;
        this.firstSets = new HashMap<>(); // Initialize the map for the first sets
    }

    /**
     * This method returns the first set of a given symbol.
     *
     * @param symbol The symbol for which the first set is returned
     * @return The first set of the given symbol
     */
    public Set<Symbol> getFirst(Symbol symbol) {
        if (isTerminal(symbol)) {
            // If the symbol is a terminal, its first set is a set containing only itself
            return new HashSet<>(Collections.singletonList(symbol));
        } else {
            // If the symbol is a non-terminal, return its first set from the map
            return firstSets.get(symbol);
        }
    }

    /**
     * This method calculates the first sets for all symbols in the grammar.
     */
    public void calculateFirstSets() {
        // Initialize the first set for each symbol in the grammar
        for (ProductionRule rule : grammar.getProductionRules()) {
            firstSets.putIfAbsent(rule.getLeftHandSide(), new HashSet<>());
            for (Symbol symbol : rule.getRightHandSide()) {
                firstSets.putIfAbsent(symbol, new HashSet<>());
            }
        }

        boolean changed;
        do {
            changed = false;

            // For each production rule in the grammar
            for (ProductionRule rule : grammar.getProductionRules()) {
                Symbol leftHandSide = rule.getLeftHandSide();
                List<Symbol> rightHandSide = rule.getRightHandSide();

                // Calculate the first set of the right hand side of the rule
                Set<Symbol> firstOfRight = new HashSet<>(firstSets.get(leftHandSide));
                for (Symbol symbol : rightHandSide) {
                    if (isNonTerminal(symbol)) {
                        // If the symbol is a non-terminal, add its first set to the first set of the right hand side
                        firstOfRight.addAll(firstSets.get(symbol));
                        if (!firstSets.get(symbol).contains(Symbol.EOF)) {
                            break;
                        }
                    } else {
                        // If the symbol is a terminal, add it to the first set of the right hand side and break the loop
                        firstOfRight.add(symbol);
                        break;
                    }
                }

                // If the first set of the left hand side has changed, set the flag to true
                if (firstSets.get(leftHandSide).addAll(firstOfRight)) {
                    changed = true;
                }
            }

        } while (changed); // Repeat the process until no changes are made
    }

    /**
     * This method checks if a given symbol is a terminal.
     *
     * @param symbol The symbol to be checked
     * @return true if the symbol is a terminal, false otherwise
     */
    public boolean isTerminal(Symbol symbol) {
        return !isNonTerminal(symbol); // A symbol is a terminal if it is not a non-terminal
    }

    /**
     * This method checks if a given symbol is a non-terminal.
     *
     * @param symbol The symbol to be checked
     * @return true if the symbol is a non-terminal, false otherwise
     */
    public boolean isNonTerminal(Symbol symbol) {
        String value = symbol.getValue();
        // A symbol is a non-terminal if its value is a single uppercase letter, "S'", or two uppercase letters
        if ((value.length() == 1 && Character.isUpperCase(value.charAt(0))) || value.equals("S'") || (value.length() == 2 && Character.isUpperCase(value.charAt(1)) && Character.isUpperCase(value.charAt(0)))) {
            return true;
        }
        return false;
    }
}