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
         pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), NOT_RATED);
            pm.reviewProduct(101, FOUR_STAR, "Nice hot cup of tea");
            pm.reviewProduct(101, TWO_STAR, "Rather weak tea");
            pm.reviewProduct(101, FOUR_STAR, "Fine tea");
            pm.reviewProduct(101, THREE_STAR, "Good tea");
            pm.reviewProduct(101, FIVE_STAR, "Perfect tea");
            pm.reviewProduct(101, ONE_STAR, "Horrible tea");
            pm.dumpData();
            pm.restoreData();
            pm.printProductReport(101);

            pm.printProducts(p -> p.getPrice().floatValue() < 2, (p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal());
    }

}
