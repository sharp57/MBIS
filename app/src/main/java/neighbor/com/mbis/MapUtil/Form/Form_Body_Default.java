package neighbor.com.mbis.MapUtil.Form;

import neighbor.com.mbis.Function.Func;

/**
 * Created by user on 2016-09-01.
 */
public class Form_Body_Default {
    private static Form_Body_Default ourInstance = new Form_Body_Default();

    public static Form_Body_Default getInstance() {
        return ourInstance;
    }

    private Form_Body_Default() {
    }

    private byte[] sendDate;
    private byte[] sendTime;
    private byte[] eventDate;
    private byte[] eventTime;
    private byte[] routeInfo;
    private byte[] gpsInfo;
    private byte[] deviceState;

    public void setSendDate(byte[] sendDate) {
        this.sendDate = sendDate;
    }
    public void setSendDate(String sendDate) {
        this.sendDate = Func.stringToByte(sendDate);
    }
    public void setSendDate(String year, String month, String day) {
        String ymd = year + month + day;
        this.sendDate = Func.stringToByte(ymd);
    }
    public void setSendDate(int year, int month, int day) {
        String y = String.format("%02d", year);
        String m = String.format("%02d", month);
        String d = String.format("%02d", day);
        String ymd = y + m + d;
        this.sendDate = Func.stringToByte(ymd);
    }


    public void setSendTime(byte[] sendTime) {
        this.sendTime = sendTime;
    }
    public void setSendTime(String sendTime) {
        this.sendTime = Func.stringToByte(sendTime);
    }
    public void setSendTime(String hour, String min, String sec) {
        String hms = hour + min + sec;
        this.sendTime = Func.stringToByte(hms);
    }
    public void setSendTime(int hour, int min, int sec) {
        String h = String.format("%02d", hour);
        String m = String.format("%02d", min);
        String s = String.format("%02d", sec);
        String hms = h + m + s;
        this.sendTime = Func.stringToByte(hms);
    }

    public void setEventDate(byte[] eventDate) {
        this.eventDate = eventDate;
    }
    public void setEventDate(String eventDate) {
        this.eventDate = Func.stringToByte(eventDate);
    }
    public void setEventDate(String year, String month, String day) {
        String ymd = year + month + day;
        this.eventDate = Func.stringToByte(ymd);
    }
    public void setEventDate(int year, int month, int day) {
        String y = String.format("%02d", year);
        String m = String.format("%02d", month);
        String d = String.format("%02d", day);
        String ymd = y + m + d;
        this.eventDate = Func.stringToByte(ymd);
    }

    public void setEventTime(byte[] eventTime) {
        this.eventTime = eventTime;
    }
    public void setEventTime(String eventTime) {
        this.eventTime = Func.stringToByte(eventTime);
    }
    public void setEventTime(String hour, String min, String sec) {
        String hms = hour + min + sec;
        this.eventTime = Func.stringToByte(hms);
    }
    public void setEventTime(int hour, int min, int sec) {
        String h = String.format("%02d", hour);
        String m = String.format("%02d", min);
        String s = String.format("%02d", sec);
        String hms = h + m + s;
        this.eventTime = Func.stringToByte(hms);
    }

    public void setRouteInfo(byte[] routeInfo) {
        this.routeInfo = routeInfo;
    }
    public void setRouteInfo(long routeId, String routeNum, int routeForm, String routeDivision) {
        String[] rNumBuff = routeNum.split("-");
        if(rNumBuff.length !=2 ) {
            rNumBuff[1] = "00";
        }
        rNumBuff[0] = String.format("%05d", Integer.parseInt(rNumBuff[0]));
        rNumBuff[1] = String.format("%02d", Integer.parseInt(rNumBuff[1]));

        byte[] rID = Func.longToByte(routeId, 8);
        byte[] rNum1 = Func.stringToByte(rNumBuff[0]);
        byte[] rNum2 = Func.stringToByte(rNumBuff[1]);

        byte[] rForm = Func.stringToByte(Integer.toString(routeForm));
        byte[] rDivision = Func.stringToByte(routeDivision);

        byte[] m1 = Func.mergyByte(rID, Func.mergyByte(rNum1, rNum2));
        byte[] m2 = Func.mergyByte(rForm, rDivision);

        this.routeInfo = Func.mergyByte(m1, m2);
    }

    public void setRouteInfo(long routeId, String routeNum, String routeForm, String routeDivision) {
        String[] rNumBuff = routeNum.split("-");
        if(rNumBuff.length !=2 ) {
            rNumBuff[1] = "00";
        }
        rNumBuff[0] = String.format("%05d", Integer.parseInt(rNumBuff[0]));
        rNumBuff[1] = String.format("%02d", Integer.parseInt(rNumBuff[1]));

        byte[] rID = Func.longToByte(routeId, 8);
        byte[] rNum1 = Func.stringToByte(rNumBuff[0]);
        byte[] rNum2 = Func.stringToByte(rNumBuff[1]);

        byte[] rForm = Func.stringToByte(routeForm);
        byte[] rDivision = Func.stringToByte(routeDivision);

        byte[] m1 = Func.mergyByte(rID, Func.mergyByte(rNum1, rNum2));
        byte[] m2 = Func.mergyByte(rForm, rDivision);

        this.routeInfo = Func.mergyByte(m1, m2);
    }



    public void setGpsInfo(byte[] gpsInfo) {
        this.gpsInfo = gpsInfo;
    }

    public void setGpsInfo(int locationX, int locationY, int bearing, int speed) {
        byte[] x = Func.integerToByte(locationX, 4);
        byte[] y = Func.integerToByte(locationY, 4);
        byte[] b = Func.integerToByte(bearing, 2);
        byte[] s = Func.integerToByte(speed, 2);

        byte[] m1 = Func.mergyByte(x, y);
        byte[] m2 = Func.mergyByte(b, s);

        this.gpsInfo = Func.mergyByte(m1, m2);
    }

    public void setDeviceState(byte[] deviceState) {
        this.deviceState = deviceState;
    }
    public void setDeviceState(byte deviceState) {
        this.deviceState = new byte[]{deviceState};
    }
    public void setDeviceState(int deviceState) {
        this.deviceState = Func.integerToByte(deviceState, 1);
    }

    public byte[] getSendDate() {
        return sendDate;
    }

    public byte[] getSendTime() {
        return sendTime;
    }

    public byte[] getEventDate() {
        return eventDate;
    }

    public byte[] getEventTime() {
        return eventTime;
    }

    public byte[] getRouteInfo() {
        return routeInfo;
    }

    public byte[] getGpsInfo() {
        return gpsInfo;
    }

    public byte[] getDeviceState() {
        return deviceState;
    }
}
