package io.amiko.app.doctorsapp.demo;

import io.amiko.app.devices.AmikoCmd;
import io.amiko.app.devices.AmikoCmdFactory;
import io.amiko.app.devices.AmikoCmdResp;
import io.amiko.app.devices.bledriver.BLEDevice;
import io.amiko.app.devices.bledriver.BLEDeviceList;
import io.amiko.app.devices.bledriver.BLEDriverConnectionException;
import io.amiko.app.devices.bledriver.BLEDriverListener;
import io.amiko.app.devices.bledriver.bluegiga.BluegigaBLEDriver;

public class DoctorsAppDemo {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		AmikoCmd cmd = AmikoCmdFactory.makeGetRecordCmd(255);
		System.out.println(cmd);

		AmikoCmd cmd2 = AmikoCmdFactory.makeGetRecordCmd(65535);
		System.out.println(cmd2);
		
		AmikoCmd cmd3 = AmikoCmdFactory.makeSetTimeCmd(1461158416343L);
		System.out.println(cmd3);
				
		BluegigaBLEDriver driver = new BluegigaBLEDriver();
		try {
			driver.connect(new BLEDriverListener() {
				
				BLEDevice currentDevice = null;
				
				@Override
				public void onNotConnected(String message) {
					System.out.println("NOT CONNECTED - cause:" + message);					
				}
				
				@Override
				public void onDiscoveredBLEDevices(BLEDeviceList deviceList) {
					System.out.println("DISCOVERED DEVICES");
					BLEDevice device = deviceList.getFromName("Amiko_Tracker_Ellipta_5");//("Nordic_UART");//("Amiko_Tracker_Ellipta_5");
					if(device!=null){
						//NOTE: se avio la connessione con un BLE device, bloco il discovering
						// che devo far ripartire in caso di disconnessione
						driver.connectBLEDevice(device);
					}
				}
				
				@Override
				public void onConnected() {
					System.out.println("CONNECTED");
					driver.startBLEDevicesDiscovering();
				}
				
				@Override
				public void onBLEDeviceConnected(BLEDevice device) {
					if(device!=null){
						System.out.println("BLE DEVICE CONNECTED: " + device.getName());
						currentDevice = device;
						
						//TODO inserire i comandi per interagire						
						//OK AmikoCmd cmd = AmikoCmdFactory.makeGetTimeCmd();
						//OK AmikoCmd cmd = AmikoCmdFactory.makeGetBatteryCmd();
						//OK AmikoCmd cmd = AmikoCmdFactory.makeGetIndexCmd();
						AmikoCmd cmd = AmikoCmdFactory.makeGetRecordCmd(1);
						driver.sendCmd(cmd);							
						
						/*AmikoCmd cmd = AmikoCmdFactory.makeSetTimeCmd(new Date().getTime());
						driver.sendCmd(cmd);							
						AmikoCmd cmd2 = AmikoCmdFactory.makeGetTimeCmd();
						driver.sendCmd(cmd2);											
						AmikoCmd cmd3 = AmikoCmdFactory.makeSetTimeCmd(new Date().getTime());
						driver.sendCmd(cmd3);							
						AmikoCmd cmd4 = AmikoCmdFactory.makeGetTimeCmd();
						driver.sendCmd(cmd4);											
						AmikoCmd cmd5 = AmikoCmdFactory.makeSetTimeCmd(new Date().getTime());
						driver.sendCmd(cmd5);							
						AmikoCmd cmd6 = AmikoCmdFactory.makeGetTimeCmd();
						driver.sendCmd(cmd6);											
						AmikoCmd cmd7 = AmikoCmdFactory.makeSetTimeCmd(new Date().getTime());
						driver.sendCmd(cmd7);							
						AmikoCmd cmd8 = AmikoCmdFactory.makeGetTimeCmd();
						driver.sendCmd(cmd8);											
						AmikoCmd cmd9 = AmikoCmdFactory.makeSetTimeCmd(new Date().getTime());
						driver.sendCmd(cmd9);							
						AmikoCmd cmd10 = AmikoCmdFactory.makeGetTimeCmd();
						driver.sendCmd(cmd10);	*/										
					}
				}
				
				@Override
				public void onBLEDeviceDisconnected(String cause) {
					System.out.println("BLE DEVICE DISCONNECTED - cause:" + cause);
					// start auto discovering
					driver.startBLEDevicesDiscovering();
				}
				
				@Override
				public void onResponse(AmikoCmdResp response){
					System.out.println(response.toString());
				}

				@Override
				public void onDongleError(String cause) {
					// TODO Auto-generated method stub
					
				}
				
			}, false);
		} catch (BLEDriverConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
