package org.example;


import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private Grammar grammar;
    private TableGenerator tableGenerator;
    private LR1AutomatonBuilder lr1AutomatonBuilder;

    public Parser(Grammar grammar, TableGenerator tableGenerator, LR1AutomatonBuilder lr1AutomatonBuilder) {
        this.grammar = grammar;
        this.tableGenerator = tableGenerator;
        this.lr1AutomatonBuilder = lr1AutomatonBuilder;
    }

    public void parse(String input, Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton) {
        Map<Integer, Map<Symbol, Action>> actionTable = tableGenerator.generateActionTable(lr1Automaton);
        Map<Integer, Map<Symbol, Integer>> gotoTable = tableGenerator.generateGotoTable(lr1Automaton);

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
        Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton = lr1AutomatonBuilder.buildLR1Automaton();
        Map<Integer, Map<Symbol, Action>> actionTable = tableGenerator.generateActionTable(lr1Automaton);
        Map<Integer, Map<Symbol, Integer>> gotoTable = tableGenerator.generateGotoTable(lr1Automaton);

        Stack<Integer> stack = new Stack<>();
        stack.push(0); // Start with the initial state

        List<Symbol> input = new ArrayList<>(inputSymbols);
        input.add(Symbol.EOF);

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