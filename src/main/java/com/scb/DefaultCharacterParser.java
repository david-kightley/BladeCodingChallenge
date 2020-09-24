package com.scb;

import com.scb.charset.CharacterSet;
import com.scb.charset.DigitalCharacterSet;
import com.scb.matcher.CharacterMatcher;

import java.util.*;
import java.util.stream.Stream;

public class DefaultCharacterParser implements CharacterParser {

    private static final int LENGTH = 27;
    private static final String NEWLINE = "\n";
    private static final String EMPTY = "";

    private int lineLength;
    private int charactersPerLine = 0;
    private int characterHeight = 0;

    private CharacterMatcher matcher;

    public DefaultCharacterParser() {
        this(LENGTH);
    }

    public DefaultCharacterParser(int length) {
        this(length, DigitalCharacterSet.values());
    }

    public DefaultCharacterParser(int length, CharacterSet[] values) {
        this.lineLength = length;
        initParser(values);
    }

    private void initParser(CharacterSet[] values) {
        validateCharacterSet(values);

        Map<Integer, Map<String, List<CharacterSet>>> characterMap = new HashMap<>();

        for (CharacterSet cs : values) {
            String[] rows = cs.getRows();
            for (int i = 0; i < rows.length; ++i) {
                Map<String, List<CharacterSet>> rowMap = characterMap.get(i);
                if (rowMap == null) {
                    rowMap = new HashMap<String, List<CharacterSet>>();
                    characterMap.put(i, rowMap);
                }
                List<CharacterSet> charList = rowMap.get(rows[i]);
                if (charList == null) {
                    charList = new ArrayList<>();
                    rowMap.put(rows[i], charList);
                }
                charList.add(cs);
            }
        }

        // Get the character dimensions from a Character
        int characterWidth = values[0].getCharacterWidth();
        if (this.lineLength % characterWidth != 0) {
            throw new RuntimeException(String.format("Character width (%d) is not compatible with the line length: %d. ", characterWidth, this.lineLength));
        }
        this.charactersPerLine = this.lineLength / characterWidth;
        this.characterHeight = values[0].getCharacterHeight();

        this.matcher = new CharacterMatcher(characterMap, characterWidth, this.charactersPerLine);
    }

    public String parseRow(String line) {
        if (matcher.getRowNumber() == 0 && line.isEmpty()) {
            // Blank line separating character
            return NEWLINE;
        }

        if (line.length() != this.lineLength) {
            throw new RuntimeException(String.format("Invalid line length received.  Found: %d  Expected: %d : %s", line.length(), this.lineLength, line));
        }

        matcher.processLine(line);

        if (matcher.getRowNumber() == this.characterHeight) {
             return matcher.getOutputAndReset();
        }
        return EMPTY;
    }

    private void validateCharacterSet(CharacterSet[] values) {
        if (values == null || values.length == 0) {
            throw new RuntimeException("CharacterSet cannot be null or empty");
        }
        Stream.of(values).forEach(c -> validateCharacter(c));
    }

    private void validateCharacter(CharacterSet cs) {
        String[] rows = cs.getRows();
        if (rows == null ||
                rows.length != cs.getCharacterHeight() ||
                Stream.of(rows).allMatch(s -> s.length() != cs.getCharacterWidth())) {
            throw new RuntimeException(String.format("Invalid character found for %s in %s", cs.getValue(), cs.getClass().getSimpleName()));
        }
    }

    public int getLineLength() {
        return this.lineLength;
    }
}
