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
import labs.pm.data.Drink;
import labs.pm.data.Food;
public class ProductManager {
    private Map<Product ,List<Review>> products = new HashMap<>();
    private static Map<String, ResourceFormatter> formatters = 
    Map.of("en-GB", new ResourceFormatter(Locale.UK), 
            "en-US", new ResourceFormatter(Locale.US),
            "ru-RU", new ResourceFormatter(Locale.of("ru", "RU")),
            "fr-FR", new ResourceFormatter(Locale.FRANCE),
            "zh-CN", new ResourceFormatter(Locale.CHINA));

    private ResourceFormatter formatter;

    public void changeLocale(String languageTag)
    {
        formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
    }

    public static Set<String> getSupportedLocales()
    {
        return formatters.keySet();
    }


    // Constructor accepting Locale
    public ProductManager(Locale locale) {
        this.formatter = formatters.getOrDefault(locale.getLanguage(), formatters.get("en-GB"));
    }

    // Constructor accepting languageTag
    public ProductManager(String languageTag) {
        this.formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
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
    
        txt.append(formatter.formatProduct(product));
    
        txt.append("\n");

        for (Review review : reviews)
        {
            txt.append(formatter.formatReview(review));
            txt.append("\n");

        }

        if(reviews.isEmpty())
        {

            try {
                txt.append(formatter.getText("no.reviews"));
            } catch (MissingResourceException e) {
                txt.append("No reviews");
            }
        }
        System.out.println(txt);
    }
    public void printProducts(Comparator<Product> sorter)
    {
        List<Product> productList = new ArrayList<>(products.keySet());
        productList.sort(sorter);
        StringBuilder txt = new StringBuilder();
        for (Product product : productList)
        {
            txt.append(formatter.formatProduct(product));
            txt.append("\n");
        }
        System.out.println(txt);
    }

    private static class ResourceFormatter
    {
        private ResourceBundle resources;
        private Locale locale;
        private DateTimeFormatter dateFormat;
        private NumberFormat moneyFormat;
        private ResourceFormatter(Locale locale)
        {
            this.locale = locale;
            resources = ResourceBundle.getBundle("labs.pm.data.resources", locale);
            dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }

        private String formatProduct(Product product)
        {
            String type = null;

            if (product instanceof Food)
                type = resources.getString("food");
            else if (product instanceof Drink)
                type = resources.getString("drink");

            return MessageFormat.format(resources.getString("product"),
                    product.getName(),
                    moneyFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateFormat.format(product.getBestBefore()),
                    type);
        }
        private String formatReview(Review review)
        {
            return MessageFormat.format(resources.getString("review"),
            review.rating().getStars(), review.comments());
        }

        private String getText(String key)
        {
            return resources.getString(key);
        }

    }
    
    
}
