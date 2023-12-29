package org.example;

public class Reduce extends Action {
    private ProductionRule productionRule;
    private int index;

    public Reduce(ProductionRule productionRule, int index) {
        this.productionRule = productionRule;
        this.index = index;
    }

    public ProductionRule getProductionRule() {
        return productionRule;
    }

    @Override
    public String toString() {
        return "r" + index;
    }
}