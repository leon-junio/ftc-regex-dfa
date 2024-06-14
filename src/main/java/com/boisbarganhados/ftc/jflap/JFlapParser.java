package com.boisbarganhados.ftc.jflap;

import com.boisbarganhados.ftc.dfa.DFA;
import com.boisbarganhados.ftc.jflap.utils.Automaton;
import com.boisbarganhados.ftc.jflap.utils.Transition;

public class JFlapParser {

    /**
     * Parse a JFLAP automaton to a DFA internal representation 
     * @param jflapAutomaton JFLAP automaton
     * @return DFA - internal representation
     */
    public static DFA parse(Automaton jflapAutomaton) {
        var minimizationAt = new DFA();
        jflapAutomaton.getAutomaton().getStates().forEach(state -> {
            minimizationAt.getStates().add(state.toDfaState());
        });
        jflapAutomaton.getAutomaton().getTransitions().forEach(transition -> {
            var fromState = minimizationAt.getStates().get(transition.getFrom());
            var toState = minimizationAt.getStates().get(transition.getTo());
            fromState.put(transition.getRead(), toState);
        });
        return minimizationAt;
    }

    /**
     * Parse a DFA internal representation to a JFLAP automaton
     * @param dfaAutomaton DFA internal representation
     * @return Automaton - JFLAP automaton
     */ 
    public static Automaton parse(DFA dfaAutomaton) {
        var jflapAutomaton = new Automaton();
        dfaAutomaton.getStates().forEach(dfaState -> {
            jflapAutomaton.getAutomaton().getStates().add(dfaState.toState());
        });
        dfaAutomaton.getStates().forEach(dfaState -> {
            dfaState.getTransitions().forEach((key, value) -> {
                value.forEach(toState -> {
                    jflapAutomaton.getAutomaton().getTransitions()
                            .add(new Transition(dfaState.getId(), toState.getId(), key));
                });
            });
        });
        return jflapAutomaton;
    }

}