package io.amiko.app.devices;

import io.amiko.app.devices.bledriver.BLEDevice;

public class Utils {

	
	public static byte[] longToBytes(long l) {
	    byte[] result = new byte[8];
	    for (int i = 7; i >= 0; i--) {
	        result[i] = (byte)(l & 0xFF);
	        l >>= 8;
	    }
	    return result;
	}
	
	public static byte[] intToBytes(int l) {
	    byte[] result = new byte[4];
	    for (int i = 3; i >= 0; i--) {
	        result[i] = (byte)(l & 0xFF);
	        l >>= 8;
	    }
	    return result;
	}

	public static long bytesToLong(byte[] b) {
	    long result = 0;
	    for (int i = 0; i < 8; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}
	
	public static int bytesToInt(byte[] b) {
	    int result = 0;
	    byte[] bis = new byte[]{0x00,0x00,0x00,0x00};// zero filling
	    if(b.length<4){
	    	for(int i=(4-b.length);i<4;i++){
	    		bis[i] = b[i-(4-b.length)];
	    	}
	    }else{
	    	bis = b;
	    }
	    
	    for (int i = 0; i < 4; i++) {
	        result <<= 8;
	        result |= (bis[i] & 0xFF);
	    }
	    return result;
	}

	public static byte[] merge(byte[] b1, byte[] b2){
		byte[] resp = new byte[b1.length+b2.length];
		for(int i=0;i<b1.length;i++){
			resp[i] = b1[i];
		}
		for(int i=b1.length;i<resp.length;i++){
			resp[i] = b2[i-b1.length];
		}
		return resp;
	}
		
	public static byte[] subArray(byte[] source, int start, int end){
		byte[] resp = null;
		if(start>=0 && end<source.length){
			resp = new byte[end-start+1];
			for(int i=0;i<(end-start+1);i++){
				resp[i] = source[start+i];
			}
		}else{
			// invalid parameters			
		}
		return resp;
	}
		
	public static String bytesToString(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for(byte b : bytes) result.append( Integer.toHexString(b & 0xFF) + " ");
        return result.toString();        
    }
	
	/**
	 * Parse BLE device data advertising in order to extract all information.
	 * Each information will be saved into the specified device.
	 * @param device
	 * @param data
	 */
	public static void parseDeviceData(BLEDevice device, byte[] data){
		if(data!=null && data.length>0 && device!=null){
			int pos = 0;
			int length = data.length;
			
			while(pos<length){
				// get the LEN
				int len = data[pos];
				if(len==0)
					break;
				// get the type
				++pos;
				int type = data[pos];
				// get the value
				++pos;
				byte[] value = new byte[len-1];
				for(int i=0;i<(len-1);i++){
					value[i] = data[pos+i];
				}
				pos+=len-1;
				// save the info
				device.addDeviceInfo(type, value);
			}
		}
	}
	
	public static void main(String args[]){
		byte[] data = new byte[]{0x00,0x00, 0x00,0x00};
		System.out.println(Utils.bytesToInt(data));
		
		byte[] data2 = new byte[]{0x01,0x00,0x00};
		System.out.println(Utils.bytesToInt(data2));		
	}
	
}
