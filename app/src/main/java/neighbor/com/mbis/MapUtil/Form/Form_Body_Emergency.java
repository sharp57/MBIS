package neighbor.com.mbis.MapUtil.Form;


import neighbor.com.mbis.Function.Func;

/**
 * Created by user on 2016-09-01.
 */
public class Form_Body_Emergency {
    private static Form_Body_Emergency ourInstance = new Form_Body_Emergency();

    public static Form_Body_Emergency getInstance() {
        return ourInstance;
    }

    private Form_Body_Emergency() {
    }

    private byte[] passStationId;
    private byte[] passStationTurn;
    private byte[] emergencyCode;
    private byte[] reservation;

    public void setPassStationId(byte[] passStationId) {
        this.passStationId = passStationId;
    }
    public void setPassStationId(int passStationId) {
        this.passStationId = Func.integerToByte(passStationId, 5);
    }
    public void setPassStationId(long passStationId) {
        this.passStationId = Func.longToByte(passStationId, 5);
    }
    public void setPassStationId(String passStationId) {
        this.passStationId = Func.integerToByte(Integer.parseInt(passStationId), 5);
    }

    public void setPassStationNum(byte[] passStationTurn) {
        this.passStationTurn = passStationTurn;
    }
    public void setPassStationNum(int passStationTurn) {
        this.passStationTurn = Func.integerToByte(passStationTurn, 2);
    }
    public void setPassStationNum(String passStationTurn) {
        this.passStationTurn = Func.integerToByte(Integer.parseInt(passStationTurn), 2);
    }

//    public void setArriveStationId(byte[] arriveStationId) {
//        this.arriveStationId = arriveStationId;
//    }
//    public void setArriveStationId(int arriveStationId) {
//        this.arriveStationId = Func.integerToByte(arriveStationId, 5);
//    }
//    public void setArriveStationId(long arriveStationId) {
//        this.arriveStationId = Func.longToByte(arriveStationId, 5);
//    }
//    public void setArriveStationId(String arriveStationId) {
//        this.arriveStationId = Func.integerToByte(Integer.parseInt(arriveStationId), 5);
//    }
//
//    public void setArriveStationNum(byte[] arriveStationTurn) {
//        this.arriveStationTurn = arriveStationTurn;
//    }
//    public void setArriveStationNum(int arriveStationTurn) {
//        this.arriveStationTurn = Func.integerToByte(arriveStationTurn, 2);
//    }
//    public void setArriveStationNum(String arriveStationTurn) {
//        this.arriveStationTurn = Func.integerToByte(Integer.parseInt(arriveStationTurn), 2);
//    }

    public void setEmergencyCode(byte[] emergencyCode) {
        this.emergencyCode = emergencyCode;
    }
    public void setEmergencyCode(int emergencyCode) {
        this.emergencyCode = Func.integerToByte(emergencyCode, 2);
    }
    public void setEmergencyCode(String emergencyCode) {
        this.emergencyCode = Func.integerToByte(Integer.parseInt(emergencyCode), 2);
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

    public byte[] getPassStationId() {
        return passStationId;
    }

    public byte[] getPassStationTurn() {
        return passStationTurn;
    }

//    public byte[] getArriveStationId() {
//        return arriveStationId;
//    }
//
//    public byte[] getArriveStationTurn() {
//        return arriveStationTurn;
//    }

    public byte[] getEmergencyCode() {
        return emergencyCode;
    }

    public byte[] getReservation() {
        return reservation;
    }
}
