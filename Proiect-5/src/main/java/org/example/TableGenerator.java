package org.example;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for generating the action and goto tables for a given grammar.
 */
public class TableGenerator {
    private Grammar grammar; // The grammar for which the tables are generated
    private FirstSetCalculator firstSetCalculator; // The first set calculator used to calculate the first sets of the grammar
    private LR1AutomatonBuilder lr1AutomatonBuilder; // The LR(1) automaton builder used to build the LR(1) automaton

    /**
     * Constructor for the TableGenerator class.
     *
     * @param grammar             The grammar for which the tables are generated
     * @param firstSetCalculator  The first set calculator used to calculate the first sets of the grammar
     * @param lr1AutomatonBuilder The LR(1) automaton builder used to build the LR(1) automaton
     */
    public TableGenerator(Grammar grammar, FirstSetCalculator firstSetCalculator, LR1AutomatonBuilder lr1AutomatonBuilder) {
        this.grammar = grammar;
        this.firstSetCalculator = firstSetCalculator;
        this.lr1AutomatonBuilder = lr1AutomatonBuilder;
    }

    /**
     * This method generates the action table for the grammar.
     *
     * @param lr1Automaton The LR(1) automaton used to generate the action table
     * @return The action table for the grammar
     */
    public Map<Integer, Map<Symbol, Action>> generateActionTable(Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
        Map<Integer, Map<Symbol, Action>> actionTable = new HashMap<>();

        // For each set of LR(1) items in the LR(1) automaton
        for (Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> entry : lr1Automaton.entrySet()) {
            // Get the state ID of the current set of LR(1) items
            int state = lr1AutomatonBuilder.getSetState(entry.getKey());

            // Initialize the actions for the current state
            Map<Symbol, Action> actions = new HashMap<>();
            // For each transition from the current set of LR(1) items
            for (Map.Entry<Symbol, Set<LR1Item>> transition : entry.getValue().entrySet()) {
                // Get the symbol and the next state of the transition
                Symbol symbol = transition.getKey();
                Set<LR1Item> nextState = transition.getValue();

                // If the symbol is a terminal, add a shift action for the symbol
                if (isTerminal(symbol)) {
                    actions.putIfAbsent(symbol, new Shift(lr1AutomatonBuilder.getSetState(nextState)));
                }
            }

            // For each item in the current set of LR(1) items
            for (LR1Item item : entry.getKey()) {
                // If the dot is at the end of the production
                if (item.getPosition() == item.getProductionRule().getRightHandSide().size()) {
                    // Get the lookaheads of the item
                    Set<Symbol> lookaheads = item.getLookaheads();
                    // For each lookahead
                    for (Symbol lookahead : lookaheads) {
                        // If the left-hand side of the production is the start symbol and the lookahead is the end of file symbol, add an accept action for the lookahead
                        if (item.getProductionRule().getLeftHandSide().equals(new Symbol("S'")) && lookahead.equals(Symbol.EOF)) {
                            actions.putIfAbsent(lookahead, new Accept());
                        } else {
                            // Otherwise, add a reduce action for the lookahead
                            actions.putIfAbsent(lookahead, new Reduce(item.getProductionRule(), grammar.getProductionRules().indexOf(item.getProductionRule())));
                        }
                    }
                }
            }

            // Add the actions for the current state to the action table
            actionTable.put(state, actions);
        }

        return actionTable;
    }

    /**
     * This method generates the goto table for the grammar.
     * The goto table is used in the LR(1) parsing algorithm to determine the next state to go to for a given state and non-terminal.
     *
     * @param lr1Automaton The LR(1) automaton used to generate the goto table
     * @return The goto table for the grammar
     */
    public Map<Integer, Map<Symbol, Integer>> generateGotoTable(Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
        // Initialize the goto table
        Map<Integer, Map<Symbol, Integer>> gotoTable = new HashMap<>();

        // For each set of LR(1) items in the LR(1) automaton
        for (Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> entry : lr1Automaton.entrySet()) {
            // Get the state ID of the current set of LR(1) items
            int state = lr1AutomatonBuilder.getSetState(entry.getKey());

            // Initialize the gotos for the current state
            Map<Symbol, Integer> gotos = new HashMap<>();
            // For each transition from the current set of LR(1) items
            for (Map.Entry<Symbol, Set<LR1Item>> transition : entry.getValue().entrySet()) {
                // Get the symbol and the next state of the transition
                Symbol symbol = transition.getKey();
                Set<LR1Item> nextState = transition.getValue();

                // If the symbol is a non-terminal, add a goto for the symbol
                if (firstSetCalculator.isNonTerminal(symbol)) {
                    gotos.put(symbol, lr1AutomatonBuilder.getSetState(nextState));
                }
            }

            // Add the gotos for the current state to the goto table
            gotoTable.put(state, gotos);
        }

        return gotoTable;
    }

    /**
     * This method checks if a given symbol is a terminal.
     * A terminal is a symbol that does not appear on the left-hand side of any production rule.
     *
     * @param symbol The symbol to be checked
     * @return true if the symbol is a terminal, false otherwise
     */
    private boolean isTerminal(Symbol symbol) {
        return firstSetCalculator.isTerminal(symbol);
    }
}