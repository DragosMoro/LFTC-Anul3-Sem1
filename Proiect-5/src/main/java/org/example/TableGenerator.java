package org.example;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TableGenerator {
    private Grammar grammar;
    private FirstSetCalculator firstSetCalculator;
    private LR1AutomatonBuilder lr1AutomatonBuilder;

    public TableGenerator(Grammar grammar, FirstSetCalculator firstSetCalculator, LR1AutomatonBuilder lr1AutomatonBuilder) {
        this.grammar = grammar;
        this.firstSetCalculator = firstSetCalculator;
        this.lr1AutomatonBuilder = lr1AutomatonBuilder;
    }

    public Map<Integer, Map<Symbol, Action>> generateActionTable(Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
        Map<Integer, Map<Symbol, Action>> actionTable = new HashMap<>();

        for (Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> entry : lr1Automaton.entrySet()) {
            int state = lr1AutomatonBuilder.getSetState(entry.getKey());

            Map<Symbol, Action> actions = new HashMap<>();
            for (Map.Entry<Symbol, Set<LR1Item>> transition : entry.getValue().entrySet()) {
                Symbol symbol = transition.getKey();
                Set<LR1Item> nextState = transition.getValue();

                if (isTerminal(symbol)) {
                    // Shift action
                    actions.putIfAbsent(symbol, new Shift(lr1AutomatonBuilder.getSetState(nextState)));
                }
            }

            for (LR1Item item : entry.getKey()) {
                if (item.getPosition() == item.getProductionRule().getRightHandSide().size()) {
                    // Reduce action or accept action
                    Set<Symbol> lookaheads = item.getLookaheads();
                    for (Symbol lookahead : lookaheads) {
                        if (item.getProductionRule().getLeftHandSide().equals(new Symbol("S'")) && lookahead.equals(Symbol.EOF)) {
                            actions.putIfAbsent(lookahead, new Accept());
                        } else {
                            actions.putIfAbsent(lookahead, new Reduce(item.getProductionRule(), grammar.getProductionRules().indexOf(item.getProductionRule())));
                        }
                    }
                }
            }

            actionTable.put(state, actions);
        }

        return actionTable;
    }

    public Map<Integer, Map<Symbol, Integer>> generateGotoTable(Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
        Map<Integer, Map<Symbol, Integer>> gotoTable = new HashMap<>();

        for (Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> entry : lr1Automaton.entrySet()) {
            int state = lr1AutomatonBuilder.getSetState(entry.getKey());

            Map<Symbol, Integer> gotos = new HashMap<>();
            for (Map.Entry<Symbol, Set<LR1Item>> transition : entry.getValue().entrySet()) {
                Symbol symbol = transition.getKey();
                Set<LR1Item> nextState = transition.getValue();

                if (firstSetCalculator.isNonTerminal(symbol)) {
                    gotos.put(symbol, lr1AutomatonBuilder.getSetState(nextState));
                }
            }

            gotoTable.put(state, gotos);
        }

        return gotoTable;
    }


    private boolean isTerminal(Symbol symbol) {
        return !firstSetCalculator.isNonTerminal(symbol);
    }
}