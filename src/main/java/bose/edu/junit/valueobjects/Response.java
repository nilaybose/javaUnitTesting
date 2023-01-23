package bose.edu.junit.valueobjects;

import java.util.List;

public class Response {
    private final String status;
    private final String message;
    private final List<Product> products;

    public Response(String status, String message, List<Product> products) {
        this.status = status;
        this.message = message;
        this.products = products;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Product> getProducts() {
        return products;
    }
}
