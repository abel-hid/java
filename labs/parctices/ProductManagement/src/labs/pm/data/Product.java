package labs.pm.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author abel-hid
 **/
public sealed abstract class Product implements Rateable<Product> permits Food, Drink {
    private int id;
    private String name;
    private BigDecimal price;
    private Rating rating;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price)
    {
        this.price = price;
    }
    public static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.1);

    public BigDecimal getDiscount() {
        return price.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public Rating getRating() {
        return rating;
    }

    Product(int id, String name, BigDecimal price, Rating rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    // public Product(int id, String name, BigDecimal price) {
    //     this(id, name, price, Rating.NOT_RATED);
    // }

    // public Product() {
    //     this(0, "no name", BigDecimal.ZERO, Rating.NOT_RATED);
    // }
    // public abstract Product applyRating(Rating newRating);

    public LocalDate getBestBefore() {
        return LocalDate.now();
    }

    @Override
    public String toString() {
        return id + ", " + name + ", " + price + ", " + getDiscount() + ", " + rating.getStars() + " " + getBestBefore();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // if (o == null || getClass() != o.getClass()) return false;
        // Product product = (Product) o;
        // return id == product.id && Objects.equals(name, product.name);
        if(o instanceof Product) 
        {
            Product product = (Product) o;
            return id == product.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
