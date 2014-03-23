package utils;

import java.util.Map;

public class Caster {
    public static String[] castKeysToStrings(Map<String, ?> map) {
        String[] array = new String[map.size()];
        int i = 0;
        for (String key : map.keySet()) {
            array[i] = key;
            i += 1;
        }
        return array;
    }
}
