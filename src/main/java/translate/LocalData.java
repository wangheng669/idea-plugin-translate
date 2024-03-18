package translate;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class LocalData {
    private static final String PREFIX_NAME = "translate";
    private static final File f = new File(System.getProperty("user.home") + "/translate.properties");
    private static final Properties p = new Properties();
    private static boolean isInitialized = false;

    public static void store(String key, String value) {
        p.setProperty(StringUtils.uncapitalize(PREFIX_NAME + "_" + key), value);
        save();
    }

    static {
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            if (f.exists()) {
                p.load(new FileReader(f));
                isInitialized = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void save() {
        if (isInitialized) {
            try {
                p.store(new FileWriter(f), "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String read(String key) {
        return isInitialized ? p.getProperty(StringUtils.uncapitalize(PREFIX_NAME + "_" + key)) : null;
    }
}
