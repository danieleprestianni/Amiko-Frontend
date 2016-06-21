package io.amiko.app.devices.bledriver;

import java.util.Date;
import java.util.Hashtable;

public class BLEDevice {
    
    protected String address;
    protected String name;
    protected int rssi;
    protected long timestamp;
    protected boolean hasExpiry = true;
    
    protected Hashtable<Integer,byte[]> info = new Hashtable<Integer,byte[]>();    
    protected Hashtable<String, BLEService> services = new Hashtable<String, BLEService>();

    public Hashtable<String, BLEService> getServices() {
        return services;
    }
    
    public String getGATTDescription() {
        String result = toString();
        for (BLEService s : services.values()) {
            result += "\n" + s.getDescription();
        }
        return result;
    }
    
    public BLEDevice(String address) {
        this.address = address;
        name = "";
        Date d = new Date();
        this.timestamp = d.getTime();
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public int getRssi() {
        return rssi;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
    
    public boolean isExpired(long thershold){
    	Date d = new Date();
    	return (d.getTime()-this.timestamp>thershold);    	
    }
    
    public void updateTimestamp(){
    	Date d = new Date();
    	this.timestamp = d.getTime();
    }
    
    public String toString() {
        return name + " [" + address + "] (" + rssi + " dBm)";
    }
    
    public String bytesToString(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        result.append("[ ");
        for(byte b : bytes) result.append( Integer.toHexString(b & 0xFF) + " ");
        result.append("]");
        return result.toString();        
    }
    
    public void addDeviceInfo(Integer key, byte[] value){
    	this.info.put(key, value);    	
    }
    
    public byte[] getDeviceInfo(Integer key){
    	return this.info.get(key);
    }
    
    public void enableExpiry(boolean value){
    	this.hasExpiry = value;
    }
    
    public boolean hasExpiry(){
    	return this.hasExpiry;
    }
    
}
