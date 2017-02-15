package neighbor.com.mbis.MapUtil.Form;


import neighbor.com.mbis.Function.Func;

/**
 * Created by user on 2016-09-01.
 */
public class Form_Body_ArriveStation {
    private static Form_Body_ArriveStation ourInstance = new Form_Body_ArriveStation();

    public static Form_Body_ArriveStation getInstance() {
        return ourInstance;
    }

    private Form_Body_ArriveStation() {
    }

    private byte[] stationId;
    private byte[] stationTurn;
    private byte[] adjacentTravelTime;
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
    public void setAdjacentTravelTime(byte[] adjacentTravelTime) {
        this.adjacentTravelTime = adjacentTravelTime;
    }
    public void setAdjacentTravelTime(int adjacentTravelTime) {
        this.adjacentTravelTime = Func.integerToByte(adjacentTravelTime, 2);
    }
    public void setAdjacentTravelTime(String adjacentTravelTime) {
        this.adjacentTravelTime = Func.integerToByte(Integer.parseInt(adjacentTravelTime), 2);
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

    public byte[] getStationId() {
        return stationId;
    }

    public byte[] getStationTurn() {
        return stationTurn;
    }

    public byte[] getAdjacentTravelTime() {
        return adjacentTravelTime;
    }

    public byte[] getReservation() {
        return reservation;
    }
}
