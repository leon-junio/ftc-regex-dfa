package com.boisbarganhados.ftc.regex.records;

import java.util.Map;
import java.util.Set;

import lombok.NonNull;

public record LambdaBody(
        @NonNull Map<Integer, Set<Integer>> mappedStates,
        @NonNull Map<Integer, Boolean> mappedVisited) {
}