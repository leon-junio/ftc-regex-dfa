package com.boisbarganhados.ftc.dfa;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class DFA {
    private List<DFAState> states;

    public DFA(List<DFAState> states) {
        this.states = states;
    }

    public DFA() {
        this.states = new ArrayList<>();
    }

    public DFAState getInitialState() {
        return states.stream().filter(DFAState::isInitialState).findFirst().orElse(null);
    }

    public List<DFAState> getFinalStates() {
        return states.stream().filter(DFAState::isFinalState).collect(Collectors.toList());
    }

    public void addState(DFAState state) {
        states.add(state);
    }

    /**
     * Add a transition to the DFA.
     * 
     * @param from   The state from which the transition starts.
     * @param to     The state to which the DFA transitions to.
     * @param symbol The input symbol that triggers the transition.
     */
    public void addTransition(int from, int to, String symbol) {
        if (from >= states.size() || to >= states.size() || from < 0 || to < 0) {
            System.out.println("Invalid state index.");
            System.out.println("From: " + from + " To: " + to + " Symbol: " + symbol);
            return;
        }
        states.get(from).put(symbol, states.get(to));
    }

    /**
     * Simulate the DFA with the given input.
     * 
     * @param input The input to be simulated.
     * @return True if the DFA accepts the input, false otherwise.
     */
    public boolean runDFA(String input) {
        DFAState currentState = getInitialState();
        if (input.isBlank() || input.isEmpty()) {
            return currentState.isFinalState();
        }
        for (int i = 0; i < input.length(); i++) {
            String symbol = String.valueOf(input.charAt(i));
            currentState = currentState.getTransitionState(symbol);
            if (currentState == null || currentState.getId() < 0) {
                return false;
            }
        }
        return currentState.isFinalState();
    }

    /**
     * Create a DFA with a simple test case with nStates states.
     * 
     * @param nStates The number of states in the DFA.
     * @return A DFA with nStates states.
     */
    public static DFA generateDoubleStateTest(int nStates) {
        var states = new ArrayList<DFAState>();
        var initialState = new DFAState(0);
        var finalState = new DFAState(nStates - 1);
        initialState.setInitialState(true);
        initialState.setName("q0");
        finalState.setFinalState(true);
        finalState.setName("q" + (nStates - 1));
        states.add(initialState);
        for (int i = 1; i < nStates - 1; i++) {
            var state = new DFAState(i);
            state.setName("q" + i);
            states.add(state);
        }
        states.add(finalState);
        DFA dfa = new DFA(states);
        for (int i = 0; i < nStates - 1; i++) {
            if (i % 2 == 0) {
                dfa.addTransition(i, i + 1, "a");
                dfa.addTransition(i + 1, i, "a");
                dfa.addTransition(i, i + 2, "b");
            } else {
                dfa.addTransition(i, i + 1, "b");
            }
        }
        if (nStates % 2 == 0) {
            dfa.addTransition(nStates - 2, nStates - 1, "b");
        }
        dfa.addTransition(nStates - 1, nStates - 1, "a");
        dfa.addTransition(nStates - 1, nStates - 1, "b");
        return dfa;
    }
}