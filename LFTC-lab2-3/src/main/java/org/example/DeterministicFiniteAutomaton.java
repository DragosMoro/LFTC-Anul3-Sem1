package org.example;

import java.io.*;
import java.util.*;


public class DeterministicFiniteAutomaton {

    public static class Transition {

        String inputSymbol;
        String nextState;

        public Transition(String inputSymbol, String nextState) {
            this.inputSymbol = inputSymbol;
            this.nextState = nextState;
        }

        public String getInputSymbol() {
            return inputSymbol;
        }

        public void setInputSymbol(String inputSymbol) {
            this.inputSymbol = inputSymbol;
        }

        public String getNextState() {
            return nextState;
        }

        public void setNextState(String nextState) {
            this.nextState = nextState;
        }
    }


    private Set<String> states;
    private List<String> alphabet;
    private Map<String, List<Transition>> transitions;
    private Set<String> finalStates;
    private String initialState;

    private boolean isDeterministic = true;

    public DeterministicFiniteAutomaton() {
        states = new LinkedHashSet<>();
        alphabet = new ArrayList<>();
        transitions = new LinkedHashMap<>();
        finalStates = new LinkedHashSet<>();
    }

    public void readDeterministicFiniteAutomaton(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean readingTransitions = false;
            boolean readingFinalStates = false;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                if (line.equals("States:")) {
                    readingTransitions = false;
                    readingFinalStates = false;
                } else if (line.equals("Alphabet:")) {
                    readingTransitions = false;
                    readingFinalStates = false;
                } else if (line.equals("Transitions:")) {
                    readingTransitions = true;
                    readingFinalStates = false;
                } else if (line.equals("Final States:")) {
                    readingTransitions = false;
                    readingFinalStates = true;
                } else if (line.startsWith("Initial State:")) {
                    readingTransitions = false;
                    readingFinalStates = false;
                } else if (readingTransitions) {
                    String[] parts = line.split("->");
                    String[] transition = parts[0].split(",");
                    String currentState = transition[0];
                    String inputSymbol = transition[1];
                    String nextState = parts[1];

                    if (!states.contains(currentState)) {
                        states.add(currentState);
                    }
                    if (!alphabet.contains(inputSymbol)) {
                        alphabet.add(inputSymbol);
                    }

                    if (!states.contains(nextState)) {
                        states.add(nextState);
                    }


                    if (transitions.containsKey(currentState) && transitions.get(currentState).stream()
                            .anyMatch(t -> t.getInputSymbol().equals(inputSymbol))) {
                        System.out.println("The NFA is not deterministic.");
                        isDeterministic = false;
                    }

                    if (!transitions.containsKey(currentState)) {
                        transitions.put(currentState, new ArrayList<>());
                    }
                    transitions.get(currentState).add(new Transition(inputSymbol, nextState));


                } else if (readingFinalStates) {
                    finalStates.add(line);
                } else {
                    initialState = line;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void displayStates() {
        System.out.println("Set of states: " + states);
    }

    public void displayAlphabet() {
        System.out.println("Alphabet: " + alphabet);
    }

    public void displayTransitions() {
        System.out.println("Transitions:");
        for (String currentState : transitions.keySet()) {
            for (Transition transition : transitions.get(currentState)) {
                System.out.println(currentState + "," + transition.getInputSymbol() + " -> " + transition.getNextState());
            }
        }
    }

    public void displayFinalStates() {
        System.out.println("Set of final states: " + finalStates);
    }

    public boolean verifySequence(String sequence) {
        String currentState = initialState;
        for (int i = 0; i < sequence.length(); i++) {
            String inputSymbol = String.valueOf(sequence.charAt(i));
            if (!alphabet.contains(inputSymbol) || !containsTransition(transitions.get(currentState), inputSymbol)) {
                return false;
            }
            currentState = getTransitionState(transitions.get(currentState), inputSymbol);
        }
        return finalStates.contains(currentState);
    }

    public String findLongestAcceptedPrefix(String sequence) {
        String prefix = "";
        String storeLatestAcceptedPrefix = "";
        String currentState = initialState;
        for (int i = 0; i < sequence.length(); i++) {
            String inputSymbol = String.valueOf(sequence.charAt(i));
            if (!alphabet.contains(inputSymbol) || !containsTransition(transitions.get(currentState), inputSymbol)) {
                break;
            }
            currentState = getTransitionState(transitions.get(currentState), inputSymbol);
            prefix += inputSymbol;
            if (finalStates.contains(currentState)) {
                storeLatestAcceptedPrefix = prefix;
            }
        }
        return storeLatestAcceptedPrefix;
    }

    // Helper method to check if a transition list contains a transition with a given input symbol
    private boolean containsTransition(List<Transition> transitions, String inputSymbol) {
        return transitions.stream().anyMatch(t -> t.getInputSymbol().equals(inputSymbol));
    }

    // Helper method to get the next state from a transition list for a given input symbol
    private String getTransitionState(List<Transition> transitions, String inputSymbol) {
        return transitions.stream()
                .filter(t -> t.getInputSymbol().equals(inputSymbol))
                .findFirst()
                .map(Transition::getNextState)
                .orElse(null);
    }


    public void readAndVerifySequenceFromConsole(BufferedReader reader) {

        String line;
        try {
            System.out.println("Enter a sequence for verification:");
            while ((line = reader.readLine()) != null) {
                try {
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (line.equals("exit")) {
                        break;
                    }
                    if (verifySequence(line)) {
                        System.out.println("The sequence is accepted by the automaton.");
                    } else {
                        System.out.println("The sequence is not accepted by the automaton.");
                        String prefix = findLongestAcceptedPrefix(line);
                        if (!prefix.isEmpty()) {
                            System.out.println("The longest accepted prefix is: " + prefix);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("An error occurred while processing user input: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error while reading from the console: " + e.getMessage());
        }
    }


    public boolean isDeterministic() {
        return isDeterministic;
    }

    public void readDeterministicFiniteAutomatonFromConsole(BufferedReader reader) {
        String line;
        boolean readingTransitions = false;
        boolean readingFinalStates = false;

        int numStates = 0;
        int numAlphabetSymbols = 0;

        try {
            System.out.println("Enter the number of states:");
            while ((line = reader.readLine()) != null) {
                try {
                    numStates = Integer.parseInt(line);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }

            // Allocate space for states
            for (int i = 0; i < numStates; i++) {
                System.out.println("Enter state " + (i + 1) + ":");
                states.add(reader.readLine());
            }

            System.out.println("Enter the number of alphabet symbols:");
            while ((line = reader.readLine()) != null) {
                try {
                    numAlphabetSymbols = Integer.parseInt(line);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }

            // Allocate space for alphabet symbols
            for (int i = 0; i < numAlphabetSymbols; i++) {
                System.out.println("Enter alphabet symbol " + (i + 1) + ":");
                alphabet.add(reader.readLine());
            }

            // Continue with reading transitions
            System.out.println("Enter transitions (format: currentState,inputSymbol->nextState):");
            System.out.println("Enter an empty line to stop.");

            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] parts = line.split("->");
                String[] transition = parts[0].split(",");
                String currentState = transition[0];
                String inputSymbol = transition[1];
                String nextState = parts[1];

                if (!states.contains(currentState)) {
                    System.out.println("Invalid state: " + currentState);
                    continue;
                }
                if (!alphabet.contains(inputSymbol)) {
                    System.out.println("Invalid alphabet symbol: " + inputSymbol);
                    continue;
                }
                if (!states.contains(nextState)) {
                    System.out.println("Invalid next state: " + nextState);
                    continue;
                }

                if (!transitions.containsKey(currentState)) {
                    transitions.put(currentState, new ArrayList<>());
                }
                transitions.get(currentState).add(new Transition(inputSymbol, nextState));
            }

            // Continue with reading final states
            System.out.println("Enter final states:");
            System.out.println("Enter an empty line to stop.");

            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (!states.contains(line)) {
                    System.out.println("Invalid final state: " + line);
                    continue;
                }
                finalStates.add(line);
            }

            // Read initial state
            System.out.println("Enter the initial state:");
            while ((line = reader.readLine()) != null) {
                if (!states.contains(line)) {
                    System.out.println("Invalid initial state: " + line);
                } else {
                    initialState = line;
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error while reading from the console: " + e.getMessage());
        }
    }


}
