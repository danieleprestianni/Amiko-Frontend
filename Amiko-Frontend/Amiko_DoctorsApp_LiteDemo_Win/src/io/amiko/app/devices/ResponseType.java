package io.amiko.app.devices;

public enum ResponseType {

	INVALID_CMD_RESP (0X80),
	PING_CMD_RESP (0x81),
	GET_INDEX_CMD_RESP (0x82),
	GET_RECORD_CMD_RESP (0x83),
	CLEAR_FS_CMD_RESP (0x84),
	GET_TIME_CMD_RESP (0x85),
	SET_TIME_CMD_RESP (0x86),
	GET_BATTERY_CMD_RESP (0x87),
	GET_SERIAL_CMD_RESP (0x88),
	SET_SERIAL_CMD_RESP (0x89);
	
	private int responseType;
	
	ResponseType (int value){
		this.responseType = value;
	}
	
	public byte getValue(){
		return (byte)(this.responseType & 0xFF);
	}
	
	public String toString(){
		switch (this.responseType) {		
		case 0x81:
			return "PING_CMD_RESP";
		case 0x82:
			return "GET_INDEX_CMD_RESP";
		case 0x83:
			return "GET_RECORD_CMD_RESP";
		case 0x84:
			return "CLEAR_FS_CMD_RESP";
		case 0x85:
			return "GET_TIME_CMD_RESP";
		case 0x86:
			return "SET_TIME_CMD_RESP";
		case 0x87:
			return "GET_BATTERY_CMD_RESP";
		}
		return "";
	}
		
}
