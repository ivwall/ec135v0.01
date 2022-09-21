package io.crtp.ec135.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import io.crtp.ec135.app.utilities.EC135Properties;

public class MariaDB implements IDataStore {

    EC135Properties props = EC135Properties.getInstance();
    Connection conn = null;
    String insertAddrSql = "INSERT INTO wallet(addr) VALUES(?)";
    PreparedStatement insertAddr;

    public MariaDB() {

        String dburl = props.getProp("db.url");
        String user = props.getProp("db.user");
        String pw = props.getProp("db.password");
        String url = dburl +"user="+user+"&password="+pw;

        try {
            //System.out.println(url);
            conn = DriverManager.getConnection(url);
            insertAddr = conn.prepareStatement(insertAddrSql);
        } catch (Exception ex){
            System.out.println(" >> "+ex.toString());
        }

    }

    public void write(String addr) {
        try {
            insertAddr.setString(1,addr);
            insertAddr.executeUpdate();
        } catch(Exception ex){
            System.out.println(addr);
            System.out.println(ex.toString());
        }
    }

    public boolean insert(String addr) {
        boolean result = true;
        try {
            insertAddr.setString(1,addr);
            insertAddr.executeUpdate();
        } catch(Exception ex){
            if (ex.toString().contains("Duplicate")) {
                // TODO: ... 
            } else {
                System.out.println(ex.toString());
            }
            result = false;
        }
        return result;
    }

}
