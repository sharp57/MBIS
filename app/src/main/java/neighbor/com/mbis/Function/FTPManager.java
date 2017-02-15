package neighbor.com.mbis.Function;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FTPManager {
    private String server = "";
    private int port = 0;
    private String id = "";
    private String password = "";
    FTPClient ftpClient;
    //FTPClientConfig conf;

    public FTPManager(String server, int port, String id, String password) throws IOException {
        this.server = server;
        this.port = port;
        this.id = id;
        this.password = password;
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("euc-kr");
        //ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
//		conf = new FTPClientConfig(FTPClientConfig.SYST_MVS);
//		ftpClient.configure(conf);

    }

    //로그인
    public boolean login(){
        try{
            if(ftpClient.isConnected() == true){
                return ftpClient.login(id, password);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    //연결
    public void connect(){
        try{
            ftpClient.connect(server, port);
            int reply = 0;

            //연결 시도후, 성공했는지 응답코드 확인
            reply = ftpClient.getReplyCode();
            if(FTPReply.isPositiveCompletion(reply) == false){
                ftpClient.disconnect();
                System.out.println("서버로부터 연결을 거부당했습니다.");

            }
            //현재 데이터 접속 방식 설정 PASSIVE_LOCAL_DATA_CONNECTION_MODE .
            ftpClient.enterLocalPassiveMode();//이코드가 없어서 로컬로 돌렸을때 파일 디렉토리를 못받아왔다 넣어주자 2시간 소모했다....
//			ftpClient.enterLocalActiveMode();

        }catch(Exception e){
            if(ftpClient.isConnected() == true){
                try{
                    ftpClient.disconnect();
                }catch(IOException f){

                }
            }
            e.printStackTrace();
        }
    }


    //서버로부터 로그아웃
    public boolean logout(){
        try{
            return ftpClient.logout();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //ftp의 ls 명령 , 모든파일 리스트를 가져온다
    public FTPFile[] list(){
        FTPFile[] files = null;
        try{
            files = ftpClient.listFiles();
            return files;
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //파일전송을 받는다
    public File get(String savePath, String recv2FTP){

        OutputStream output = null;
        try{
            File local = new File(savePath);
            output = new FileOutputStream(local);
        }catch(Exception e){
            e.printStackTrace();
        }
        File file = new File(savePath);
        try{
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE); //이 코드는 반드시 서버와 커넥션이후에 써야된다
            boolean flag = ftpClient.retrieveFile(recv2FTP, output);
            output.flush();
            output.close();

            if(flag == true){
                return file;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //파일 업로드
    public boolean upload(File file){
        boolean resultCode = false;
        try{
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE); //이 코드는 반드시 서버와 커넥션이후에 써야된다
            FileInputStream fis = new FileInputStream(file);
            boolean isSuccess = ftpClient.storeFile(file.getName(), fis);
            if(isSuccess == true){
                resultCode = true;
            }
            fis.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return resultCode;
    }

    //서버 디렉토리 이동
    public void cd(String path){
        try{
            ftpClient.changeWorkingDirectory(path);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //서버로 부터 연결을 닫는다
    public void disconnect(){
        try{
            ftpClient.disconnect();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 서버 현재 경로에 디렉토리를 만든다.
    public void mkdir(String path) {
        try {
            ftpClient.makeDirectory(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}