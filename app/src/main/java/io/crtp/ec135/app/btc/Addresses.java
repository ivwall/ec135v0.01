package io.crtp.ec135.app.btc;

import java.security.MessageDigest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;

import java.util.Date;
import java.text.SimpleDateFormat;

import io.crtp.ec135.app.db.MariaDB;
import io.crtp.ec135.app.rpc.BitcoinRPCs;

// https://www.arcblock.io/blog/en/post/2018/08/16/index-bitcoin
// https://gobittest.appspot.com/Address
//-----------------------------------
/***
 * 
 */
public class Addresses {

    int number_of_block_scan_threads = 1;

    MariaDB db = null;
    BitcoinRPCs bitcoinRPCs = null;
    private int checkNumberOfAddresses = 10000;

    ParseBlock parseBlock;

    private Thread blkThreads[] = new Thread[ number_of_block_scan_threads ];

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public Addresses(MariaDB mdb, BitcoinRPCs rpc) {
        db = mdb;
        bitcoinRPCs = rpc;
        parseBlock = new ParseBlock( mdb, rpc );
    }
    
    public void scan01() {

        long two_second = 2000;
        long one_second = 1000;
        long half_second = 500;
        long qtr_second = 250;
        long sleep_time = two_second;

        long last_time = System.nanoTime();

        int[] blkNumbers = {700009,700010,700011,700012,700013,700014,700015,700016};

        int work_This_Block = 0;
        //Long last_Block = bitcoinRPCs.getBlockCount();
        //int last_Block = 700016;
        int last_Block = 400000;

        // loops through all the blocks
        System.out.println("+++while ( work_This_Block ("+work_This_Block+") > last_Block("+last_Block+") ) {");
        while ( work_This_Block < last_Block ) {

            parseBlock.setBlock( work_This_Block );

            if ( parseBlock.getTransactionCount() < 100 ) {

                parseBlock.addressScan();
                int number_of_trxs = parseBlock.getTransactionCount();
                String block_time = parseBlock.getBlockTime();
                System.out.println("block "+work_This_Block+", time "+block_time+", trx "+number_of_trxs);

            } else {

            }

            work_This_Block++;

            /******
            // loops through the array of threads
            System.out.println("+++for ( int threadIndex = 0; threadIndex < 2; threadIndex++ ) {");
            for ( int threadIndex = 0; threadIndex < number_of_block_scan_threads; threadIndex++ ) {

                // thread doesn't exist instiate and start
                System.out.println("+++if ( blkThreads[ threadIndex ] == null ) {");
                if ( blkThreads[ threadIndex ] == null ) {

                    //blkThreads[ threadIndex ] = new ParseBlockExtds( work_This_Block, db, bitcoinRPCs );
                    blkThreads[ threadIndex ] = new ParseBlockExtds( work_This_Block, bitcoinRPCs );
                    blkThreads[ threadIndex ].setName(""+work_This_Block);
                    blkThreads[ threadIndex ].start();
                    System.out.println("+++ "+blkThreads[ threadIndex ].getName()+" blkThreads[ threadIndex ].start();");

                    work_This_Block++;

                // if this thread is running, runnable let it run
                } else if ( blkThreads[ threadIndex ].getState() == Thread.State.RUNNABLE ) {

                    int trx_cnt = ((ParseBlockExtds)blkThreads[ threadIndex ]).getTrxCount();
                    System.out.println("+++ "+blkThreads[ threadIndex ].getName()+" num of trxs "+trx_cnt);

                    if (trx_cnt < 100) {
                        sleep_time = two_second;
                    } else if (trx_cnt < 1000) {
                        sleep_time = one_second;
                    } else {
                        sleep_time = half_second;
                    }
                    
                // if this thread is terminated, null the thread in the array, clean up via garbage collect
                } else if ( blkThreads[ threadIndex ].getState() == Thread.State.TERMINATED ) {

                    System.out.println( "+++ "+blkThreads[ threadIndex ].getName()+" Thread.State.TERMINATED " + blkThreads[ threadIndex ].getName() );
                    blkThreads[ threadIndex ] = null;

                } else {
                    System.out.println( 
                        "+++ "+blkThreads[ threadIndex ].getName()+" thread index " + threadIndex + " btc block "+blkThreads[ threadIndex ].getName()+
                            " state "+ blkThreads[ threadIndex ].getState());
                }
            }
             */

            // give the system sometime time to 'clean up' and write addresses 
            /***** 
            try {
                Thread.sleep( half_second );
            } catch(Exception ex) {
                System.out.println("+++this.wait "+ex.toString());
            }
            */

        }

        long time = System.nanoTime();
        System.out.println("+++run time, milis "+(int)((time - last_time) / 1000000));

    }

}
