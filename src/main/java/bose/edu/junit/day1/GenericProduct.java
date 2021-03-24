package bose.edu.junit.day1;

import java.util.Map;

public class GenericProduct {
    private final String name;
    private final String price;
    private final Map<String, String> subTypes;

    public GenericProduct(String name, String price, Map<String, String> subTypes) {
        this.name = name;
        this.price = price;
        this.subTypes = subTypes;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getSubTypes() {
        return subTypes;
    }
}
