package com.scb.charset;

import java.util.stream.Stream;

public enum DigitalCharacterSet implements CharacterSet {

    ONE('1', new String[]{"   ","  |","  |"}),
    TWO('2', new String[]{" _ ", " _|","|_ "}),
    THREE('3', new String[]{" _ ", " _|"," _|"}),
    FOUR('4', new String[]{"   ","|_|","  |"}),
    FIVE('5', new String[]{" _ ", "|_ "," _|"}),
    SIX('6', new String[]{" _ ","|_ ","|_|"}),
    SEVEN('7', new String[]{" _ ","  |","  |"}),
    EIGHT('8', new String[]{" _ ","|_|","|_|"}),
    NINE('9', new String[]{" _ ","|_|"," _|"}),
    ZERO('0', new String[]{" _ ","| |","|_|"});


    char val;
    String[] rows;

    DigitalCharacterSet(char value, String[] r) {
        val = value;
        rows = r;
    }

    public char getValue() {
        return val;
    }

    public String[] getRows() {
        return rows;
    }

    public int getCharacterWidth() {
        return 3;
    }

    public int getCharacterHeight() {
        return 3;
    }

}
