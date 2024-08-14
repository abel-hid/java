package labs.pm.app;

import labs.pm.data.Product;
import labs.pm.data.ProductManager;

import java.math.BigDecimal;
import static labs.pm.data.Rating.*;
import java.util.Comparator;
import java.time.LocalDate;

public class Shop {
    public static void main(String[] args) {

        ProductManager pm = new ProductManager("en-GB");
         pm.printProductReport(101);
         pm.printProductReport(10);
    }

}
