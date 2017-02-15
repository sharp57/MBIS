package neighbor.com.mbis.Function;

import android.content.ContentValues;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import neighbor.com.mbis.CSV_Util.RouteUtil;
import neighbor.com.mbis.Database.DBManager;

public class FileManager {

    String state;
    File path;    //저장 데이터가 존재하는 디렉토리경로
    File file;     //파일명까지 포함한 경로

    public FileManager(String name) {
        getState(name);
    }
    public FileManager(String name, String fileType) {
        getState(name, fileType);
    }

    //파일 저장하는 함수
    public void getState(String name) {
        state = Environment.getExternalStorageState(); //외부저장소(SDcard)의 상태 얻어오기
        if (!state.equals(Environment.MEDIA_MOUNTED)) { // SDcard 의 상태가 쓰기 가능한 상태로 마운트되었는지 확인

        }
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file = new File(path, name + ".log"); //파일명까지 포함함 경로의 File 객체 생성

    }
    public void getState(String name, String fileType) {
        state = Environment.getExternalStorageState(); //외부저장소(SDcard)의 상태 얻어오기
        if (!state.equals(Environment.MEDIA_MOUNTED)) { // SDcard 의 상태가 쓰기 가능한 상태로 마운트되었는지 확인

        }
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file = new File(path, name + "." + fileType); //파일명까지 포함함 경로의 File 객체 생성

    }


    public void saveData(String data) {
        try {
//            Log.e("SaveData: ", "11");
            try {
                Log.e("SaveData: ", "saveData: " + data);
                //데이터 추가가 가능한 파일 작성자(FileWriter 객체생성)
                FileWriter wr = new FileWriter(file, true); //두번째 파라미터 true: 기존파일에 추가할지 여부를 나타냅니다.

                PrintWriter writer = new PrintWriter(wr);
                writer.println(data);
                writer.close();

            } catch (IOException e) {
                Log.e("SaveData: ", "33");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
        }

    }

    public void saveData(byte[] data) {
        try {
            Log.e("SaveData: ", "11");
            try {
                Log.e("SaveData: ", "22");
                //데이터 추가가 가능한 파일 작성자(FileWriter 객체생성)
                FileWriter wr = new FileWriter(file, true); //두번째 파라미터 true: 기존파일에 추가할지 여부를 나타냅니다.

                PrintWriter writer = new PrintWriter(wr);

                String hexStrin2 = new java.math.BigInteger(data).toString(16);
                for (int i = 0; i < data.length; i++) {
                    writer.print(data[i] + " ");
                }
                writer.println(hexStrin2);
                writer.close();
            } catch (IOException e) {
                Log.e("SaveData: ", "33");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
        }

    }

    public void saveData(char[] data) {
        try {
            Log.e("SaveData: ", "11");
            try {
                Log.e("SaveData: ", "22");
                //데이터 추가가 가능한 파일 작성자(FileWriter 객체생성)

                FileWriter wr = new FileWriter(file, true); //두번째 파라미터 true: 기존파일에 추가할지 여부를 나타냅니다.

                PrintWriter writer = new PrintWriter(wr);

                writer.println(data);
                writer.close();


            } catch (IOException e) {
                Log.e("SaveData: ", "33");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
        }

    }

    public void saveData(double data) {
        try {
            Log.e("SaveData: ", "11");
            try {
                Log.e("SaveData: ", "22");
                //데이터 추가가 가능한 파일 작성자(FileWriter 객체생성)

                FileWriter wr = new FileWriter(file, true); //두번째 파라미터 true: 기존파일에 추가할지 여부를 나타냅니다.

                PrintWriter writer = new PrintWriter(wr);

                writer.println(data);
                writer.close();


            } catch (IOException e) {
                Log.e("SaveData: ", "33");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
        }

    }

//    public void readFileWriteDB(String fileName, int flag, DBManager db) {
//        String s = "";
//
//        File mFile = new File(path, fileName + ".csv"); //파일명까지 포함함 경로의 File 객체 생성
//        try {
//            FileReader reader = new FileReader(mFile);
//            BufferedReader in = new BufferedReader(reader);
//
//            if (flag == 1) {
//
//                while ((s = in.readLine()) != null) {
//                    //Split to separate the name from the capital
//                    String[] rowData = s.split(",");
//
//                    //Create a State object for this row's data.
//                    RouteUtil r = new RouteUtil();
//                    r.setId(rowData[0]);
//                    r.setRoute_id(rowData[1]);
//                    r.setSt_sta_id(rowData[3]);
//                    r.setEd_sta_id(rowData[4]);
//                    r.setCompany_nm(rowData[9]);
//                    r.setAdmin_nm(rowData[10]);
//                    r.setCompany_id(rowData[11]);
//                    r.setDirection(rowData[14]);
//
//                    addUpdateRouteUtil(r, db);
//
//                }
//                in.close();
//            }
//
//        } catch (IOException e) {
//        }
//    }
//
//    private void addUpdateRouteUtil(RouteUtil ru, DBManager db) {
////        mDatabase = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put("id", ru.getId()); // Contact Name
//        values.put("route_id", ru.getRoute_id());
//        values.put("st_sta_id", ru.getSt_sta_id());
//        values.put("ed_sta_id", ru.getEd_sta_id());
//        values.put("company_nm", ru.getCompany_nm());
//        values.put("admin_nm", ru.getAdmin_nm());
//        values.put("company_id", ru.getCompany_id());
//        values.put("direction", ru.getDirection());
//
//        // Inserting Row
//        db.insertRoute(values);
//    }

}