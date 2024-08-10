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
        pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), NOT_RATED);
        pm.reviewProduct(101, THREE_STAR, "Nice hot cup of tea");
        pm.reviewProduct(101, TWO_STAR, "Rather weak tea");
        pm.reviewProduct(101, FOUR_STAR, "Fine tea");
        pm.reviewProduct(101, FOUR_STAR, "Good tea");
        pm.reviewProduct(101, FIVE_STAR, "Perfect tea");
        pm.reviewProduct(101, THREE_STAR, "Just add some lemon");


        pm.createProduct(102, "Coffee", BigDecimal.valueOf(1.99), NOT_RATED);
        pm.reviewProduct(102, THREE_STAR, "Coffee was ok");
        pm.reviewProduct(102, ONE_STAR, "Where is the milk?!");
        pm.reviewProduct(102, FIVE_STAR, "It's perfect with ten spoons of sugar!");

        pm.createProduct(103, "Cake", BigDecimal.valueOf(3.99), NOT_RATED, LocalDate.now().plusDays(2));
        pm.reviewProduct(103, FIVE_STAR, "Very nice cake");
        pm.reviewProduct(103, FOUR_STAR, "It good, but I've expected more chocolate");

        pm.createProduct(104, "Cookie", BigDecimal.valueOf(2.99), NOT_RATED, LocalDate.now().plusDays(5));
        pm.reviewProduct(104, THREE_STAR, "Just another cookie");
        pm.reviewProduct(104, THREE_STAR, "OK");

        pm.createProduct(105, "Hot Chocolate", BigDecimal.valueOf(2.50), NOT_RATED);
        pm.reviewProduct(105, FOUR_STAR, "Tasty!");
        pm.reviewProduct(105, FOUR_STAR, "Not bad at all");

        pm.createProduct(106, "Chocolate", BigDecimal.valueOf(2.50), NOT_RATED);
        pm.reviewProduct(106, TWO_STAR, "Too sweet");
        pm.reviewProduct(106, THREE_STAR, "Better than other cookies");
        pm.reviewProduct(106, TWO_STAR, "Too bitter");
        pm.reviewProduct(106, ONE_STAR, "I don't like it");
        pm.printProductReport(106);
       pm.printProducts(p -> p.getPrice().floatValue() < 2, (p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal());
    }
}
