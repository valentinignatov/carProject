package com.cars.service.impl;

import com.cars.model.Car;
import com.cars.service.CarService;
import com.cars.service.ExcelExporter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExporterImpl implements ExcelExporter {
    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet;
    private List<Car> carList;

    public ExcelExporterImpl() {}

    @Override
    public void exportExcel(List<Car> carList, HttpServletResponse response) {
        try {
            this.carList = carList;
            export(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Offer Type", style);
        createCell(row, 2, "Price", style);
        createCell(row, 3, "Negociable", style);
        createCell(row, 4, "Author Type", style);
        createCell(row, 5, "Model", style);
        createCell(row, 6, "Condition", style);
        createCell(row, 7, "Registration", style);
        createCell(row, 8, "Registration Year", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        int i = 0;

        for (Car car : carList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            System.out.println(i);
            if (i > 199) break;

            createCell(row, columnCount++, car.getId().toString(), style);
            createCell(row, columnCount++, car.getOfferType().toString(), style);
            createCell(row, columnCount++, car.getPrice().toString(), style);
            createCell(row, columnCount++, car.getNegotiable(), style);
            createCell(row, columnCount++, car.getAuthorType().toString(), style);
            createCell(row, columnCount++, car.getModel().toString(), style);
            createCell(row, columnCount++, car.getCondition().toString(), style);
            createCell(row, columnCount++, car.getRegistration().toString(), style);
            createCell(row, columnCount++, car.getRegYear(), style);

            i++;
        }
    }

    private void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
