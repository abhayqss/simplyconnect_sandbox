package com.scnsoft.eldermark;

import java.io.*;

public class CorrectLineSeparator {

    /**
     * Converts end of line to format of system where code is executed.
     */
    public static void main(String[] args) {
        String pathToOriginalFile = args[0];
        String pathToCorrectedFile = args[1];

        try {
            correctSeparator(new File(pathToOriginalFile), new File(pathToCorrectedFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void correctSeparator(File originalFile, File correctedFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(correctedFile));

        while(br.ready()) {
            bw.write(br.readLine());
            bw.newLine();
        }

        bw.close();
        br.close();
    }
}
