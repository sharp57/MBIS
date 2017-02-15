package neighbor.com.mbis.MapUtil.MakeFile;

/**
 * Created by user on 2016-10-19.
 */

public class Buf_R {
    private long routeID;
    private String routeNum;
    private int routeForm, routeType, routeFirstStartTime, routeLastStartTime, routeAverageInterval, routeAverageTime, routeLength, routeStationNum;
    private String routeStartStation, routeImportantStation1, routeImportantStation2, routeLastStation;

    public long getRouteID() {
        return routeID;
    }

    public void setRouteID(long routeID) {
        this.routeID = routeID;
    }

    public String getRouteNum() {
        return routeNum;
    }

    public void setRouteNum(String routeNum) {
        this.routeNum = routeNum;
    }
    public void setRouteNum(String routeName1, String routeName2) {
        if(routeName2 != null) {
            this.routeNum = routeName1 + "-" + routeName2;
        } else {
            this.routeNum = routeName1;
        }
    }

    public int getRouteForm() {
        return routeForm;
    }

    public void setRouteForm(int routeForm) {
        this.routeForm = routeForm;
    }

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }

    public int getRouteFirstStartTime() {
        return routeFirstStartTime;
    }

    public void setRouteFirstStartTime(int routeFirstStartTime) {
        this.routeFirstStartTime = routeFirstStartTime;
    }

    public int getRouteLastStartTime() {
        return routeLastStartTime;
    }

    public void setRouteLastStartTime(int routeLastStartTime) {
        this.routeLastStartTime = routeLastStartTime;
    }

    public int getRouteAverageInterval() {
        return routeAverageInterval;
    }

    public void setRouteAverageInterval(int routeAverageInterval) {
        this.routeAverageInterval = routeAverageInterval;
    }

    public int getRouteAverageTime() {
        return routeAverageTime;
    }

    public void setRouteAverageTime(int routeAverageTime) {
        this.routeAverageTime = routeAverageTime;
    }

    public int getRouteLength() {
        return routeLength;
    }

    public void setRouteLength(int routeLength) {
        this.routeLength = routeLength;
    }

    public int getRouteStationNum() {
        return routeStationNum;
    }

    public void setRouteStationNum(int routeStationNum) {
        this.routeStationNum = routeStationNum;
    }

    public String getRouteStartStation() {
        return routeStartStation;
    }

    public void setRouteStartStation(String routeStartStation) {
        this.routeStartStation = routeStartStation;
    }

    public String getRouteImportantStation1() {
        return routeImportantStation1;
    }

    public void setRouteImportantStation1(String routeImportantStation1) {
        this.routeImportantStation1 = routeImportantStation1;
    }

    public String getRouteImportantStation2() {
        return routeImportantStation2;
    }

    public void setRouteImportantStation2(String routeImportantStation2) {
        this.routeImportantStation2 = routeImportantStation2;
    }

    public String getRouteLastStation() {
        return routeLastStation;
    }

    public void setRouteLastStation(String routeLastStation) {
        this.routeLastStation = routeLastStation;
    }
}
