package org.example;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        try {
            Grammar grammar = GrammarReader.readGrammarFromFile("src/main/resources/gramatica.txt");
            FirstSetCalculator firstSetCalculator = new FirstSetCalculator(grammar);
            LR1AutomatonBuilder lr1AutomatonBuilder = new LR1AutomatonBuilder(grammar, firstSetCalculator);
            TableGenerator tableGenerator = new TableGenerator(grammar, firstSetCalculator, lr1AutomatonBuilder);


            Printer printer = new Printer(grammar, tableGenerator, lr1AutomatonBuilder, firstSetCalculator);
            Parser parser = new Parser(grammar, tableGenerator, lr1AutomatonBuilder);


            firstSetCalculator.calculateFirstSets();

            Map<Set<LR1Item>, Map<Symbol, Set<LR1Item>>> lr1Automaton = lr1AutomatonBuilder.buildLR1Automaton();

            printer.printLR1Automaton(lr1Automaton);
            printer.printActionGotoTables(lr1Automaton);

            List<Symbol> inputSymbols = Arrays.asList(new Symbol("c"), new Symbol("d"), new Symbol("d"));

            parser.parseInput(inputSymbols);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}