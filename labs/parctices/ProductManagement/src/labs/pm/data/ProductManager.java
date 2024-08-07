package labs.pm.data;
import java.math.BigDecimal;
import java.time.LocalDate;
import labs.pm.data.Product;
import labs.pm.data.Review;
public class ProductManager {
    private Product product;

    private Review review;
    

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
        StringBuilder txt = new StringBuilder();
        txt.append(product);
        txt.append("\n");
        if (review != null) {
            txt.append(review);
        }
        else {
            txt.append("Not reviewed yet");
        }
        System.out.println(txt);
    }
}
