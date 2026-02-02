package com.scnsoft.exchange.audit.controller;

import com.scnsoft.exchange.audit.model.ReportDto;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public abstract class ExcelView<T extends ReportDto> {
    public abstract String getFileName();

    public abstract String getSheetName();

    public abstract String[] getColumnNames();

    public abstract void write(HSSFSheet sheet, int rowNumber, T dto);

    protected HSSFCell getCell(HSSFSheet sheet, int row, int col) {
        HSSFRow sheetRow = sheet.getRow(row);
        if (sheetRow == null) {
            sheetRow = sheet.createRow(row);
        }
        HSSFCell cell = sheetRow.getCell((short) col);
        if (cell == null) {
            cell = sheetRow.createCell((short) col);
        }
        return cell;
    }

    protected void setText(HSSFCell cell, String text) {
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue(text);
    }

}
