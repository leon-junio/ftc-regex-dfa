package com.boisbarganhados.ftc.regex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.boisbarganhados.ftc.dfa.DFA;
import com.boisbarganhados.ftc.dfa.DFAState;
import com.boisbarganhados.ftc.dfa.RegexDFElement;
import com.boisbarganhados.ftc.regex.records.DFABody;

public final class RegexUtils {

    public static final char EXPANSION = '\\';
    public static final int CUT_KEY_SIZE = 2;

    /**
     * Simulate the DFA with the given sentences and print the results.
     * 
     * @param regexDfa  The DFA to be simulated.
     * @param sentences The sentences to be tested.
     * @throws Exception If an error occurs while simulating the DFA.
     */
    public static void simulateDFA(RegexDFElement regexDfa, List<String> sentences) throws Exception {
        if (sentences == null || sentences.isEmpty())
            throw new Exception("Sentences list cannot be null or empty.");
        sentences.forEach(sentence -> {
            try {
                if (regexDfa.simulate(sentence)) {
                    System.out.println("Accepted by DFA: " + sentence);
                } else
                    System.out.println("Rejected by DFA: " + sentence);
            } catch (Exception e) {
                System.err.println("Error while simulating DFA: " + e.getMessage());
            }
        });
    }

    /**
     * Read the regex from the given file.
     * 
     * @param path The path to the file containing the regex.
     * @return The regex read from the file.
     * @throws Exception If an error occurs while reading the regex.
     */
    public static String readRegex(String path) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
            if (reader.ready())
                return reader.readLine();
            else
                throw new Exception("Regex file is empty.");
        } catch (Exception e) {
            System.err.println("Error while reading regex: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Read the sentences from the given file.
     * 
     * @param path The path to the file containing the sentences.
     * @return The sentences read from the file.
     * @throws Exception If an error occurs while reading the sentences.
     */
    public static List<String> readSentences(String path) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
            if (reader.ready())
                return reader.lines().toList();
            else
                throw new Exception("Sentences file is empty.");
        } catch (Exception e) {
            System.err.println("Error while reading sentences: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get the transition that is expandable in the given map.
     * 
     * @param transitionMap The map containing the transitions.
     * @return String The expandable transition.
     */
    public static String findNextSymbol(HashMap<String, List<Integer>> transitionMap) {
        return transitionMap.keySet().stream()
                .filter(key -> key.length() > RegexUtils.CUT_KEY_SIZE - 1
                        && !(key.length() == RegexUtils.CUT_KEY_SIZE && key.charAt(0) == RegexUtils.EXPANSION))
                .findFirst().orElse(null);
    }

    public static DFA parseToJFlapDFA(RegexDFElement regexDfa) {
        var dfa = new DFA();
        var states = new HashSet<Integer>();
        states.add(regexDfa.getInitialState());
        regexDfa.getTransitions().forEach(transition -> {
            transition.keySet().forEach(key -> {
                transition.get(key).forEach(state -> {
                    states.add(state);
                });
            });
        });
        states.forEach(state -> {
            var dfaState = new DFAState(state);
            if (regexDfa.getFinalStates().contains(state)) {
                dfaState.setFinalState(true);
            }
            if (regexDfa.getInitialState() == state) {
                dfaState.setInitialState(true);
            }
            dfa.addState(dfaState);
        });
        var transitions = regexDfa.getTransitions();
        for (int i = 0; i < transitions.size(); i++) {
            var transition = transitions.get(i);
            var index = i;
            transition.keySet().forEach(key -> {
                transition.get(key).forEach(state -> {
                    dfa.addTransition(index, state, key);
                });
            });
        }
        return dfa;
    }

    /**
     * Convert a NFA to a DFA created with Thompson's algorithm.
     * 
     * @param targetNfaStructure The NFA to be converted.
     * @return The DFA equivalent to the NFA.
     */
    public static RegexDFElement convertToDeterministic(RegexDFElement targetNfaStructure) {
        var targetDfaStructure = RegexDFElement.builder().alphabetSet(targetNfaStructure.getAlphabetSet())
                .finalStates(new HashSet<>())
                .transitions(new ArrayList<>()).deterministic(true).build();
        var dfaBody = new DFABody(new HashMap<>(), new ArrayList<>(), new ArrayList<>());
        dfaBody.initialStates().add(targetNfaStructure.getInitialState());
        dfaBody.createdStates().put(dfaBody.initialStates(), targetDfaStructure.getTransitionsTotal());
        if (targetNfaStructure.getFinalStates().contains(targetNfaStructure.getInitialState())) {
            targetDfaStructure.getFinalStates().add(targetDfaStructure.getTransitionsTotal());
        }
        targetDfaStructure.getTransitions().add(new HashMap<String, List<Integer>>());
        dfaBody.processWaitList().add(dfaBody.initialStates());
        while (dfaBody.processWaitList().size() > 0) {
            iterateOverNFA(targetNfaStructure, targetDfaStructure, dfaBody);
        }
        targetDfaStructure.setDeterministic(true);
        return targetDfaStructure;
    }

    /**
     * Iterate over the NFA to convert it to a DFA.
     * 
     * @param targetNfaStructure The NFA structure to be converted.
     * @param targetDfaStructure The DFA structure to be created.
     * @param dfaBody            The DFA body to be used.
     */
    private static void iterateOverNFA(RegexDFElement targetNfaStructure, RegexDFElement targetDfaStructure,
            DFABody dfaBody) {
        var processWaitingList = dfaBody.processWaitList().get(0);
        dfaBody.processWaitList().remove(0);
        var targetState = dfaBody.createdStates().get(processWaitingList);
        var alphabetUsed = new HashSet<String>();
        processWaitingList.forEach(statesTransition -> {
            targetNfaStructure.getTransitions().get(statesTransition).keySet().forEach(key -> {
                alphabetUsed.add(key);
            });
        });
        RegexParser.alphabetTransitionsConversor(
                targetNfaStructure,
                targetDfaStructure,
                dfaBody,
                targetState,
                alphabetUsed,
                processWaitingList);
    }
}