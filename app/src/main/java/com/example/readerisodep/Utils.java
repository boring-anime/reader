package com.example.readerisodep;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Utils {

    private static final String HEX_CHARS = "0123456789ABCDEF";
    private static final char[] HEX_CHARS_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * This method converts Hexadecimal String to byte array.
     * @param data hexadecimal String.
     * @return byte array.
     */
    static byte[] hexStringToByteArray(String data){
        byte[] result = new byte[data.length() / 2];

        for(int i = 0; i < data.length(); i += 2){
            int firstIndex = HEX_CHARS.indexOf(data.charAt(i));
            int secondIndex = HEX_CHARS.indexOf(data.charAt(i+1));

            int octet = ((firstIndex << 4) | secondIndex);
            result[i >> 1] = (byte)(octet);
        }
        return result;
    }

    /**
     * This method converts byte array to Hexadecimal String.
     * @param bytes byte array.
     * @return hexadecimal String.
     */
    static String toHexString(byte[] bytes){
        StringBuilder result = new StringBuilder();

        for(byte b : bytes){
            int firstIndex = (((int)b) & 0xF0) >>> 4;
            int secondIndex = ((int)b & 0x0F);

            result.append(HEX_CHARS_ARRAY[firstIndex]);
            result.append(HEX_CHARS_ARRAY[secondIndex]);
        }
        return result.toString();
    }

    /**
     * This method converts input stream to String.
     * @param inputStream input stream.
     * @return String.
     */
    static String inputStreamToString(InputStream inputStream){
        String text = null;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;
    }


}
