package com.boisbarganhados.ftc.dfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.boisbarganhados.ftc.regex.RegexParser;
import com.boisbarganhados.ftc.regex.RegexUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
public class RegexDFElement {

    private Set<String> alphabetSet;
    private Set<Integer> finalStates;

    private int initialState;
    private boolean deterministic;

    @EqualsAndHashCode.Exclude
    private List<HashMap<String, List<Integer>>> transitions;

    /**
     * Start the NFA structure with the initial and final states.
     * 
     * @param regex The regex to be used in the NFA.
     */
    public void startNfaStructure(String regex) throws Exception {
        if (isDeterministic()) {
            throw new Exception("Could not start a deterministic automaton with a regex.");
        }
        this.setAlphabetSet(RegexParser.getAlphabetFromRegex(regex));
        this.getTransitions().add(new HashMap<String, List<Integer>>());
        List<Integer> auxTargetStates = new ArrayList<Integer>();
        this.getTransitions().get(this.getInitialState()).put(regex, auxTargetStates);
        this.getTransitions().add(new HashMap<String, List<Integer>>());
        auxTargetStates.add(this.getTransitionsTotal() - 1);
        this.getFinalStates().add(this.getTransitionsTotal() - 1);
    }

    /**
     * Simulate the automaton with the given word.
     * 
     * @param wordStr The word to simulate the automaton.
     * @return True if the automaton accepts the word, false otherwise.
     * @throws Exception If the automaton is non-deterministic or any error occurs
     */
    public boolean simulate(String wordStr) throws Exception {
        if (!isDeterministic()) {
            throw new Exception("Could not simulate a non-deterministic automaton.");
        }
        if (wordStr.length() == 0) {
            return this.finalStates.contains(initialState);
        }
        var checkUpState = initialState;
        var symbol = "";
        for (int i = 0; i < wordStr.length(); i++) {
            if (wordStr.charAt(i) == RegexUtils.EXPANSION) {
                symbol = wordStr.substring(i, i + 2);
                i++;
            } else
                symbol = wordStr.substring(i, i + 1);
            var toState = this.transitions.get(checkUpState).get(symbol);
            if (toState == null || (Integer.valueOf(checkUpState = toState.get(0))) == null) {
                return false;
            }
        }
        return this.finalStates.contains(checkUpState);
    }

    /**
     * Add a new state to the automaton.
     * 
     * @param fromState   The state from which the transition starts.
     * @param stateSymbol The symbol that represents the state.
     * @return The index of the new state.
     */
    public int addNewState(int from, String stateSymbol) {
        this.transitions.add(new HashMap<String, List<Integer>>());
        addNewTransition(from, getTransitionsTotal() - 1, stateSymbol);
        return getTransitionsTotal() - 1;
    }

    /**
     * Add transitions to the finite automaton.
     * 
     * @param fromState The state from which the transition starts.
     * @param toState   The state to which the transition goes.
     * @param letter    The letter that represents the transition.
     */
    public void addNewTransition(int fromState, int toState, String letter) {
        var transition = transitions.get(fromState).get(letter);
        if (transition == null) {
            transition = new ArrayList<Integer>();
            transitions.get(fromState).put(letter, transition);
        }
        if (!transition.contains(toState))
            transition.add(toState);
    }

    /**
     * Get total number of transitions in the automaton.
     * 
     * @return The total number of transitions.
     */
    public int getTransitionsTotal() {
        if (transitions == null)
            return 0;
        return transitions.size();
    }
}