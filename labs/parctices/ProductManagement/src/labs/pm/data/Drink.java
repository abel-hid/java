package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public final class Drink  extends Product
{
    Drink(int id, String name, BigDecimal price, Rating rating) {
        super(id, name, price, rating);
    }

    @Override
    public BigDecimal getDiscount() {
        LocalTime now = LocalTime.now();
        LocalTime startDiscountTime = LocalTime.of(17, 30);
        LocalTime endDiscountTime = LocalTime.of(18, 30);
    
        return (now.isAfter(startDiscountTime) && now.isBefore(endDiscountTime))
            ? super.getDiscount()
            : BigDecimal.ZERO;
    }

    @Override
    public Product applyRating(Rating newRating) {
        return new Drink(getId(), getName(), getPrice(), newRating);
    }
}

