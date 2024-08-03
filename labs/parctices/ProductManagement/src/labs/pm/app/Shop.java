package labs.pm.app;

import labs.pm.data.Product;

import java.math.BigDecimal;

public class Shop {
    public static void main(String[] args) {

        Product p1 = new Product();
        p1.setId(101);
        p1.setName("tea");
        p1.setPrice(BigDecimal.valueOf(1.99));

        System.out.println(p1.getId() + " " + p1.getName() + " " + p1.getPrice());
    }
}