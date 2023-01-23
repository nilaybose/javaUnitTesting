package bose.edu.junit.valueobjects;

import com.google.gson.Gson;

public class Controller {
    private Service service;
    private Logger logger ;

    public Controller(Service service, Logger logger) {
        this.service = service;
        this.logger = logger;
    }

    public String getAllProducts(String region) {
        Response response;
        try {
            response = service.getAllProducts(region);
        } catch (Exception ex) {
            logger.log(ex);
            response = new Response("500", "Error in fetching products, please try later", null);
        }
        return new Gson().toJson(response, Response.class);
    }
}
