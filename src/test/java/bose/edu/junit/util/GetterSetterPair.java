package bose.edu.junit.util;

import java.lang.reflect.Method;

/**
 * A utility class which holds a related getter and setter method.
 * This class supports {@link PojoTester}
 */
class GetterSetterPair {
  /**
   * The get method.
   */
  private Method getter;

  /**
   * The set method.
   */
  private Method setter;

  /**
   * Returns the get method.
   *
   * @return The get method.
   */
  Method getGetter() {
    return getter;
  }

  /**
   * Sets the get Method.
   *
   * @param getter The get Method.
   */
  void setGetter(Method getter) {
    this.getter = getter;
  }

  /**
   * Returns the set method.
   *
   * @return The set method.
   */
  Method getSetter() {
    return setter;
  }

  /**
   * Sets the set Method.
   *
   * @param setter The set Method.
   */
  void setSetter(Method setter) {
    this.setter = setter;
  }

  /**
   * Returns if this has a getter and setting method set.
   *
   * @return If this has a getter and setting method set.
   */
  boolean hasGetterAndSetter() {
    return this.getter != null && this.setter != null;
  }
}

