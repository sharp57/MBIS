package neighbor.com.mbis.MapUtil.Form;


import neighbor.com.mbis.Function.Func;

/**
 * Created by user on 2016-09-01.
 */
public class Form_Body_StartDrive {
    private static Form_Body_StartDrive ourInstance = new Form_Body_StartDrive();

    public static Form_Body_StartDrive getInstance() {
        return ourInstance;
    }

    private Form_Body_StartDrive() {
    }

    private byte[] driveDivision;
    private byte[] reservation;

    public byte[] getReservation() {
        return reservation;
    }

    public byte[] getDriveDivision() {
        return driveDivision;
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
    public void setReservation(byte[] reservation) {
        this.reservation = reservation;
    }
    public void setReservation(int reservation) {
        this.reservation = Func.integerToByte(reservation, 4);
    }
    public void setReservation(String reservation) {
        this.reservation = Func.integerToByte(Integer.parseInt(reservation), 4);
    }

}
