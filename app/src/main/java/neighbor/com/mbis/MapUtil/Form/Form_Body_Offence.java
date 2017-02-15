package neighbor.com.mbis.MapUtil.Form;

import neighbor.com.mbis.Function.Func;

/**
 * Created by user on 2016-09-12.
 */
public class Form_Body_Offence {
    private static Form_Body_Offence ourInstance = new Form_Body_Offence();

    public static Form_Body_Offence getInstance() {
        return ourInstance;
    }

    private Form_Body_Offence() {
    }

    private byte[] passStationId;
    private byte[] passStationTurn;
//    private byte[] arriveStationId;
//    private byte[] arriveStationTurn;
    private byte[] offenceCode;
//    private byte[] speeding_ending;
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

    public void setOffenceCode(byte[] offenceCode) {
        this.offenceCode = offenceCode;
    }
    public void setOffenceCode(int offenceCode) {
        this.offenceCode = Func.integerToByte(offenceCode, 1);
    }
    public void setOffenceCode(String offenceCode) {
        this.offenceCode = Func.integerToByte(Integer.parseInt(offenceCode), 1);
    }

//    public void setSpeeding_ending(byte[] speeding_ending) {
//        this.speeding_ending = speeding_ending;
//    }
//    public void setSpeeding_ending(int speeding_ending) {
//        this.speeding_ending = Func.integerToByte(speeding_ending, 1);
//    }
//    public void setSpeeding_ending(String speeding_ending) {
//        this.speeding_ending = Func.integerToByte(Integer.parseInt(speeding_ending), 1);
//    }

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

    public byte[] getOffenceCode() {
        return offenceCode;
    }

//    public byte[] getSpeeding_ending() {
//        return speeding_ending;
//    }

    public byte[] getReservation() {
        return reservation;
    }
}
