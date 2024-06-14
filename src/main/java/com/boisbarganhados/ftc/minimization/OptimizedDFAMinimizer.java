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

public class OptimizedDFAMinimizer {

  /**
   * Minimizes a Deterministic Finite Automaton (DFA) mapping the resulting states
   * to their corresponding minimized states.
   *
   * @param dfa The DFA to be minimized.
   * @return The minimized DFA.
   */
  public static DFA minimizeDFA(DFA dfa) {
    Set<String> inputSymbols = new HashSet<>();
    dfa.getStates().forEach(state -> {
      state.getTransitions().keySet().forEach(symbol -> {
        inputSymbols.add(symbol);
      });
    });
    System.out.println("Input symbols: " + inputSymbols);

    long start = System.currentTimeMillis();
    // Initialize the partition with the final and non-final states
    Map<Set<DFAState>, Set<DFAState>> partition = new HashMap<>();
    Set<DFAState> finalStates = new HashSet<>();
    Set<DFAState> nonFinalStates = new HashSet<>();
    dfa.getStates().forEach(state -> {
      if (state.isFinalState())
        finalStates.add(state);
      else
        nonFinalStates.add(state);
    });
    partition.put(finalStates, finalStates);
    partition.put(nonFinalStates, nonFinalStates);

    // Refine the partition until it remains the same
    Map<Set<DFAState>, Set<DFAState>> newPartition = new HashMap<>();
    while (!partition.equals(newPartition)) {
      newPartition = new HashMap<>(partition);
      partition = refinePartition(partition, inputSymbols);
    }

    long end = System.currentTimeMillis();

    // Map original states to their corresponding minimized states
    List<DFAState> minimizedStates = new ArrayList<>();
    Map<DFAState, DFAState> stateMap = new HashMap<>();
    partition.forEach((key, value) -> {
      var representativeState = key.iterator().next(); // Pick any state from the group
      minimizedStates.add(representativeState);
      value.forEach(state -> {
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
            transitionState != null ? new HashSet<>(Collections.singletonList(transitionState)) : new HashSet<>());
      });
      state.setTransitions(transitions);
    });

    System.out.println("Minimized DFA: " + minimizedStates.size() + " states");
    System.out.println("Minimization time: " + (end - start) + "ms");
    return new DFA(minimizedStates);
  }

  /**
   * Refines the partition of states based on the input symbols.
   *
   * @param partition    The current partition of states.
   * @param inputSymbols The set of input symbols.
   * @return The refined partition of states.
   */
  private static Map<Set<DFAState>, Set<DFAState>> refinePartition(Map<Set<DFAState>, Set<DFAState>> partition,
      Set<String> inputSymbols) {
    Map<Set<DFAState>, Set<DFAState>> newPartition = new HashMap<>();

    // Iterate over each existing partition
    for (Set<DFAState> group : partition.keySet()) {
      Set<DFAState> equivalentStates = new HashSet<>();
      Set<DFAState> nonEquivalentStates = new HashSet<>();

      // Check each state in the group
      for (DFAState state : group) {
        boolean isEquivalent = true;

        // Compare transitions for each input symbol
        for (String symbol : inputSymbols) {
          DFAState transitionState = state.getTransitionState(symbol);
          if (transitionState != null) {
            // Get the equivalent state from the previous partition
            DFAState equivalentTransitionState = null;
            for (Set<DFAState> equivalentGroup : partition.values()) {
              if (equivalentGroup.contains(transitionState)) {
                equivalentTransitionState = equivalentGroup.iterator().next();
                break;
              }
            }

            // Check if the equivalent state is in the same group
            if (!group.contains(equivalentTransitionState)) {
              isEquivalent = false;
              break;
            }
          }
        }

        // Add the state to the appropriate set
        if (isEquivalent) {
          equivalentStates.add(state);
        } else {
          nonEquivalentStates.add(state);
        }
      }

      // Update the new partition
      if (!equivalentStates.isEmpty()) {
        newPartition.put(equivalentStates, equivalentStates);
      }
      if (!nonEquivalentStates.isEmpty()) {
        newPartition.put(nonEquivalentStates, nonEquivalentStates);
      }
    }

    return newPartition;
  }

}
