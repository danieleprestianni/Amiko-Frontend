package io.amiko.app.devices;

/**
 * 
 * @author Daniele Prestianni
 *
 */
public enum DeviceMode {
	MODE_TEXT (0x00),
	MODE_CMD (0xAA);
	
	private int deviceMode;
	DeviceMode(int mode){
		this.deviceMode = mode;
	}
	
	public byte getValue(){
		return (byte)(this.deviceMode & 0xFF);
	}
	
	public static void main(String args[]){
		System.out.println(DeviceMode.MODE_CMD.getValue());
	}
}

