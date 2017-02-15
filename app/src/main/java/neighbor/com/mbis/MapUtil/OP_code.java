package neighbor.com.mbis.MapUtil;

import neighbor.com.mbis.MapUtil.Form.Form_Body_ArriveStation;
import neighbor.com.mbis.MapUtil.Form.Form_Body_BusLocation;
import neighbor.com.mbis.MapUtil.Form.Form_Body_Emergency;
import neighbor.com.mbis.MapUtil.Form.Form_Body_EndDrive;
import neighbor.com.mbis.MapUtil.Form.Form_Body_Offence;
import neighbor.com.mbis.MapUtil.Form.Form_Body_StartDrive;
import neighbor.com.mbis.MapUtil.Form.Form_Body_StartStation;
import neighbor.com.mbis.MapUtil.Form.Form_Body_Default;
import neighbor.com.mbis.MapUtil.Form.Form_Header;

/**
 * Created by user on 2016-09-01.
 */
public class OP_code {
    Form_Header h = Form_Header.getInstance();
    Form_Body_Default bd = Form_Body_Default.getInstance();
    Form_Body_ArriveStation bas = Form_Body_ArriveStation.getInstance();
    Form_Body_StartDrive bsd = Form_Body_StartDrive.getInstance();
    Form_Body_StartStation bss = Form_Body_StartStation.getInstance();
    Form_Body_EndDrive bed = Form_Body_EndDrive.getInstance();
    Form_Body_Offence bof = Form_Body_Offence.getInstance();
    Form_Body_Emergency beg = Form_Body_Emergency.getInstance();
    Form_Body_BusLocation bbl = Form_Body_BusLocation.getInstance();

    static byte[] headerBuf = null;
    static byte[] bodyBuf_Default = null;
    static byte[] bodyBuf_ArriveStation = null;
    static byte[] bodyBuf_StartStation = null;
    static byte[] bodyBuf_StartDrive = null;
    static byte[] bodyBuf_EndDrive = null;
    static byte[] bodyBuf_Offence = null;
    static byte[] bodyBuf_Emergency = null;
    static byte[] bodyBuf_BusLocation = null;


    public OP_code(byte[] op) {
        h.setOp_code(op);
        makeHeader();
        makeBodyDefault();

        switch (op[0]) {
            case OPUtil.OP_START_DRIVE:
                //drive start
                Data.writeData = makeBodyStartDrive();
                break;
            case OPUtil.OP_BUS_LOCATION:
                //bus location information
                Data.writeData = makeBodyBusLocation();
                break;
            case OPUtil.OP_ARRIVE_STATION:
                //station arrive
                Data.writeData = makeBodyArriveStation();
                break;
            case OPUtil.OP_START_STATION:
                //station start
                Data.writeData = makeBodyStartStation();
                break;
            case OPUtil.OP_OFFENCE_INFO:
                //offence
                Data.writeData = makeBodyOffence();
                break;
            case OPUtil.OP_END_DRIVE:
                //drive end
                Data.writeData = makeBodyEndDrive();
                break;
            case OPUtil.OP_EMERGENCY_INFO:
                //emergency
                Data.writeData = makeBodyEmergency();
                break;
        }

//        if(op[0] == OPUtil.OP_START_DRIVE) {
//            //drive start
//            Data.writeData = makeBodyStartDrive();
//        } else if (op[0] == OPUtil.OP_BUS_LOCATION) {
//            //bus location information
//            Data.writeData = makeBodyBusLocation();
//        } else if (op[0] == OPUtil.OP_ARRIVE_STATION) {
//            //station arrive
//            Data.writeData = makeBodyArriveStation();
//        } else if(op[0] == OPUtil.OP_START_STATION) {
//            //station start
//            Data.writeData = makeBodyStartStation();
//        } else if(op[0] == OPUtil.OP_OFFENCE_INFO) {
//            //offence
//            Data.writeData = makeBodyOffence();
//        } else if(op[0] == OPUtil.OP_END_DRIVE) {
//            //drive end
//            Data.writeData = makeBodyEndDrive();
//        } else if(op[0] == OPUtil.OP_EMERGENCY_INFO) {
//            //emergency
//            Data.writeData = makeBodyEmergency();
//        }
    }

    private byte[] makeHeader() {
        headerBuf = new byte[BytePosition.HEADER_SIZE];

        putHeader(h.getVersion(), BytePosition.HEADER_VERSION_START);
        putHeader(h.getOp_code(), BytePosition.HEADER_OPCODE);
        putHeader(h.getSr_cnt(), BytePosition.HEADER_SRCNT);
        putHeader(h.getDeviceID(), BytePosition.HEADER_DEVICEID);
        putHeader(h.getLocalCode(), BytePosition.HEADER_LOCALCODE);
        putHeader(h.getDataLength(), BytePosition.HEADER_DATALENGTH);

        return headerBuf;
    }
    private byte[] makeBodyDefault() {
        bodyBuf_Default = new byte[BytePosition.BODY_DEFAULT_SIZE];

        putBody_Default(headerBuf, BytePosition.HEADER_VERSION_START);

        putBody_Default(bd.getSendDate(), BytePosition.BODY_DEFAULT_SENDYEAR_DATE);
        putBody_Default(bd.getSendTime(), BytePosition.BODY_DEFAULT_SENDHOUR_TIME);
        putBody_Default(bd.getEventDate(), BytePosition.BODY_DEFAULT_EVENTYEAR_DATE);
        putBody_Default(bd.getEventTime(), BytePosition.BODY_DEFAULT_EVENTHOUR_TIME);
        putBody_Default(bd.getRouteInfo(), BytePosition.BODY_DEFAULT_ROUTEID_INFO);
        putBody_Default(bd.getGpsInfo(), BytePosition.BODY_DEFAULT_LOCATIONX_GPS);
        putBody_Default(bd.getDeviceState(), BytePosition.BODY_DEFAULT_DEVICESTATE);

        return bodyBuf_Default;
    }


    private byte[] makeBodyArriveStation() {
        bodyBuf_ArriveStation = new byte[BytePosition.BODY_STATION_ARRIVE_SIZE];

        putBody_ArriveStation(bodyBuf_Default, BytePosition.HEADER_VERSION_START);

        putBody_ArriveStation(bas.getStationId(), BytePosition.BODY_STATION_ARRIVE_STATIONID);
        putBody_ArriveStation(bas.getStationTurn(), BytePosition.BODY_STATION_ARRIVE_STATIONTURN);
        putBody_ArriveStation(bas.getAdjacentTravelTime(), BytePosition.BODY_STATION_ARRIVE_ADJACENTTRAVELTIME);
        putBody_ArriveStation(bas.getDriveDivision(), BytePosition.BODY_STATION_ARRIVE_DRIVEDIVISION);
        putBody_ArriveStation(bas.getReservation(), BytePosition.BODY_STATION_ARRIVE_RESERVATION);

        return bodyBuf_ArriveStation;
    }
    private byte[] makeBodyStartStation() {
        bodyBuf_StartStation = new byte[BytePosition.BODY_STATION_START_SIZE];

        putBody_StartStation(bodyBuf_Default, BytePosition.HEADER_VERSION_START);

        putBody_StartStation(bss.getStationId(), BytePosition.BODY_STATION_START_STATIONID);
        putBody_StartStation(bss.getStationTurn(), BytePosition.BODY_STATION_START_STATIONTURN);
        putBody_StartStation(bss.getServiceTime(), BytePosition.BODY_STATION_START_SERVICETIME);
        putBody_StartStation(bss.getAdjacentTravelTime(), BytePosition.BODY_STATION_START_ADJACENTTRAVELTIME);
        putBody_StartStation(bss.getDriveDivision(), BytePosition.BODY_STATION_START_DRIVEDIVISION);
        putBody_StartStation(bss.getReservation(), BytePosition.BODY_STATION_START_RESERVATION);

        return bodyBuf_StartStation;
    }
    private byte[] makeBodyStartDrive() {
        bodyBuf_StartDrive = new byte[BytePosition.BODY_DRIVE_START_SIZE];

        putBody_StartDrive(bodyBuf_Default, BytePosition.HEADER_VERSION_START);
        putBody_StartDrive(bsd.getDriveDivision(), BytePosition.BODY_DRIVE_START_DRIVEDIVISION);
        putBody_StartDrive(bsd.getReservation(), BytePosition.BODY_DRIVE_START_RESERVATION);

        return bodyBuf_StartDrive;
    }
    private byte[] makeBodyEndDrive() {
        bodyBuf_EndDrive = new byte[BytePosition.BODY_DRIVE_END_SIZE];

        putBody_EndDrive(bodyBuf_Default, BytePosition.HEADER_VERSION_START);

        putBody_EndDrive(bed.getDriveDate(), BytePosition.BODY_DRIVE_END_DRIVEDATE);
        putBody_EndDrive(bed.getStartTime(), BytePosition.BODY_DRIVE_END_STARTTIME);
        putBody_EndDrive(bed.getStationId(), BytePosition.BODY_DRIVE_END_STATIONID);
        putBody_EndDrive(bed.getStationTurn(), BytePosition.BODY_DRIVE_END_STATIONTURN);

        putBody_EndDrive(bed.getDetectStationArriveNum(), BytePosition.BODY_DRIVE_END_DETECTSTATION_ARRIVENUM);
        putBody_EndDrive(bed.getDetectStationStartNum(), BytePosition.BODY_DRIVE_END_DETECTSTATION_STARTNUM);
        putBody_EndDrive(bed.getDriveDivision(), BytePosition.BODY_DRIVE_END_DRIVEDIVISION);
        putBody_EndDrive(bed.getReservation(), BytePosition.BODY_DRIVE_END_RESERVATION);

        return bodyBuf_EndDrive;
    }
    private byte[] makeBodyOffence() {
        bodyBuf_Offence = new byte[BytePosition.BODY_OFFENCE_SIZE];

        putBody_Offence(bodyBuf_Default, BytePosition.HEADER_VERSION_START);

        putBody_Offence(bof.getPassStationId(), BytePosition.BODY_OFFENCE_PASS_STATIONID);
        putBody_Offence(bof.getPassStationTurn(), BytePosition.BODY_OFFENCE_PASS_STATIONTURN);
        putBody_Offence(bof.getOffenceCode(), BytePosition.BODY_OFFENCE_OFFENCECODE);
        putBody_Offence(bof.getReservation(), BytePosition.BODY_OFFENCE_RESERVATION);

        return bodyBuf_Offence;

    }
    private byte[] makeBodyEmergency() {
        bodyBuf_Emergency = new byte[BytePosition.BODY_EMERGENCY_SIZE];

        putBody_Emergency(bodyBuf_Default, BytePosition.HEADER_VERSION_START);

        putBody_Emergency(beg.getPassStationId(), BytePosition.BODY_EMERGENCY_PASS_STATIONID);
        putBody_Emergency(beg.getPassStationTurn(), BytePosition.BODY_EMERGENCY_PASS_STATIONTURN);
        putBody_Emergency(beg.getEmergencyCode(), BytePosition.BODY_EMERGENCY_EMERGENCYCODE);
        putBody_Emergency(beg.getReservation(), BytePosition.BODY_EMERGENCY_RESERVATION);

        return bodyBuf_Emergency;

    }
    private byte[] makeBodyBusLocation() {
        bodyBuf_BusLocation = new byte[BytePosition.BODY_BUSLOCATION_SIZE];

        putBody_BusLocation(bodyBuf_Default, BytePosition.HEADER_VERSION_START);

        putBody_BusLocation(bbl.getPassStationId(), BytePosition.BODY_BUSLOCATION_PASS_STATIONID);
        putBody_BusLocation(bbl.getPassStationTurn(), BytePosition.BODY_BUSLOCATION_PASS_STATIONTURN);
        putBody_BusLocation(bbl.getArriveStationId(), BytePosition.BODY_BUSLOCATION_ARRIVE_STATIONID);
        putBody_BusLocation(bbl.getArriveStationTurn(), BytePosition.BODY_BUSLOCATION_ARRIVE_STATIONTURN);
        putBody_BusLocation(bbl.getDriveDivision(), BytePosition.BODY_BUSLOCATION_DRIVEDIVISION);
        putBody_BusLocation(bbl.getReservation(), BytePosition.BODY_BUSLOCATION_RESERVATION);

        return bodyBuf_BusLocation;

    }


    private void putHeader(byte[] b, int position) {
        System.arraycopy(b, 0, headerBuf, position, b.length);
    }
    private void putBody_Default(byte[] b, int position) {
        System.arraycopy(b, 0, bodyBuf_Default, position, b.length);
    }
    private void putBody_StartDrive(byte[] b, int position) {
        System.arraycopy(b, 0, bodyBuf_StartDrive, position, b.length);
    }
    private void putBody_ArriveStation(byte[] b, int position) {
        System.arraycopy(b, 0, bodyBuf_ArriveStation, position, b.length);
    }
    private void putBody_StartStation(byte[] b, int position) {
        System.arraycopy(b, 0, bodyBuf_StartStation, position, b.length);
    }
    private void putBody_EndDrive(byte[] b, int position) {
        System.arraycopy(b, 0, bodyBuf_EndDrive, position, b.length);
    }
    private void putBody_Offence(byte[] b, int position) {
        System.arraycopy(b, 0, bodyBuf_Offence, position, b.length);
    }
    private void putBody_Emergency(byte[] b, int position) {
        System.arraycopy(b, 0, bodyBuf_Emergency, position, b.length);
    }
    private void putBody_BusLocation(byte[] b, int position) {
        System.arraycopy(b, 0, bodyBuf_BusLocation, position, b.length);
    }
}
