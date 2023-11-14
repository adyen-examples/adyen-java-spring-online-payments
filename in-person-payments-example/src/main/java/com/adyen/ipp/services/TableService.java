import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TableService {
    private List<TableModel> tables;

    public TableService() {
        tables = new ArrayList<>();

        // Add tables.
        for (int i = 0; i < 4; i++) {
            int tableNumber = i + 1;
            tables.add(new TableModel(
                    "Table " + tableNumber,
                    BigDecimal.valueOf(22.22).multiply(BigDecimal.valueOf(tableNumber)),
                    "EUR",
                    PaymentStatus.NotPaid,
                    new PaymentStatusDetails()
            ));
        }
    }

    public List<TableModel> getTables() {
        return tables;
    }
}