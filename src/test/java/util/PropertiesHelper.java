package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {

    public static String getMasterKey() {
        return getProperty("master_key");
    }

    public static String getReadOnlyKey() {
        return getProperty("read_only_access_key");
    }

    public static String getDeleteCreateKey() {
        return getProperty("delete_create_access_key");
    }

    public static String getUpdateOnlyKey() {
        return getProperty("update_only_access_key");
    }

    private static String getProperty(String propertyName) {
        Properties prop = new Properties();
        ClassLoader classLoader = BinsHelper.class.getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream("bins.properties")) {
            prop.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop.getProperty(propertyName);
    }

}
