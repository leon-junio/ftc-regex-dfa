package com.boisbarganhados.ftc.regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.boisbarganhados.ftc.dfa.RegexDFElement;
import com.boisbarganhados.ftc.regex.records.ThompsonIteration;

/**
 * Thompson'nextWord algorithm implementation.
 * Thompson's construction is a way of transforming a regular expression into an
 * equivalent nondeterministic finite automaton (NFA).
 */
public final class Thompson {

    /**
     * Get the NFA from a regex string.
     * 
     * @param regex The regex to be converted to NFA with the Thompson's algorithm.
     * @return The NFA structure equivalent to the regex.
     * @throws Exception If the regex is invalid or any error occurs.
     */
    public static RegexDFElement getNfaFromRegex(String regex) throws Exception {
        RegexDFElement targetNfaStructure = RegexDFElement.builder().alphabetSet(new HashSet<>())
                .finalStates(new HashSet<>())
                .transitions(new ArrayList<>()).deterministic(false).build();
        targetNfaStructure.startNfaStructure(regex);
        for (int i = 0; i < targetNfaStructure.getTransitionsTotal(); i++) {
            var statesTransition = targetNfaStructure.getTransitions().get(i);
            iterateOverStates(targetNfaStructure, i, statesTransition,
                    RegexUtils.findNextSymbol(statesTransition));
        }
        LambdaSolver.removeLambda(targetNfaStructure);
        return targetNfaStructure;
    }

    /**
     * Iterate over the set of states to run the Thompson's iteration.
     * 
     * @param targetNfaStructure The NFA structure to run the iteration.
     * @param index              The index of the NFA structure.
     * @param statesTransition   The set of states to run the iteration.
     * @param nextWord           The next word to run the iteration.
     * @throws Exception If the operation is invalid or any error occurs.
     */
    private static void iterateOverStates(RegexDFElement targetNfaStructure, int index,
            HashMap<String, List<Integer>> statesTransition, String nextWord)
            throws Exception {
        while (nextWord != null) {
            var actualPosition = -1;
            var operationFinded = Character.MIN_VALUE;
            var result = runThompsonIteration(targetNfaStructure, index, statesTransition,
                    new ThompsonIteration(actualPosition, operationFinded, nextWord));
            nextWord = result.nextWord();
            actualPosition = result.actualPosition();
            operationFinded = result.actualCharacter();
            var operation = Operations.getOperationBySymbol(operationFinded);
            if (operation == null) {
                throw new Exception(
                        "Invalid operation or Regex could not be recognized. Operation: " + operationFinded);
            }
            operation.doOperation(targetNfaStructure, index, nextWord, actualPosition);
            nextWord = RegexUtils.findNextSymbol(statesTransition);
        }
    }

    /**
     * Run the Thompson's iteration over the set of states.
     * 
     * @param targetNfaStructure The NFA structure to run the iteration.
     * @param index              The index of the NFA structure.
     * @param statesTransition   The set of states to run the iteration.
     * @param nextWord           The next word to run the iteration.
     * @param thompsonIteration  The Thompson's iteration record
     * @return The Thompson's iteration record after the iteration.
     */
    private static ThompsonIteration runThompsonIteration(RegexDFElement targetNfaStructure, int index,
            HashMap<String, List<Integer>> statesTransition, ThompsonIteration thompsonIteration) {
        for (int wordIndex = 0; wordIndex < thompsonIteration.nextWord().length() - 1;) {
            if (thompsonIteration.nextWord().charAt(wordIndex) == RegexUtils.EXPANSION) {
                wordIndex += RegexUtils.CUT_KEY_SIZE;
            } else if (thompsonIteration.nextWord().charAt(wordIndex) == '(') {
                wordIndex = RegexParser.findParenthesis(thompsonIteration.nextWord(), wordIndex);
            } else {
                wordIndex++;
            }
            if (thompsonIteration.actualPosition() == -1 && wordIndex == thompsonIteration.nextWord().length()) {
                var statesSet = statesTransition.get(thompsonIteration.nextWord());
                statesTransition.remove(thompsonIteration.nextWord());
                thompsonIteration = new ThompsonIteration(thompsonIteration.actualPosition(),
                        thompsonIteration.actualCharacter(),
                        thompsonIteration.nextWord().substring(1, thompsonIteration.nextWord().length() - 1));
                var checkWord = thompsonIteration.nextWord();
                statesSet.forEach(state -> targetNfaStructure.addNewTransition(index, state, checkWord));
                wordIndex = 0;
                continue;
            }
            if (thompsonIteration.actualPosition() == -1
                    || Operations.getOperationBySymbol((thompsonIteration.nextWord().charAt(wordIndex)))
                            .getPriority() > Operations
                                    .getOperationBySymbol((thompsonIteration.actualCharacter())).getPriority()) {
                thompsonIteration = new ThompsonIteration(wordIndex, thompsonIteration.actualCharacter(),
                        thompsonIteration.nextWord());
                if (thompsonIteration.nextWord().charAt(wordIndex) != Operations.UNION.getSymbol().charAt(0)
                        && thompsonIteration.nextWord().charAt(wordIndex) != Operations.STAR.getSymbol().charAt(0)) {
                    thompsonIteration = new ThompsonIteration(thompsonIteration.actualPosition(),
                            Operations.CONCAT.getSymbol().charAt(0), thompsonIteration.nextWord());
                } else {
                    thompsonIteration = new ThompsonIteration(thompsonIteration.actualPosition(),
                            thompsonIteration.nextWord().charAt(wordIndex), thompsonIteration.nextWord());
                }
            }
        }
        return thompsonIteration;
    }
}