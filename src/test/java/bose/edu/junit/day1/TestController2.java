package bose.edu.junit.day1;

import com.google.gson.Gson;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JsonProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@DisplayName("Unit test for TestController")
public class TestController2 {

    private Controller objUnderTest;
    private Logger logger;
    private Service service;
    private Gson gson = new Gson();
    JsonProvider jsonProvider = Configuration.defaultConfiguration().jsonProvider();

    @BeforeEach
    public void init() {
        logger = mock(Logger.class);
        service = spy(Service.class);
        objUnderTest = new Controller(service, logger);
    }

    @Test
    @DisplayName("Unit test for test all products with region 1")
    public void testGetAllProducts() {
        String response = objUnderTest.getAllProducts("1");
        System.out.println(response);
        assertThat("Valid response", response, notNullValue());

        //https://github.com/json-path/JsonPath/tree/master/json-path-assert

        Object respAsJson = jsonProvider.parse(response);
        assertThat("Valid Lookup response must match", respAsJson, isJson(allOf(
                withJsonPath("$.status", equalTo("200")),
                withJsonPath("$.message", equalTo("Products fetched")),
                withJsonPath("$.products", hasSize(3)),
                withJsonPath("$.products[*].name",
                        containsInAnyOrder("ipad", "iphone", "mac book pro"))
        )));

        assertThat("Response Object", gson.fromJson(response, Response.class), allOf(
                hasProperty("status", is("200")),
                hasProperty("message", is("Products fetched")),
                hasProperty("products", hasSize(3))));

        verify(service).getAllProducts("1");
        verifyZeroInteractions(logger);
    }

    @Test
    @DisplayName("Unit test for test all products with region other")
    public void testGetAllProductsOtherRegion() {
        String response = objUnderTest.getAllProducts("2");
        System.out.println(response);
        assertThat("Valid response", response, notNullValue());

        //https://github.com/json-path/JsonPath/tree/master/json-path-assert

        Object respAsJson = jsonProvider.parse(response);
        assertThat("Not supported region", respAsJson, isJson(allOf(
                withJsonPath("$.status", equalTo("400")),
                withJsonPath("$.message", equalTo("Region not supported")),
                withoutJsonPath("$.products")
        )));

        verify(service).getAllProducts("2");
        verifyZeroInteractions(logger);
    }

    @Test
    @DisplayName("Unit test for test all products with exception")
    public void testGetAllProductsException() {
        RuntimeException ex = new RuntimeException("AWS down");
        doThrow(ex).when(service).getAllProducts("3");

        String response = objUnderTest.getAllProducts("3");
        System.out.println(response);

        assertThat("Valid response", response, notNullValue());

        //https://github.com/json-path/JsonPath/tree/master/json-path-assert

        Object respAsJson = jsonProvider.parse(response);
        assertThat("Exception region", respAsJson, isJson(allOf(
                withJsonPath("$.status", equalTo("500")),
                withJsonPath("$.message", equalTo("Error in fetching products, please try later")),
                withoutJsonPath("$.products")
        )));

        verify(logger).log(ex);
    }
}
