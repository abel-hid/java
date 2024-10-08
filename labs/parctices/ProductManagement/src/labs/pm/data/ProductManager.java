package labs.pm.data;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.io.File.*;

public class ProductManager {
    private Map<Product, List<Review>> products = new HashMap<>();
    public static final Logger logger = Logger.getLogger(ProductManager.class.getName());

    private MessageFormat productFormat;
    private ResourceBundle config = ResourceBundle.getBundle("labs.pm.data.config");
    private MessageFormat reviewFormat;
    private Path reportsFolder;
    private Path dataFolder;
    private Path tempFolder;

    private static final Map<String, ResourceFormatter> formatters = Map.of(
        "en-GB", new ResourceFormatter(Locale.UK),
        "en-US", new ResourceFormatter(Locale.US),
        "ru-RU", new ResourceFormatter(new Locale("ru", "RU")),
        "fr-FR", new ResourceFormatter(Locale.FRANCE),
        "zh-CN", new ResourceFormatter(Locale.CHINA)
    );

    private ResourceFormatter formatter;

    public void changeLocale(String languageTag) 
    {
        formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
    }

    public static Set<String> getSupportedLocales() 
    {
        return formatters.keySet();
    }

    public ProductManager(Locale locale) 
    {
        this.formatter = formatters.getOrDefault(locale.toLanguageTag(), formatters.get("en-GB"));
    }

    public ProductManager(String languageTag) 
    {
        this.formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
        loadResourceBundle();
        initializeFields();
        loadAllData();
    }

    private void loadResourceBundle() 
    {
        try {
            config = ResourceBundle.getBundle("labs.pm.data.config");
            System.out.println("Resource Bundle loaded successfully.");
            System.out.println("Available keys: " + Arrays.toString(config.keySet().toArray()));
        } catch (MissingResourceException e) {
            logger.log(Level.SEVERE, "Resource bundle not found: labs.pm.data.config", e);
        }
    }
    
    
    private void initializeFields() 
    {
        if (config != null) {
            try {
                reviewFormat = new MessageFormat(config.getString("review.data.format"));
                productFormat = new MessageFormat(config.getString("product.data.format"));
                reportsFolder = Path.of(config.getString("reports.folder"));
                dataFolder = Path.of(config.getString("data.folder"));
                tempFolder = Path.of(config.getString("temp.folder"));
                System.out.println("Fields initialized successfully.");
            } catch (MissingResourceException e) {
                logger.log(Level.SEVERE, "Missing key in resource bundle", e);
            }
        } else {
            logger.log(Level.SEVERE, "Configuration is not loaded. Fields cannot be initialized.");
        }
    }
    private void loadAllData() 
    {
        try {
            dataFolder = Path.of("/Users/abel-hid/Desktop/oracle/labs/data");
            products = Files.list(dataFolder)
                .filter(file -> file.getFileName().toString().startsWith("product"))
                .map(file -> loadProduct(file))
                .filter(product -> product != null)
                .collect(Collectors.toMap(
                    product -> product,
                    product -> loadReviews(product)
                ));
        } catch (IOException e) 
        {
            logger.log(Level.SEVERE, "Error loading data: " + e.getMessage(), e);
        }
    }
    @SuppressWarnings("unchecked")
    public void restoreData() 
    {
        try 
        {
            Path tempFile = Files.list(tempFolder)
                .filter(path -> path.getFileName().toString().endsWith("tmp"))
                .findFirst()
                .orElseThrow();
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE)))
            {
                products = (HashMap) in.readObject();
                logger.log(Level.INFO, "Data restored successfully from " + tempFile);
                
            }
        } 
        catch (IOException | ClassNotFoundException e)
        {
            logger.log(Level.SEVERE, "Error restoring data: " + e.getMessage(), e);
        }
        finally
        {
            
        }
    }
 
    public void dumpData() 
    {
        if (products.isEmpty()) 
        {
            logger.log(Level.INFO, "No data to dump.");
        } 
        else 
        {
            try 
            {
                if (Files.notExists(tempFolder)) 
                {
                    Files.createDirectory(tempFolder);
                }
                Path tempFile = tempFolder.resolve(MessageFormat.format(config.getString("temp.data.file"), Instant.now()));
                try (ObjectOutput out = new ObjectOutputStream(Files.newOutputStream(tempFile, StandardOpenOption.CREATE)))
                {
                    out.writeObject(products);
                    products = new HashMap<>();
                    logger.log(Level.INFO, "Data dumped successfully to " + tempFile);
                }
            } 
            catch (IOException e) 
            {
                logger.log(Level.SEVERE, "Error dumping data: " + e.getMessage(), e);
            }
        }
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

    public Product findProduct(int id) throws ProductManagerException 
    {
        return products.keySet()
            .stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElseThrow(() -> new ProductManagerException("Product with id " + id + " not found"));
    }

    public Product reviewProduct(int id, Rating rating, String comments) 
    {
        try {
            return reviewProduct(findProduct(id), rating, comments);
        } catch (ProductManagerException e) {
            logger.log(Level.INFO, e.getMessage());
            return null;
        }
    }

    private Review parseReview(String text)
    {
        Review review = null;
        try {
            Object[] values = reviewFormat.parse(text);
            review = new Review(Rateable.convert(Integer.parseInt((String) values[0])), (String) values[1]);
            
        } catch (ParseException | NumberFormatException e)
        {
            logger.log(Level.WARNING, "Error parsing review: " + text, e.getMessage());
        }
        return review;
    }

   

    private Product loadProduct(Path file)
    {
        Product product = null;
        dataFolder = Path.of(config.getString("data.folder"));
        try 
        {
            product = parseProduct(Files.lines(dataFolder.resolve(file), Charset.forName("UTF-8"))
            .findFirst()
            .orElseThrow());
        }
        catch (IOException e)
        {
            logger.log(Level.WARNING, "Error loading product: " + e.getMessage());
        }
        return product;
    }

    private List<Review> loadReviews(Product product) 
    {
        List<Review> reviews = new ArrayList<>();
        Path file = dataFolder.resolve(MessageFormat.format(config.getString("review.data.file"), product.getId()));
        try {
            if (Files.notExists(file)) {
                System.out.println("Review file does not exist: " + file);
            } else {
                reviews = Files.lines(file, Charset.forName("UTF-8"))
                    .map(text -> parseReview(text))
                    .filter(review -> review != null)
                    .collect(Collectors.toList());
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error loading reviews: " + e.getMessage(), e);
        }
        return reviews;
    }
    
     
    private Product parseProduct(String text)
    {
        Product product = null;
        productFormat = new MessageFormat("{0},{1},{2},{3},{4},{5}");
        try {
            Object[] values = productFormat.parse(text);
            String type = (String) values[0];
            int id = Integer.parseInt((String) values[1]);
            String name = (String) values[2];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
            Rating rating = Rateable.convert(Integer.parseInt((String) values[4]));
            switch (type) {
                case "F":
                    product = new Food(id, name, price, rating, LocalDate.parse((String) values[5]));
                    break;
                case "D":
                    product = new Drink(id, name, price, rating);
                    break;
            }
        } catch (ParseException | NumberFormatException | DateTimeException e) {
            logger.log(Level.WARNING, "Error parsing product: " + text, e.getMessage());
        }
        return product;
    }
    

    public Product reviewProduct(Product product, Rating rating, String comments) 
    {
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
    
    public void printProductReport(int id)
    {
        try 
        {
            Product product = findProduct(id);
            printProductReport(product);
        } catch (ProductManagerException e) {
            logger.log(Level.INFO, e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error printing product report" + e.getMessage(), e);
        }
    }
    
    public void printProductReport(Product product) throws IOException 
    {
        System.out.println(formatter.formatProduct(product));
        List<Review> reviews = products.getOrDefault(product, new ArrayList<>());
        Collections.sort(reviews);
    
        Path productFile = reportsFolder.resolve(MessageFormat.format(config.getString("reports.file"), product.getId()));
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(
            Files.newOutputStream(productFile), "UTF-8")))
        {
            out.append(formatter.formatProduct(product)).append(System.lineSeparator());
        
            if (reviews.isEmpty()) {
                out.append(formatter.getText("no.review")).append(System.lineSeparator());
            } else {
                out.append(reviews.stream()
                    .map(r -> formatter.formatReview(r) + System.lineSeparator())
                    .reduce(String::concat)
                    .orElse(""));
            }
            System.out.println(out);
        }
    }
    
    
    

    public void printProducts(Predicate<Product> filter, Comparator<Product> sorter) 
    {
        StringBuilder txt = new StringBuilder();
        products.keySet().stream()
            .sorted(sorter)
            .filter(filter)
            .forEach(p -> txt.append(formatter.formatProduct(p)).append("\n"));
        System.out.println(txt);
    }

    public Map<String, String> getDiscounts() 
    {
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

    private static class ResourceFormatter 
    {
        private ResourceBundle resources;
        private Locale locale;
        private DateTimeFormatter dateFormat;
        private NumberFormat moneyFormat;
    
        private ResourceFormatter(Locale locale) 
        {
            this.locale = locale;
            try 
            {
                resources = ResourceBundle.getBundle("labs.pm.data.resources", locale);
            } 
            catch (MissingResourceException e) 
            {
                logger.log(Level.SEVERE, "Resource bundle not found for locale: " + locale, e);
                resources = ResourceBundle.getBundle("labs.pm.data.resources", Locale.ENGLISH);
            }
            dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }
    
        private String formatProduct(Product product) 
        {
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
    
        private String formatReview(Review review) 
        {
            return MessageFormat.format(getSafeString("review"),
                    review.rating().getStars(), review.comments());
        }
    
        private String getText(String key) 
        {
            return getSafeString(key);
        }
    
        private String getSafeString(String key) 
        {
            try 
            {
                return resources.getString(key);
            } 
            catch (MissingResourceException e) 
            {
                logger.log(Level.WARNING, "Missing resource key: " + key, e);
                return "Missing key: " + key;
            }
        }
    }
    
}
