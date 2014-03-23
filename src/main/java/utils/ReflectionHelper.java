package utils;

import java.lang.reflect.Field;

public class ReflectionHelper {

    public static Object createInstance(String className) {
        try {
            return Class.forName(className).newInstance();
        } catch (Exception e) {
            System.err.println("ReflectionHelper, createInstance");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void setFieldValue(Object object, String fieldName, String value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            if (field.getType().equals(String.class)) {
                field.set(object, value);
            } else if (field.getType().equals(int.class)) {
                field.set(object, Integer.parseInt(value));
            }

            field.setAccessible(false);
        } catch (Exception e) {
            System.err.println("ReflectionHelper, setFieldValue");
            e.printStackTrace();
        }
    }
}