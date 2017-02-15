package neighbor.com.mbis.Network;

import android.os.Environment;

import java.io.File;

/**
 * Created by user on 2016-09-29.
 */

public class NetworkUtil {
    //통신 변수들
    public final static String IP = "183.98.222.54"; //이게 진짜 실제 아이피
//    public final static String IP = "211.189.132.187"; //이거는 김선명책임님 자리
//    public final static String IP = "197.168.100.20"; // 이거는 내 컴퓨터
    public final static int PORT = 33000; // port number
    public final static File FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
}
