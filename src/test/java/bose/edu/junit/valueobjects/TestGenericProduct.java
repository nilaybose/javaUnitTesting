package bose.edu.junit.valueobjects;

import bose.edu.junit.util.PojoTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestGenericProduct {
    @Test
    @DisplayName("Pojo setter getter test")
    public void test() {
        assertThat("Validation passes",
                PojoTester.validate(GenericProduct.class), is(true));
    }
}
