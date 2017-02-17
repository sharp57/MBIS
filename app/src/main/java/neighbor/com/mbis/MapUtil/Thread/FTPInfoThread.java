package neighbor.com.mbis.maputil.thread;

import java.io.FileNotFoundException;
import java.io.IOException;

import neighbor.com.mbis.function.FTPManager;
import neighbor.com.mbis.maputil.Data;
import neighbor.com.mbis.maputil.value.MapVal;
import neighbor.com.mbis.network.NetworkUtil;

/**
 * Created by user on 2016-11-10.
 */

public class FTPInfoThread extends Thread {
    MapVal mv = MapVal.getInstance();

    @SuppressWarnings("null")
    @Override
    public synchronized void run() {

        FTPManager conn = null;

        try {
            conn = new FTPManager(mv.getFtpIP(), mv.getFtpPort(), mv.getFtpID(), mv.getFtpPW());

            conn.connect();
            conn.login();

            conn.cd(mv.getPathData());

            conn.get(NetworkUtil.FILE_PATH + "/info.txt", "info.txt");
            conn.disconnect();
            Data.readFTPData = null;

        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException");
        } catch (IOException e) {
            System.err.println("IOException");
        } catch (Exception e) {
            System.err.println("Exception");
        }


    }
}
