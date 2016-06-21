package io.amiko.app.devices;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

/**
 * Factory class used to generate Amiko commands
 * @author Daniele Prestianni
 *
 */
public class AmikoCmdFactory {

	private static Logger logger = Logger.getLogger(AmikoCmdFactory.class);
	
	public static AmikoCmd makePingCmd(){
		return new AmikoCmd(CommandType.PING_CMD);
	} 
	
	public static AmikoCmd makeGetIndexCmd(){
		return new AmikoCmd(CommandType.GET_INDEX_CMD);
	}
	
	/**
	 * 
	 * @param index 2 bytes value
	 * @return
	 */
	public static AmikoCmd makeGetRecordCmd(int index){
		AmikoCmd cmd = new AmikoCmd(CommandType.GET_RECORD_CMD);		
		// convert int into 4 bytes
		byte[] data = Utils.intToBytes(index);	
		cmd.setParameters(new byte[]{data[3],data[2]});
		return cmd;
	}
	
	public static AmikoCmd makeClearFsCmd(){
		return new AmikoCmd(CommandType.CLEAR_FS_CMD);		
	}
	
	public static AmikoCmd makeGetTimeCmd(){
		return new AmikoCmd(CommandType.GET_TIME_CMD);
	}
	
	/**
	 * 
	 * @param timestamp timespamp from epoc date, milliseconds
	 * @return
	 */
	public static AmikoCmd makeSetTimeCmd(long timestamp){
		// from msec to sec
		long normalTimestamp = timestamp/1000;
		byte[] ntb = Utils.longToBytes(normalTimestamp);
		// epoch_time (4 bytes)		
		AmikoCmd cmd = new AmikoCmd(CommandType.SET_TIME_CMD);	
		cmd.setParameters(new byte[]{ntb[7],ntb[6],ntb[5],ntb[4]});
		return cmd;
	}
	
	public static AmikoCmd makeGetBatteryCmd(){
		return new AmikoCmd(CommandType.GET_BATTERY_CMD);
	}
	
	public static AmikoCmd makeSetSerialCmd(String serialCode){
		AmikoCmd cmd = new AmikoCmd(CommandType.SET_SERIAL_CMD);
		byte[] serialCodeByteArray;
		try {
			serialCodeByteArray = serialCode.getBytes("US-ASCII");
			cmd.setParameters(serialCodeByteArray);
			return cmd;
		} catch (UnsupportedEncodingException e) {	
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static AmikoCmd makeGetSerialCodeCmd(){
		return new AmikoCmd(CommandType.GET_SERIAL_CMD);
	}
}
