package com.boisbarganhados.ftc.regex.records;

import java.util.List;
import java.util.Map;

import lombok.NonNull;

public record DFABody(
        @NonNull Map<List<Integer>, Integer> createdStates,
        @NonNull List<List<Integer>> processWaitList,
        @NonNull List<Integer> initialStates) {
}
