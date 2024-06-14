package com.boisbarganhados.ftc;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.boisbarganhados.ftc.dfa.DFA;
import com.boisbarganhados.ftc.jflap.JFlapParser;
import com.boisbarganhados.ftc.jflap.XMLController;
import com.boisbarganhados.ftc.minimization.OptimizedDFAMinimizer;
import com.boisbarganhados.ftc.minimization.RootDFAMinimizer;
import com.boisbarganhados.ftc.regex.RegexUtils;
import com.boisbarganhados.ftc.regex.Thompson;

/**
 * Regex to DFA - FTC Assignment/PUC Minas - 2024/1
 * 
 * @author Leon Junio Martins Ferreira [https://github.com/leon-junio]
 * @author Edmar Melandes Junior [https://github.com/Lexizz7]
 */
public class Main {

    private final static Scanner scanner = new Scanner(System.in);
    private final static String JFLAP_PATH = "bin/JFLAP/JFLAP.jar";
    private final static String TEST_REGEX_DFA = "test/regex.jff";
    private final static String REGEX_TEST = "(a+b)*";

    public static void main(String[] args) {
        try {
            System.out.println("Regex to DFA - FTC Assignment/PUC Minas - 2024/1");
            if (args.length > 0) {
                if (args.length < 2)
                    cliUsage();
                else {
                    runRegexTransformation(args[0], args[1]);
                }
            }
            while (true) {
                menu();
            }
        } catch (Exception e) {
            System.err.println("Fatal main error:");
            e.printStackTrace();
        }
    }

    private static void cliUsage() {
        System.out.println("Usage: java -jar regex-to-dfa.jar <path to regex file> <path to the sentences file>");
        System.out.println("Example: java -jar regex-to-dfa.jar regex.txt sentences.txt");
        System.out.println("Regex structure:");
        System.out.println("Operations allowed * (Kleene star), + (Union) and (Concatenation)");
        System.out.println("Example: a+b*");
        System.out.println("To empty string use: \"λ\"");
        System.out.println("Example: a*(λ+bc)");
        System.out.println("Sentences file structure:");
        System.out.println("Each line is a sentence to be tested");
        System.out.println("Example:");
        System.out.println("abc");
        System.out.println("ab");
        System.exit(1);
    }

    /**
     * Main menu of the program
     * 
     * @throws Exception
     */
    public static void menu() throws Exception {
        System.out.println("Enter the path (or file name if file is located at program root folder) \n " +
                "to the regex file and after the path to the sentences file separated by space");
        System.out
                .println("Enter 'test' to test a default regex to DFA conversion \nenter 'exit' to close the program");
        String response = scanner.nextLine();
        if (response == null || response.isEmpty()) {
            cliUsage();
        } else if (response.equals("gen")) {
            generate();
        } else if (response.equals("test")) {
            test();
        } else if (response.equals("exit")) {
            System.exit(0);
        } else {
            startMinimization(response);
        }
    }

    /**
     * Test method to run the program with the test files
     * 
     * @throws Exception
     */
    private static void test() {
        try {
            System.out.println("Testing...");
            var sentences = Arrays.asList("abbbbbb", "a", "b", "");
            runRegexTransformation(REGEX_TEST, sentences);
            System.out.println("Test finished");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Error while testing:");
            e.printStackTrace();
        }
    }

    /**
     * Start the minimization process with the given XML file
     * 
     * @param xmlFilePath Path to the XML file
     */
    private static void startMinimization(String xmlFilePath) {
        try {
            System.out.println("Starting minimization...");
            int option;
            do {
                option = 0;
                System.out.println("1- Root DFA minimization (N^2 complexity)");
                System.out.println("2- Optimized minimization (N log N complexity)");
                System.out.println("Choose the minimization method:");
                option = scanner.nextInt();
                scanner.nextLine();
            } while (option <= 0 || option > 2);
            System.out.println(xmlFilePath);
            var jflapDFA = XMLController.reader(xmlFilePath);
            var internalDfa = JFlapParser.parse(jflapDFA);
            var minimizedDFA = option == 1 ? RootDFAMinimizer.minimizeDFA(internalDfa)
                    : OptimizedDFAMinimizer.minimizeDFA(internalDfa);
            var minimizedPath = xmlFilePath.replace(".jff",
                    option == 1 ? "_root_minimized.jff" : "_optimized_minimized.jff");
            XMLController.writer(JFlapParser.parse(minimizedDFA), minimizedPath);
            System.out.println("Minimization finished. Result saved to " + minimizedPath);
            runJFLAP(minimizedPath);
        } catch (Exception e) {
            System.err.println("Error while running minimization");
            e.printStackTrace();
        }
    }

    /**
     * Run JFLAP with the results
     * 
     * @param xmlFilePath Path to the XML file
     */
    private static void runJFLAP(String xmlFilePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", JFLAP_PATH, xmlFilePath);
            pb.directory(new File("."));
            pb.start();
            System.out.println("Running JFLAP with results");
        } catch (Exception e) {
            System.err.println("JFLAP Launcher error:");
            e.printStackTrace();
        }
    }

    /**
     * Generate a test DFA
     */
    private static void generate() {
        try {
            System.out.println("Generating test DFA...");
            System.out.println("Enter the number of states for the test DFA:");
            int nStates = scanner.nextInt();
            scanner.nextLine();
            var dfa = DFA.generateDoubleStateTest(nStates);
            var dfaPath = "test/generated_" + nStates + "_states.jff";
            XMLController.writer(JFlapParser.parse(dfa), dfaPath);
            System.out.println("Test DFA generated. Path: " + dfaPath);
            runJFLAP(dfaPath);
        } catch (Exception e) {
            System.err.println("Error while generating test DFA:");
            e.printStackTrace();
        }
    }

    private static void runRegexTransformation(String pathToRegex, String pathToSentences) {
        try {
            System.out.println("Running regex transformation...");
            var regex = RegexUtils.readRegex(pathToRegex);
            var sentences = RegexUtils.readSentences(pathToSentences);
            var regexDfa = Thompson.getNfaFromRegex(regex);
            regexDfa = RegexUtils.convertToDeterministic(regexDfa);
            var regexPath = pathToRegex.substring(0, pathToRegex.lastIndexOf('.')) +
                    "_regex_dfa.jff";
            RegexUtils.simulateDFA(regexDfa, sentences);
            XMLController.writer(JFlapParser.parse(RegexUtils.parseToJFlapDFA(regexDfa)),
                    regexPath);
            runJFLAP(regexPath);
            System.out.println("Regex transformation finished.");
        } catch (Exception e) {
            System.err.println("Error while converting regex to DFA:");
            e.printStackTrace();
        }
    }

    private static void runRegexTransformation(String regex, List<String> sentences) {
        try {
            var regexDfa = Thompson.getNfaFromRegex(regex);
            var dfa = RegexUtils.convertToDeterministic(regexDfa);
            System.out.println(dfa);
            RegexUtils.simulateDFA(dfa, sentences);
            var pathToRegex = TEST_REGEX_DFA;
            var regexPath = pathToRegex.substring(0, pathToRegex.lastIndexOf('.')) +
                    "_regex_dfa.jff";
            XMLController.writer(JFlapParser.parse(RegexUtils.parseToJFlapDFA(dfa)),
                    regexPath);
            runJFLAP(regexPath);
        } catch (Exception e) {
            System.err.println("Error while converting regex to DFA:");
            e.printStackTrace();
        }
    }
}