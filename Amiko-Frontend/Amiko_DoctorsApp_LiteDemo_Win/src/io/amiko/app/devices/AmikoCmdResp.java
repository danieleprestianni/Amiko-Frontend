package io.amiko.app.devices;

import org.apache.log4j.Logger;

/**
 * Amiko device response message
 * 
 * @author Admin
 *
 */
public class AmikoCmdResp {

	private static final Logger logger = Logger.getLogger(AmikoCmdResp.class);
	
	private byte[] data;// all response bytes array
	private ResponseType responseType;// response type
	private ResponseStatus responseStatus;// response status
	// could be an empty array if the response doesn't have a result
	private byte[] result;// response output result
	private static AmikoCmdResp instance;

	private AmikoCmdResp() {		
	}

	/**
	 * 
	 * @param data
	 *            If NULL or its length is less of 2 bytes, the parse method
	 *            will return NULL
	 * @return
	 */
	public static AmikoCmdResp parse(byte[] data) {
		// start data parsing
		if (data == null || data.length < 2) {
			return null;
		}

		instance = new AmikoCmdResp();
		instance.setData(data);

		// set the response type
		if (data[0] == ResponseType.CLEAR_FS_CMD_RESP.getValue()) {
			instance.setResponseType(ResponseType.CLEAR_FS_CMD_RESP);
		} else if (data[0] == ResponseType.GET_BATTERY_CMD_RESP.getValue()) {
			instance.setResponseType(ResponseType.GET_BATTERY_CMD_RESP);
		} else if (data[0] == ResponseType.GET_INDEX_CMD_RESP.getValue()) {
			instance.setResponseType(ResponseType.GET_INDEX_CMD_RESP);
		} else if (data[0] == ResponseType.GET_RECORD_CMD_RESP.getValue()) {
			instance.setResponseType(ResponseType.GET_RECORD_CMD_RESP);
		} else if (data[0] == ResponseType.GET_TIME_CMD_RESP.getValue()) {
			instance.setResponseType(ResponseType.GET_TIME_CMD_RESP);
		} else if (data[0] == ResponseType.PING_CMD_RESP.getValue()) {
			instance.setResponseType(ResponseType.PING_CMD_RESP);
		} else if (data[0] == ResponseType.SET_TIME_CMD_RESP.getValue()) {
			instance.setResponseType(ResponseType.SET_TIME_CMD_RESP);
		} else if (data[0] == ResponseType.SET_SERIAL_CMD_RESP.getValue()) {
			instance.setResponseType(ResponseType.SET_SERIAL_CMD_RESP);
		}else if (data[0] == ResponseType.GET_SERIAL_CMD_RESP.getValue()) {
			instance.setResponseType(ResponseType.GET_SERIAL_CMD_RESP);
		}else{
			instance.setResponseType(ResponseType.INVALID_CMD_RESP);
			logger.error("Invalid Response type value=" + (data[1]& 0xFF));
		}
		
		// set the response status
		if(data[1] == ResponseStatus.SUCCESS.getValue()){
			instance.setResponseStatus(ResponseStatus.SUCCESS);
		}else if(data[1] == ResponseStatus.PARAMETER_ERROR.getValue()){
			instance.setResponseStatus(ResponseStatus.PARAMETER_ERROR);
		}else if(data[1] == ResponseStatus.FS_ERROR.getValue()){
			instance.setResponseStatus(ResponseStatus.FS_ERROR);			
		}

		// set the result (all remain bytes)
		byte[] resultData = new byte[data.length-2];
		for(int i=2;i<data.length;i++){
			resultData[i-2] = data[i];
		}
		instance.setResult(resultData);
		
		return instance;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	public ResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(ResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	public byte[] getResult() {
		return result;
	}

	public void setResult(byte[] result) {
		this.result = result;
	}
	
	@Override
	public String toString(){
		String respStr = "";
		respStr+="Resp Type=" + this.responseType + " Resp status=" + this.responseStatus + " data=[";
		
		byte[] data = this.getResult();		
		for(byte b:data){
			respStr +=String.format("0x%02X", (b & 0xFF)) + "-";
		}		
		if(data.length>0){
			respStr = respStr.substring(0,respStr.length()-1) + "]";
		}else{
			respStr += "]";
		}
		return respStr;
	}

	public static void main(String args[]){
		// PING_CMD_RESP
		AmikoCmdResp resp = AmikoCmdResp.parse(new byte[]{(byte) 0x81, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11,0x12});
		System.out.println(resp);
				
		// GET_INDEX_CMD_RESP
		AmikoCmdResp resp2 = AmikoCmdResp.parse(new byte[]{(byte)0x82, 0x00, 0x00,0x01});
		System.out.println(resp2);
		
		// GET_RECORD_CMD_RESP
		AmikoCmdResp resp3 = AmikoCmdResp.parse(new byte[]{(byte)0x83, 0x01, 0x01, 0x03, 0x01, 0x02, 0x03, 0x04, 0x01, 0x01, 0x01, 0x02, 0x01, 0x02, 0x01, 0x02, 0x01, 0x02});
		System.out.println(resp3);
		
		// CLEAR_FS_CMD_RESP
		AmikoCmdResp resp4 = AmikoCmdResp.parse(new byte[]{(byte)0x84, 0x00});
		System.out.println(resp4);
		
		// GET_TIME_CMD_RESP
		AmikoCmdResp resp5 = AmikoCmdResp.parse(new byte[]{(byte)0x85, 0x00, 0x57, 0x17, (byte)0x82, 0x10});
		System.out.println(resp5);

		// SET_TIME_CMD_RESP
		AmikoCmdResp resp6 = AmikoCmdResp.parse(new byte[]{(byte)0x86, 0x00, 0x57, 0x17, (byte)0x82, 0x10});
		System.out.println(resp6);

		// GET_BATTERY_CMD_RESP
		AmikoCmdResp resp7 = AmikoCmdResp.parse(new byte[]{(byte)0x87, 0x00, (byte)0xFF});
		System.out.println(resp7);

	}
}
