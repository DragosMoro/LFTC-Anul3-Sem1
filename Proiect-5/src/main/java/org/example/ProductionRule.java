package org.example;

import java.util.List;
import java.util.Objects;

public class ProductionRule {
    private Symbol leftHandSide;
    private List<Symbol> rightHandSide;

    public ProductionRule(Symbol leftHandSide, List<Symbol> rightHandSide) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
    }

    public Symbol getLeftHandSide() {
        return leftHandSide;
    }

    public List<Symbol> getRightHandSide() {
        return rightHandSide;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProductionRule that = (ProductionRule) obj;
        return leftHandSide.equals(that.leftHandSide) &&
                rightHandSide.equals(that.rightHandSide);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftHandSide, rightHandSide);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(leftHandSide).append(" -> ");
        for (Symbol symbol : rightHandSide) {
            sb.append(symbol).append(" ");
        }
        return sb.toString().trim();
    }
}
