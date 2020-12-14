package com.safe.passtrack.Security;

public class Encrypt {

    public String EncryptText(String text) {
        String code = "", cypher = "";

        // generating first key
        int min = 3, max = 10;
        int firstKey = ((int) Math.random() * (max - min) + min);

        // Encrypting with first key
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (i == 0 || i % 2 == 0) {
                if (i < 25) {
                    ch += firstKey - i;
                } else {
                    int j = i / 4;
                    ch += firstKey - j;
                }
            } else if (i % 2 == 1) {
                if (i < 25) {
                    ch -= firstKey - i;
                } else {
                    int j = i / 4;
                    ch -= firstKey - j;
                }
            }
            code += ch;
        }

        // Embedding 1st key
        String firstKeyAscii = String.valueOf(firstKey + '0');
        char firstLetter = firstKeyAscii.charAt(0);
        char lastLetter = firstKeyAscii.charAt(1);

        code = lastLetter + code + firstLetter;

        // generating 2nd key
        String len = String.valueOf(code.length());
        char firstDigit = len.charAt(0);
        char lastDigit = len.charAt(1);

        int secondKey = Math.abs(Character.getNumericValue(lastDigit) - Character.getNumericValue(firstDigit));
        if (secondKey == 0 || secondKey == 1) {
            secondKey = Character.getNumericValue(lastDigit) + Character.getNumericValue(firstDigit);
        }

        //Re-Encrypting code with 2nd key
        for (int j = 0; j < code.length(); j++) {
            char ch = code.charAt(j);
            ch -= secondKey;
            cypher += ch;
        }

        return cypher;
    }

    public String DecryptText(String text) {
        String code = "", decypher = "",secondKeyAscii="";

        //finding 1st key
        String len = String.valueOf(text.length());
        char firstDigit = len.charAt(0);
        char lastDigit = len.charAt(1);

        int firstKey = Math.abs(Character.getNumericValue(lastDigit) - Character.getNumericValue(firstDigit));
        if (firstKey == 0 || firstKey == 1) {
            firstKey = Character.getNumericValue(lastDigit) + Character.getNumericValue(firstDigit);
        }

        // Decrypting with 1st key
        for (int j = 0; j < text.length(); j++) {
            char ch = text.charAt(j);
            ch += firstKey;
            code += ch;
        }

        // extracting 2nd key
        char firstLetter = code.charAt(code.length() - 1);
        char lastLetter = code.charAt(0);
        secondKeyAscii = "" + firstLetter + lastLetter;
        int ascii = Integer.parseInt(secondKeyAscii);
        int secondKey = Character.getNumericValue(ascii);

        // removing 2nd key
        code = code.substring(1, code.length() - 1);

        //Decrypting with 2nd key
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            if (i == 0 || i % 2 == 0) {
                if (i < 25) {
                    ch -= secondKey - i;
                } else {
                    int j = i / 4;
                    ch -= secondKey - j;
                }
            } else if (i % 2 == 1) {
                if (i < 25) {
                    ch += secondKey - i;
                } else {
                    int j = i / 4;
                    ch += secondKey - j;
                }
            }
            decypher += ch;
        }
        return decypher;
    }

}
