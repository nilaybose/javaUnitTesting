package bose.edu.junit.day1;

import bose.edu.junit.util.PojoTester;
import com.google.common.collect.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestGenericProductWithHashEquals {
    @Test
    @DisplayName("Pojo setter getter test")
    public void test() {
        assertThat("Validation passes",
                PojoTester.validateWithEqualsAndHashcode(
                        GenericProductWithHashEquals.class, null), is(true));
    }
}
