package org.example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for parsing an input string based on the grammar, action table, and goto table.
 */
public class Parser {
    private final TableGenerator tableGenerator; // The table generator used to generate the action and goto tables
    private final LR1AutomatonBuilder lr1AutomatonBuilder; // The LR(1) automaton builder used to build the LR(1) automaton
    private Grammar grammar; // The grammar used for parsing

    /**
     * Constructor for the Parser class.
     *
     * @param grammar             The grammar used for parsing
     * @param tableGenerator      The table generator used to generate the action and goto tables
     * @param lr1AutomatonBuilder The LR(1) automaton builder used to build the LR(1) automaton
     */
    public Parser(Grammar grammar, TableGenerator tableGenerator, LR1AutomatonBuilder lr1AutomatonBuilder) {
        this.grammar = grammar;
        this.tableGenerator = tableGenerator;
        this.lr1AutomatonBuilder = lr1AutomatonBuilder;
    }

    /**
     * This method parses an input string based on the grammar, action table, and goto table.
     *
     * @param inputSymbols The input string represented as a list of symbols
     */
    public void parseInput(List<Symbol> inputSymbols) {
        // Build the LR(1) automaton and generate the action and goto tables
        Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton = lr1AutomatonBuilder.buildLR1Automaton();
        Map<Integer, Map<Symbol, Action>> actionTable = tableGenerator.generateActionTable(lr1Automaton);
        Map<Integer, Map<Symbol, Integer>> gotoTable = tableGenerator.generateGotoTable(lr1Automaton);

        // Initialize the stack with the initial state
        Stack<Integer> stack = new Stack<>();
        stack.push(0);

        // Add the end of file symbol to the end of the input
        List<Symbol> input = new ArrayList<>(inputSymbols);
        input.add(Symbol.EOF);

        // Print the header of the parsing table
        System.out.println("+-----+---------+----------------+------------+");
        System.out.println("| Step |  Stack   |     Input      | Action   |");
        System.out.println("+-----+---------+----------------+------------+");

        int step = 1;
        while (!input.isEmpty()) {
            // Get the current state and symbol
            int currentState = stack.peek();
            Symbol currentSymbol = input.get(0);

            // Get the action for the current state and symbol
            Map<Symbol, Action> actions = actionTable.get(currentState);
            Action action = actions.get(currentSymbol);

            // Print the current step, stack, input, and action
            String stackString = stack.toString().replaceAll("\\[|\\]", "");
            String inputString = input.stream().map(Symbol::getValue).collect(Collectors.joining(" "));
            System.out.printf("|  %-3d | %-8s | %-15s | %-7s |\n", step, stackString, inputString, action);

            // Perform the action
            if (action instanceof Shift) {
                // For a shift action, push the next state onto the stack and consume the current symbol
                stack.push(((Shift) action).getNextState());
                input.remove(0);
            } else if (action instanceof Reduce) {
                // For a reduce action, pop states from the stack and push the goto state
                ProductionRule rule = ((Reduce) action).getProductionRule();
                for (int i = 0; i < rule.getRightHandSide().size(); i++) {
                    stack.pop();
                }
                currentState = stack.peek();
                stack.push(gotoTable.get(currentState).get(rule.getLeftHandSide()));
            } else if (action instanceof Accept) {
                // For an accept action, print a message and return
                System.out.println("Input accepted by the grammar.");
                return;
            } else {
                // If there is no valid action, print an error message and return
                System.out.println("Error: No valid action found.");
                return;
            }

            step++;
            System.out.println("+-----+---------+----------------+-----------+");
        }

        // If the end of the input is reached without an accept action, print an error message
        System.out.println("Error: Input not accepted. End of input reached without accept action.");
    }
}