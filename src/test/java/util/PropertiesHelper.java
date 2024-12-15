package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {

    public static String getBaseURL() {
        return getProperty("base_url");
    }

    public static String getBasePath() {
        return getProperty("base_path");
    }

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

    public static String getPath_BinID() {
        return getProperty("json_path_bin_id");
    }

    public static String getPath_BinAccess() {
        return getProperty("json_path_bin_access");
    }

    public static String getPath_BinName() {
        return getProperty("json_path_bin_name");
    }

    public static String getPath_Etag() {
        return getProperty("json_path_etag");
    }

    public static String getPath_BinVersion() {
        return getProperty("json_path_bin_version");
    }

    public static String getPath_BinParentId() {
        return getProperty("json_path_bin_parent_id");
    }

    private static String getProperty(String propertyName) {
        Properties prop = new Properties();
        ClassLoader classLoader = PropertiesHelper.class.getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream("bins.properties")) {
            prop.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop.getProperty(propertyName);
    }

}
