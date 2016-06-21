package io.amiko.app.devices.bledriver;

import io.amiko.app.devices.AmikoCmdResp;

public interface BLEDriverListener {

	public void onConnected();
	public void onNotConnected(String message);
	public void onDiscoveredBLEDevices(BLEDeviceList deviceList);
	public void onBLEDeviceConnected(BLEDevice device);
	public void onBLEDeviceDisconnected(String cause);
	public void onResponse(AmikoCmdResp response);
	public void onDongleError(String cause);
}
