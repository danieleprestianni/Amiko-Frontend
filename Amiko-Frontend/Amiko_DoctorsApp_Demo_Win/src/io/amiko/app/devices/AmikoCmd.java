package io.amiko.app.devices;

/**
 * Reppresents a generic Amiko device command 
 * 
 * Protocol v.??:
 * 1st byte: DEVICE MODE (1 byte = 0xAA)
 * 2nd byte: CMD TYPE (1 byte)
 * 
 * @author Daniele Prestianni 
 *
 */
public class AmikoCmd {

	private DeviceMode deviceMode = DeviceMode.MODE_CMD;//defalt value
	private CommandType commandType;
	private byte[] parameters;
	
	public AmikoCmd(CommandType type){
		this.commandType = type;
	}
	
	public AmikoCmd(DeviceMode mode, CommandType type){
		this.deviceMode = mode;
		this.commandType = type;
	}
	
	public byte[] getBytes(){
		byte[] header = new byte[]{deviceMode.getValue(),commandType.getValue()};
		if(this.parameters==null){
			return header;
		}else{
			return Utils.merge(header, this.parameters);
		}		
	}
	
	public void setParameters(byte[] data){
		this.parameters = data;
	}
	
	@Override
	public String toString(){
		byte[] data = this.getBytes();
		String respStr = "";
		if(CommandType.CLEAR_FS_CMD.getValue()==data[1]){
			respStr +="type=CLEAR_FS_CMD data:";
		}else if(CommandType.GET_BATTERY_CMD.getValue()==data[1]){
			respStr +="type=GET_BATTERY_CMD data:";
		}else if(CommandType.GET_INDEX_CMD.getValue()==data[1]){
			respStr +="type=GET_INDEX_CMD data:";
		}else if(CommandType.GET_RECORD_CMD.getValue()==data[1]){
			respStr +="type=GET_RECORD_CMD data:";
		}else if(CommandType.GET_SERIAL_CMD.getValue()==data[1]){
			respStr +="type=GET_SERIAL_CMD data:";
		}else if(CommandType.GET_TIME_CMD.getValue()==data[1]){
			respStr +="type=GET_TIME_CMD data:";
		}else if(CommandType.PING_CMD.getValue()==data[1]){
			respStr +="type=GET_TIME_CMD data:";
		}else if(CommandType.SET_SERIAL_CMD.getValue()==data[1]){
			respStr +="type=SET_SERIAL_CMD data:";
		}else if(CommandType.SET_TIME_CMD.getValue()==data[1]){
			respStr +="type=SET_TIME_CMD data:";
		}
		
		
		for(byte b:data){
			respStr +=String.format("0x%02X", (b & 0xFF)) + "-";
		}		
		return respStr.substring(0,respStr.length()-1);
	}
	
	public String getType(){
		byte[] data = this.getBytes();
		String respStr = "";
		if(CommandType.CLEAR_FS_CMD.getValue()==data[1]){
			respStr +="type=CLEAR_FS_CMD data:";
		}else if(CommandType.GET_BATTERY_CMD.getValue()==data[1]){
			respStr +="type=GET_BATTERY_CMD data:";
		}else if(CommandType.GET_INDEX_CMD.getValue()==data[1]){
			respStr +="type=GET_INDEX_CMD data:";
		}else if(CommandType.GET_RECORD_CMD.getValue()==data[1]){
			respStr +="type=GET_RECORD_CMD data:";
		}else if(CommandType.GET_SERIAL_CMD.getValue()==data[1]){
			respStr +="type=GET_SERIAL_CMD data:";
		}else if(CommandType.GET_TIME_CMD.getValue()==data[1]){
			respStr +="type=GET_TIME_CMD data:";
		}else if(CommandType.PING_CMD.getValue()==data[1]){
			respStr +="type=GET_TIME_CMD data:";
		}else if(CommandType.SET_SERIAL_CMD.getValue()==data[1]){
			respStr +="type=SET_SERIAL_CMD data:";
		}else if(CommandType.SET_TIME_CMD.getValue()==data[1]){
			respStr +="type=SET_TIME_CMD data:";
		}
				
		return respStr.substring(0,respStr.length()-1);
	}
	
	public static void main(String args[]){
		AmikoCmd cmd = new AmikoCmd(DeviceMode.MODE_CMD,CommandType.CLEAR_FS_CMD);
		byte[] data = cmd.getBytes();
		for(byte b:data){
			System.out.println(b & 0xFF);
		}
		System.out.println(cmd);
	}
	
}
