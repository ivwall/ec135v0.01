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

    //BitcoinRPCs bitcoinRPCs = null;
    MariaDB db = null;

    BitcoinRPCs bitcoinRPCs = new BitcoinRPCs();


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public Addresses(MariaDB mdb, BitcoinRPCs rpc) {
        //bitcoinRPCs = rpc;
        db = mdb;
    }
    
    public void scan01() {
        long last_time = System.nanoTime();
        for (int x=0; x<10; x++){
            //for (int x=749148; x<749151; x++){
            //for (int x=0; x<749874; x++){
                try {
                    System.out.println(x);
                    parseBlockXTrxs(x);
                    Thread.sleep(125);
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }
            long time = System.nanoTime();
        System.out.println("run time, milis "+(int)((time - last_time) / 1000000));
    }

    /***
     * 
     */
    public void parseBlockXTrxs(int blockN) {
        //123456789 1234567890123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789
        //         1         2         3         4         5         6         7         8         8         0
        //  749151, 2342, bc1qwedezsjqyx5cl56dgzajtpawwste50gl05ty0t94zw38mp9zs64s7a5k8k, 1.473742370000
        StringBuffer r2 = new StringBuffer();
        System.out.println("parseBlockXTrxs");
        String rx = bitcoinRPCs.getBlockHash(blockN);
        System.out.println(rx);
        JSONObject block = bitcoinRPCs.getBlock(bitcoinRPCs.getBlockHash(blockN),2);

        Long mediantime = (Long)((JSONObject)block.get("result")).get("mediantime");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(mediantime*1000L);
        String dateStr = ", "+sdf.format(date);

        JSONArray trxs = (JSONArray)((JSONObject)block.get("result")).get("tx");
        int cnt = trxs.size();

        r2.append(blockNumString(blockN));

        String trxString;
        String addrStr;

        for (int j=0; j<cnt; j++) {

            JSONObject tx = (JSONObject)trxs.get(j);
            JSONArray vout = (JSONArray)tx.get("vout");
            int voutCnt = vout.size();

            trxString = trxNumString(j+1);
            r2.append(trxString);

            for (int i=0; i<voutCnt; i++){

                JSONObject utxo = (JSONObject)vout.get(i);
                JSONObject script = (JSONObject)utxo.get("scriptPubKey");
                JSONArray addrs = (JSONArray)script.get("addresses");
                Double value = (Double)utxo.get("value");
                String valueStr = String.format("%.12f",value); 

                if (addrs != null) {
                    int addrCnt = addrs.size();
                    for (int k=0; k<addrCnt; k++) {
                        String addr = (String)addrs.get(k);

                        addrStr = addrString(addr);
                        //mySqlIDS.putAddrData(addrStr,"null");
                        r2.append(addrStr);

                    }

                    //String valueStr = String.format("%.12f",value); 
                    r2.append(" "+valueStr);

                } else if (addrs == null) {

                    // see line 331
                    //r2.append("  "+(String)script.get("asm"));
                    String x = asmWork((String)script.get("asm"));
                    r2.append("  "+addrString(x));
                    r2.append(" "+valueStr);

                }
                r2.append(dateStr);
                System.out.println(r2.toString());
                r2 = new StringBuffer();
                r2.append("         ");// block field
                r2.append("      "); // trx field
                dateStr = "";
            }
            r2 = new StringBuffer();
            r2.append("         ");
        }
    }

    public String blockNumString(int blk) {
        String result = null;
        try {
            String blkString = ""+blk;
            //                  12345678
            String tempBuild = "        ";
            int blkLength = blkString.length();
            int diff = tempBuild.length() - blkLength;
            StringBuffer buildStr = new StringBuffer();
            for (int x=0; x<diff; x++){
                buildStr.append(' ');
            }
            buildStr.append(blkString);
            buildStr.append(',');
            result = buildStr.toString();
        } catch(Exception ex){
            System.out.println(ex.toString());
        }
        return result;
    }

    public String trxNumString(int trxN){
        String result = null;
        try {
            String trxStringIn = ""+trxN;
            //                 12345
            String buildStr = "     ";
            int trxStringInLength = trxStringIn.length();
            int diff = buildStr.length() - trxStringInLength;
            StringBuffer stringBfr = new StringBuffer();
            for (int x=0; x<diff; x++){
                stringBfr.append(' ');
            }
            stringBfr.append(trxStringIn);
            stringBfr.append(',');
            result = stringBfr.toString();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return result;
    }

    public String addrString(String addr){
        StringBuffer result = new StringBuffer();
        //              16   1         2         3         4         5         6       8  
        //123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789
        //         1         2         3         4         5         6         7         8         8         0
        //                                                                              72
        //  749151, 2342, bc1qwedezsjqyx5cl56dgzajtpawwste50gl05ty0t94zw38mp9zs64s7a5k8k, 1.473742370000
        try {
            //56789 1234567890123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789
            //     1         2         3         4         5         6         7         8         8         0
            String te = "                                                               ";
            int addrLength = addr.length();
            int diff = te.length() - addrLength;
            for (int x=0; x< diff; x++){
                result.append(' ');
            }
            result.append(addr);
            result.append(',');
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return result.toString();
    }


    public String asmWork(String asm){
        String result = "not processed";
        try {

            String[] splited = asm.split(" ");

            if ( splited[1].contains("OP_CHECKSIG") ) {
                result = addressFromPubKey(splited[0]);
            } else {
                result = "asm: "+asm;
            }

        } catch ( Exception ex ){
            result = asm;
        }
        return result;
    }


    /***
     * 
     */
    public String addressFromPubKey( String a ){
        String result = "addr not found";
        try {
            //System.out.println("learn me btc: https://learnmeabitcoin.com/technical/hash-function");
            //https://gobittest.appspot.com/Address
            byte[] iti = hexStringToByteArray(a);
            Sha256Hash itiHash = Sha256Hash.of(iti);

            MessageDigest rmdx = MessageDigest.getInstance("RipeMD160", "BC");
            byte[] r1x = rmdx.digest(itiHash.getBytes());
            //Converting the byte array in to HexString format
            StringBuffer hexString = new StringBuffer();
            for (int i = 0;i<r1x.length;i++) {
                hexString.append(Integer.toHexString(0xFF & r1x[i]));
            }

            byte[] zero = hexStringToByteArray("00");
            byte[] s5 = new byte[r1x.length + 1];
            s5[0] = zero[0];
            System.arraycopy(r1x, 0, s5, 1, r1x.length);
            
            Sha256Hash s5x = Sha256Hash.of(s5);
            Sha256Hash s6x = Sha256Hash.of(s5x.getBytes());

            byte[] first4of6 = new byte[4];
            System.arraycopy(s6x.getBytes(), 0, first4of6, 0, 4);

            byte[] s8 = new byte[s5.length + first4of6.length];

            System.arraycopy(s5, 0, s8, 0, s5.length);
            System.arraycopy(first4of6, 0, s8, s5.length, first4of6.length);

            //7 - First four bytes of 6
            result = Base58.encode(s8);
        } catch(Exception ex) {
            System.out.println("address_From_Pub_Key  "+ex.toString());
            ex.printStackTrace();
        }
        return result;
    }


    /* s must be an even-length string. */
    //https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java/140861#140861
    /***
     * 
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    



}
