package neighbor.com.mbis.MapUtil.Form;


import neighbor.com.mbis.Function.Func;

/**
 * Created by user on 2016-09-01.
 */
public class Form_Body_EndDrive {
    private static Form_Body_EndDrive ourInstance = new Form_Body_EndDrive();

    public static Form_Body_EndDrive getInstance() {
        return ourInstance;
    }

    private Form_Body_EndDrive() {
    }
    private byte[] driveDate;
    private byte[] startTime;
    private byte[] stationId;
    private byte[] stationTurn;

    private byte[] detectStationArriveNum;
    private byte[] detectStationStartNum;
    private byte[] driveDivision;
    private byte[] reservation;

    public void setStationId(byte[] stationId) {
        this.stationId = stationId;
    }
    public void setStationId(long stationId) {
        this.stationId = Func.longToByte(stationId, 5);
    }
    public void setStationId(String stationId) {
        this.stationId = Func.longToByte(Long.parseLong(stationId), 5);
    }
    public void setStationTurn(byte[] stationNum) {
        this.stationTurn = stationNum;
    }
    public void setStationTurn(int stationNum) {
        this.stationTurn = Func.integerToByte(stationNum, 2);
    }

    public byte[] getDriveDate() {
        return driveDate;
    }

    public void setDriveDate(byte[] driveDate) {
        this.driveDate = driveDate;
    }
    public void setDriveDate(String sendDate) {
        this.driveDate = Func.stringToByte(sendDate);
    }
    public void setDriveDate(String year, String month, String day) {
        String ymd = year + month + day;
        this.driveDate = Func.stringToByte(ymd);
    }
    public void setDriveDate(int year, int month, int day) {
        String y = String.format("%02d", year);
        String m = String.format("%02d", month);
        String d = String.format("%02d", day);
        String ymd = y + m + d;
        this.driveDate = Func.stringToByte(ymd);
    }

    public byte[] getStartTime() {
        return startTime;
    }

    public void setStartTime(byte[] startTime) {
        this.startTime = startTime;
    }
    public void setStartTime(String eventTime) {
        this.startTime = Func.stringToByte(eventTime);
    }
    public void setStartTime(String hour, String min, String sec) {
        String hms = hour + min + sec;
        this.startTime = Func.stringToByte(hms);
    }
    public void setStartTime(int hour, int min, int sec) {
        String h = String.format("%02d", hour);
        String m = String.format("%02d", min);
        String s = String.format("%02d", sec);
        String hms = h + m + s;
        this.startTime = Func.stringToByte(hms);
    }

//    public byte[] getDriveTurn() {
//        return driveTurn;
//    }
//
//    public void setDriveTurn(byte[] driveTurn) {
//        this.driveTurn = driveTurn;
//    }
//    public void setDriveTurn(int driveTurn) {
//        this.driveTurn = Func.integerToByte(driveTurn, 2);
//    }
//    public void setDriveTurn(String driveTurn) {
//        this.driveTurn = Func.integerToByte(Integer.parseInt(driveTurn), 2);
//    }

    public byte[] getDetectStationArriveNum() {
        return detectStationArriveNum;
    }

    public void setDetectStationArriveNum(byte[] detectStationNum) {
        this.detectStationArriveNum = detectStationNum;
    }
    public void setDetectStationArriveNum(int detectStationNum) {
        this.detectStationArriveNum = Func.integerToByte(detectStationNum, 1);
    }
    public void setDetectStationArriveNum(String detectStationNum) {
        this.detectStationArriveNum = Func.integerToByte(Integer.parseInt(detectStationNum), 1);
    }


    public byte[] getDetectStationStartNum() {
        return detectStationStartNum;
    }

    public void setDetectStationStartNum(byte[] detectStationStartNum) {
        this.detectStationStartNum = detectStationStartNum;
    }
    public void setDetectStationStartNum(int detectStationStartNum) {
        this.detectStationStartNum = Func.integerToByte(detectStationStartNum, 1);
    }
    public void setDetectStationStartNum(String detectStationStartNum) {
        this.detectStationStartNum = Func.integerToByte(Integer.parseInt(detectStationStartNum), 1);
    }

    public void setReservation(byte[] reservation) {
        this.reservation = reservation;
    }
    public void setReservation(int reservation) {
        this.reservation = Func.integerToByte(reservation, 4);
    }
    public void setReservation(String reservation) {
        this.reservation = Func.integerToByte(Integer.parseInt(reservation), 4);
    }

    public byte[] getStationId() {
        return stationId;
    }

    public byte[] getStationTurn() {
        return stationTurn;
    }

    public byte[] getReservation() {
        return reservation;
    }

    public void setDriveDivision(byte[] driveDivision) {
        this.driveDivision = driveDivision;
    }
    public void setDriveDivision(int driveDivision) {
        this.driveDivision = Func.integerToByte(driveDivision, 1);
    }
    public void setDriveDivision(String driveDivision) {
        this.driveDivision = Func.integerToByte(Integer.parseInt(driveDivision), 1);
    }

    public byte[] getDriveDivision() {
        return driveDivision;
    }

}
