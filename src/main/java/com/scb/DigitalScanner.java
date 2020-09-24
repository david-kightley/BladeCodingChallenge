package com.scb;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class DigitalScanner {

    private final File inputFile;
    private CharacterParser parser;

    public DigitalScanner(String filename) {
        this.inputFile = new File(filename);
        parser = new DefaultCharacterParser();
    }

    public DigitalScanner(String filename, int lineLength) {
        this.inputFile = new File(filename);
        parser = new DefaultCharacterParser(lineLength);
    }

    public void parseInputFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while((line = reader.readLine()) != null) {
                System.out.print(parser.parseRow(line));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error opening input file", e);
        }
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser("f:c:l:");
        parser.allowsUnrecognizedOptions();
        OptionSet optionSet = parser.parse(args);

        if (optionSet.hasOptions()) {
            if (!optionSet.has("f")) {
                System.out.println("You must specify an input file: ");
                printUsage();
            }
            Map<OptionSpec<?>, List<?>> options = optionSet.asMap();

            String fileName = (String) optionSet.valueOf("f");

            if (optionSet.has("c")) {
                System.out.println("Alternate character sets not supported yet...");
            }

            final DigitalScanner scanner;

            if (optionSet.has("l")) {
                String length = (String) optionSet.valueOf("l");
                try {
                    int len = Integer.parseInt(length);
                    scanner = new DigitalScanner(fileName, len);
                } catch (NumberFormatException e) {
                    System.out.println("Unable to parse length override option: " + length);
                    return;
                }
            } else {
                scanner = new DigitalScanner(fileName);
            }

            // Run parsing on the input file
            scanner.parseInputFile();
        } else {
            System.out.println("No options specified: ");
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("DigitalScanner -f <filename> (-c <characterSet name>) (-w <line width>)");
    }

}
