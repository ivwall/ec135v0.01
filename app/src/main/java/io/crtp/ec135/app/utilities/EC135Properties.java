package io.crtp.ec135.app.utilities;
//https://mkyong.com/java/java-properties-file-examples/

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EC135Properties {

    private static EC135Properties ec135Properties = null;
    private Properties prop = new Properties();

    public EC135Properties() {

        try (InputStream input = new FileInputStream(Constants.properties_file_name)) {

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("db.url"));
            System.out.println(prop.getProperty("db.user"));
            System.out.println(prop.getProperty("db.password"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static EC135Properties getInstance() {
        if (ec135Properties == null ) {
            ec135Properties = new EC135Properties();
        }
        return ec135Properties;
    }

    public String getProp(String p){
        return prop.getProperty(p);
    }    
}
