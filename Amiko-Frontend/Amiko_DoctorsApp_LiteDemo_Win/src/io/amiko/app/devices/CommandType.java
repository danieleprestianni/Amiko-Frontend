package io.amiko.app.devices;
/**
 * 
 * @author Daniele Prestianni 
 */
public enum CommandType {
	PING_CMD (0x01),
	GET_INDEX_CMD (0x02),
	GET_RECORD_CMD (0x03),
	CLEAR_FS_CMD (0x04),
	GET_TIME_CMD (0x05),
	SET_TIME_CMD (0x06),
	GET_BATTERY_CMD (0x07),
	GET_SERIAL_CMD (0x08),
	SET_SERIAL_CMD (0x09);
	
	private int commandType;
	CommandType (int value){
		this.commandType = value;
	}
	
	public byte getValue(){
		return (byte)(this.commandType & 0xFF);
	}

}
