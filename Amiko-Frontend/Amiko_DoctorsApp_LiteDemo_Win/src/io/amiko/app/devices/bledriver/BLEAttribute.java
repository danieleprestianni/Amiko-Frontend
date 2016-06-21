package io.amiko.app.devices.bledriver;

public class BLEAttribute {
    protected byte[] uuid;
    protected int handle;
    
    public BLEAttribute(byte[] uuid, int handle) {
        this.uuid = uuid;
        this.handle = handle;
    }
    
    public byte[] getUuid() {
        return uuid;
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
        return "ATT " + getUuidString() + " => 0x" + Integer.toHexString(handle).toUpperCase();
    }
    
}
