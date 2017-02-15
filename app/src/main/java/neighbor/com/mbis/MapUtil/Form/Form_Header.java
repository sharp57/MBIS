package neighbor.com.mbis.MapUtil.Form;

import neighbor.com.mbis.Function.Func;

/**
 * Created by user on 2016-09-01.
 */
public class Form_Header {
    private static Form_Header ourInstance = new Form_Header();

    public static Form_Header getInstance() {
        return ourInstance;
    }

    private Form_Header() {
    }

    private byte[] version;
    private byte[] op_code;
    private byte[] sr_cnt;
    private byte[] deviceID;
    private byte[] localCode;
    private byte[] dataLength;

    public byte[] getSr_cnt() {
        return sr_cnt;
    }

    public void setSr_cnt(byte[] sr_cnt) {
        this.sr_cnt = sr_cnt;
    }
    public void setSr_cnt(int sr_cnt) {
        this.sr_cnt = Func.integerToByte(sr_cnt, 2);
    }
    public void setVersion(byte[] version) {
        this.version = version;
    }
    public void setVersion(int version) {
        this.version = Func.integerToByte(version, 1);
    }

    public void setOp_code(byte[] op_code) {
        this.op_code = op_code;
    }
    public void setOp_code(byte op_code) {
        this.op_code = new byte[]{op_code};
    }


    public void setDeviceID(byte[] deviceID) {
        this.deviceID = deviceID;
    }
    public void setDeviceID(long deviceID) {
        this.deviceID = Func.longToByte(deviceID, 8);
    }

    public void setLocalCode(byte[] localCode) {
        this.localCode = localCode;
    }
    public void setLocalCode(int localCode) {
        this.localCode = Func.integerToByte(localCode, 2);
    }

    public void setDataLength(byte[] dataLength) {
        this.dataLength = dataLength;
    }
    public void setDataLength(int dataLength) {
        this.dataLength = Func.integerToByte(dataLength, 4);
    }

    public byte[] getVersion() {
        return version;
    }

    public byte[] getOp_code() {
        return op_code;
    }

    public byte[] getDeviceID() {
        return deviceID;
    }

    public byte[] getLocalCode() {
        return localCode;
    }

    public byte[] getDataLength() {
        return dataLength;
    }
}
