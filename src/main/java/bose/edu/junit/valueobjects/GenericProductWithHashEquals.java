package bose.edu.junit.valueobjects;

import java.util.Map;
import java.util.Objects;

public class GenericProductWithHashEquals {
    private final String name;
    private final String price;
    private final Map<String, String> subTypes;

    public GenericProductWithHashEquals(String name, String price, Map<String, String> subTypes) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericProductWithHashEquals that = (GenericProductWithHashEquals) o;
        return Objects.equals(name, that.name)
                && Objects.equals(price, that.price) && Objects.equals(subTypes, that.subTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, subTypes);
    }
}
