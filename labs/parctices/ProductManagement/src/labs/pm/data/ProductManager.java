package labs.pm.data;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import labs.pm.data.Product;
import labs.pm.data.Review;
import labs.pm.data.Rating;

public class ProductManager {
    
    // private Product product;
    // private Review[] reviews = new Review[5];
    private Map<Product ,List<Review>> products = new HashMap<>();
    
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
 

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) 
    {
        Product product = new Food(id, name, price, rating, bestBefore);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) 
    {
        Product product = new Drink(id, name, price, rating);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product findProduct(int id)
    {
        Product result = null;

        for (Product product : products.keySet())
        {
            if (product.getId() == id)
            {
                result = product;
                break;
            }
        }
        return result;
        // Product product = products.keySet()
        //     .stream()
        //     .filter(p -> p.getId() == id)
        //     .findFirst()
        //     .orElse(null);
        // return product;
    }

    public Product reviewProduct(int id, Rating rating, String comments) 
    {
        return reviewProduct(findProduct(id), rating, comments);
    }

    public Product reviewProduct(Product product, Rating rating, String comments) 
    {
        List<Review> reviews = products.get(product);
        products.remove(product, reviews);
        reviews.add(new Review(rating, comments));
        int sum = 0;

        for (Review review : reviews)
        {
            if (review == null) {
                break;
            }
            sum += review.rating().ordinal();
        }
        product = product.applyRating(Rateable.convert(Math.round((float)sum / reviews.size())));
        products.put(product, reviews);
        return product;

    }
    public void printProductReport(int id)
    {
        printProductReport(findProduct(id));
    }

    public void printProductReport(Product product)
    {
        if (product == null)
        {
            System.out.println("No product available.");
            return;
        }
        List<Review> reviews = products.get(product);
        Collections.sort(reviews);
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
            txt.append(MessageFormat.format(resources.getString("review"),
                    review.rating().getStars(), review.comments()));
            txt.append("\n");

        }

        if(reviews.isEmpty())
        {
            txt.append(resources.getString("no.review"));
            txt.append("\n");
        }
        System.out.println(txt);
    }
    
    
}
