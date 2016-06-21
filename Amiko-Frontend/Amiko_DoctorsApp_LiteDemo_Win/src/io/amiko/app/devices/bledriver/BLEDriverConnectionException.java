package io.amiko.app.devices.bledriver;


public class BLEDriverConnectionException extends Exception {

	public BLEDriverConnectionException(String message){
		super(message);
	}
	
	public BLEDriverConnectionException(Throwable cause){
		super(cause);
	}
	
	public BLEDriverConnectionException(String message, Throwable cause){
		super(message, cause);
	}
	

}
