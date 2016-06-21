package io.amiko.app.devices.bledriver;

import java.util.ArrayList;

import org.apache.log4j.Logger;


public class BLEDeviceList  {
    
	private static final Logger logger = Logger.getLogger(BLEDeviceList.class);
	
    protected ArrayList<BLEDevice> devices = new ArrayList<BLEDevice>();
    /*        
    public ArrayList<BLEDevice> getDevices() {
        return devices;
    }
    */
    
    public void clear() {
        int idx = devices.size() - 1;
        if (idx < 0) return;
        devices.clear();
    }
    
    public void add(BLEDevice d) {
        devices.add(d);
    }
    
    public void changed(BLEDevice d) {
        if (devices.isEmpty()) return;
    }
    
    public BLEDevice getFromAddress(String address) {
        for (BLEDevice d : devices) {
            if (d.address.equals(address)) return d;
        }
        return null;
    }
    
    public BLEDevice removeFromAddress(String address) {
        for (BLEDevice d : devices) {
            if (d.address.equals(address)){
            	this.devices.remove(d);
            }
        }
        return null;
    }
    
    public BLEDevice getFromName(String name) {
        for (BLEDevice d : devices) {
            if (d.name.equals(name)) return d;
        }
        return null;
    }

    public int getSize() {
        return devices.size();
    }

    public Object getElementAt(int index) {
        return devices.get(index);
    }
    
    public String[] getList(){   
    	String[] listStr = new String[this.devices.size()];
    	for(int i=0;i<this.devices.size();i++){
    		listStr[i] = this.devices.get(i).name;
    	}
    	return listStr;
    }
    
    /**
     * 
     * @param thershold
     * @return true if list is changed, otherwise returns false
     */
    public boolean removeExpiredDevices(long thershold){
    	for(int i=0;i<this.devices.size();i++){
    		if(this.devices.get(i).hasExpiry() && this.devices.get(i).isExpired(thershold)){
    			logger.debug("[BLE-DISCOVERING]-Device " + this.devices.get(i).name + " has expired");
    			this.devices.remove(i);
    			return true;
    		}
    	}
    	return false;
    }
    
}
