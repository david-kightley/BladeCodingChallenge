package com.scb;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultCharacterParserTest {

    private static final String[] ONES =
            {"                           ",
             "  |  |  |  |  |  |  |  |  |",
             "  |  |  |  |  |  |  |  |  |"};
    private static final String[] VALID_MIXED =
            {" _  _  _  _     _     _  _ ",
             "|_||_|  ||_ |_| _|  | _||_ ",
             "|_| _|  ||_|  | _|  ||_  _|"};
    private static final String[] ZEROS =
            {" _  _  _  _  _  _  _  _  _ ",
             "| || || || || || || || || |",
             "|_||_||_||_||_||_||_||_||_|"};
    private static final String[] SINGLE_ERROR =
            {" _  _  _  _     _     _  _ ",
             "|_||_|  ||_ | | _|  | _||_ ",
             "|_| _|  ||_|  | _|  ||_  _|"};
    private static final String[] MULTIPLE_ERRORS =
            {"    _  _  _     _     _  _ ",
             "|_||_| | |_   | _|  |  ||_ ",
             "|_| _|  ||_|  |  | _||_  _|"};
    private static final String[] INVALID_CHARS =
            {"    _  _  _     _     _  _ ",
             "__ |||   | |___ _ _|_ |   |",
             " |  _  _ |   _|   |_  | __ "};

    @Test
    public void testStandardCharactersForLength() {
        assertTrue(Stream.of(ONES).allMatch(s -> s.length() == 27));
        assertTrue(Stream.of(VALID_MIXED).allMatch(s -> s.length() == 27));
        assertTrue(Stream.of(ZEROS).allMatch(s -> s.length() == 27));
        assertTrue(Stream.of(SINGLE_ERROR).allMatch(s -> s.length() == 27));
        assertTrue(Stream.of(MULTIPLE_ERRORS).allMatch(s -> s.length() == 27));
        assertTrue(Stream.of(INVALID_CHARS).allMatch(s -> s.length() == 27));
    }

    @Test
    public void testValidParserConstruction() {
        DefaultCharacterParser dcp = new DefaultCharacterParser();
        assertEquals(27, dcp.getLineLength());

        dcp = new DefaultCharacterParser(30);
        assertEquals(30, dcp.getLineLength());
    }

    @Test(expected = RuntimeException.class)
    public void testParserConstructionWithInvalidWidth() {
        DefaultCharacterParser dcp = new DefaultCharacterParser(28);
    }

    @Test
    public void testParseOfEmptyLine() {
        CharacterParser cp = new DefaultCharacterParser();
        // Empty line triggers newline
        assertEquals("\n", cp.parseRow(""));
        assertEquals("\n", cp.parseRow(""));
        assertEquals("\n", cp.parseRow(""));
        final String emptyRow = "                           ";
        assertEquals(27, emptyRow.length());
        assertEquals("", cp.parseRow(emptyRow));
        try {
            // Blank row in middle of a character is not allowed.
            cp.parseRow("");
        } catch (RuntimeException e) {
            // expected
        }
    }

    @Test
    public void testParseOfSingleValidCharacterLine() {
        CharacterParser cp = new DefaultCharacterParser();
        assertEquals("", cp.parseRow(ONES[0]));
        assertEquals("", cp.parseRow(ONES[1]));
        // Output 9 '1's
        assertEquals("111111111\n", cp.parseRow(ONES[2]));
    }

    @Test
    public void testParseOfMixedValidCharacterLine() {
        CharacterParser cp = new DefaultCharacterParser();
        assertEquals("", cp.parseRow(VALID_MIXED[0]));
        assertEquals("", cp.parseRow(VALID_MIXED[1]));
        assertEquals("897643125\n", cp.parseRow(VALID_MIXED[2]));
    }

    @Test
    public void testParseOfZerosInLine() {
        CharacterParser cp = new DefaultCharacterParser();
        assertEquals("", cp.parseRow(ZEROS[0]));
        assertEquals("", cp.parseRow(ZEROS[1]));
        assertEquals("000000000\n", cp.parseRow(ZEROS[2]));
    }

    @Test
    public void testParseOfMixedCharactersWithSingleInvalidCharacter() {
        CharacterParser cp = new DefaultCharacterParser();
        assertEquals("", cp.parseRow(SINGLE_ERROR[0]));
        assertEquals("", cp.parseRow(SINGLE_ERROR[1]));
        assertEquals("8976?3125 ILL\n", cp.parseRow(SINGLE_ERROR[2]));
    }

    @Test
    public void testParseOfMixedCharactersWithMultipleInvalidCharacters() {
        CharacterParser cp = new DefaultCharacterParser();
        assertEquals("", cp.parseRow(MULTIPLE_ERRORS[0]));
        assertEquals("", cp.parseRow(MULTIPLE_ERRORS[1]));
        assertEquals("?9?61???5 ILL\n", cp.parseRow(MULTIPLE_ERRORS[2]));
    }

    @Test
    public void testParseWithNoValidCharactersInLine() {
        CharacterParser cp = new DefaultCharacterParser();
        assertEquals("", cp.parseRow(INVALID_CHARS[0]));
        assertEquals("", cp.parseRow(INVALID_CHARS[1]));
        assertEquals("????????? ILL\n", cp.parseRow(INVALID_CHARS[2]));
    }

    @Test
    public void testParseWithNonBarAndUnderscoreCharacters() {
        final String line1 = "abcdefghijklmnopqrstuvwxyz ";
        final String line2 = "\"@£$%^&*()+=-{}][\n|'?/><,.~";
        final String line3 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
        assertEquals(27, line1.length());
        assertEquals(27, line2.length());
        assertEquals(27, line3.length());

        CharacterParser cp = new DefaultCharacterParser();
        assertEquals("", cp.parseRow(line1));
        assertEquals("", cp.parseRow(line1));
        assertEquals("????????? ILL\n", cp.parseRow(line1));
    }

    @Test
    public void testParseOfMultipleValidCharacterLines() {
        CharacterParser cp = new DefaultCharacterParser();
        assertEquals("", cp.parseRow(VALID_MIXED[0]));
        assertEquals("", cp.parseRow(VALID_MIXED[1]));
        assertEquals("897643125\n", cp.parseRow(VALID_MIXED[2]));

        assertEquals("\n", cp.parseRow(""));

        assertEquals("", cp.parseRow(ONES[0]));
        assertEquals("", cp.parseRow(ONES[1]));
        assertEquals("111111111\n", cp.parseRow(ONES[2]));
        // No blank line
        assertEquals("", cp.parseRow(VALID_MIXED[0]));
        assertEquals("", cp.parseRow(VALID_MIXED[1]));
        assertEquals("897643125\n", cp.parseRow(VALID_MIXED[2]));

        assertEquals("\n", cp.parseRow(""));
        assertEquals("\n", cp.parseRow(""));

        assertEquals("", cp.parseRow(ZEROS[0]));
        assertEquals("", cp.parseRow(ZEROS[1]));
        assertEquals("000000000\n", cp.parseRow(ZEROS[2]));

        assertEquals("\n", cp.parseRow(""));

        assertEquals("", cp.parseRow(SINGLE_ERROR[0]));
        assertEquals("", cp.parseRow(SINGLE_ERROR[1]));
        assertEquals("8976?3125 ILL\n", cp.parseRow(SINGLE_ERROR[2]));

        assertEquals("\n", cp.parseRow(""));

        assertEquals("", cp.parseRow(VALID_MIXED[0]));
        assertEquals("", cp.parseRow(VALID_MIXED[1]));
        assertEquals("897643125\n", cp.parseRow(VALID_MIXED[2]));

        assertEquals("\n", cp.parseRow(""));
    }

}