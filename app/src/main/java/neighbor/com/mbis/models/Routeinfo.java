package neighbor.com.mbis.models;

/**
 * Created by 권오철 on 2017-02-22.
 */

public class RouteInfo {

    private String category;
    private String busNum;
    private String direction;
    private String brt_type;
    private String start_station;
    private String last_station;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBusNum() {
        return busNum;
    }

    public void setBusNum(String busNum) {
        this.busNum = busNum;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getBrt_type() {
        return brt_type;
    }

    public void setBrt_type(String brt_type) {
        this.brt_type = brt_type;
    }

    public String getStart_station() {
        return start_station;
    }

    public void setStart_station(String start_station) {
        this.start_station = start_station;
    }

    public String getLast_station() {
        return last_station;
    }

    public void setLast_station(String last_station) {
        this.last_station = last_station;
    }
}
