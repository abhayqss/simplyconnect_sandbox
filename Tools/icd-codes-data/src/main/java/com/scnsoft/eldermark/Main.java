package com.scnsoft.eldermark;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Main {

    private static int ORDER_START = 0;
    private static int ORDER_END = ORDER_START + 5;

    private static int CODE_START = 6;
    private static int CODE_END = CODE_START + 7;

    private static int HEADER_FLAG_START = 14;
    private static int HEADER_FLAG_END = HEADER_FLAG_START + 1;

    private static int LONG_DESCR_START = 77;

    private static String SEPARATOR = "|";
    private static String CODE_HEADER_SEPARATOR = ".";

    private static String resultFile = "result.txt";

    public static void main(String[] args) throws IOException {
        final String filename = args[0];
        final InputStream is = Main.class.getClassLoader().getResourceAsStream(filename);
        final Scanner scanner = new Scanner(is);

        final StringBuilder result = new StringBuilder();

        String currentHeader = null;
        int i = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (isHeader(line)) {
                final String newHeader = getCode(line);
                if (currentHeader == null || !newHeader.startsWith(currentHeader)) {
                    currentHeader = newHeader;
                }
            } else {
                final String code = getCode(line);
                if (currentHeader != null && code.startsWith(currentHeader)) {
                    result.append(currentHeader)
                            .append(CODE_HEADER_SEPARATOR)
                            .append(code.substring(currentHeader.length()));


                } else {
                    currentHeader = null;
                    result.append(code);
                }
                result.append(SEPARATOR)
                        .append(getDescription(line)).append("\r\n");
            }
            ++i;
//            if (i > 20) {
//                break;
//            }
        }
        System.out.println(result.toString());

        FileWriter writer = new FileWriter(resultFile);
        writer.write(result.toString());
        writer.close();
    }

    private static String getCode(String s) {
        return s.substring(CODE_START, CODE_END).trim();
    }

    private static boolean isHeader(String s) {
        return "0".equals(s.substring(HEADER_FLAG_START, HEADER_FLAG_END));
    }

    private static String getDescription(String s) {
        return s.substring(LONG_DESCR_START).trim();
    }
}
