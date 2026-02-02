package com.scnsoft.eldermark.dump.service.writer;

import com.scnsoft.eldermark.dump.model.RawAssessmentDump;
import com.scnsoft.eldermark.dump.model.assessment.AssessmentElement;
import com.scnsoft.eldermark.dump.model.assessment.AssessmentElementsAware;
import com.scnsoft.eldermark.dump.model.assessment.AssessmentPage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.*;
import java.util.stream.Stream;

public abstract class RawAssessmentExcelDumpWriter<T extends RawAssessmentDump>
        extends ExcelDumpWriter
        implements DumpWriter<T> {

    protected boolean hasQuestions(AssessmentPage page) {
        //there exist at least one question on the page
        Queue<Collection<AssessmentElement>> elementsListQueue = new ArrayDeque<>();
        elementsListQueue.add(CollectionUtils.emptyIfNull(page.getElements()));
        while (!elementsListQueue.isEmpty()) {
            var elementList = elementsListQueue.poll();
            for (var element : elementList) {
                if (isQuestion(element)) {
                    return true;
                }
                elementsListQueue.add(CollectionUtils.emptyIfNull(element.getElements()));
            }
        }
        return false;
    }

    protected int writeAssessmentHeaderRows(XSSFSheet sheet, Map<String, CellStyle> styles, AssessmentPage page,
                                          Map<Integer, String> colIdxQuestionMapping, int startAtRow, int startAtCol) {

        //1. build a tree from page which describes panels/questions layout using nodeToVisitQueue as FIFO queue
        Deque<HeaderTreeNode> nodeToVisitDequeue = new ArrayDeque<>();
        var treeRoot = new HeaderTreeNode(page);
        nodeToVisitDequeue.add(treeRoot);
        while (!nodeToVisitDequeue.isEmpty()) {
            var node = nodeToVisitDequeue.poll();
            var elementsToAdd = CollectionUtils.emptyIfNull(node.elementsAware.getElements());

            for (var element : elementsToAdd) {
//                if (isQuestion(element) || isPanel(element)) {
                var childNode = new HeaderTreeNode(element);
                node.addChild(childNode);
                nodeToVisitDequeue.add(childNode);
//                }
            }
        }

        //2. calculate rows and columns width and heights
        calcTreeHeaderNodeRowWidthAndTreeHeight(treeRoot);

        //3. build header grid
        for (int rowNum = startAtRow; rowNum < treeRoot.subTreeHeight; ++rowNum) {
            var row = sheet.createRow(rowNum);
            for (var colNum = 0; colNum < treeRoot.rowWidths + startAtCol; ++colNum) {
                //start from 0 because columns before startAtCol should also be created.
                row.createCell(colNum);
            }
        }

        //4. and finally fill in the header
        fillInAssessmentHeader(sheet, styles, treeRoot, treeRoot.subTreeHeight, startAtCol, startAtRow, 0,
                colIdxQuestionMapping);

        return treeRoot.subTreeHeight;
    }

    private void fillInAssessmentHeader(XSSFSheet sheet, Map<String, CellStyle> styles, HeaderTreeNode node, int fullTreeHeight, int colNumber,
                                        int startAtRow, int rowIdxShift, Map<Integer, String> colIdxQuestionMapping) {
        if (node.rowWidths == 0) {
            return;
        }

        int colLeftIdx = colNumber;
        int colRightIdx = colNumber + node.rowWidths - 1;
        int rowUpperIdx = startAtRow + rowIdxShift;
        int rowBottomIdx = startAtRow + fullTreeHeight - node.subTreeHeight - 1;

        if (node.assessmentElement != null) {
            //don't write root
            if (colLeftIdx != colRightIdx || rowBottomIdx != rowUpperIdx) {
                sheet.addMergedRegion(new CellRangeAddress(rowUpperIdx, rowBottomIdx, colLeftIdx, colRightIdx));
            }

            var cell = sheet.getRow(rowUpperIdx).getCell(colLeftIdx);
            writeHeaderValue(cell, styles, node.assessmentElement.getTitle());

            if (isQuestion(node.assessmentElement)) {
                colIdxQuestionMapping.put(colNumber, node.assessmentElement.getName());
            }
        }

        if (CollectionUtils.isNotEmpty(node.children)) {
            int colIdxOffset = 0;
            rowIdxShift = rowIdxShift + rowBottomIdx - rowUpperIdx + 1;
            for (int i = 0; i < node.children.size(); ++i) {
                fillInAssessmentHeader(sheet, styles, node.children.get(i), fullTreeHeight,
                        colNumber + colIdxOffset, startAtRow, rowIdxShift, colIdxQuestionMapping);
                colIdxOffset += (node.children.get(i).rowWidths);
            }
        }
    }

    protected void writeAdditionalHeader(XSSFSheet sheet, Map<String, CellStyle> styles, List<String> additionalHeaderColumns,
                                         int startAtRow, int startAtCol, int totalHeight) {
        //assuming that cells have already been created
        for (int i = 0; i < additionalHeaderColumns.size(); ++i) {
            writeHeaderValue(sheet.getRow(startAtRow).getCell(i), styles, additionalHeaderColumns.get(i));
            if (totalHeight > 1) {
                sheet.addMergedRegion(new CellRangeAddress(startAtRow, startAtRow + totalHeight - 1, i, i));
            }
        }
    }

    protected void writeHeaderValue(Cell cell, Map<String, CellStyle> styles, String value) {
        cell.setCellValue(value);
        cell.setCellStyle(styles.get("header"));
    }

    //returns tree height
    private void calcTreeHeaderNodeRowWidthAndTreeHeight(HeaderTreeNode node) {
        if (isQuestion(node.assessmentElement)) {
            node.rowWidths = 1;
            node.subTreeHeight = 0;
        }
        if (node.assessmentElement == null || isPanel(node.assessmentElement) && CollectionUtils.isNotEmpty(node.children)) {
            node.children.forEach(this::calcTreeHeaderNodeRowWidthAndTreeHeight);
            node.rowWidths = node.children.stream().mapToInt(child -> child.rowWidths).sum();

            var maxSubTreeHeight = node.children.stream().mapToInt(c -> c.subTreeHeight).max().orElse(0);
            node.subTreeHeight = maxSubTreeHeight + 1;
        }

    }

    protected boolean isPanel(AssessmentElement element) {
        return element != null && "panel".equals(element.getType());
    }

    protected boolean isQuestion(AssessmentElement element) {
        return element != null && Stream.of("panel", "html").noneMatch(t -> t.equals(element.getType()));
    }

    private static class HeaderTreeNode {
        //in order to write unified code for AssessmentPage root node and AssessmentElement during tree construction
        AssessmentElementsAware elementsAware;

        AssessmentElement assessmentElement;
        private int subTreeHeight;
        private int rowWidths;

        List<HeaderTreeNode> children;

        public HeaderTreeNode(AssessmentPage page) {
            this.elementsAware = page;
        }

        public HeaderTreeNode(AssessmentElement assessmentElement) {
            this.assessmentElement = assessmentElement;
            this.elementsAware = assessmentElement;
        }

        void addChild(HeaderTreeNode other) {
            if (this.children == null) {
                this.children = new ArrayList<>();
            }
            this.children.add(other);
        }
    }
}
