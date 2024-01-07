package org.example;

import java.util.*;

public class FirstSetCalculator {
    private Grammar grammar;
    private Map<Symbol, Set<Symbol>> firstSets;

    public FirstSetCalculator(Grammar grammar) {
        this.grammar = grammar;
        this.firstSets = new HashMap<>();
    }

    public Set<Symbol> getFirst(Symbol symbol) {
        if (isTerminal(symbol)) {
            return new HashSet<>(Collections.singletonList(symbol));
        } else {
            return firstSets.get(symbol);
        }
    }

    public void calculateFirstSets() {
        for (ProductionRule rule : grammar.getProductionRules()) {
            firstSets.putIfAbsent(rule.getLeftHandSide(), new HashSet<>());
            for (Symbol symbol : rule.getRightHandSide()) {
                firstSets.putIfAbsent(symbol, new HashSet<>());
            }
        }

        boolean changed;
        do {
            changed = false;

            for (ProductionRule rule : grammar.getProductionRules()) {
                Symbol leftHandSide = rule.getLeftHandSide();
                List<Symbol> rightHandSide = rule.getRightHandSide();

                Set<Symbol> firstOfRight = new HashSet<>(firstSets.get(leftHandSide));
                for (Symbol symbol : rightHandSide) {
                    if (isNonTerminal(symbol)) {
                        firstOfRight.addAll(firstSets.get(symbol));
                        if (!firstSets.get(symbol).contains(Symbol.EOF)) {
                            break;
                        }
                    } else {
                        firstOfRight.add(symbol);
                        break;
                    }
                }

                if (firstSets.get(leftHandSide).addAll(firstOfRight)) {
                    changed = true;
                }
            }

        } while (changed);
    }

    private boolean isTerminal(Symbol symbol) {
        return !isNonTerminal(symbol);
    }

    public boolean isNonTerminal(Symbol symbol) {
        String value = symbol.getValue();
        if ((value.length() == 1 && Character.isUpperCase(value.charAt(0))) || value.equals("S'") || (value.length() == 2 && Character.isUpperCase(value.charAt(1)) && Character.isUpperCase(value.charAt(0)))) {
            return true;
        }
        return false;
    }
}