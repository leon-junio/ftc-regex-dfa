package com.boisbarganhados.ftc.dfa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.boisbarganhados.ftc.jflap.utils.State;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class DFAState {
    private int id;
    private List<Integer> ids;
    private String name;
    private boolean initialState, finalState;
    @EqualsAndHashCode.Exclude
    private Map<String, HashSet<DFAState>> transitions;

    private static final int UI_XY_FACTOR = 50;

    public DFAState(int i) {
        id = i;
        transitions = new HashMap<>();
    }

    /**
     * Add a transition to the state. 
     * @param key The input symbol.
     * @param value The state that the DFA transitions to for the given input symbol.
     */
    public void put(String key, DFAState value) {
        HashSet<DFAState> transitionsSet;
        if (transitions.containsKey(key)) {
            transitionsSet = transitions.get(key);
        } else {
            transitionsSet = new HashSet<>();
        }
        transitionsSet.add(value);
        transitions.put(key, transitionsSet);
    }

    /**
     * Returns the state that the DFA transitions to for a given input symbol.
     * (This method needs to be implemented in the DFAState class)
     *
     * @param symbol The input symbol.
     * @return The state reached after transitioning on the given symbol, or null if
     *         no transition exists.
     */
    public DFAState getTransitionState(String symbol) {
        HashSet<DFAState> possibleStates = transitions.get(symbol);
        return possibleStates != null && !possibleStates.isEmpty() ? possibleStates.iterator().next() : null;
    }

    /**
     * Return a State object with the same attributes as this DFAState.
     * @return A State object with the same attributes as this DFAState.
     */
    public State toState() {
        var state = new State();
        state.setId(this.getId());
        state.setName(this.getName());
        state.setStateInitial(this.isInitialState());
        state.setStateFinal(this.isFinalState());
        state.setX(this.getId() * UI_XY_FACTOR);
        state.setY(this.getId() * UI_XY_FACTOR);
        return state;
    }

    @Override
    public String toString() {
        return "DFAState{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", initialState=" + initialState +
                ", finalState=" + finalState +
                ", transitions=" + transitions.keySet() + " -- " + printTransitions() +
                '}';
    }

    /**
     * Print transitions in a more readable way for the toString method.
     * 
     * @return A string with the transitions.
     */
    private String printTransitions() {
        StringBuilder sb = new StringBuilder();
        transitions.forEach((key, value) -> {
            sb.append(key).append(" -> ");
            if (value.isEmpty())
                sb.append("[], ");
            else
                value.forEach(v -> sb.append(v.getId()).append(" (" + v.getName() + ")").append(", "));
        });
        return sb.toString();
    }
}