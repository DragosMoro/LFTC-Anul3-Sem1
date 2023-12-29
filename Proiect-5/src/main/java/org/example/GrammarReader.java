package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GrammarReader {
    private Grammar grammar;

    public GrammarReader(Grammar grammar) {
        this.grammar = grammar;
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
}