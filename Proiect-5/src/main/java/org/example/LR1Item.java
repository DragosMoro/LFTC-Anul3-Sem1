package org.example;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LR1Item {
    private ProductionRule productionRule;
    private int position;
    private Set<Symbol> lookaheads;

    public LR1Item(ProductionRule productionRule, int position, Set<Symbol> lookaheads) {
        this.productionRule = productionRule;
        this.position = position;
        this.lookaheads = lookaheads;
    }

    public ProductionRule getProductionRule() {
        return productionRule;
    }

    public int getPosition() {
        return position;
    }

    public Set<Symbol> getLookaheads() {
        return lookaheads;
    }

    public Symbol getNextSymbol() {
        if (position < productionRule.getRightHandSide().size()) {
            return productionRule.getRightHandSide().get(position);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LR1Item lr1Item = (LR1Item) obj;
        return position == lr1Item.position &&
                productionRule.equals(lr1Item.productionRule) &&
                lookaheads.equals(lr1Item.lookaheads);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productionRule, position, lookaheads);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(productionRule.getLeftHandSide()).append(" -> ");
        List<Symbol> rhs = productionRule.getRightHandSide();
        for (int i = 0; i < rhs.size(); i++) {
            if (i == position) {
                sb.append(".");
            }
            sb.append(rhs.get(i)).append(" ");
        }
        if (position == rhs.size()) {
            sb.append(".");
        }
        sb.append(", ").append(lookaheads.stream().map(Symbol::toString).collect(Collectors.joining(", ")));
        return sb.toString().trim();
    }


}
