package com.boisbarganhados.ftc.regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.boisbarganhados.ftc.dfa.RegexDFElement;
import com.boisbarganhados.ftc.regex.records.LambdaBody;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LambdaSolver {

    private final RegexDFElement dfElement;

    private static LambdaSolver instance;

    /**
     * Get the instance of the LambdaSolver class
     * 
     * @param dfElement RegexDFElement to be solved
     * @return LambdaSolver instance of the class
     * @throws Exception if dfElement is null
     */
    public static LambdaSolver getInstance(RegexDFElement dfElement) throws Exception {
        if (dfElement == null) {
            throw new Exception("dfElement cannot be null");
        }
        if (instance == null) {
            instance = new LambdaSolver(dfElement);
        }
        return instance;
    }

    /**
     * Solve the lambda transitions of the regex element
     * 
     * @param fromStateId From state id of the lambda mapping
     * @param dfElement   Regex element to be solved
     * @param lambdaBody  Lambda body to be solved
     * @return Set<Integer> Set of integers representing the lambda mapping
     * @throws Exception
     */
    public Set<Integer> lambdaMapping(int fromStateId, LambdaBody lambdaBody) {
        if (dfElement == null) {
            throw new IllegalArgumentException("dfElement cannot be null");
        }
        var resultantStates = dfElement.getTransitions().get(fromStateId).get("λ") == null ? new ArrayList<Integer>()
                : dfElement.getTransitions().get(fromStateId).get("λ");
        Set<Integer> lambdaMappingSet = new HashSet<>();
        lambdaBody.mappedVisited().put(fromStateId, true);
        solver(resultantStates, fromStateId, lambdaBody, lambdaMappingSet);
        mapElements(lambdaMappingSet, fromStateId);
        lambdaMappingSet.addAll(resultantStates);
        lambdaMappingSet.add(fromStateId);
        lambdaBody.mappedStates().put(fromStateId, lambdaMappingSet);
        return lambdaMappingSet;
    }

    /**
     * Map the elements of the lambdaMappingSet to the finalStates of the
     * dfElement
     * 
     * @param lambdaMappingSet lambda mapping set
     * @param dfElement        regex element
     * @param fromStateId      from state id of the lambda mapping
     */
    private void mapElements(Set<Integer> lambdaMappingSet, int fromStateId) {
        lambdaMappingSet.stream().filter(state -> {
            return dfElement.getFinalStates().contains(state);
        }).findFirst().ifPresent(state -> {
            dfElement.getFinalStates().add(fromStateId);
        });
    }

    /**
     * Solve the lambda transitions of the regex element recursively
     * 
     * @param resultantStates  the resultant states set of the lambda mapping
     * @param fromStateId      the from state id of the lambda mapping
     * @param lambdaBody       the lambda body to be solved
     * @param lambdaMappingSet the lambda mapping set
     */
    private void solver(List<Integer> resultantStates, int fromStateId,
            LambdaBody lambdaBody, Set<Integer> lambdaMappingSet) {
        resultantStates.forEach(checkStateTarget -> {
            if (checkStateTarget < fromStateId && lambdaBody.mappedStates().get(checkStateTarget) != null) {
                lambdaMappingSet.addAll(lambdaBody.mappedStates().get(checkStateTarget));
            } else if (lambdaBody.mappedVisited().get(checkStateTarget) == null) {
                var state = lambdaMapping(checkStateTarget, lambdaBody);
                lambdaMappingSet.addAll(state);
            }
        });
    }

    /**
     * Remove lambda transitions from the given Target RegexDF that is a NFA.
     * 
     * @param targetRegexDf
     * 
     * @return void
     */
    public static void removeLambda(RegexDFElement targetRegexDf) throws Exception {
        if (targetRegexDf == null) {
            throw new Exception("Target RegexDF cannot be null.");
        }
        if (targetRegexDf.isDeterministic()) {
            return;
        }
        var lambdaBody = new LambdaBody(new HashMap<>(), new HashMap<>());
        for (int i = 0; i < targetRegexDf.getTransitionsTotal(); i++) {
            lambdaBody.mappedStates().put(i, LambdaSolver.getInstance(targetRegexDf).lambdaMapping(i, lambdaBody));
            targetRegexDf.getTransitions().get(i).remove("λ");
            lambdaBody.mappedVisited().clear();
        }
        targetRegexDf.getAlphabetSet().forEach(word -> {
            for (int i = 0; i < targetRegexDf.getTransitionsTotal(); i++) {
                if (lambdaBody.mappedStates().get(i) == null) {
                    continue;
                }
                iterateOverLambdaTransitions(targetRegexDf, lambdaBody, i, word);
            }
        });
    }

    /**
     * Iterate over the transitions of the targetRegexDf.
     * 
     * @param targetRegexDf target regexDf or element to be solved
     * @param lambdaBody    lambda body to be solved
     * @param index         index of the transition
     * @param word          string to be solved
     * 
     * @return void
     */
    private static void iterateOverLambdaTransitions(RegexDFElement targetRegexDf, LambdaBody lambdaBody, int index,
            String word) {
        lambdaBody.mappedStates().get(index).forEach(mappedStateTarget -> {
            if (mappedStateTarget == index) {
                return;
            }
            var checkingStates = targetRegexDf.getTransitions().get(mappedStateTarget).get(word);
            if (checkingStates == null) {
                return;
            }
            checkingStates.forEach(state -> {
                updateTransitions(state, targetRegexDf, index, word, lambdaBody);
            });
        });
    }

    /**
     * Update the transitions of the targetRegexDf.
     * 
     * @param state         state to be updated
     * @param targetRegexDf target regexDf or element to be solved
     * @param index         index of the transition
     * @param word          string to be solved
     * @param lambdaBody    lambda body to be solved
     * 
     * @return void
     */
    private static void updateTransitions(Integer state, RegexDFElement targetRegexDf, int index, String word,
            LambdaBody lambdaBody) {
        targetRegexDf.addNewTransition(index, state, word);
        if (lambdaBody.mappedStates().get(state) == null) {
            return;
        }
        lambdaBody.mappedStates().get(state).forEach(lambdaCheckup -> {
            var occurence = true;
            if ((occurence && targetRegexDf.getFinalStates().contains(lambdaCheckup))
                    || !(targetRegexDf.getTransitions().get(lambdaCheckup).keySet().contains("λ")
                            && targetRegexDf.getTransitions().get(lambdaCheckup).keySet().size() == 1)) {
                targetRegexDf.addNewTransition(index, lambdaCheckup, word);
                occurence = false;
            }
        });
    }
}
