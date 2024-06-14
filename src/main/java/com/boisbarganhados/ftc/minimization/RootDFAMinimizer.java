package com.boisbarganhados.ftc.minimization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.boisbarganhados.ftc.dfa.DFA;
import com.boisbarganhados.ftc.dfa.DFAState;

public class RootDFAMinimizer {

    /**
     * Minimizes a Deterministic Finite Automaton (DFA) mapping the resulting states
     * to their corresponding minimized states.
     *
     * @param dfa The DFA to be minimized.
     * @return The minimized DFA.
     */
    public static DFA minimizeDFA(DFA dfa) {
        long start = System.currentTimeMillis();
        Set<String> inputSymbols = new HashSet<>();
        dfa.getStates().forEach(state -> {
            state.getTransitions().keySet().forEach(symbol -> {
                inputSymbols.add(symbol);
            });
        });
        System.out.println("Input symbols: " + inputSymbols);
        var minimalPartition = startMinimization(new HashSet<>(dfa.getStates()), inputSymbols);
        List<DFAState> minimizedStates = new ArrayList<>();

        // Map original states to their corresponding minimized states
        Map<DFAState, DFAState> stateMap = new HashMap<>();
        minimalPartition.forEach(group -> {
            var representativeState = group.iterator().next(); // Pick any state from the group
            minimizedStates.add(representativeState);
            group.forEach(state -> {
                stateMap.put(state, representativeState);
            });
        });

        // Update transitions based on the minimized states
        minimizedStates.forEach(state -> {
            Map<String, HashSet<DFAState>> transitions = new HashMap<>();
            inputSymbols.forEach(symbol -> {
                var originalState = state;
                var transitionState = originalState.getTransitionState(symbol);
                if (transitionState != null) {
                    transitionState = stateMap.get(transitionState); // Get minimized equivalent
                }
                transitions.put(symbol,
                        transitionState != null ? new HashSet<>(Collections.singletonList(transitionState))
                                : new HashSet<>());
            });
            state.setTransitions(transitions);
        });
        long end = System.currentTimeMillis();
        System.out.println("Minimized DFA: " + minimizedStates.size() + " states");
        System.out.println("Minimization time: " + (end - start) + "ms");
        return new DFA(minimizedStates);
    }

    /**
     * Minimizes a Deterministic Finite Automaton (DFA) by partitioning its states.
     *
     * @param states       Set of all states in the DFA.
     * @param inputSymbols Set of all input symbols the DFA can recognize.
     * @return A set of sets representing the minimized partition of states.
     */
    private static Set<Set<DFAState>> startMinimization(Set<DFAState> states, Set<String> inputSymbols) {
        Set<Set<DFAState>> P0 = new HashSet<>();
        // Step 1: Partition states into final and non-final states
        Set<DFAState> finalStates = new HashSet<>();
        Set<DFAState> nonFinalStates = new HashSet<>();
        states.forEach(state -> {
            if (state.isFinalState())
                finalStates.add(state);
            else
                nonFinalStates.add(state);
        });
        if (!finalStates.isEmpty()) {
            P0.add(finalStates);
        }
        if (!nonFinalStates.isEmpty()) {
            P0.add(nonFinalStates);
        }
        Set<Set<DFAState>> Pk = new HashSet<>(P0);
        boolean changed;
        // Step 3: Refine partitions until no further refinement is possible
        do {
            changed = false;
            Set<Set<DFAState>> newPk = new HashSet<>();
            for (Set<DFAState> group : Pk) {
                Set<Set<DFAState>> subGroups = partition(group, Pk, inputSymbols);
                changed |= subGroups.size() > 1;
                newPk.addAll(subGroups);
            }
            Pk = newPk;
        } while (changed);
        return Pk;
    }

    /**
     * Partitions a group of states based on their transitions for all input
     * symbols.
     *
     * @param group        Set of states to be partitioned.
     * @param Pk           Current partition of the DFA states.
     * @param inputSymbols Set of all input symbols the DFA can recognize.
     * @return A set of sets representing the refined partition of the group.
     */
    private static Set<Set<DFAState>> partition(Set<DFAState> group, Set<Set<DFAState>> Pk, Set<String> inputSymbols) {
        if (group.size() <= 1) {
            return Collections.singleton(group); // No partitioning for single-state groups
        }
        Map<DFAState, Map<String, Set<DFAState>>> transitionMap = new HashMap<>();
        group.forEach(state -> {
            Map<String, Set<DFAState>> symbolToState = new HashMap<>();
            inputSymbols.forEach(symbol -> {
                DFAState transitionState = state.getTransitionState(symbol);
                if(transitionState == null) {
                    transitionState = state;
                }else{
                    symbolToState.put(symbol, findGroupForState(transitionState, Pk));
                }
            });
            transitionMap.put(state, symbolToState);
        });
        Set<Set<DFAState>> subGroups = new HashSet<>();
        while (!transitionMap.isEmpty()) {
            var iterator = transitionMap.keySet().iterator();
            var firstState = iterator.next();
            Set<DFAState> newGroup = new HashSet<>();
            newGroup.add(firstState);
            var firstTransitions = transitionMap.remove(firstState);
            iterator = transitionMap.keySet().iterator(); // Reset iterator after removal
            while (iterator.hasNext()) {
                DFAState nextState = iterator.next();
                if (firstTransitions.equals(transitionMap.get(nextState))) {
                    newGroup.add(nextState);
                    iterator.remove(); // Remove state with identical transitions
                }
            }
            subGroups.add(newGroup);
        }
        return subGroups;
    }

    /**
     * Finds the group in the partition Pk to which a given state belongs.
     *
     * @param state The state to find the group for.
     * @param Pk    The current partition of the DFA states.
     * @return The group in Pk that contains the given state, or an empty set if not
     *         found.
     */
    private static Set<DFAState> findGroupForState(DFAState state, Set<Set<DFAState>> Pk) {
        for (Set<DFAState> group : Pk) {
            if (group.contains(state)) {
                return group;
            }
        }
        System.out.println("State not found in partition: " + state);
        return Collections.emptySet();
    }
}
