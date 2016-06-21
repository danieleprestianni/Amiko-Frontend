package io.amiko.app.devices.bledriver;

import io.amiko.app.devices.AmikoCmd;

public interface BLEDriver {
	/**
	 * Establish a connection with the Bluetooth BLE stack layer 
	 * @param listener 
	 * @param debug TRUE enable the logging for the communication layer
	 * @throws BLEDriverConnectionException
	 */
	public void connect(BLEDriverListener listener, boolean debug) throws BLEDriverConnectionException;
	
	/**
	 * Disconnect from the BLE stack
	 */
	public void disconnect();
	
	/**
	 * Starts the BLE devices discovering
	 */
	public void startBLEDevicesDiscovering();
	
	/**
	 * Stops the BLE device discovering
	 */
	public void stopBLEDevicesDiscovering();
	
	/**
	 * Start a connection with the specified BLE device
	 * @param device
	 */
	public void connectBLEDevice(BLEDevice device);
	
	/**
	 * Start a connection with the specified BLE device
	 * @param device
	 */
	public void disconnectBLEDevice(BLEDevice device);
	
	public void sendCmd(AmikoCmd command);
	
	/**
	 * Clean the commands queue
	 */
	public void clean();
	
	/**
	 * Clean all devices lists
	 */
	public void cleanDevicesLists();
}
