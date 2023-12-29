package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    private List<ProductionRule> productionRules;
    private Map<Symbol, Set<Symbol>> firstSets;

    private int currentStateId = 0;
    private Map<Set<LR1Item>, Integer> stateMap = new HashMap<>();

    public Grammar(List<ProductionRule> productionRules) {
        this.productionRules = productionRules;
        this.firstSets = new HashMap<>();
        calculateFirstSets();
    }

    public static Grammar readGrammarFromFile(String filePath) throws IOException {
        List<ProductionRule> productionRules = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s*->\\s*");
                if (parts.length == 2) {
                    Symbol leftHandSide = new Symbol(parts[0].trim());
                    List<Symbol> rightHandSide = Arrays.asList(parts[1].trim().split("\\s+")).stream()
                            .map(Symbol::new)
                            .collect(Collectors.toList());

                    productionRules.add(new ProductionRule(leftHandSide, rightHandSide));
                }
            }
        }

        return new Grammar(productionRules);
    }

    public List<ProductionRule> getProductionRules() {
        return productionRules;
    }

    public Set<Symbol> getFirst(Symbol symbol) {
        if (isTerminal(symbol)) {
            // Dacă simbolul este terminal, first set-ul este simbolul însuși
            return new HashSet<>(Collections.singletonList(symbol));
        } else {
            return firstSets.get(symbol);
        }
    }

    private void calculateFirstSets() {
        for (ProductionRule rule : productionRules) {
            firstSets.putIfAbsent(rule.getLeftHandSide(), new HashSet<>());
            for (Symbol symbol : rule.getRightHandSide()) {
                firstSets.putIfAbsent(symbol, new HashSet<>());
            }
        }


        boolean changed;
        do {
            changed = false;

            for (ProductionRule rule : productionRules) {
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


    public List<LR1Item> closure(Set<LR1Item> items) {
        Set<LR1Item> closure = new HashSet<>(items);
        boolean changed;

        do {
            changed = false;
            Set<LR1Item> tempClosure = new HashSet<>(closure);

            for (LR1Item item : tempClosure) {
                Symbol nextSymbol = item.getNextSymbol();

                if (nextSymbol != null && isNonTerminal(nextSymbol)) {
                    List<Symbol> restOfProduction = item.getProductionRule().getRightHandSide().subList(item.getPosition() + 1, item.getProductionRule().getRightHandSide().size());
                    Set<Symbol> firstOfRest = calculateFirstOfRest(item, closure);

                    if (firstOfRest.contains(Symbol.EOF)) {
                        firstOfRest.remove(Symbol.EOF);
                        firstOfRest.addAll(item.getLookaheads());
                    }

                    for (ProductionRule rule : getProductionsForNonTerminal(nextSymbol)) {
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
                productionRules.get(0), 0, Collections.singleton(Symbol.EOF)))));
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

    private Set<Symbol> calculateLookaheads(LR1Item item, Set<LR1Item> closure) {
        Set<Symbol> lookaheads = new HashSet<>();

        if (item.getPosition() < item.getProductionRule().getRightHandSide().size()) {
            Symbol nextSymbol = item.getProductionRule().getRightHandSide().get(item.getPosition());
            Set<Symbol> firstOfRest = calculateFirstOfRest(item, closure);
            lookaheads.addAll(firstOfRest);

            if (isNullable(nextSymbol)) {
                lookaheads.addAll(item.getLookaheads());
            }
        }

        return lookaheads;
    }


    private Set<Symbol> calculateFirstOfRest(LR1Item item, Set<LR1Item> closure) {
        Set<Symbol> firstOfRest = new HashSet<>();

        int position = item.getPosition() + 1;
        List<Symbol> rest = item.getProductionRule().getRightHandSide().subList(position, item.getProductionRule().getRightHandSide().size());
        boolean isRestNullable = true;

        for (Symbol symbol : rest) {
            Set<Symbol> first = getFirst(symbol);
            System.out.println("First of " + symbol + " is " + first);
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

    public boolean isNonTerminal(Symbol symbol) {
        String value = symbol.getValue();
        if (value.length() == 1 && Character.isUpperCase(value.charAt(0)) || value.equals("S'")) {
            return true;
        }
        return false;
    }

    public boolean isNullable(Symbol symbol) {
        return firstSets.containsKey(symbol) && firstSets.get(symbol).contains(Symbol.EOF);
    }

    public List<ProductionRule> getProductionsForNonTerminal(Symbol nonTerminal) {
        List<ProductionRule> nonTerminalProductions = new ArrayList<>();
        for (ProductionRule rule : productionRules) {
            if (rule.getLeftHandSide().equals(nonTerminal)) {
                nonTerminalProductions.add(rule);
            }
        }
        return nonTerminalProductions;
    }


    public Map<Integer, Map<Symbol, Action>> generateActionTable(Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
        Map<Integer, Map<Symbol, Action>> actionTable = new HashMap<>();

        for (Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> entry : lr1Automaton.entrySet()) {
            int state = getSetState(entry.getKey());

            Map<Symbol, Action> actions = new HashMap<>();
            for (Map.Entry<Symbol, Set<LR1Item>> transition : entry.getValue().entrySet()) {
                Symbol symbol = transition.getKey();
                Set<LR1Item> nextState = transition.getValue();

                if (isTerminal(symbol)) {
                    // Shift action
                    actions.putIfAbsent(symbol, new Shift(getSetState(nextState)));
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
                            actions.putIfAbsent(lookahead, new Reduce(item.getProductionRule(), productionRules.indexOf(item.getProductionRule())));
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
            int state = getSetState(entry.getKey());

            Map<Symbol, Integer> gotos = new HashMap<>();
            for (Map.Entry<Symbol, Set<LR1Item>> transition : entry.getValue().entrySet()) {
                Symbol symbol = transition.getKey();
                Set<LR1Item> nextState = transition.getValue();

                if (isNonTerminal(symbol)) {
                    gotos.put(symbol, getSetState(nextState));
                }
            }

            gotoTable.put(state, gotos);
        }

        return gotoTable;
    }

    private int getSetState(Set<LR1Item> lr1Items) {
        // Check if the set of LR(1) items already has a state number
        if (stateMap.containsKey(lr1Items)) {
            return stateMap.get(lr1Items);
        }

        // If not, generate a new state number and add it to the map
        int newState = currentStateId++;
        stateMap.put(lr1Items, newState);
        return newState;
    }

    private boolean isTerminal(Symbol symbol) {
        // Verificați dacă simbolul este terminal
        return !isNonTerminal(symbol);
    }

    public void printLR1Automaton(Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
        int maxGotoLength = 0;
        int maxKernelLength = 0;
        int maxStateLength = 0;
        int maxClosureLength = 0;

        // Calculate the maximum length of each column
        for (Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> entry : lr1Automaton.entrySet()) {
            int state = getSetState(entry.getKey());

            for (Map.Entry<Symbol, Set<LR1Item>> transition : entry.getValue().entrySet()) {
                Symbol symbol = transition.getKey();
                Set<LR1Item> nextState = transition.getValue();
                int nextStateId = getSetState(nextState);

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
        sortedAutomaton.sort(Comparator.comparingInt(entry -> getSetState(entry.getKey())));

        Set<Integer> printedStates = new HashSet<>();

        for (Map.Entry<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> entry : sortedAutomaton) {
            int state = getSetState(entry.getKey());

            if (!printedStates.contains(state)) {
                printedStates.add(state);

                List<Map.Entry<Symbol, Set<LR1Item>>> sortedTransitions = new ArrayList<>(entry.getValue().entrySet());
                sortedTransitions.sort(Comparator.comparing(entry2 -> entry2.getKey().getValue()));
                int i = sortedTransitions.size();
                for (Map.Entry<Symbol, Set<LR1Item>> transition : sortedTransitions) {
                    Symbol symbol = transition.getKey();
                    Set<LR1Item> nextState = transition.getValue();
                    int nextStateId = getSetState(nextState);

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
        Map<Integer, Map<Symbol, Action>> actionTable = generateActionTable(lr1Automaton);
        Map<Integer, Map<Symbol, Integer>> gotoTable = generateGotoTable(lr1Automaton);

        // Get the terminal and non-terminal symbols from the grammar
        Set<Symbol> terminals = new HashSet<>();
        Set<Symbol> nonTerminals = new HashSet<>();
        for (ProductionRule rule : productionRules) {
            nonTerminals.add(rule.getLeftHandSide());
            for (Symbol symbol : rule.getRightHandSide()) {
                if (isNonTerminal(symbol)) {
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

    // Metodă pentru formatarea acțiunilor într-un șir de caractere
    private String formatActions(Map<Symbol, Action> actions) {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<Symbol, Action> entry : actions.entrySet()) {
            result.append("[").append(entry.getKey()).append(": ").append(entry.getValue()).append("], ");
        }

        // Eliminăm ultima virgulă și spațiu adăugate
        if (result.length() > 0) {
            result.setLength(result.length() - 2);
        }

        return result.toString();
    }

    public void parse(String input, Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
        Map<Integer, Map<Symbol, Action>> actionTable = generateActionTable(lr1Automaton);
        Map<Integer, Map<Symbol, Integer>> gotoTable = generateGotoTable(lr1Automaton);

        // Initialize the stack and the input index
        Stack<Integer> stateStack = new Stack<>();
        stateStack.push(0); // Push the start state
        int inputIndex = 0;

        // Print the table header
        System.out.println("+-----+---------+----------------+--------+");
        System.out.println("| Step |  Stack   |     Input      | Action |");
        System.out.println("+-----+---------+----------------+--------+");

        int step = 1;
        while (true) {
            // Get the current state and input symbol
            int currentState = stateStack.peek();
            Symbol currentInput = (inputIndex < input.length()) ? new Symbol(String.valueOf(input.charAt(inputIndex))) : Symbol.EOF;

            // Get the action for the current state and input symbol
            Action action = actionTable.get(currentState).get(currentInput);

            // Print the current step, stack, input, and action
            System.out.printf("|  %-3d | %-8s | %-15s | %-6s |\n", step, stateStack, input.substring(inputIndex), action);

            // Perform the action
            if (action instanceof Shift) {
                // Shift action: push the next state onto the stack and consume the current input symbol
                stateStack.push(((Shift) action).getNextState());
                inputIndex++;
            } else if (action instanceof Reduce) {
                // Reduce action: pop states from the stack and push the goto state
                ProductionRule rule = ((Reduce) action).getProductionRule();
                for (int i = 0; i < rule.getRightHandSide().size(); i++) {
                    stateStack.pop();
                }
                currentState = stateStack.peek();
                stateStack.push(gotoTable.get(currentState).get(rule.getLeftHandSide()));
            } else if (action instanceof Accept) {
                // Accept action: input is accepted
                System.out.println("+-----+---------+----------------+--------+");
                System.out.println("Input accepted!");
                break;
            } else {
                // No action: input is not accepted
                System.out.println("+-----+---------+----------------+--------+");
                System.out.println("Error: Input not accepted.");
                break;
            }

            // Increment the step
            step++;
        }
    }

    public void parseInput(List<Symbol> inputSymbols) {
        Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton = buildLR1Automaton();
        Map<Integer, Map<Symbol, Action>> actionTable = generateActionTable(lr1Automaton);
        Map<Integer, Map<Symbol, Integer>> gotoTable = generateGotoTable(lr1Automaton);

        Stack<Integer> stack = new Stack<>();
        stack.push(0); // Start with the initial state

        List<Symbol> input = new ArrayList<>(inputSymbols);
        input.add(Symbol.EOF); // Add the EOF symbol at the end of the input

        System.out.println("+-----+---------+----------------+------------+");
        System.out.println("| Step |  Stack   |     Input      | Action   |");
        System.out.println("+-----+---------+----------------+------------+");

        int step = 1;
        while (!input.isEmpty()) {
            int currentState = stack.peek();
            Symbol currentSymbol = input.get(0);

            Map<Symbol, Action> actions = actionTable.get(currentState);
            Action action = actions.get(currentSymbol);

            // Format the stack and input as strings without brackets
            String stackString = stack.toString().replaceAll("\\[|\\]", "");
            String inputString = input.stream().map(Symbol::getValue).collect(Collectors.joining(" "));

            System.out.printf("|  %-3d | %-8s | %-15s | %-7s |\n", step, stackString, inputString, action);

            if (action instanceof Shift) {
                stack.push(((Shift) action).getNextState());
                input.remove(0); // Consume the current symbol
            } else if (action instanceof Reduce) {
                ProductionRule rule = ((Reduce) action).getProductionRule();
                for (int i = 0; i < rule.getRightHandSide().size(); i++) {
                    stack.pop(); // Remove states for symbols on the right-hand side of the rule
                }
                currentState = stack.peek();
                stack.push(gotoTable.get(currentState).get(rule.getLeftHandSide()));
            } else if (action instanceof Accept) {
                System.out.println("Input accepted by the grammar.");
                return;
            } else {
                System.out.println("Error: No valid action found.");
                return;
            }

            step++;
            System.out.println("+-----+---------+----------------+-----------+");
        }

        System.out.println("Error: Input not accepted. End of input reached without accept action.");
    }
}
