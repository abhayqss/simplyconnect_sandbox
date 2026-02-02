package com.scnsoft.eldermark.hl7v2.h2;

import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class automatically generates DbUnit xml dataset with HL7 Code Tables by parsing sql migration files.
 * Intention is to simplify support of H2 integration tests - one doesn't have to build XML manually so that
 * codes can be imported in H2 database.
 * <p>
 * Obviously, this won't be needed when transition to SQL Server with testcontainers is done for integration tests,
 * because there we will run migration scripts directly.
 */
public class HL7CodeTableXmlDatasetGenerator {
    private static final PathMatcher migrationScriptMatcher = FileSystems.getDefault().getPathMatcher("glob:**/V*.sql");

    private static void test(List<List<String>> linesList, List<Hl7CodeInsertProcedureCall> expected) {
        var calls = linesList.stream().
                map(lines -> {
                    var it = lines.iterator();
                    return buildProcedureCall(it.next(), it);
                })
                .collect(Collectors.toList());

        for (int i = 0; i < calls.size(); i++) {
            if (!calls.get(i).equals(expected.get(i))) {
                throw new RuntimeException("Mismatch: " + calls.get(i) + " vs " + expected.get(i));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        //self-test of procedure call parsing including calls with escaped parameters and line breaks
        test(List.of(
                        List.of("exec addHL7Code 'I', 'Specimen in lab; ''results'' pending', '0085', 'HL7';"),
                        List.of("exec addHL7Code 'N',",
                                "                'Not asked; used to affirmatively document that the observation identified in the OBX was not sought when the universal service ID in OBR-4 implies that it would be sought.',",
                                "                '0085', 'HL7';")
                ),
                List.of(
                        new Hl7CodeInsertProcedureCall("I", "Specimen in lab; 'results' pending", "0085", "HL7"),
                        new Hl7CodeInsertProcedureCall("N", "Not asked; used to affirmatively document that the observation identified in the OBX was not sought when the universal service ID in OBR-4 implies that it would be sought.",
                                "0085", "HL7")

                )
        );

        //actual work
        var migrationDirectoryPath = Path.of("..\\ExchangeDatabase\\src\\main\\resources\\db\\migration")
                .toAbsolutePath();

        var resultXmlFile = Path.of("eldermark-exchange-hl7v2\\src\\test\\resources\\h2\\datasets\\hl7codes.xml");

        var hl7Codes = Files.walk(migrationDirectoryPath)
                .filter(HL7CodeTableXmlDatasetGenerator::isMigrationFile)
                .flatMap(HL7CodeTableXmlDatasetGenerator::getHl7CodeProcedureCalls);
        //todo there are also several updates to Hl7CodeTable table in migration scripts

        writeXmlFIle(resultXmlFile, hl7Codes);
    }

    private static Stream<Hl7CodeInsertProcedureCall> getHl7CodeProcedureCalls(Path path) {
        try {
            return readProcedureCallsFromFile(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Failed to read file " + path.getFileName() + " with UTF-8 encoding");
        }

        try {
            return readProcedureCallsFromFile(path, Charset.forName("windows-1251"));
        } catch (IOException e) {
            System.out.println("Failed to read file " + path.getFileName() + " with windows-1251 encoding");
        }

        throw new RuntimeException("Failed to read file");
    }

    private static Stream<Hl7CodeInsertProcedureCall> readProcedureCallsFromFile(Path path, Charset charset) throws IOException {
        var lines = Files.readAllLines(path, charset);
        var result = new ArrayList<Hl7CodeInsertProcedureCall>();

        var it = lines.iterator();
        while (it.hasNext()) {
            var current = it.next();

            if (current.toLowerCase().contains("exec addhl7code")) {
                result.add(buildProcedureCall(current, it));
            }
        }
        return result.stream();
    }


    private static boolean isMigrationFile(Path path) {
        return Files.isRegularFile(path) && migrationScriptMatcher.matches(path);
    }

    private static Hl7CodeInsertProcedureCall buildProcedureCall(String firstLine, Iterator<String> nextLinesIterator) {
        var ctx = new ProcedureCallParseState();
        ctx.currentLine = firstLine;
        ctx.iterator = nextLinesIterator;
        ctx.previousParamEndIndex = firstLine.toLowerCase().indexOf("exec addhl7code") + "exec addhl7code".length();

        var code = readNextParam(ctx);
        var value = readNextParam(ctx);
        var table = readNextParam(ctx);
        var type = readNextParam(ctx);

        return new Hl7CodeInsertProcedureCall(
                unescapeSqlServerParam(code),
                unescapeSqlServerParam(value),
                table,
                type);
    }

    private static String readNextParam(ProcedureCallParseState state) {
        var paramChar = '\'';

        var paramStart = state.currentLine.indexOf(paramChar, state.previousParamEndIndex + 1);
        while (paramStart == -1) {
            state.currentLine = state.iterator.next();
            paramStart = state.currentLine.indexOf(paramChar);
        }

        //we assume that parameter starts and ends on the same line in script, because there are no values in HL7 with line breaks
        var paramEnd = state.currentLine.indexOf(paramChar, paramStart + 1);

        //process '' occurrences, which is escaped ' in sql server
        while (state.currentLine.charAt(paramEnd + 1) == paramChar) {
            paramEnd = state.currentLine.indexOf(paramChar, paramEnd + 2);
        }

        state.previousParamEndIndex = paramEnd;
        return state.currentLine.substring(paramStart + 1, paramEnd);
    }

    private static String unescapeSqlServerParam(String line) {
        return line.replaceAll("''", "'");
    }

    private static void writeXmlFIle(Path resultXmlFile, Stream<Hl7CodeInsertProcedureCall> hl7Codes) throws IOException {
        var content = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?>\n<!--this dataset is generated automatically by com.scnsoft.eldermark.hl7v2.h2.HL7CodeTableXmlDatasetGenerator-->\n<dataset>\n");
        var idGenerator = new IdGenerator();
        hl7Codes.forEach(code -> appendCode(content, code, idGenerator));

        content.append("</dataset>");
        Files.writeString(resultXmlFile, content);
    }

    private static Map<String, String> typeToTableMapping = Map.of(
            "HL7", "HL7DefinedCodeTable",
            "USER", "HL7UserDefinedCodeTable"
    );

    private static void appendCode(StringBuilder content, Hl7CodeInsertProcedureCall procedureCall, IdGenerator idGenerator) {
        var id = idGenerator.nextId();
        content.append("    <HL7CodeTable id=\"")
                .append(id)
                .append("\" code=\"")
                .append(StringEscapeUtils.escapeXml10(procedureCall.code))
                .append("\" value=\"")
                .append(StringEscapeUtils.escapeXml10(procedureCall.value))
                .append("\" table_number=\"")
                .append(procedureCall.tableNumber)
                .append("\"/>\n    ");

        var typeTable = typeToTableMapping.getOrDefault(procedureCall.type, null);
        if (typeTable == null) {
            throw new RuntimeException("Unknown code type " + typeTable);
        }

        content.append("<")
                .append(typeTable)
                .append(" id=\"")
                .append(id)
                .append("\"/>\n\n");
    }

    private static class ProcedureCallParseState {
        String currentLine;
        Iterator<String> iterator;
        int previousParamEndIndex;
    }

    private static class Hl7CodeInsertProcedureCall {
        String code;
        String value;
        String tableNumber;
        String type;

        public Hl7CodeInsertProcedureCall() {
        }

        public Hl7CodeInsertProcedureCall(String code, String value, String tableNumber, String type) {
            this.code = code;
            this.value = value;
            this.tableNumber = tableNumber;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Hl7CodeInsertProcedureCall that = (Hl7CodeInsertProcedureCall) o;
            return Objects.equals(code, that.code) && Objects.equals(value, that.value) && Objects.equals(tableNumber, that.tableNumber) && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, value, tableNumber, type);
        }

        @Override
        public String toString() {
            return "Hl7CodeInsertProcedureCall{" +
                    "code='" + code + '\'' +
                    ", value='" + value + '\'' +
                    ", tableNumber='" + tableNumber + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    private static class IdGenerator {
        private int current = 0;

        public int nextId() {
            return ++current;
        }
    }
}
