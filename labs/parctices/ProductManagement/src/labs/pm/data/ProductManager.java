package labs.pm.data;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProductManager {
    private Map<Product, List<Review>> products = new HashMap<>();
    public static final Logger logger = Logger.getLogger(ProductManager.class.getName());

    private ResourceBundle config = ResourceBundle.getBundle("labs.pm.data.config");
    private MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    // private MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    private MessageFormat productFormat = new MessageFormat("{0},{1},{2},{3},{4},{5}");

    private static final Map<String, ResourceFormatter> formatters = Map.of(
        "en-GB", new ResourceFormatter(Locale.UK),
        "en-US", new ResourceFormatter(Locale.US),
        "ru-RU", new ResourceFormatter(new Locale("ru", "RU")),
        "fr-FR", new ResourceFormatter(Locale.FRANCE),
        "zh-CN", new ResourceFormatter(Locale.CHINA)
    );

    private ResourceFormatter formatter;

    public void changeLocale(String languageTag) {
        formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
    }

    public static Set<String> getSupportedLocales() {
        return formatters.keySet();
    }

    public ProductManager(Locale locale) {
        this.formatter = formatters.getOrDefault(locale.toLanguageTag(), formatters.get("en-GB"));
    }

    public ProductManager(String languageTag) {
        this.formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        Product product = new Food(id, name, price, rating, bestBefore);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        Product product = new Drink(id, name, price, rating);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product findProduct(int id) throws ProductManagerException {
        return products.keySet()
            .stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElseThrow(() -> new ProductManagerException("Product with id " + id + " not found"));
    }

    public Product reviewProduct(int id, Rating rating, String comments) {
        try {
            return reviewProduct(findProduct(id), rating, comments);
        } catch (ProductManagerException e) {
            logger.log(Level.INFO, e.getMessage());
            return null;
        }
    }

    public void parseReview(String text) {

        try {
            Object[] values = reviewFormat.parse(text);
            int id = Integer.parseInt((String) values[0]);
            Rating rating = Rateable.convert(Integer.parseInt((String) values[1]));
            String comments = (String)values[2];
            reviewProduct(id, rating, comments);
            
        } catch (ParseException | NumberFormatException e)
        {
            logger.log(Level.WARNING, "Error parsing review: " + text, e.getMessage());
        }
    }
    public void parseProduct(String text) {
        try {
            Object[] values = productFormat.parse(text);
            String type = (String) values[0];
            int id = Integer.parseInt((String) values[1]);
            String name = (String) values[2];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
            Rating rating = Rateable.convert(Integer.parseInt((String) values[4]));
            switch (type) {
                case "F":
                    createProduct(id, name, price, rating, LocalDate.parse((String) values[5]));
                    break;
                case "D":
                    createProduct(id, name, price, rating);
                    break;
            }
        } catch (ParseException | NumberFormatException | DateTimeException e) {
            logger.log(Level.WARNING, "Error parsing product: " + text, e.getMessage());
        }
    }
    

    public Product reviewProduct(Product product, Rating rating, String comments) {
        List<Review> reviews = products.get(product);
        if (reviews == null) {
            reviews = new ArrayList<>();
            products.put(product, reviews);
        }
        reviews.add(new Review(rating, comments));
    
        product = product.applyRating(Rateable.convert(
            (int) Math.round(reviews.stream()
                .mapToInt(r -> r.rating().ordinal())
                .average()
                .orElse(0))));
    
        products.remove(product);
        products.put(product, reviews); 
        return product;
    }
    
    public void printProductReport(int id) {
        try {
            Product product = findProduct(id);
            printProductReport(product);
        } catch (ProductManagerException e) {
            logger.log(Level.INFO, e.getMessage());
        }
    }
    public void printProductReport(Product product) {
        List<Review> reviews = products.getOrDefault(product, new ArrayList<>());
        Collections.sort(reviews);
    
        StringBuilder txt = new StringBuilder();
        txt.append(formatter.formatProduct(product)).append("\n");
    
        if (reviews.isEmpty()) {
            txt.append(formatter.getText("no.review")).append("\n");
        } else {
            txt.append(reviews.stream()
                .map(r -> formatter.formatReview(r) + "\n")
                .reduce(String::concat)
                .orElse(""));
        }
        System.out.println(txt);
    }
    

    public void printProducts(Predicate<Product> filter, Comparator<Product> sorter) {
        StringBuilder txt = new StringBuilder();
        products.keySet().stream()
            .sorted(sorter)
            .filter(filter)
            .forEach(p -> txt.append(formatter.formatProduct(p)).append("\n"));
        System.out.println(txt);
    }

    public Map<String, String> getDiscounts() {
        return products.keySet().stream()
            .collect(
                Collectors.groupingBy(
                    p -> p.getRating().getStars(),
                    Collectors.collectingAndThen(
                        Collectors.summarizingDouble(p -> p.getDiscount().doubleValue()),
                        summary -> formatter.moneyFormat.format(summary.getAverage())
                    )
                )
            );
    }

    private static class ResourceFormatter {
        private ResourceBundle resources;
        private Locale locale;
        private DateTimeFormatter dateFormat;
        private NumberFormat moneyFormat;
    
        private ResourceFormatter(Locale locale) {
            this.locale = locale;
            try {
                resources = ResourceBundle.getBundle("labs.pm.data.resources", locale);
            } catch (MissingResourceException e) {
                logger.log(Level.SEVERE, "Resource bundle not found for locale: " + locale, e);
                resources = ResourceBundle.getBundle("labs.pm.data.resources", Locale.ENGLISH);
            }
            dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }
    
        private String formatProduct(Product product) {
            String type = null;
            if (product instanceof Food)
                type = getSafeString("food");
            else if (product instanceof Drink)
                type = getSafeString("drink");
    
            return MessageFormat.format(getSafeString("product"),
                    product.getName(),
                    moneyFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateFormat.format(product.getBestBefore()),
                    type);
        }
    
        private String formatReview(Review review) {
            return MessageFormat.format(getSafeString("review"),
                    review.rating().getStars(), review.comments());
        }
    
        private String getText(String key) {
            return getSafeString(key);
        }
    
        private String getSafeString(String key) {
            try {
                return resources.getString(key);
            } catch (MissingResourceException e) {
                logger.log(Level.WARNING, "Missing resource key: " + key, e);
                return "Missing key: " + key;
            }
        }
    }
    
}
