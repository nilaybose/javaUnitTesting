package bose.edu.junit.util;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class to help in unit testing by accessing member and method at object and class level.
 *
 * @author worldpay
 */
public class JUnitReflectionUtil {
  /**
   * Cannonical class name.
   */
  private static final String FIELD_NAME_NOT_FOUND = "Field Name [%s] not found in class [%s]";

  private static final String METHOD_NAME_NOT_FOUND = "Method Name [%s] not found in class [%s]";

  private JUnitReflectionUtil() {
  }

  /**
   * This method sets the static field of the class.
   *
   * @param clazz      Class
   * @param fieldName  Field Name who value needs to be set
   * @param fieldValue Field value to be set
   */
  public static void setStaticField(Class<?> clazz, String fieldName, Object fieldValue) {
    Field field = ReflectionUtils.findField(clazz, fieldName);
    if (field == null) {
      throw new IllegalArgumentException(
          String.format(FIELD_NAME_NOT_FOUND, fieldName, clazz));
    }
    field.setAccessible(true);
    ReflectionUtils.setField(field, null, fieldValue);
  }

  /**
   * This method gets the value for the static field of the class.
   *
   * @param clazz     Class
   * @param fieldName Field Name who value needs to be set
   * @return Field value
   */
  public static Object getStaticField(Class<?> clazz, String fieldName) {
    Field field = ReflectionUtils.findField(clazz, fieldName);
    if (field == null) {
      throw new IllegalArgumentException(
          String.format(FIELD_NAME_NOT_FOUND, fieldName, clazz));
    }
    field.setAccessible(true);
    return ReflectionUtils.getField(field, null);
  }

  /**
   * This method gets the value for the object field.
   *
   * @param obj       Object
   * @param fieldName Field Name who value needs to be set
   * @return Field value
   */
  public static Object getObjectField(Object obj, String fieldName) {
    Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
    if (field == null) {
      throw new IllegalArgumentException(
          String.format(FIELD_NAME_NOT_FOUND, fieldName, obj.getClass()));
    }
    field.setAccessible(true);
    return ReflectionUtils.getField(field, obj);
  }

  /**
   * This method sets the object field.
   *
   * @param obj        Object passed
   * @param fieldName  Field Name who value needs to be set
   * @param fieldValue Field value to be set
   */
  public static void setObjectField(Object obj, String fieldName, Object fieldValue) {
    Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
    if (field == null) {
      throw new IllegalArgumentException(
          String.format(FIELD_NAME_NOT_FOUND, fieldName, obj.getClass()));
    }
    field.setAccessible(true);
    ReflectionUtils.setField(field, obj, fieldValue);
  }

  /**
   * This method is called to invoke a static method of a class.
   *
   * @param clazz      Class
   * @param methodName Method name
   * @param paramTypes array of method parameters type
   * @param args       array of method arguments
   * @return Object from method invocation
   */
  public static Object invokeStaticMethod(Class<?> clazz,
                                          String methodName,
                                          Class<?>[] paramTypes,
                                          Object[] args) {
    Method method = ReflectionUtils.findMethod(clazz, methodName, paramTypes);
    if (method == null) {
      throw new IllegalArgumentException(
          String.format(METHOD_NAME_NOT_FOUND, methodName, clazz));
    }
    method.setAccessible(true);
    return ReflectionUtils.invokeMethod(method, null, args);
  }

  /**
   * This method is called to invoke a static method of a class with no arguments.
   *
   * @param clazz      Class
   * @param methodName Method name
   * @return Object from method invocation
   */
  public static Object invokeStaticMethod(Class<?> clazz, String methodName) {
    return invokeStaticMethod(clazz, methodName, new Class[0], new Object[0]);
  }

  /**
   * This method is called to invoke a method of a object.
   *
   * @param target     Object
   * @param methodName Method name
   * @param paramTypes array of method parameters type
   * @param args       array of method arguments
   * @return Object from method invocation
   */
  public static Object invokeObjectMethod(Object target,
                                          String methodName,
                                          Class<?>[] paramTypes,
                                          Object[] args) {
    Method method = ReflectionUtils.findMethod(target.getClass(), methodName, paramTypes);
    if (method == null) {
      throw new IllegalArgumentException(
          String.format(METHOD_NAME_NOT_FOUND, methodName, target.getClass()));
    }
    method.setAccessible(true);
    return ReflectionUtils.invokeMethod(method, target, args);
  }

  /**
   * This method is called to invoke a object method with no arguments.
   *
   * @param target     Object
   * @param methodName Method name
   * @return Object from method invocation
   */
  public static Object invokeObjectMethod(Object target, String methodName) {
    return invokeObjectMethod(target, methodName, new Class[0], new Object[0]);
  }

  /**
   * This methods finds an annotation of given type for the field name.
   *
   * @param clazz          class
   * @param fieldName      field name
   * @param annotationType type of annotation
   * @param <A>            Annotation class
   * @return Annotation if found, null otherwise
   */
  public static <A extends Annotation> A findFieldAnnotation(Class<?> clazz, String fieldName,
                                                             Class<A> annotationType) {
    Field field = ReflectionUtils.findField(clazz, fieldName);
    return AnnotationUtils.findAnnotation(field, annotationType);
  }

  /**
   * This methods finds an annotation of given type for the field name.
   *
   * @param clazz          class
   * @param methodName     method name
   * @param annotationType type of annotation
   * @param <A>            Annotation class
   * @return Annotation if found, null otherwise
   */
  public static <A extends Annotation> A findMethodAnnotation(Class<?> clazz, String methodName,
                                                              Class<A> annotationType) {
    Method method = ReflectionUtils.findMethod(clazz, methodName);
    return AnnotationUtils.findAnnotation(method, annotationType);
  }

  /**
   * This methods finds an annotation of given type for the field name.
   *
   * @param clazz          class
   * @param methodName     method name
   * @param classes        method param class
   * @param annotationType type of annotation
   * @param <A>            Annotation class
   * @return Annotation if found, null otherwise
   */
  public static <A extends Annotation> A findMethodAnnotation(Class<?> clazz, String methodName,
                                                              Class[] classes,
                                                              Class<A> annotationType) {
    Method method = ReflectionUtils.findMethod(clazz, methodName, classes);
    return AnnotationUtils.findAnnotation(method, annotationType);
  }
}

