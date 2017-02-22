package neighbor.com.mbis.views.maputil.thread;

import java.io.FileNotFoundException;
import java.io.IOException;

import neighbor.com.mbis.common.ConnectInfo;
import neighbor.com.mbis.managers.FTPManager;
import neighbor.com.mbis.views.maputil.Data;
import neighbor.com.mbis.models.value.MapVal;


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

            conn.get(ConnectInfo.FILE_PATH + "/info.txt", "info.txt");
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
