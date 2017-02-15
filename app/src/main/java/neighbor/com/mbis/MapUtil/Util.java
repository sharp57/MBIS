package neighbor.com.mbis.MapUtil;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import neighbor.com.mbis.Activity.LoginActivity;
import neighbor.com.mbis.MapUtil.Form.Form_Header;

/**
 * Created by 권오철 on 2017-02-06.
 */

public class Util {
    static boolean isDebug = true;

    static public void sqliteExport(Context context){
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/neighbor.com.mbis/databases/MBIS.db";
                String backupDBPath = "mbis.sqlite";

                Util.log("Util","sqliteExport:1: " + data.getPath().toString() + currentDBPath);
                Util.log("Util","sqliteExport:2: " + sd.getPath().toString() + backupDBPath);
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                if(backupDB.exists()){
                    Toast.makeText(context, "DB Export Complete!!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "DB Export Failed!!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Util.log("Util","sqliteExport:3");
            }
        } catch (Exception e) {
            Util.log("Util","sqliteExport");
            e.printStackTrace();
        }
    }

    static public Form_Header setHeader(Form_Header h, Context context,
                                        byte version, byte opCode, byte[] srCnt, byte[] localCode, byte[] dataLength){

        h.setVersion(version);
        h.setOp_code(opCode);
        h.setSr_cnt(srCnt);
        h.setDeviceID(Util.hexStringToByteArray(Util.getDeviceID(context)));
        h.setLocalCode(localCode);
        h.setDataLength(dataLength);

        return h;
    }
    static public byte[] makeHeader(Form_Header h, byte[] headerBuf) {
        headerBuf = new byte[BytePosition.HEADER_SIZE];

        putHeader(headerBuf, h.getVersion(), BytePosition.HEADER_VERSION_START);
        putHeader(headerBuf, h.getOp_code(), BytePosition.HEADER_OPCODE);
        putHeader(headerBuf, h.getSr_cnt(), BytePosition.HEADER_SRCNT);
        putHeader(headerBuf, h.getDeviceID(), BytePosition.HEADER_DEVICEID);
        putHeader(headerBuf, h.getLocalCode(), BytePosition.HEADER_LOCALCODE);
        putHeader(headerBuf, h.getDataLength(), BytePosition.HEADER_DATALENGTH);

        return headerBuf;
    }

    static public void putHeader(byte[] headerBuf, byte[] b, int position) {
        System.arraycopy(b, 0, headerBuf, position, b.length);
    }
    static public String getDeviceID(Context context){
        return android.provider.Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    /**
     * bytesToHex method
     * Found on the internet
     * http://stackoverflow.com/a/9855338
     */
    static final char[] hexArray = "0123456789abcdef".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }

    static public void log(String tag, String debug){
        if(isDebug) Log.e(tag, debug);
    }


}
