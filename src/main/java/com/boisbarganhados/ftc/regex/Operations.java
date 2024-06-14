package com.boisbarganhados.ftc.regex;

import com.boisbarganhados.ftc.dfa.RegexDFElement;

import lombok.Getter;

@Getter
public enum Operations {

    UNION("union", "+", 2),
    STAR("star", "*", 0),
    CONCAT("concat", ".", 1),
    ALPHABET("alphabet", "", 1);

    private final String name;
    private final String symbol;
    private final int priority;

    private Operations(String name, String symbol, int priority) {
        this.name = name;
        this.symbol = symbol;
        this.priority = priority;
    }

    /**
     * Perform the operation in the regex element.
     * 
     * @param regexNonDetElement The regex element to perform the operation.
     * @param stateIndex         The index of the state to perform the operation.
     * @param symbolStr          The symbol to perform the operation.
     * @param index              The index of the symbol to perform the operation.
     * @return The regex element after the operation.
     * @throws Exception
     */
    public void doOperation(RegexDFElement regexNonDetElement, int stateIndex, String symbolStr, int index)
            throws Exception {
        int[] intermediateStates = new int[4];
        var states = regexNonDetElement.getTransitions().get(stateIndex).get(symbolStr);
        regexNonDetElement.getTransitions().get(stateIndex).remove(symbolStr);
        intermediateStates[0] = regexNonDetElement.addNewState(stateIndex, "λ");
        if (this.symbol == "+") {
            intermediateStates[1] = regexNonDetElement.addNewState(stateIndex, "λ");
            intermediateStates[2] = regexNonDetElement.addNewState(intermediateStates[0],
                    symbolStr.substring(0, index));
            intermediateStates[3] = regexNonDetElement.addNewState(intermediateStates[1],
                    symbolStr.substring(index + 1));
        } else if (this.symbol == "*") {
            intermediateStates[1] = regexNonDetElement.addNewState(intermediateStates[0],
                    symbolStr.substring(0, index));
        } else if (this.symbol == ".") {
            intermediateStates[1] = regexNonDetElement.addNewState(intermediateStates[0],
                    symbolStr.substring(0, index));
            intermediateStates[2] = regexNonDetElement.addNewState(intermediateStates[1], "λ");
            intermediateStates[3] = regexNonDetElement.addNewState(intermediateStates[2], symbolStr.substring(index));
        } else {
            throw new Exception(
                    "Invalid operation or Regex could not be recognized. Operation: " + this.symbol);
        }
        states.forEach(state -> {
            if (this.symbol == "+") {
                regexNonDetElement.addNewTransition(intermediateStates[2], state, "λ");
                regexNonDetElement.addNewTransition(intermediateStates[3], state, "λ");
            } else if (this.symbol == "*") {
                regexNonDetElement.addNewTransition(stateIndex, state, "λ");
                regexNonDetElement.addNewTransition(intermediateStates[1], state, "λ");
                regexNonDetElement.addNewTransition(state, stateIndex, "λ");
            } else if (this.symbol == ".") {
                regexNonDetElement.addNewTransition(intermediateStates[3], state, "λ");
            }
        });
    }

    /**
     * Get the operation by the symbol used in the regex.
     * 
     * @param symbol The symbol used in the regex.
     * @return The operation that corresponds to the symbol.
     */
    public static Operations getOperationBySymbol(char symbol) {
        for (Operations operation : Operations.values()) {
            if (operation.getSymbol().equals(String.valueOf(symbol))) {
                return operation;
            }
        }
        return ALPHABET;
    }
}
