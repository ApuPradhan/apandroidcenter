package com.apandroidcenter;

import com.apandroidcenter.type.CaseType;

public class CaseConverter {


    public static String stringToCase(String input, CaseType caseType) {
        // Initialize the result string
        String resultString = "";
        String[] words = input.split(" ");

        switch (caseType) {
            case PASCAL_CASE:
                // Convert to pascal case by removing spaces and capitalizing
                // the first character of each word
                for (String word : words) {
                    resultString += word.substring(0, 1).toUpperCase() + word.substring(1);
                }
                break;
            case PASCAL_CASE_WITH_SPACES:
                StringBuilder sb = new StringBuilder();
                for (String word : words) {
                    sb.append(Character.toUpperCase(word.charAt(0)));
                    sb.append(word.substring(1));
                }
                return sb.toString();
            case CAMEL_CASE:
                // Convert to camel case by making the first character lowercase
                // and removing spaces
                resultString = input.substring(0, 1).toLowerCase() + input.substring(1).replaceAll(" ", "");
                return resultString;
            case SNAKE_CASE:
                // Convert to snake case by replacing spaces with underscores
                // and making all characters lowercase
                resultString = input.replaceAll(" ", "_").toLowerCase();
                return resultString;
            default:
                throw new IllegalArgumentException("Invalid choice");
        }

        return resultString;
    }


}

