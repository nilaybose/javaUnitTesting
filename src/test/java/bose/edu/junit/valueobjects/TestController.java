package bose.edu.junit.valueobjects;

import com.google.gson.Gson;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JsonProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit test for TestController")
public class TestController {
    private Gson gson = new Gson();
    private JsonProvider jsonProvider = Configuration.defaultConfiguration().jsonProvider();

    @Mock
    private Logger logger ;
    @Spy
    private Service service ;

    @InjectMocks
    private Controller objUnderTest;

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
        RuntimeException ex = new RuntimeException("AWS down") ;
        doThrow(ex).when(service).getAllProducts("3") ;

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
