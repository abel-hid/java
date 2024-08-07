package labs.pm.app;

import labs.pm.data.Drink;
import labs.pm.data.Product;
import labs.pm.data.ProductManager;
import labs.pm.data.Rating;

import java.math.BigDecimal;
import static labs.pm.data.Rating.*;
import labs.pm.data.Food;
import java.time.LocalDate;
import java.util.Locale;


public class Shop {
    public static void main(String[] args) {
        
        ProductManager pm = new ProductManager(Locale.UK);
        Product p1 = pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), NOT_RATED);
       
        pm.printProductReport();
        p1 = pm.reviewProduct(p1, Rating.FOUR_STAR, "Nice hot cup of tea");
        pm.printProductReport();
    //     Product p2 = pm.createProduct(102, "Coffee", BigDecimal.valueOf(1.99), FOUR_STAR);
    //     Product p3 = pm.createProduct(103, "Cake", BigDecimal.valueOf(3.99), FIVE_STAR , LocalDate.now().plusDays(2));
    //     Product p4 = pm.createProduct(105, "Cookie", BigDecimal.valueOf(2.99), TWO_STAR , LocalDate.now().plusDays(2));

    //     Product p5 = p3.applyRating(THREE_STAR);
    //     // System.out.println(p5.getId() + " " + p5.getName() + " " + p5.getPrice() + " " + p5.getDiscount() + " " + p5.getRating().getStars());
    //     Product p6 =  pm.createProduct(104, "Chocolate", BigDecimal.valueOf(2.99), FIVE_STAR);
    //     Product p7 =  pm.createProduct(104, "Chocolate", BigDecimal.valueOf(2.99), FIVE_STAR , LocalDate.now().plusDays(2));
    //     Product p8 =  p4.applyRating(FIVE_STAR);
    //     Product p9 =  p1.applyRating(TWO_STAR);
    //     System.err.println(p1);
    //     System.err.println(p2);
    //     System.err.println(p3);
    //     System.err.println(p4);
    //     System.err.println(p5);
    //     System.err.println(p6.equals(p7));
    //     System.err.println(p8);
    //     System.err.println(p9);
    //    System.out.println(p3.getBestBefore());
    //    System.out.println(p1.getBestBefore());
        
    }
}