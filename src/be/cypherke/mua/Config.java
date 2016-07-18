package be.cypherke.mua;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

class Config {
    private Properties prop;
    private String filename;

    Config(String basename, String extension) {
        prop = new Properties();
        filename = basename + "." + extension;

        File configFile = new File(filename);
        if (!configFile.exists()) {
            System.out.println("Config: No valid file found.");
            System.exit(1);
        }
        load();
    }

    private void load() {
        // LOAD CONFIGURATION FILE
        InputStream is = null;
        try {
            is = new FileInputStream(filename);
            prop.loadFromXML(is);

            for (Map.Entry<Object, Object> e : prop.entrySet()) {
                String key = (String) e.getKey();
                String value = (String) e.getValue();
                if (value.isEmpty()) {
                    System.out.println("Config: key " + key + " doesn't contain a value!");
                    System.exit(1);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not load config file: " + e.toString());
        } catch (IOException e) {
            System.err.println("Could not load configuration properties: " + e.toString());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    String getString(String key) {
        return prop.getProperty(key);
    }
}
