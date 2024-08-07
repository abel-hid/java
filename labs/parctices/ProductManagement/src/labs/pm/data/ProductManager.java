package labs.pm.data;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;

import labs.pm.data.Product;
import labs.pm.data.Review;
import labs.pm.data.Rating;

public class ProductManager {
    private Product product;

    private Review review;
    
    private ResourceBundle resources;
    private Locale locale;
    private DateTimeFormatter dateFormat;
    private NumberFormat moneyFormat;

    public ProductManager(Locale locale) 
    {
        this.locale = locale;
        resources = ResourceBundle.getBundle("labs.pm.data.resources", locale);
        dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
        moneyFormat = NumberFormat.getCurrencyInstance(locale);
    }
    // public ProductManager() {
    //     this.product = new Food(0, "Product", 0, Rating.NOT_RATED, null);
    // }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) 
    {
        product = new Food(id, name, price, rating, bestBefore);
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) 
    {
        product = new Drink(id, name, price, rating);
        return product;
    }

    public Product reviewProduct(Product product, Rating rating, String comments) 
    {
        review = new Review(rating, comments);
        this.product = product.applyRating(rating);
        return this.product;

    }

    public void printProductReport() {
        if (product == null) {
            System.out.println("No product available.");
            return;
        }
    
        StringBuilder txt = new StringBuilder();
        String type;
    
        if (product instanceof Food) {
            type = resources.getString("food");
        } else if (product instanceof Drink) {
            type = resources.getString("drink");
        } else {
            type = "Unknown";
        }
    
        // Format product details
        txt.append(MessageFormat.format(resources.getString("product"),
                product.getName(),
                moneyFormat.format(product.getPrice()),
                product.getRating().getStars(),
                dateFormat.format(product.getBestBefore()),
                type));
    
        txt.append("\n");
        if (review != null) {
            txt.append(MessageFormat.format(resources.getString("review"),
                    review.rating().getStars(), review.comments()));
        } else {
            txt.append(resources.getString("no.review"));
        }
        txt.append("\n");
        System.out.println(txt);
    }
    
    
}
