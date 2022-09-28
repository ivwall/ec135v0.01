package io.crtp.ec135.app.db;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.HashSet;

public class AddressSet implements Serializable {

    private HashSet<String> addresses = null;

    public AddressSet() {
        /***
        try {
            read_Hashset_From_Disk();
        } catch(IOException iox) {
            System.out.println("AddressSet constructor "+iox.toString());
            System.out.println("AddressSet constructor instantiate addresses HashSet");
            addresses = new HashSet<String>();
        } catch(ClassNotFoundException c) {
            System.out.println("AddressSet constructor "+c.toString());
            System.out.println("AddressSet constructor instantiate addresses HashSet");
            addresses = new HashSet<String>();
        }
         */
        addresses = new HashSet<String>();
    }

    public void read_Hashset_From_Disk() throws IOException, ClassNotFoundException{
        try {
            FileInputStream fis = new FileInputStream("addresses.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            addresses = (HashSet)ois.readObject();
            ois.close();
            fis.close();
        } catch(IOException iox) {
            iox.printStackTrace();
        } catch(ClassNotFoundException c) {
            c.printStackTrace();
        }
    }

    public void write_Hashset_To_Disk() {
        try {
            FileOutputStream fos = new FileOutputStream("addresses.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(addresses);
            oos.close();
            fos.close();
        } catch(IOException iox) {
            iox.printStackTrace();
        }
    }

    public void write(String addr) {
        try {
            addresses.add(addr);
        } catch(Exception ex) {
            System.out.println("AddressSet write error "+ex.toString());
        }
    }

    public boolean contains(String addr){
        boolean result = false;
        try {
            result = addresses.contains(addr);
        } catch (Exception ex) {
            System.out.println("AddressSet contains error "+ex.toString());
        }
        System.out.println("AddressSet CONTAINS "+addr+" "+result+" "+addresses.size());
        return result;
    }

}
