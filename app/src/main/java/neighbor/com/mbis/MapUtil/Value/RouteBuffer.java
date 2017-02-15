package neighbor.com.mbis.MapUtil.Value;

/**
 * Created by user on 2016-09-07.
 */
public class RouteBuffer {
    private static RouteBuffer ourInstance = new RouteBuffer();

    public static RouteBuffer getInstance() {
        return ourInstance;
    }

    private long routeID;
    private String routeName;
    private int routeType;


    private RouteBuffer() {
    }

    public String getRouteName() {
        return routeName;
    }
    public void setRouteName(String routeName) {
        String[] buf = routeName.split("-");
        if(buf.length != 2) {
            this.routeName = routeName + "-00";
        } else this.routeName = routeName;
    }

    public long getRouteID() {
        return routeID;
    }
    public void setRouteID(long routeID) {
        this.routeID = routeID;
    }

    public int getRouteType() {
        return routeType;
    }
    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }
}
