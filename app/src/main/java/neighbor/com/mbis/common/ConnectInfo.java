package neighbor.com.mbis.common;

import android.os.Environment;

import java.io.File;

/**
 * Created by ts.ha on 2017-02-22.
 */

public class ConnectInfo {

    public final static int BUSTIMER_30SEC = 1000 * 30;
    public final static int SERVER_CONNECT_TIMEOUT = 3000;
    public final static int SERVER_READ_TIMEOUT = 1000 * 60 * 5;

    //통신 변수들
    public final static String IP = "183.98.222.54"; //이게 진짜 실제 아이피
    //    public final static String IP = "211.189.132.187"; //이거는 김선명책임님 자리
//    public final static String IP = "197.168.100.20"; // 이거는 내 컴퓨터
    public final static int PORT = 33000; // port number
    public final static File FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


    public final static String FTP_IP = "211.189.132.192";
    public final static int FTP_PORT = 30300;
    public final static String FTP_ID = "mbis";
    public final static String FTP_PW = "mbis";
    public final static String FILE_PATH_2 = "/MBIS/";
}
