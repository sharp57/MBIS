package neighbor.com.mbis.MapUtil;

/**
 * Created by user on 2016-09-01.
 */
public class BytePosition {

    //HEADER
    public final static int HEADER_SIZE = 18;
    public final static int HEADER_VERSION_START = 0;
    public final static int HEADER_OPCODE = 1;
    public final static int HEADER_SRCNT = 2;
    public final static int HEADER_DEVICEID = 4;
    public final static int HEADER_LOCALCODE = 12;
    public final static int HEADER_DATALENGTH = 14;

    //BODY(DEFAULT)
    public final static int BODY_DEFAULT_SIZE = 73;
    public final static int BODY_DEFAULT_SENDYEAR_DATE = 18;
    public final static int BODY_DEFAULT_SENDMONTH = 20;
    public final static int BODY_DEFAULT_SENDDAY = 22;
    public final static int BODY_DEFAULT_SENDHOUR_TIME = 24;
    public final static int BODY_DEFAULT_SENDMIN = 26;
    public final static int BODY_DEFAULT_SENDSEC = 28;

    public final static int BODY_DEFAULT_EVENTYEAR_DATE = 30;
    public final static int BODY_DEFAULT_EVENTMONTH = 32;
    public final static int BODY_DEFAULT_EVENTDAY = 34;
    public final static int BODY_DEFAULT_EVENTHOUR_TIME = 36;
    public final static int BODY_DEFAULT_EVENTMIN = 38;
    public final static int BODY_DEFAULT_EVENTSEC = 40;

    public final static int BODY_DEFAULT_ROUTEID_INFO = 42;
    public final static int BODY_DEFAULT_ROUTENUM = 50;
    public final static int BODY_DEFAULT_ROUTENUMEXPANSION = 55;
    public final static int BODY_DEFAULT_ROUTEFORM = 57;
    public final static int BODY_DEFAULT_ROUTEDIVISION = 58;

    public final static int BODY_DEFAULT_LOCATIONX_GPS = 60;
    public final static int BODY_DEFAULT_LOCATIONY = 64;
    public final static int BODY_DEFAULT_BEARING = 68;
    public final static int BODY_DEFAULT_SPEED = 70;

    public final static int BODY_DEFAULT_DEVICESTATE = 72;


    //BODY(STATION_ARRIVE)
    public final static int BODY_STATION_ARRIVE_SIZE = 87;
    public final static int BODY_STATION_ARRIVE_STATIONID = 73;
    public final static int BODY_STATION_ARRIVE_STATIONTURN = 78;
    public final static int BODY_STATION_ARRIVE_ADJACENTTRAVELTIME = 80;
    public final static int BODY_STATION_ARRIVE_DRIVEDIVISION = 82;
    public final static int BODY_STATION_ARRIVE_RESERVATION = 83;

    //BODY(STATION_START)
    public final static int BODY_STATION_START_SIZE = 89;
    public final static int BODY_STATION_START_STATIONID = 73;
    public final static int BODY_STATION_START_STATIONTURN = 78;
    public final static int BODY_STATION_START_SERVICETIME = 80;
    public final static int BODY_STATION_START_ADJACENTTRAVELTIME = 82;
    public final static int BODY_STATION_START_DRIVEDIVISION = 84;
    public final static int BODY_STATION_START_RESERVATION = 85;

    //BODY(DRIVE_START)
    public final static int BODY_DRIVE_START_SIZE = 78;
    public final static int BODY_DRIVE_START_DRIVEDIVISION = 73;
    public final static int BODY_DRIVE_START_RESERVATION = 74;

    //BODY(DRIVE_END)
    public final static int BODY_DRIVE_END_SIZE = 99;
    public final static int BODY_DRIVE_END_DRIVEDATE = 73;
    public final static int BODY_DRIVE_END_STARTTIME = 79;
    public final static int BODY_DRIVE_END_STATIONID = 85;
    public final static int BODY_DRIVE_END_STATIONTURN = 90;
    public final static int BODY_DRIVE_END_DETECTSTATION_ARRIVENUM = 92;
    public final static int BODY_DRIVE_END_DETECTSTATION_STARTNUM = 93;
    public final static int BODY_DRIVE_END_DRIVEDIVISION = 94;
    public final static int BODY_DRIVE_END_RESERVATION = 95;

    //BODY(OFFENCE)
    public final static int BODY_OFFENCE_SIZE = 85;
    public final static int BODY_OFFENCE_PASS_STATIONID = 73;
    public final static int BODY_OFFENCE_PASS_STATIONTURN = 78;
    public final static int BODY_OFFENCE_OFFENCECODE = 80;
    public final static int BODY_OFFENCE_RESERVATION = 81;

    //BODY(EMERGENCY)
    public final static int BODY_EMERGENCY_SIZE = 85;
    public final static int BODY_EMERGENCY_PASS_STATIONID = 73;
    public final static int BODY_EMERGENCY_PASS_STATIONTURN = 78;
    public final static int BODY_EMERGENCY_EMERGENCYCODE = 80;
    public final static int BODY_EMERGENCY_RESERVATION = 81;

    //BODY(BUS LOCATION)
    public final static int BODY_BUSLOCATION_SIZE = 92;
    public final static int BODY_BUSLOCATION_PASS_STATIONID = 73;
    public final static int BODY_BUSLOCATION_PASS_STATIONTURN = 78;
    public final static int BODY_BUSLOCATION_ARRIVE_STATIONID = 80;
    public final static int BODY_BUSLOCATION_ARRIVE_STATIONTURN = 85;
    public final static int BODY_BUSLOCATION_DRIVEDIVISION = 87;
    public final static int BODY_BUSLOCATION_RESERVATION = 88;



    //BODY(OTHER BUS INFO)
    public final static int BODY_OTHER_BUS_INFO_SIZE = 72;
    public final static int BODY_OTHER_BUS_INFO_SENDYEAR_DATE = 18;
    public final static int BODY_OTHER_BUS_INFO_SENDMONTH = 20;
    public final static int BODY_OTHER_BUS_INFO_SENDDAY = 22;
    public final static int BODY_OTHER_BUS_INFO_SENDHOUR_TIME = 24;
    public final static int BODY_OTHER_BUS_INFO_SENDMIN = 26;
    public final static int BODY_OTHER_BUS_INFO_SENDSEC = 28;
    public final static int BODY_OTHER_BUS_INFO_DEVICEID = 30;
    public final static int BODY_OTHER_BUS_INFO_ROUTEID_INFO = 38;
    public final static int BODY_OTHER_BUS_INFO_ROUTENUM = 46;
    public final static int BODY_OTHER_BUS_INFO_ROUTENUMEXPANSION = 51;
    public final static int BODY_OTHER_BUS_INFO_ROUTEFORM = 53;
    public final static int BODY_OTHER_BUS_INFO_ROUTEDIVISION = 54;
    public final static int BODY_OTHER_BUS_INFO_BEFORE_BUS_DISTANCE= 56;
    public final static int BODY_OTHER_BUS_INFO_BEFORE_BUS_TIME = 57;
    public final static int BODY_OTHER_BUS_INFO_BEFORE_BUS_NUM = 58;
    public final static int BODY_OTHER_BUS_INFO_AFTER_BUS_DISTANCE= 60;
    public final static int BODY_OTHER_BUS_INFO_AFTER_BUS_TIME = 61;
    public final static int BODY_OTHER_BUS_INFO_AFTER_BUS_NUM = 62;
    public final static int BODY_OTHER_BUS_INFO_REMAIN_DISTANCE = 64;
    public final static int BODY_OTHER_BUS_INFO_REMAIN_TIME = 66;
    public final static int BODY_OTHER_BUS_INFO_RESERVATION = 68;



    //ACK
    public final static int BODY_ACK_SIZE = 33;
    public final static int BODY_ACK_SENDYEAR_DATE = 18;
    public final static int BODY_ACK_SENDMONTH = 20;
    public final static int BODY_ACK_SENDDAY = 22;
    public final static int BODY_ACK_SENDHOUR_TIME = 24;
    public final static int BODY_ACK_SENDMIN = 26;
    public final static int BODY_ACK_SENDSEC = 28;
    public final static int BODY_ACK_OPCODE = 30;
    public final static int BODY_ACK_RESERVATION = 31;

    //NACK
    public final static int BODY_NACK_SIZE = 33;
    public final static int BODY_NACK_SENDYEAR_DATE = 18;
    public final static int BODY_NACK_SENDMONTH = 20;
    public final static int BODY_NACK_SENDDAY = 22;
    public final static int BODY_NACK_SENDHOUR_TIME = 24;
    public final static int BODY_NACK_SENDMIN = 26;
    public final static int BODY_NACK_SENDSEC = 28;
    public final static int BODY_NACK_OPCODE = 30;
    public final static int BODY_NACK_RESERVATION = 31;

    //USER CERTIFICATION
    public final static int BODY_USER_CERTIFICATION_SIZE = 40;
    public final static int BODY_USER_CERTIFICATION_SENDYEAR_DATE = 18;
    public final static int BODY_USER_CERTIFICATION_SENDMONTH = 20;
    public final static int BODY_USER_CERTIFICATION_SENDDAY = 22;
    public final static int BODY_USER_CERTIFICATION_SENDHOUR_TIME = 24;
    public final static int BODY_USER_CERTIFICATION_SENDMIN = 26;
    public final static int BODY_USER_CERTIFICATION_SENDSEC = 28;
    public final static int BODY_USER_CERTIFICATION_PHONE_NUM = 30;
    public final static int BODY_USER_CERTIFICATION_BUS_NUM = 34;
    public final static int BODY_USER_CERTIFICATION_RESERVATION = 36;


    //USER CERTIFICATION AFTER DEVICEID SEND
    public final static int BODY_USER_CERTIFICATION_AFTER_SIZE = 43;
    public final static int BODY_USER_CERTIFICATION_AFTER_SENDYEAR_DATE = 18;
    public final static int BODY_USER_CERTIFICATION_AFTER_SENDMONTH = 20;
    public final static int BODY_USER_CERTIFICATION_AFTER_SENDDAY = 22;
    public final static int BODY_USER_CERTIFICATION_AFTER_SENDHOUR_TIME = 24;
    public final static int BODY_USER_CERTIFICATION_AFTER_SENDMIN = 26;
    public final static int BODY_USER_CERTIFICATION_AFTER_SENDSEC = 28;
    public final static int BODY_USER_CERTIFICATION_AFTER_DEVICEID = 30;
    public final static int BODY_USER_CERTIFICATION_AFTER_OPERATION_DIVISION_CODE = 38;
    public final static int BODY_USER_CERTIFICATION_AFTER_RESERVATION = 39;



    //ROUTE STATION INFORMATION
    public final static int BODY_ROUTE_STATION_SENDYEAR_DATE = 18;
    public final static int BODY_ROUTE_STATION_SENDMONTH = 20;
    public final static int BODY_ROUTE_STATION_SENDDAY = 22;
    public final static int BODY_ROUTE_STATION_SENDHOUR_TIME = 24;
    public final static int BODY_ROUTE_STATION_SENDMIN = 26;
    public final static int BODY_ROUTE_STATION_SENDSEC = 28;

    public final static int BODY_ROUTE_STATION_REVISION_NUM = 30;

    public final static int BODY_ROUTE_STATION_ROUTEID_INFO = 32;
    public final static int BODY_ROUTE_STATION_ROUTENUM = 40;
    public final static int BODY_ROUTE_STATION_ROUTENUMEXPANSION = 45;
    public final static int BODY_ROUTE_STATION_ROUTEFORM = 47;
    public final static int BODY_ROUTE_STATION_ROUTEDIVISION = 48;

    public final static int BODY_ROUTE_STATION_TOTALSTATIONNUM = 50;

    public final static int BODY_ROUTE_STATION_APPLY_SENDYEAR_DATE = 52;
    public final static int BODY_ROUTE_STATION_APPLY_SENDMONTH = 54;
    public final static int BODY_ROUTE_STATION_APPLY_SENDDAY = 56;
    public final static int BODY_ROUTE_STATION_APPLY_SENDHOUR_TIME = 58;
    public final static int BODY_ROUTE_STATION_APPLY_SENDMIN = 60;
    public final static int BODY_ROUTE_STATION_APPLY_SENDSEC = 62;

    public final static int BODY_ROUTE_STATION_DATA_START = 64;
    public final static int BODY_ROUTE_STATION_DATA_SIZE = 11;



    //ROUTE INFORMATION
    public final static int BODY_ROUTE_SENDYEAR_DATE = 18;
    public final static int BODY_ROUTE_SENDMONTH = 20;
    public final static int BODY_ROUTE_SENDDAY = 22;
    public final static int BODY_ROUTE_SENDHOUR_TIME = 24;
    public final static int BODY_ROUTE_SENDMIN = 26;
    public final static int BODY_ROUTE_SENDSEC = 28;

    public final static int BODY_ROUTE_REVISION_NUM = 30;

    public final static int BODY_ROUTE_TOTALROUTENUM = 32;

    public final static int BODY_ROUTE_APPLY_SENDYEAR_DATE = 33;
    public final static int BODY_ROUTE_APPLY_SENDMONTH = 35;
    public final static int BODY_ROUTE_APPLY_SENDDAY = 37;
    public final static int BODY_ROUTE_APPLY_SENDHOUR_TIME = 39;
    public final static int BODY_ROUTE_APPLY_SENDMIN = 41;
    public final static int BODY_ROUTE_APPLY_SENDSEC = 43;

    public final static int BODY_ROUTE_DATA_START = 45;
    public final static int BODY_ROUTE_DATA_SIZE = 150;


    //STATION INFORMATION
    public final static int BODY_STATION_SENDYEAR_DATE = 18;
    public final static int BODY_STATION_SENDMONTH = 20;
    public final static int BODY_STATION_SENDDAY = 22;
    public final static int BODY_STATION_SENDHOUR_TIME = 24;
    public final static int BODY_STATION_SENDMIN = 26;
    public final static int BODY_STATION_SENDSEC = 28;

    public final static int BODY_STATION_REVISION_NUM = 30;

    public final static int BODY_STATION_TOTALSTATIONNUM = 32;

    public final static int BODY_STATION_APPLY_SENDYEAR_DATE = 34;
    public final static int BODY_STATION_APPLY_SENDMONTH = 36;
    public final static int BODY_STATION_APPLY_SENDDAY = 38;
    public final static int BODY_STATION_APPLY_SENDHOUR_TIME = 40;
    public final static int BODY_STATION_APPLY_SENDMIN = 42;
    public final static int BODY_STATION_APPLY_SENDSEC = 44;

    public final static int BODY_STATION_DATA_START = 46;
    public final static int BODY_STATION_DATA_SIZE = 51;


    //FTP
    public final static int BODY_FTP_SIZE = 247;
    public final static int BODY_FTP_SENDYEAR_DATE = 18;
    public final static int BODY_FTP_SENDMONTH = 20;
    public final static int BODY_FTP_SENDDAY = 22;
    public final static int BODY_FTP_SENDHOUR_TIME = 24;
    public final static int BODY_FTP_SENDMIN = 26;
    public final static int BODY_FTP_SENDSEC = 28;
    public final static int BODY_FTP_DEVICEID = 30;
    public final static int BODY_FTP_IP = 38;
    public final static int BODY_FTP_PORT = 54;
    public final static int BODY_FTP_ID = 56;
    public final static int BODY_FTP_PW = 66;
    public final static int BODY_FTP_MODE = 76;
    public final static int BODY_FTP_PATH_SW_APP = 77;
    public final static int BODY_FTP_PATH_DATA = 107;
    public final static int BODY_FTP_PATH_UPLOAD = 137;
    public final static int BODY_FTP_STATION_FILE_NAME = 167;
    public final static int BODY_FTP_ROUTE_FILE_NAME = 187;
    public final static int BODY_FTP_ROUTESTATION_FILE_NAME = 207;
    public final static int BODY_FTP_APP_FILE_NAME = 227;


    //CONTROL
    public final static int BODY_CONTROL_SIZE = 39;
    public final static int BODY_CONTROL_SENDYEAR_DATE = 18;
    public final static int BODY_CONTROL_SENDMONTH = 20;
    public final static int BODY_CONTROL_SENDDAY = 22;
    public final static int BODY_CONTROL_SENDHOUR_TIME = 24;
    public final static int BODY_CONTROL_SENDMIN = 26;
    public final static int BODY_CONTROL_SENDSEC = 28;
    public final static int BODY_CONTROL_DEVICEID = 30;
    public final static int BODY_CONTROL_CONTROLCODE = 38;




}
