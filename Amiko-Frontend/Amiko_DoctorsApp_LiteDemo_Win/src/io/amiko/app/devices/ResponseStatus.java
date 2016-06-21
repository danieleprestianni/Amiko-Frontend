package io.amiko.app.devices;

public enum ResponseStatus {

	SUCCESS (0x00),
	PARAMETER_ERROR (0x01),
	FS_ERROR (0x02);
	
	private int responseStatus;
	
	ResponseStatus (int value){
		this.responseStatus = value;
	}
	
	public byte getValue(){
		return (byte)(this.responseStatus & 0xFF);
	}
	
	public String toString(){
		switch (this.responseStatus) {		
		case 0x00:
			return "SUCCESS";
		case 0x01:
			return "PARAMETER_ERROR";
		case 0x02:
			return "FS_ERROR";	
		}
		return "";
	}
}
