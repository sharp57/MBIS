package neighbor.com.mbis.MapUtil.Value;

import java.util.ArrayList;

/**
 * Created by user on 2016-08-25.
 */
public class StationSubBuffer_2 {
    private static StationSubBuffer_2 ourInstance = new StationSubBuffer_2();

    public static StationSubBuffer_2 getInstance() {
        return ourInstance;
    }

    private ArrayList<Long> referenceStationId;
    private ArrayList<Double> referenceLatPosition;
    private ArrayList<Double> referenceLngPosition;
    private ArrayList<Double> distance;
    private ArrayList<Double> remark;
    private ArrayList<Integer> stationOrder;
    private ArrayList<Integer> stationDivision;
    private ArrayList<String> referenceStationName;

    public ArrayList<String> getReferenceStationName() {
        return referenceStationName;
    }

    public void setReferenceStationName(ArrayList<String> referenceStationName) {
        this.referenceStationName = referenceStationName;
    }



    private StationSubBuffer_2() {
        referenceLatPosition = new ArrayList<Double>();
        referenceLngPosition = new ArrayList<Double>();
        distance = new ArrayList<Double>();
        referenceStationId = new ArrayList<Long>();
        remark = new ArrayList<Double>();
        stationOrder= new ArrayList<Integer>();
        stationDivision= new ArrayList<Integer>();
        referenceStationName= new ArrayList<String >();
    }

    public ArrayList<Double> getDistance() {
        return distance;
    }

    public void setDistance(ArrayList<Double> distance) {
        this.distance = distance;
    }



    public ArrayList<Double> getReferenceLatPosition() {
        return referenceLatPosition;
    }

    public void setReferenceLatPosition(ArrayList<Double> referenceLatPosition) {
        this.referenceLatPosition = referenceLatPosition;
    }

    public ArrayList<Double> getReferenceLngPosition() {
        return referenceLngPosition;
    }

    public void setReferenceLngPosition(ArrayList<Double> referenceLngPosition) {
        this.referenceLngPosition = referenceLngPosition;
    }

    public ArrayList<Long> getReferenceStationId() {
        return referenceStationId;
    }

    public void setReferenceStationId(ArrayList<Long> refernceUniqueNum) {
        this.referenceStationId = refernceUniqueNum;
    }

    public ArrayList<Double> getRemark() {
        return remark;
    }

    public void setRemark(ArrayList<Double> remark) {
        this.remark = remark;
    }

    public ArrayList<Integer> getStationOrder() {
        return stationOrder;
    }

    public void setStationOrder(ArrayList<Integer> stationOrder) {
        this.stationOrder = stationOrder;
    }

    public ArrayList<Integer> getStationDivision() {
        return stationDivision;
    }

    public void setStationDivision(ArrayList<Integer> stationDivision) {
        this.stationDivision = stationDivision;
    }
    public void addReferenceLatPosition(double item) {
        referenceLatPosition.add(item);
    }
    public void addReferenceLngPosition(double item) {
        referenceLngPosition.add(item);
    }
    public void addReferenceStationId(long item) {
        referenceStationId.add(item);
    }
    public void addDistance(double item) {distance.add(item);}
    public void addRemark(double item) {remark.add(item);}
    public void addStationOrder(int item) {stationOrder.add(item);}
    public void clearAll() {
        referenceLatPosition.clear();
        referenceLngPosition.clear();
        referenceStationId.clear();
        referenceStationName.clear();
        distance.clear();
        remark.clear();
        stationDivision.clear();
        stationOrder.clear();
    }
}
