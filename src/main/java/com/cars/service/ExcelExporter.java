package com.cars.service;

import com.cars.model.Car;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ExcelExporter {

    void exportExcel(List<Car> listUsers, HttpServletResponse response);

}
