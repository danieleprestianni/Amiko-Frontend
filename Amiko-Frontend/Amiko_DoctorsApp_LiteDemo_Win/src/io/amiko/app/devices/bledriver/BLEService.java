package io.amiko.app.devices.bledriver;


import java.util.ArrayList;
import java.util.Hashtable;

public class BLEService {
    
    public static Hashtable<String, String> profiles = new Hashtable<String, String>();
    static {
        profiles.put("0x180A", "");
    }
    
    
    protected int start, end;
    protected byte[] uuid;
    
    protected ArrayList<BLEAttribute> attributes = new ArrayList<BLEAttribute>();
    
    public BLEService(byte[] uuid, int start, int end) {
        this.uuid = uuid;
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public byte[] getUuid() {
        return uuid;
    }

    public ArrayList<BLEAttribute> getAttributes() {
        return attributes;
    }
    
    public String getUuidString() {
        String result = "";
        for(int i = 0; i<uuid.length; i++) {
            result = String.format("%02X", uuid[i]) + result;
        }
        result = "0x" + result;
        return result;
    }
    
    public String toString() {
        return "BLEService " + getUuidString() + " (" + start + ".." + end + ")";
    }
    
    public String getDescription() {
        String result = toString();
        for (BLEAttribute a : attributes) {
            result += "\n\t" + a.toString();
        }
        return result;
    }
    
}
