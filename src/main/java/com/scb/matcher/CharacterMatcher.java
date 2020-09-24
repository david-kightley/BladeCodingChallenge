package com.scb.matcher;

import com.scb.charset.CharacterSet;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class CharacterMatcher {
    private final Map<Integer, Map<String, List<CharacterSet>>> characterMap;

    private final int characterWidth;
    private final Set<CharacterSet>[] characterSetArray;

    private int rowNumber = 0;

    public CharacterMatcher(Map<Integer, Map<String, List<CharacterSet>>> map, int charWidth, int charactersPerLine) {
        this.characterMap = map;
        this.characterWidth = charWidth;
        this.characterSetArray = new Set[charactersPerLine];
        for (int i = 0; i < this.characterSetArray.length; ++i) {
            this.characterSetArray[i] = new HashSet<>();
        }
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void processLine(String line) {
        String key;
        int start;
        for (int i = 0; i < characterSetArray.length; ++i) {
            start = i * this.characterWidth;
            key = line.substring(start, start + this.characterWidth);
            final List<CharacterSet> matches = characterMap.get(rowNumber).getOrDefault(key, Collections.EMPTY_LIST);
            if (rowNumber == 0) {
                characterSetArray[i].addAll(matches);
            } else {
                characterSetArray[i].retainAll(matches);
            }
        }

        rowNumber++;
    }

    public String getOutputAndReset() {
        final AtomicBoolean containsInvalidCharacter = new AtomicBoolean(false);
        StringBuilder sb = new StringBuilder();
        Stream.of(characterSetArray).forEach(s -> {
            if (s.size() == 1) {
                CharacterSet cs = s.stream().findFirst().get();
                sb.append(cs.getValue());
            } else {
                // Invalid character
                sb.append("?");
                containsInvalidCharacter.set(true);
            }
            s.clear();
        });
        if (containsInvalidCharacter.get()) {
            sb.append(" ILL");
        }
        sb.append("\n");

        rowNumber = 0;
        return sb.toString();
    }
}
