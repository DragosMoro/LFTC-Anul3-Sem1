package org.example;


import java.util.*;

public class LR1AutomatonBuilder {
    private Grammar grammar;
    private int currentStateId = 0;
    private Map<Set<LR1Item>, Integer> stateMap = new HashMap<>();

    private FirstSetCalculator firstSetCalculator;


    public LR1AutomatonBuilder(Grammar grammar, FirstSetCalculator firstSetCalculator) {
        this.grammar = grammar;
        this.firstSetCalculator = firstSetCalculator;
    }

    public List<LR1Item> closure(Set<LR1Item> items) {
        Set<LR1Item> closure = new HashSet<>(items);
        boolean changed;

        do {
            changed = false;
            Set<LR1Item> tempClosure = new HashSet<>(closure);

            for (LR1Item item : tempClosure) {
                Symbol nextSymbol = item.getNextSymbol();

                if (nextSymbol != null && firstSetCalculator.isNonTerminal(nextSymbol)) {
                    List<Symbol> restOfProduction = item.getProductionRule().getRightHandSide().subList(item.getPosition() + 1, item.getProductionRule().getRightHandSide().size());
                    Set<Symbol> firstOfRest = calculateFirstOfRest(item, closure);

                    if (firstOfRest.contains(Symbol.EOF)) {
                        firstOfRest.remove(Symbol.EOF);
                        firstOfRest.addAll(item.getLookaheads());
                    }

                    for (ProductionRule rule : grammar.getProductionsForNonTerminal(nextSymbol)) {
                        LR1Item newItem = new LR1Item(rule, 0, firstOfRest);
                        if (closure.add(newItem)) {
                            changed = true;
                        }
                    }
                }
            }

        } while (changed);

        return new ArrayList<>(closure);
    }

    public Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> buildLR1Automaton() {
        Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> automaton = new LinkedHashMap<>();
        Set<LR1Item> initialItems = new HashSet<>(closure(Collections.singleton(new LR1Item(
                grammar.getProductionRules().get(0), 0, Collections.singleton(Symbol.EOF)))));
        Queue<Set<LR1Item>> queue = new LinkedList<>();
        queue.add(initialItems);
        automaton.put(initialItems, new HashMap<>());

        while (!queue.isEmpty()) {
            Set<LR1Item> currentState = queue.poll();
            Map<Symbol, Set<LR1Item>> transitions = new HashMap<>();

            for (LR1Item item : currentState) {
                Symbol nextSymbol = item.getNextSymbol();

                if (nextSymbol != null) {
                    Set<LR1Item> nextState = new HashSet<>(goTo(currentState, nextSymbol));
                    transitions.put(nextSymbol, nextState);

                    if (!automaton.containsKey(nextState)) {
                        queue.add(nextState);
                        automaton.put(nextState, new HashMap<>());
                    }
                }
            }

            automaton.put(currentState, transitions);
        }

        return automaton;
    }

    public List<LR1Item> goTo(Set<LR1Item> items, Symbol symbol) {
        Set<LR1Item> goToSet = new HashSet<>();

        for (LR1Item item : items) {
            Symbol nextSymbol = item.getNextSymbol();

            if (nextSymbol != null && nextSymbol.equals(symbol)) {
                goToSet.add(new LR1Item(item.getProductionRule(), item.getPosition() + 1, item.getLookaheads()));
            }
        }

        return closure(goToSet);
    }

    private Set<Symbol> calculateFirstOfRest(LR1Item item, Set<LR1Item> closure) {
        Set<Symbol> firstOfRest = new HashSet<>();

        int position = item.getPosition() + 1;
        List<Symbol> rest = item.getProductionRule().getRightHandSide().subList(position, item.getProductionRule().getRightHandSide().size());
        boolean isRestNullable = true;

        for (Symbol symbol : rest) {
            Set<Symbol> first = firstSetCalculator.getFirst(symbol);
            if (!first.contains(Symbol.EOF)) {
                firstOfRest.addAll(first);
                isRestNullable = false;
                break;
            } else {
                first.remove(Symbol.EOF);
                firstOfRest.addAll(first);
            }
        }

        if (isRestNullable && item.getLookaheads().size() > 0) {
            firstOfRest.addAll(item.getLookaheads());
        }

        if (firstOfRest.isEmpty()) {
            firstOfRest.add(Symbol.EOF);
        }

        return firstOfRest;
    }

    public int getSetState(Set<LR1Item> lr1Items) {
        if (stateMap.containsKey(lr1Items)) {
            return stateMap.get(lr1Items);
        }

        int newState = currentStateId++;
        stateMap.put(lr1Items, newState);
        return newState;
    }

}