package com.adyen.ipp.service;

import com.adyen.ipp.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class TableService {
    private ArrayList<Table> tables;

    public TableService() {
        tables = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int tableNumber = i + 1;
            var table = new Table();
            table.setTableName("Table " + tableNumber);
            table.setAmount(BigDecimal.valueOf(22.22).multiply(BigDecimal.valueOf(tableNumber)));
            table.setCurrency("EUR");
            table.setPaymentStatus(PaymentStatus.NotPaid);
            table.setPaymentStatusDetails(new PaymentStatusDetails());
            tables.add(table);
        }
    }

    public ArrayList<Table> getTables() {
        return tables;
    }
}