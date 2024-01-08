package org.example;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents an LR(1) item, which is a production rule with a position (dot) and a set of lookahead symbols.
 */
public class LR1Item {
    private ProductionRule productionRule; // The production rule of the LR(1) item
    private int position; // The position of the dot in the production rule
    private Set<Symbol> lookaheads; // The set of lookahead symbols

    /**
     * Constructor for the LR1Item class.
     *
     * @param productionRule The production rule of the LR(1) item
     * @param position       The position of the dot in the production rule
     * @param lookaheads     The set of lookahead symbols
     */
    public LR1Item(ProductionRule productionRule, int position, Set<Symbol> lookaheads) {
        this.productionRule = productionRule;
        this.position = position;
        this.lookaheads = lookaheads;
    }

    /**
     * This method returns the production rule of the LR(1) item.
     *
     * @return The production rule of the LR(1) item
     */
    public ProductionRule getProductionRule() {
        return productionRule;
    }

    /**
     * This method returns the position of the dot in the production rule.
     *
     * @return The position of the dot in the production rule
     */
    public int getPosition() {
        return position;
    }

    /**
     * This method returns the set of lookahead symbols.
     *
     * @return The set of lookahead symbols
     */
    public Set<Symbol> getLookaheads() {
        return lookaheads;
    }

    /**
     * This method returns the symbol after the dot in the production rule.
     *
     * @return The symbol after the dot in the production rule, or null if the dot is at the end of the production rule
     */
    public Symbol getNextSymbol() {
        if (position < productionRule.getRightHandSide().size()) {
            return productionRule.getRightHandSide().get(position);
        }
        return null;
    }

    /**
     * This method checks if this LR(1) item is equal to another object.
     *
     * @param obj The object to be compared with this LR(1) item
     * @return true if the object is an LR(1) item and has the same production rule, position, and lookahead symbols as this LR(1) item, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LR1Item lr1Item = (LR1Item) obj;
        return position == lr1Item.position &&
                productionRule.equals(lr1Item.productionRule) &&
                lookaheads.equals(lr1Item.lookaheads);
    }

    /**
     * This method returns the hash code of this LR(1) item.
     *
     * @return The hash code of this LR(1) item
     */
    @Override
    public int hashCode() {
        return Objects.hash(productionRule, position, lookaheads);
    }

    /**
     * This method returns a string representation of this LR(1) item.
     *
     * @return A string representation of this LR(1) item
     */
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