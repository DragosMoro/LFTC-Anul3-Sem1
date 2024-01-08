package org.example;


import java.util.*;

/**
 * This class is responsible for building the LR(1) automaton for a given grammar.
 * The LR(1) automaton is a state machine used in the parsing of LR(1) grammars.
 */
public class LR1AutomatonBuilder {
    private final Grammar grammar; // The grammar for which the LR(1) automaton is built
    private final Map<Set<LR1Item>, Integer> stateMap = new HashMap<>(); // The map storing the state IDs for each set of LR(1) items
    private final FirstSetCalculator firstSetCalculator; // The first set calculator used to calculate the first sets of the grammar
    private int currentStateId = 0; // The current state ID in the LR(1) automaton

    /**
     * Constructor for the LR1AutomatonBuilder class.
     *
     * @param grammar            The grammar for which the LR(1) automaton is built
     * @param firstSetCalculator The first set calculator used to calculate the first sets of the grammar
     */
    public LR1AutomatonBuilder(Grammar grammar, FirstSetCalculator firstSetCalculator) {
        this.grammar = grammar;
        this.firstSetCalculator = firstSetCalculator;
    }

    /**
     * This method calculates the closure of a set of LR(1) items.
     * The closure of a set of LR(1) items is the set of all LR(1) items that can be derived from the given set.
     *
     * @param items The set of LR(1) items for which the closure is calculated
     * @return The closure of the given set of LR(1) items
     */
    public List<LR1Item> closure(Set<LR1Item> items) {
        // Initialize the closure with the given set of items
        Set<LR1Item> closure = new HashSet<>(items);
        boolean changed;

        do {
            changed = false;
            Set<LR1Item> tempClosure = new HashSet<>(closure);

            // For each item in the closure
            for (LR1Item item : tempClosure) {
                Symbol nextSymbol = item.getNextSymbol();

                // If the next symbol is a non-terminal
                if (nextSymbol != null && firstSetCalculator.isNonTerminal(nextSymbol)) {
                    // Calculate the first set of the rest of the production
                    Set<Symbol> firstOfRest = calculateFirstOfRest(item);

                    // If the first set contains the end of file symbol, remove it and add the lookaheads of the item
                    if (firstOfRest.contains(Symbol.EOF)) {
                        firstOfRest.remove(Symbol.EOF);
                        firstOfRest.addAll(item.getLookaheads());
                    }

                    // For each production rule of the next symbol
                    for (ProductionRule rule : grammar.getProductionsForNonTerminal(nextSymbol)) {
                        // Create a new item with the dot at the beginning and the first set as the lookaheads
                        LR1Item newItem = new LR1Item(rule, 0, firstOfRest);
                        // If the new item is not already in the closure, add it and set the flag to true
                        if (closure.add(newItem)) {
                            changed = true;
                        }

                        // If the production leads to epsilon, add a new item with the dot at the beginning and the lookaheads of the item
                        if (rule.getRightHandSide().size() == 1 && rule.getRightHandSide().get(0).equals(Symbol.EPSILON)) {
                            LR1Item epsilonItem = new LR1Item(rule, 0, item.getLookaheads());
                            if (closure.add(epsilonItem)) {
                                changed = true;
                            }
                        }
                    }
                }
            }

        } while (changed); // Repeat the process until no changes are made

        return new ArrayList<>(closure);
    }


    /**
     * This method builds the LR(1) automaton for the grammar.
     * The LR(1) automaton is a state machine used in the parsing of LR(1) grammars.
     *
     * @return The LR(1) automaton for the grammar
     */
    public Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> buildLR1Automaton() {
        // Initialize the automaton
        Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> automaton = new LinkedHashMap<>();
        // Get the initial set of items
        Set<LR1Item> initialItems = new HashSet<>(closure(Collections.singleton(new LR1Item(
                grammar.getProductionRules().get(0), 0, Collections.singleton(Symbol.EOF)))));
        // Initialize the queue with the initial set of items
        Queue<Set<LR1Item>> queue = new LinkedList<>();
        queue.add(initialItems);
        // Add the initial set of items to the automaton
        automaton.put(initialItems, new HashMap<>());

        // While the queue is not empty
        while (!queue.isEmpty()) {
            // Get the current state
            Set<LR1Item> currentState = queue.poll();
            // Initialize the transitions from the current state
            Map<Symbol, Set<LR1Item>> transitions = new HashMap<>();

            // For each item in the current state
            for (LR1Item item : currentState) {
                // Get the symbol after the dot
                Symbol nextSymbol = item.getNextSymbol();

                // If there is a symbol after the dot
                if (nextSymbol != null) {
                    // Calculate the next state
                    Set<LR1Item> nextState = new HashSet<>(goTo(currentState, nextSymbol));
                    // Add the transition to the next state by the symbol to the transitions
                    transitions.put(nextSymbol, nextState);

                    // If the next state is not already in the automaton
                    if (!automaton.containsKey(nextState)) {
                        // Add the next state to the queue and the automaton
                        queue.add(nextState);
                        automaton.put(nextState, new HashMap<>());
                    }
                }
            }

            // Add the transitions from the current state to the automaton
            automaton.put(currentState, transitions);
        }

        return automaton;
    }

    /**
     * This method calculates the set of LR(1) items that can be reached from a given set of LR(1) items by a given symbol.
     *
     * @param items  The set of LR(1) items from which the transition is made
     * @param symbol The symbol by which the transition is made
     * @return The set of LR(1) items that can be reached from the given set of LR(1) items by the given symbol
     */
    public List<LR1Item> goTo(Set<LR1Item> items, Symbol symbol) {
        // Initialize the set of items that can be reached
        Set<LR1Item> goToSet = new HashSet<>();

        // For each item in the given set
        for (LR1Item item : items) {
            Symbol nextSymbol = item.getNextSymbol();

            // If the next symbol is equal to the given symbol
            if (nextSymbol != null && nextSymbol.equals(symbol)) {
                // Add a new item with the dot moved one position to the right and the same lookaheads to the set
                goToSet.add(new LR1Item(item.getProductionRule(), item.getPosition() + 1, item.getLookaheads()));
            }
        }

        // Return the closure of the set of items that can be reached
        return closure(goToSet);
    }

    /**
     * This method calculates the first set of the rest of the production after the dot in a given LR(1) item.
     * The first set of a sequence of symbols is the set of terminals that begin the strings derived from that sequence.
     *
     * @param item The LR(1) item for which the first set of the rest of the production is calculated
     * @return The first set of the rest of the production after the dot in the given LR(1) item
     */
    private Set<Symbol> calculateFirstOfRest(LR1Item item) {
        Set<Symbol> firstOfRest = new HashSet<>();

        // Get the position after the dot
        int position = item.getPosition() + 1;
        // Get the rest of the production after the dot
        List<Symbol> rest = item.getProductionRule().getRightHandSide().subList(position, item.getProductionRule().getRightHandSide().size());
        boolean isRestNullable = true;

        // For each symbol in the rest of the production
        for (Symbol symbol : rest) {
            // Get the first set of the symbol
            Set<Symbol> first = firstSetCalculator.getFirst(symbol);
            // If the first set does not contain the end of file symbol
            if (!first.contains(Symbol.EOF)) {
                // Add the first set to the first set of the rest and break the loop
                firstOfRest.addAll(first);
                isRestNullable = false;
                break;
            } else {
                // If the first set contains the end of file symbol, remove it and add the rest to the first set of the rest
                first.remove(Symbol.EOF);
                firstOfRest.addAll(first);
            }
        }

        // If the rest of the production is nullable and the item has lookaheads
        if (isRestNullable && item.getLookaheads().size() > 0) {
            // Add the lookaheads to the first set of the rest
            firstOfRest.addAll(item.getLookaheads());
        }

        // If the first set of the rest is empty, add the end of file symbol
        if (firstOfRest.isEmpty()) {
            firstOfRest.add(Symbol.EOF);
        }

        return firstOfRest;
    }


    /**
     * This method returns the state ID of a given set of LR(1) items in the LR(1) automaton.
     *
     * @param lr1Items The set of LR(1) items for which the state ID is returned
     * @return The state ID of the given set of LR(1) items in the LR(1) automaton
     */
    public int getSetState(Set<LR1Item> lr1Items) {
        if (stateMap.containsKey(lr1Items)) {
            return stateMap.get(lr1Items);
        }

        int newState = currentStateId++;
        stateMap.put(lr1Items, newState);
        return newState;
    }

}