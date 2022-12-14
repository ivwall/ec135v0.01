/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.crtp.ec135.app;

import io.crtp.ec135.list.LinkedList;

import static io.crtp.ec135.utilities.StringUtils.join;
import static io.crtp.ec135.utilities.StringUtils.split;
import static io.crtp.ec135.app.MessageUtils.getMessage;

import org.apache.commons.text.WordUtils;

import io.crtp.ec135.app.rpc.BitcoinRPCs;
import io.crtp.ec135.app.db.MariaDB;
import io.crtp.ec135.app.btc.Addresses;

public class App {
    public static void main(String[] args) {
        LinkedList tokens;
        tokens = split(getMessage());
        String result = join(tokens);
        System.out.println(WordUtils.capitalize(result));

        //BitcoinRPCs bitcoinRPCs = new BitcoinRPCs();
        //MariaDB db = new MariaDB();
        //Addresses address = new Addresses(db, bitcoinRPCs);

        Addresses address = new Addresses(new MariaDB(), new BitcoinRPCs());
        address.scan01();
    }
}
