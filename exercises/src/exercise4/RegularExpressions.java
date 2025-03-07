package exercise4;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hedy Huang
 * @version 1.0
 */
public class RegularExpressions {
    public static void main(String[] args) {
        String[][] testCases = {
                {"[A-z0-9._-]+@[A-z0-9_-]+\\.[A-Z|a-z]{2,}", "huang@gmail.com", "$#huang@123.cn"}, // Email Address
                {"\\d{3}-\\d{2}-\\d{4}", "417-56-3682", "67-7543-321"}, // SSN
                {"\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})", "(435) 564 9654", "875-975-87073"}, // Phone Number
                {"([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)", "14:49:02", "24:61:70"}, // Time in HH:MM:SS Format
                {"(\\d{4}[-\\s]?)\\d{4}[-\\s]?\\d{4}[-\\s]?\\d{4}", "9817-3654-9574-8645", "675-798-8324"} // Credit Card Number
        };

//        Iterate above all regex patterns
        for (String[] testCase : testCases) {
            String regex = testCase[0];
            String positiveCase = testCase[1];
            String negativeCase = testCase[2];

            System.out.println("Testing regex: " + regex);
            System.out.println("Positive case: " + positiveCase + " - " + (matchesRegex(regex, positiveCase) ? "Match" : "No match"));
            System.out.println("Negative case: " + negativeCase + " - " + (matchesRegex(regex, negativeCase) ? "Match" : "No match"));
            System.out.println();
        }
    }

    /**
     * Checks if a string matches a given regular expression.
     *
     * @param regex The regular expression pattern.
     * @param input The string to test.
     * @return True if the input matches the regex, false otherwise.
     */
    public static boolean matchesRegex(String regex, String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}