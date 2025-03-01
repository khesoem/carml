package com.taxonic.carml.rdfmapper.impl;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

class PropertyUtils {

  private PropertyUtils() {}

  /**
   * Finds the {@code Method} representing the setter specified by {@code setterName}. The algorithm
   * simply gathers all methods in the specified class (inherited or declared) with the specified name
   * that have 1 parameter. Note that the algorithm does not consider the type of the parameter. If
   * multiple such methods exist, a {@code RuntimeException} is thrown.
   *
   * @param clazz the class whose setter must be found
   * @param setterName the name of the setter
   * @return an optional setter method
   */
  public static Optional<Method> findSetter(Class<?> clazz, String setterName) {
    List<Method> setters = stream(clazz.getMethods()).filter(method -> method.getName()
        .equals(setterName))
        .filter(method -> method.getParameterCount() == 1)
        .collect(toList());
    if (setters.isEmpty()) {
      return Optional.empty();
    }
    if (setters.size() > 1) {
      throw new CarmlMapperException(String.format(
          "in class %s, multiple setters with name [%s] and 1 parameter were found, while expecting only 1",
          clazz.getCanonicalName(), setterName));
    }
    return Optional.of(setters.get(0));
  }

  /**
   * Get the configured RDF property name for the provided getter or setter-method.
   *
   * @param getterOrSetterName Full name of the getter or setter-method of the property. Example:
   *        {@code getName}.
   * @return the string name value of the configured RDF property
   */
  public static String getPropertyName(String getterOrSetterName) {
    String prefix = getGetterOrSetterPrefix(getterOrSetterName);
    if (prefix == null) {
      // no prefix detected - use method name as-is
      return firstToLowerCase(getterOrSetterName);
    }

    return firstToLowerCase(getterOrSetterName.substring(prefix.length()));
  }

  private static String getGetterOrSetterPrefix(String name) {
    return Stream.of("set", "get", "is")
        .filter(name::startsWith)
        .filter(p -> startsWithUppercase(name.substring(p.length())))
        .findFirst()
        .orElse(null);
  }

  private static boolean startsWithUppercase(String str) {
    if (str.isEmpty()) {
      return false;
    }

    String first = str.substring(0, 1);

    return first.equals(first.toUpperCase());
  }

  private static String firstToLowerCase(String str) {
    return transformFirst(str, String::toLowerCase);
  }

  private static String firstToUpperCase(String str) {
    return transformFirst(str, String::toUpperCase);
  }

  private static String transformFirst(String str, UnaryOperator<String> f) {
    if (str.isEmpty()) {
      return str;
    }
    String first = str.substring(0, 1);

    return f.apply(first) + str.substring(1);
  }

  public static String createSetterName(String property) {
    return "set" + firstToUpperCase(property);
  }

}
