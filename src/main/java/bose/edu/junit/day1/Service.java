package bose.edu.junit.day1;

import com.google.common.collect.Lists;

import java.util.List;

public class Service {
    Response response ;
    public Response getAllProducts(String region) {
        if ("1".equals(region)) {
            List<Product> products = Lists.newArrayList(new Product("ipad", "1000$"),
                    new Product("iphone", "700$"), new Product("mac book pro", "2000$"));

            response = new Response( "200", "Products fetched", products);
        }
        else{
            response = new Response( "400", "Region not supported", null);
        }

        return response ;
    }
}
