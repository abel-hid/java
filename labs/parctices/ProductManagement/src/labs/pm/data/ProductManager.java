package labs.pm.data;
import java.lang.reflect.Array;
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
import java.util.Arrays;

public class ProductManager {
    private Product product;

    // private Review review;
    private Review[] reviews = new Review[5];
    
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
        // review = new Review(rating, comments);
        if(reviews[reviews.length - 1] != null)
        {
            // Review[] temp = new Review[reviews.length + 5];
            // System.arraycopy(reviews, 0, temp, 0, reviews.length);
            // reviews = temp;
            reviews = Arrays.copyOf(reviews, reviews.length + 5);
        }
        int sum = 0;
        boolean reviewed = false;
        int i = 0;
        while(!reviewed && i < reviews.length)
        {
            if(reviews[i] == null)
            {
                reviews[i] = new Review(rating, comments);
                reviewed = true;
            }
            sum += reviews[i].rating().ordinal();
            i++;
        }
        this.product = product.applyRating(Rateable.convert(Math.round((float)sum / i)));
        return this.product;

    }

    public void printProductReport() {
        if (product == null)
        {
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

        for (Review review : reviews)
        {
            if (review == null) {
                break;
            }
            txt.append(MessageFormat.format(resources.getString("review"),
                    review.rating().getStars(), review.comments()));
            txt.append("\n");

        }

        if(reviews[0] == null)
        {
            txt.append(resources.getString("no.review"));
            txt.append("\n");
        }
        System.out.println(txt);
    }
    
    
}
