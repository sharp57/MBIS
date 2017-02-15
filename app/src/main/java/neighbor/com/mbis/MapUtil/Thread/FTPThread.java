package neighbor.com.mbis.MapUtil.Thread;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import neighbor.com.mbis.Function.FTPManager;
import neighbor.com.mbis.Function.Func;
import neighbor.com.mbis.MapUtil.Value.LogicBuffer;
import neighbor.com.mbis.MapUtil.Value.MapVal;
import neighbor.com.mbis.Network.NetworkUtil;

/**
 * Created by user on 2016-11-10.
 */

public class FTPThread extends Thread {
    MapVal mv = MapVal.getInstance();

    @SuppressWarnings("null")
    @Override
    public synchronized void run() {

        FTPManager conn = null;
        String[] myUpdateFileName = new String[3];

        try {
            conn = new FTPManager(mv.getFtpIP(), mv.getFtpPort(), mv.getFtpID(), mv.getFtpPW());

            conn.connect();
            conn.login();

            conn.cd(mv.getPathData());

            conn.cd("DATA");

            BufferedReader in;
            in = new BufferedReader(new FileReader(NetworkUtil.FILE_PATH + "/info.txt"));
            String s;

            int flag = 0;
            while ((s = in.readLine()) != null) {
                if (s.contains("[ROUTE]")) {
                    flag = 0;
                } else if (s.contains("[ROUTE_STATION]")) {
                    flag = 1;
                } else if (s.contains("[STATION]")) {
                    flag = 2;
                } else if (s.contains("=")) {
                    String[] buf = s.split("=");
                    if (buf[0].equals("APPLYDTIME")) {
                        LogicBuffer.applyDateTime[flag] = Long.parseLong(buf[1]);
                    } else if (buf[0].equals("FILENAME")) {
                        LogicBuffer.ftpServerFileName[flag] = buf[1];
                    } else if (buf[0].equals("MD5")) {
                        LogicBuffer.md5[flag] = buf[1];
                    }
                }
            }
            //data 디렉토리가 없으면 만들어줌
            if(!(new File(NetworkUtil.FILE_PATH + "/data").exists())) {
                (new File(NetworkUtil.FILE_PATH + "/data")).mkdirs();
            }
            for (int i = 0; i < conn.list().length; i++) {
                Log.e("[ls]", conn.list()[i] + "");
                String fileLastName = "";
                switch (i) {
                    case 0:
                        fileLastName = "_R.csv";
                        break;
                    case 1:
                        fileLastName = "_RS.csv";
                        break;
                    case 2:
                        fileLastName = "_S.csv";
                        break;
                }
                myUpdateFileName[i] = Long.toString(LogicBuffer.applyDateTime[i]) + fileLastName;
                String name = conn.list()[i].getName();
                conn.get(NetworkUtil.FILE_PATH + "/data/" + myUpdateFileName[i], name);
            }

            conn.disconnect();

            //여기서 md5 확인 후에 무결성이 보장 안되면 파일을 지워버린다.
            for (int i=0 ; i< myUpdateFileName.length ; i++) {
                if (!LogicBuffer.md5[i].equals(Func.getStringByMD5(NetworkUtil.FILE_PATH + "/data/" + myUpdateFileName[i]))) {
                    new File(NetworkUtil.FILE_PATH + "/data/" + myUpdateFileName[i]).delete();
                }
            }


        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException");
        } catch (IOException e) {
            System.err.println("IOException");
        } catch (Exception e) {
            System.err.println("Exception");
        }


    }
}
