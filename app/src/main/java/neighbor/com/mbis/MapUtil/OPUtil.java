package neighbor.com.mbis.MapUtil;

/**
 * Created by user on 2016-09-29.
 */

public class OPUtil {
    public final static byte OP_ACK = (byte) 0x01;
    public final static byte OP_NACK = (byte) 0x02;
    public final static byte OP_USER_CERTIFICATION = (byte) 0x03;
    public final static byte OP_USER_CERTIFICATION_AFTER_DEVICEID_SEND = (byte) 0x04;
    public final static byte OP_START_DRIVE = (byte) 0x15;
    public final static byte OP_BUS_LOCATION = (byte) 0x20;
    public final static byte OP_ARRIVE_STATION = (byte) 0x21;
    public final static byte OP_START_STATION = (byte) 0x22;
    public final static byte OP_OFFENCE_INFO = (byte) 0x24;
    public final static byte OP_END_DRIVE = (byte) 0x31;
    public final static byte OP_EMERGENCY_INFO = (byte) 0x51;

    public final static byte OP_OTHER_BUS_INFO = (byte) 0x25;

    public final static byte OP_ROUTE_STATION_DATA_INFO = (byte) 0x73;
    public final static byte OP_STATION_DATA_INFO = (byte) 0x72;
    public final static byte OP_ROUTE_DATA_INFO = (byte) 0x71;

    public final static byte OP_FTP_INFO = (byte) 0x33;
    public final static byte OP_CONTROL_INFO = (byte) 0x34;


    public static boolean opCheck(byte[] op) {
        if (op[0] == OP_ACK
                || op[0] == OP_NACK
                || op[0] == OP_USER_CERTIFICATION
                || op[0] == OP_USER_CERTIFICATION_AFTER_DEVICEID_SEND
                || op[0] == OP_START_DRIVE
                || op[0] == OP_BUS_LOCATION
                || op[0] == OP_ARRIVE_STATION
                || op[0] == OP_START_STATION
                || op[0] == OP_OFFENCE_INFO
                || op[0] == OP_END_DRIVE
                || op[0] == OP_EMERGENCY_INFO
                || op[0] == OP_OTHER_BUS_INFO
                || op[0] == OP_ROUTE_STATION_DATA_INFO
                || op[0] == OP_STATION_DATA_INFO
                || op[0] == OP_ROUTE_DATA_INFO
                || op[0] == OP_FTP_INFO
                || op[0] == OP_CONTROL_INFO
                ) return true;
        else return false;
    }

    public static boolean opCheck(byte op) {
        if (op == OP_ACK
                || op == OP_NACK
                || op == OP_USER_CERTIFICATION
                || op == OP_USER_CERTIFICATION_AFTER_DEVICEID_SEND
                || op == OP_START_DRIVE
                || op == OP_BUS_LOCATION
                || op == OP_ARRIVE_STATION
                || op == OP_START_STATION
                || op == OP_OFFENCE_INFO
                || op == OP_END_DRIVE
                || op == OP_EMERGENCY_INFO
                || op == OP_OTHER_BUS_INFO
                || op == OP_ROUTE_STATION_DATA_INFO
                || op == OP_STATION_DATA_INFO
                || op == OP_ROUTE_DATA_INFO
                || op == OP_FTP_INFO
                || op == OP_CONTROL_INFO
                ) return true;
        else return false;
    }
}
