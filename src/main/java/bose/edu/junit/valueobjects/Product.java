package bose.edu.junit.valueobjects;

public class Product {
    private final String name;
    private final String price;


    public Product(String name, String price){
        this.name = name;
        this.price = price ;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
