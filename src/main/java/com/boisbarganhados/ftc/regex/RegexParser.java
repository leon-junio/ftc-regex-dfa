package com.boisbarganhados.ftc.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.boisbarganhados.ftc.dfa.RegexDFElement;
import com.boisbarganhados.ftc.regex.records.DFABody;

import lombok.Data;

@Data
public final class RegexParser {

    public static final List<String> allowedOperators = List.of("*", "+", "(", ")");

    /**
     * Parse the regex and return the alphabet.
     * 
     * @param regexStr The regex to be parsed.
     * @return The alphabet of the regex.
     */
    public static Set<String> getAlphabetFromRegex(String regexStr) {
        var alphabetSet = new HashSet<String>();
        var wordCharList = new ArrayList<>(Arrays.asList(regexStr.split("")));
        wordCharList.forEach(charW -> {
            if (charW.charAt(0) == RegexUtils.EXPANSION) {
                alphabetSet.add(charW + String.valueOf(wordCharList.get(wordCharList.indexOf(charW) + 1)));
            } else if (!allowedOperators.contains(String.valueOf(charW)))
                alphabetSet.add(String.valueOf(charW.charAt(0)));
        });
        return alphabetSet;
    }

    /**
     * Find the parenthesis in the regex string to get the next word.
     * 
     * @param nextWord  The regex string to find the parenthesis.
     * @param wordIndex The index of the word to find the parenthesis.
     * @return The index of the parenthesis.
     */
    public static int findParenthesis(String nextWord, int wordIndex) {
        int acc = 1;
        while (acc != 0) {
            wordIndex++;
            acc += parenthesisValidator(nextWord.charAt(wordIndex)) == 1 ? +1
                    : parenthesisValidator(nextWord.charAt(wordIndex)) == 2 ? -1 : 0;
        }
        wordIndex++;
        return wordIndex;
    }

    /**
     * Resolve the group partition of new set of states
     * 
     * @param targetNfaStructure Target NFA structure to be resolved
     * @param statesGroup        New set of states to be resolved
     * @param symbol             Symbol to be resolved
     * @param processWaitingList List of states to be resolved
     */
    private static void resolveGroupPartition(RegexDFElement targetNfaStructure, List<Integer> statesGroup,
            String symbol,
            List<Integer> processWaitingList) {
        processWaitingList.forEach(process -> {
            var transitions = targetNfaStructure.getTransitions().get(process).get(symbol);
            if (transitions != null) {
                transitions.forEach(stateTarget -> {
                    if (!statesGroup.contains(stateTarget)) {
                        statesGroup.add(stateTarget);
                    }
                });
            }
        });
        if (statesGroup.isEmpty()) {
            return;
        }
        statesGroup.sort((a, b) -> {
            return Integer.compare(a, b);
        });
    }

    /**
     * Convert the alphabet of the NFA to the DFA, and remap all the transitions
     * with the new states.
     * 
     * @param targetNfaStructure The NFA structure to be converted.
     * @param targetDfaStructure The DFA structure to be created.
     * @param dfaBody            The DFA body to be used.
     * @param targetState        The target state to be converted.
     * @param alphabetUsed       The alphabet used in the conversion.
     * @param processWaitingList The list of states to be converted.
     * 
     * @return void
     */
    public static void alphabetTransitionsConversor(RegexDFElement targetNfaStructure,
            RegexDFElement targetDfaStructure,
            DFABody dfaBody, int targetState, Set<String> alphabetUsed, List<Integer> processWaitingList) {
        alphabetUsed.forEach(symbol -> {
            var statesGroup = new ArrayList<Integer>();
            RegexParser.resolveGroupPartition(targetNfaStructure, statesGroup, symbol, processWaitingList);
            if (!dfaBody.createdStates().containsKey(statesGroup)) {
                dfaBody.createdStates().put(statesGroup, targetDfaStructure.getTransitionsTotal());
                targetDfaStructure.getTransitions().add(new HashMap<String, List<Integer>>());
                statesGroup.forEach(chkState -> {
                    if (targetNfaStructure.getFinalStates().contains(chkState)) {
                        targetDfaStructure.getFinalStates().add(targetDfaStructure.getTransitionsTotal() - 1);
                        return;
                    }
                });
                dfaBody.processWaitList().add(statesGroup);
            }
            var stateIndex = dfaBody.createdStates().get(statesGroup);
            if (targetDfaStructure.getTransitions().get(targetState).get(symbol) == null) {
                targetDfaStructure.getTransitions().get(targetState).put(symbol, new ArrayList<Integer>());
            }
            targetDfaStructure.getTransitions().get(targetState).get(symbol).add(stateIndex);
        });
    }

    /**
     * Validate the parenthesis in the regex string.
     * 
     * @param charW The character to be validated.
     * @return int number 1- open parenthesis, 2- close parenthesis, 3- other
     *         character.
     */
    private static int parenthesisValidator(char charW) {
        if (charW == '(') {
            return 1;
        } else if (charW == ')') {
            return 2;
        } else {
            return 3;
        }
    }

}