package org.example;

import java.util.ArrayList;
import java.util.List;

public class Grammar {
    private final List<ProductionRule> productionRules;

    public Grammar(List<ProductionRule> productionRules) {
        this.productionRules = productionRules;
    }

    public List<ProductionRule> getProductionRules() {
        return productionRules;
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
}