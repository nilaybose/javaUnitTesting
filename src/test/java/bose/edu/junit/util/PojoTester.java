package bose.edu.junit.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.assertj.core.util.Sets;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * A utility class which allows for testing entity and value object classes.
 * This is mainly for code coverage since
 * these types of objects are normally nothing more than getters and setters.
 */
public class PojoTester {
    /**
     * A map of default mappers for common objects.
     */
    private static final ImmutableMap<Class<?>, Supplier<?>> DEFAULT_MAPPERS;
    private static final Map<Class<?>, Object> EQUALS_NULLIFY_MAPPER;
    private static final Map<String, Class<?>> ARRAY_FIELD_MAPPER;

    static {
        final Builder<Class<?>, Supplier<?>> mapperBuilder = ImmutableMap.builder();
        Random random = new Random(mapperBuilder.hashCode());

        /* Primitives */
        mapperBuilder.put(int.class, () -> 1 + random.nextInt(32767));
        mapperBuilder.put(double.class, () -> (double) (1 + random.nextInt(32767)));
        mapperBuilder.put(float.class, () -> (float) (1 + random.nextInt(32767)));
        mapperBuilder.put(long.class, () -> (long) (1 + random.nextInt(32767)));
        mapperBuilder.put(boolean.class, () -> true);
        mapperBuilder.put(short.class, () -> (short) (1 + random.nextInt(32767)));
        mapperBuilder.put(byte.class, () -> (byte) (1 + random.nextInt(127)));
        mapperBuilder.put(char.class, () -> (char) (1 + random.nextInt(127)));

        mapperBuilder.put(Integer.class, () -> 0);
        mapperBuilder.put(Double.class, () -> 0.0);
        mapperBuilder.put(Float.class, () -> 0.0f);
        mapperBuilder.put(Long.class, () -> 0L);
        mapperBuilder.put(Boolean.class, () -> Boolean.TRUE);
        mapperBuilder.put(Short.class, () -> (short) 0);
        mapperBuilder.put(Byte.class, () -> (byte) 0);
        mapperBuilder.put(Character.class, () -> (char) 0);
        mapperBuilder.put(String.class, () -> String.valueOf(random.nextInt(32767)));

        mapperBuilder.put(BigDecimal.class, () -> BigDecimal.ONE);
        mapperBuilder.put(Date.class, Date::new);
        mapperBuilder.put(LocalDateTime.class, LocalDateTime::now);

        /* Collection Types. */
        mapperBuilder.put(Set.class, () -> Sets.newLinkedHashSet(String.valueOf(random.nextInt(32767))));
        mapperBuilder.put(List.class, () -> Collections.singletonList(String.valueOf(random.nextInt(32767))));
        mapperBuilder.put(Map.class, () -> Collections.singletonMap("1", String.valueOf(random.nextInt(32767))));

        DEFAULT_MAPPERS = mapperBuilder.build();

        EQUALS_NULLIFY_MAPPER = new HashMap<>();
        EQUALS_NULLIFY_MAPPER.put(int.class, 0);
        EQUALS_NULLIFY_MAPPER.put(double.class, 0);
        EQUALS_NULLIFY_MAPPER.put(float.class, 0);
        EQUALS_NULLIFY_MAPPER.put(long.class, 0);
        EQUALS_NULLIFY_MAPPER.put(boolean.class, false);
        EQUALS_NULLIFY_MAPPER.put(short.class, 0);
        EQUALS_NULLIFY_MAPPER.put(byte.class, 0);
        EQUALS_NULLIFY_MAPPER.put(char.class, 0);

        ARRAY_FIELD_MAPPER = new HashMap<>();
        ARRAY_FIELD_MAPPER.put("int[]", int.class);
        ARRAY_FIELD_MAPPER.put("double[]", double.class);
        ARRAY_FIELD_MAPPER.put("float[]", float.class);
        ARRAY_FIELD_MAPPER.put("long[]", long.class);
        ARRAY_FIELD_MAPPER.put("boolean[]", boolean.class);
        ARRAY_FIELD_MAPPER.put("short[]", short.class);
        ARRAY_FIELD_MAPPER.put("byte[]", byte.class);
        ARRAY_FIELD_MAPPER.put("char[]", char.class);
    }

    /**
     * The get fields to ignore and not try to test.
     */
    private final Set<String> ignoredGetMethods;

    /**
     * The get fields to ignore equals and not try to test.
     */
    private final Set<String> ignoredEqualsAndHash;

    /**
     * A custom mapper. Normally used when the test class has abstract objects.
     */
    private final ImmutableMap<Class<?>, Supplier<?>> mappers;
    private Object objectUnderTest;

    /**
     * Creates an instance of {@link PojoTester} with ignore fields and additional custom mappers.
     *
     * @param customMappers     Any custom mappers for a given class type.
     * @param ignoredGetMethods The getters which should be ignored (e.g., "getId" or "isActive").
     */
    private PojoTester(Class<?> clazz, Map<Class<?>, Supplier<?>> customMappers,
                       Set<String> ignoredGetMethods, Set<String> ignoredEqualsAndHash) {
        this.ignoredGetMethods = new HashSet<>();
        if (ignoredGetMethods != null) {
            this.ignoredGetMethods.addAll(ignoredGetMethods);
        }
        this.ignoredGetMethods.add("getClass");

        if (customMappers == null) {
            this.mappers = DEFAULT_MAPPERS;
        } else {
            Builder<Class<?>, Supplier<?>> builder = ImmutableMap.builder();
            builder.putAll(customMappers);
            builder.putAll(DEFAULT_MAPPERS);
            this.mappers = builder.build();
        }
        this.ignoredEqualsAndHash
                = Optional.ofNullable(ignoredEqualsAndHash).orElseGet(HashSet::new);

        setObjectUnderTest(clazz);
    }

    /**
     * Tests a Pojo.
     * Creates an instance of {@link PojoTester} with the default ignore fields.
     *
     * @param clazz class to test
     * @return true if test passes
     */
    public static boolean validate(Class<?> clazz) {
        return validate(clazz, null, null);
    }

    /**
     * Creates an instance of {@link PojoTester} with ignore fields and additional custom mappers.
     *
     * @param clazz         class of object under test
     * @param customMappers Any custom mappers for a given class type.
     * @param ignoreFields  The getters which should be ignored (e.g., "getId" or "isActive").
     * @return true if test passes
     */
    public static boolean validate(Class<?> clazz, Map<Class<?>, Supplier<?>> customMappers,
                                   Set<String> ignoreFields) {
        new PojoTester(clazz, customMappers, ignoreFields, null).testGettersAndSetters();
        return true;
    }

    /**
     * Tests a pojo with equals and hashcode coverage.
     * Creates an instance of {@link PojoTester} with the default ignore fields.
     *
     * @param clazz class to test
     * @return true if test passes
     */
    public static boolean validateWithEqualsAndHashcode(Class<?> clazz) {
        return validateWithEqualsAndHashcode(clazz, null, null, null);
    }

    /**
     * Tests a pojo with equals and hashcode coverage.
     * Creates an instance of {@link PojoTester} with the default ignore fields.
     *
     * @param clazz        class to test
     * @param ignoreFields fields to ignore in equals and Hash test
     * @return true if test passes
     */
    public static boolean validateWithEqualsAndHashcode(Class<?> clazz, Set<String> ignoreFields) {
        return validateWithEqualsAndHashcode(clazz, null, null, ignoreFields);
    }

    /**
     * Creates an instance of {@link PojoTester} with ignore fields and additional custom mappers.
     *
     * @param clazz                class of object under test
     * @param customMappers        Any custom mappers for a given class type.
     * @param ignoreFields         The getters which should be ignored (e.g., "getId" or "isActive").
     * @param ignoredEqualsAndHash fields to ignore in equals and hash test
     * @return true if test passes
     */
    public static boolean validateWithEqualsAndHashcode(Class<?> clazz,
                                                        Map<Class<?>, Supplier<?>> customMappers,
                                                        Set<String> ignoreFields,
                                                        Set<String> ignoredEqualsAndHash) {
        PojoTester tester = new PojoTester(clazz, customMappers, ignoreFields, ignoredEqualsAndHash);
        tester.testGettersAndSetters();
        tester.testEqualsAndHashcode();
        return true;
    }

    /**
     * Tests the equality of Object under test with another object using shallow copy.
     */
    private void testEqualsAndHashcode() {
        final Object sameAsObjUnderTest = getAnObjectCopy(objectUnderTest);

        assertThat("Equality matches with self", objectUnderTest, is(objectUnderTest));
        assertThat("Not equals with other object", objectUnderTest.equals(new Object()), is(false));
        assertThat("Equality matches with other object of same properties",
                objectUnderTest, is(sameAsObjUnderTest));
        assertThat("Equality matches with other object of same properties",
                sameAsObjUnderTest, is(objectUnderTest));
        assertThat("Hashcode matches", objectUnderTest.hashCode(), is(sameAsObjUnderTest.hashCode()));

        getGetterSettersPair().forEach((k, v) -> {
            String fieldName = k.substring(0, 1).toLowerCase() + k.substring(1);
            if (!ignoredEqualsAndHash.contains(fieldName)) {
                copyProperties(objectUnderTest, sameAsObjUnderTest);
                JUnitReflectionUtil.setObjectField(sameAsObjUnderTest, fieldName,
                        EQUALS_NULLIFY_MAPPER.getOrDefault(v.getGetter().getReturnType(), null));

                assertThat("Must not be equal with object of different property",
                        objectUnderTest, is(not(sameAsObjUnderTest)));
                assertThat("Hash code must not same with object of different property",
                        objectUnderTest.hashCode(), is(not(sameAsObjUnderTest.hashCode())));
            }
        });
    }

    /**
     * This method gets an object under test.
     * It also scans all the constructor for code coverage
     *
     * @param clazz Class of object under test
     */
    private void setObjectUnderTest(Class<?> clazz) {
        Constructor[] allConstructors = clazz.getDeclaredConstructors();
        for (Constructor constructor : allConstructors) {
            try {
                constructor.setAccessible(true);
                Class<?>[] parameterTypesType = constructor.getParameterTypes();
                Object[] consArgs = new Object[parameterTypesType.length];
                for (int i = 0; i < parameterTypesType.length; i++) {
                    consArgs[i] = createField(String.format("%s - param [%d]", constructor, i),
                            parameterTypesType[i]);
                }

                objectUnderTest = constructor.newInstance(consArgs);
            } catch (Exception ex) {
                //ignore if a constructor cannot be invoked
            }
        }
        if (objectUnderTest == null) {
            throw new IllegalStateException("Unable to create object under test of type: " + clazz);
        }
    }

    /**
     * Calls a getter and verifies the result is what is expected.
     *
     * @param fieldName       The field name (used for error messages).
     * @param getter          The get {@link Method}.
     * @param objectUnderTest The test object.
     * @param expected        The expected result.
     * @throws InvocationTargetException if the underlying method throws an exception.
     */
    private void verifyGetter(String fieldName, Method getter,
                              Object objectUnderTest, Object expected)
            throws IllegalAccessException, InvocationTargetException {
        final Object getResult = getter.invoke(objectUnderTest);

        if (getter.getReturnType().isPrimitive()) {
            assertThat(fieldName + " is different", expected, is(getResult));
        } else {
            //The object passed must exactly match.
            assertThat(fieldName + " is different", expected, sameInstance(getResult));
        }
    }

    /**
     * Creates an object for the given {@link Class}.
     *
     * @param fieldName The name of the field.
     * @param clazz     The {@link Class} type to create.
     * @return Object A new instance for the given field object
     * @throws IllegalStateException If this Class instantiation fails or
     *                               If the class or its default constructor is not accessible.
     */
    private Object createField(String fieldName, Class<?> clazz) {
        Object field;
        try {
            if (clazz.getSimpleName().contains("[]")) {
                return getArrayField(clazz);
            }
            final Supplier<?> supplier = this.mappers.get(clazz);
            if (supplier != null) {
                return supplier.get();
            }

            if (clazz.isEnum()) {
                return clazz.getEnumConstants()[0];
            }

            field = createFldUsingDftConstructor(clazz);

            if (field == null) {
        /* Try to find a constructor for a field object, no default constructor present
           object may be immutable */
                field = createFldUsingOtherConstructor(clazz);
            }
        } catch (Exception ex) {
            throw new IllegalStateException(String.format("Unable to create field [%s] of type [%s]",
                    fieldName, clazz), ex);
        }
        return field;
    }

    /**
     * Return field using default constructor.
     *
     * @param clazz class type
     * @return object
     */
    private Object createFldUsingDftConstructor(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception ex) { //no default constructor or may not be public
            return null;
        }
    }

    /**
     * Return field using any of the constructors.
     *
     * @param clazz class type
     * @return object
     */
    private Object createFldUsingOtherConstructor(Class<?> clazz) {
        Object field = null;
        Constructor[] allConstructors = clazz.getDeclaredConstructors();
        for (Constructor constructor : allConstructors) {
            try {
                constructor.setAccessible(true);
                Class<?>[] parameterTypesType = constructor.getParameterTypes();
                Object[] consArgs = new Object[parameterTypesType.length];
                for (int i = 0; i < parameterTypesType.length; i++) {
                    consArgs[i] = createField(String.format("%s - param [%d]", constructor, i),
                            parameterTypesType[i]);
                }
                field = constructor.newInstance(consArgs);
                break;
            } catch (Exception ex) {
                //ignore
            }
        }
        if (field == null) {
            throw new IllegalStateException("Unable to create a field of type: " + clazz);
        }
        return field;
    }

    /**
     * Tests all the getters and setters. This will also use reflection to
     * set the field if no setter exists.
     */
    private void testGettersAndSetters() {
        try {
            Map<String, GetterSetterPair> getterSetterMapping = getGetterSettersPair();
            /*
             * Found all our mappings. Now call the getter and setter or set the field via reflection
             * and call the getting it doesn't have a setter.
             */
            for (final Entry<String, GetterSetterPair> entry : getterSetterMapping.entrySet()) {
                final GetterSetterPair pair = entry.getValue();

                final String objectName = entry.getKey();
                final String fieldName = objectName.substring(0, 1).toLowerCase() + objectName.substring(1);

                if (pair.hasGetterAndSetter()) {
                    /* Create an object. */
                    final Class<?> parameterType = pair.getSetter().getParameterTypes()[0];
                    final Object newObject = createField(fieldName, parameterType);

                    Object objReturn =
                            pair.getSetter().invoke(objectUnderTest, newObject);

                    /* fluent style pojo setter return check */
                    if (objReturn != null) {
                        assertThat("Return of setter must match pojo objectUnderTest", objReturn,
                                sameInstance(objectUnderTest));
                    }
                    verifyGetter(fieldName, pair.getGetter(), objectUnderTest, newObject);
                } else if (pair.getGetter() != null) {
                    /*
                     * Object has no setter. Using reflection to set object and verify that same object
                     * is returned when calling the getter.
                     */
                    final Object newObject = createField(fieldName, pair.getGetter().getReturnType());
                    JUnitReflectionUtil.setObjectField(objectUnderTest, fieldName, newObject);

                    verifyGetter(fieldName, pair.getGetter(), objectUnderTest, newObject);
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Exception in Pojo Tester", ex);
        }
    }

    /**
     * Returns map of field name and associated getter and setter.
     *
     * @return Map - field name and getter setter pair
     */
    private Map<String, GetterSetterPair> getGetterSettersPair() {
        /* Sort items for consistent test runs. */
        final SortedMap<String, GetterSetterPair> getterSetterMapping = new TreeMap<>();

        for (final Method method : objectUnderTest.getClass().getMethods()) {
            final String methodName = method.getName();

            if (this.ignoredGetMethods.contains(methodName)) {
                continue;
            }

            String objectName;
            GetterSetterPair getterSettingPair = new GetterSetterPair();
            if (methodName.startsWith("get") && method.getParameters().length == 0) {
                /* Found the get method. */
                objectName = methodName.substring("get".length());
                getterSettingPair.setGetter(method);

                getterSetterMapping.merge(objectName, getterSettingPair,
                        (k, v) -> {
                            k.setGetter(method);
                            return k;
                        });
            } else if (methodName.startsWith("set") && method.getParameters().length == 1) {
                /* Found the set method. */
                objectName = methodName.substring("set".length());
                getterSettingPair.setSetter(method);

                getterSetterMapping.merge(objectName, getterSettingPair,
                        (k, v) -> {
                            k.setSetter(method);
                            return k;
                        });
            } else if (methodName.startsWith("is") && method.getParameters().length == 0) {
                /* Found the is method, which really is a get method. */
                objectName = methodName.substring("is".length());
                getterSettingPair.setGetter(method);

                getterSetterMapping.merge(objectName, getterSettingPair,
                        (k, v) -> {
                            k.setGetter(method);
                            return k;
                        });
            }
        }
        return getterSetterMapping;
    }

    /**
     * Get an object as param using shallow copy.
     *
     * @param objectUnderTest object to copy
     * @return copied object
     */
    private Object getAnObjectCopy(Object objectUnderTest) {
        Object sameAsObjectUnderTest = createField("copy", objectUnderTest.getClass());
        getGetterSettersPair().forEach((k, v) -> {
            String fieldName = k.substring(0, 1).toLowerCase() + k.substring(1);
            JUnitReflectionUtil.setObjectField(sameAsObjectUnderTest, fieldName,
                    JUnitReflectionUtil.getObjectField(objectUnderTest, fieldName));
        });
        return sameAsObjectUnderTest;
    }

    /**
     * Shallow copy.
     *
     * @param source source object
     * @param target target object
     */
    private void copyProperties(Object source, Object target) {
        getGetterSettersPair().forEach((k, v) -> {
            String fieldName = k.substring(0, 1).toLowerCase() + k.substring(1);
            JUnitReflectionUtil.setObjectField(target, fieldName,
                    JUnitReflectionUtil.getObjectField(source, fieldName));
        });
    }

    private Object getArrayField(Class<?> clazz) {
        try {
            String arrayClassName = clazz.getSimpleName();

            Class arrayClazz = ARRAY_FIELD_MAPPER.get(arrayClassName);
            if (arrayClazz == null) {
                arrayClazz = Class.forName(clazz.getCanonicalName().replaceAll("\\[]", ""));
            }

            Object object = Array.newInstance(arrayClazz, 1);
            Array.set(object, 0, createField("array field", arrayClazz));
            return object;
        } catch (Exception ex) {
            throw new IllegalStateException("unable to create array class");
        }
    }
}

