package io.amiko.app.doctorsapp.demo;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

// TODO trasferire su MainWindow cosi da non avere questa dipendenza
import org.eclipse.swt.widgets.Display;

import com.ibm.icu.text.SimpleDateFormat;

import io.amiko.app.dataanalysis.IntakeRule1;
import io.amiko.app.devices.AmikoCmd;
import io.amiko.app.devices.AmikoCmdFactory;
import io.amiko.app.devices.AmikoCmdResp;
import io.amiko.app.devices.ResponseType;
import io.amiko.app.devices.Utils;
import io.amiko.app.devices.bledriver.BLEDevice;
import io.amiko.app.devices.bledriver.BLEDeviceList;
import io.amiko.app.devices.bledriver.BLEDriver;
import io.amiko.app.devices.bledriver.BLEDriverConnectionException;
import io.amiko.app.devices.bledriver.BLEDriverListener;
import io.amiko.app.devices.bledriver.bluegiga.BluegigaBLEDriver;

public class MainController implements BLEDriverListener {

	private static final Logger logger = Logger.getLogger(MainController.class);

	// BLE driver used in order to interact with the BLE stack and Amiko devices
	private BLEDriver driver;
	// BLE devices list
	private BLEDeviceList deviceList = new BLEDeviceList();
	// selected (current) BLE device
	private BLEDevice currentDevice = null;
	// used for BLE auto-reconnection
	private BLEDevice lastCurrentDevice = null;
	// used for battery status polling
	private Timer batteryCheckerTimer = new Timer("Battery Checker Timer");
	// used for device time polling
	private Timer timerCheckerTimer = new Timer("Time Checker Timer");
	// 0x00 - records number acquisition
	// 0x01 - we know the number of records, now we start to query for each one
	// 0x02 - we have sent all request for each record
	// 0x03 - all records have been acquired
	private int eventAcquisitionStatus = 0x00;
	// range between the [total records stored into the Amiko flash memory,0]
	private int recordsNumber = -1;
	// total records stored into the Amiko flash memory
	private int totalRecordsNumber = -1;
	private int globalIndex = 1;
	// key=index value=RecordModel pojo
	private Hashtable<Integer, RecordModel> tableData = new Hashtable<Integer, RecordModel>();
	// STATUS
	private boolean driverConnected = false;
	private boolean retryConnection = false;
	// TRUE if auto-reconnect is enabled
	private boolean autoReconnect = true;
	private boolean connectionIsEnabled = true;
	// TRUE if we have to recovery the records download
	private boolean startDownloadRecovery = false;
	private String deviceName;
	private MainWindow mainWindow = null;
	private int retryInterval = 2000;
	private boolean devModeIsEnabled = false;
	// Regola utilizzata per il riconoscimento di INTAKE "deboli" (LOW)
	private IntakeRule1 intakeRule1 = new IntakeRule1();
	private boolean intakesAcquired = false;
	private RecordModel lastIntake = null;
	private int totalNumber = 0;
	private int totalIntakes = 0;

	public MainController() {
		this.driver = new BluegigaBLEDriver();

		String retryIntervalStr = ConfigLoader.getInstance().getProperty("maincontroller.retry.interval");
		if (retryIntervalStr != null) {
			try {
				this.retryInterval = Integer.parseInt(retryIntervalStr);
			} catch (NumberFormatException e) {
				logger.error("config.properties: maincontroller.retry.interval is non valid: " + retryIntervalStr);
			}
		}
	}

	/**
	 * Setter/Getter
	 */
	public void setMainWindow(MainWindow win) {
		this.mainWindow = win;
	}

	public void start() {
		logger.info("AMIKO Doctor's App STARTED");

		// start connection with the BLE stack
		try {
			logger.debug("[CONNECTION]-Start the connection with the BLE stack");
			this.driver.connect(this, false);
		} catch (BLEDriverConnectionException e) {
			logger.error(e.getMessage(), e);
		}

	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		this.driverConnected = true;
		this.retryConnection = true;
		this.mainWindow.onConnected();
		logger.debug("[CONNECTION]-Connected with the BLE stack");
		logger.debug("[CONNECTION]-COMPLETED");

		logger.debug("[BLE-DISCOVERING]-Start Amiko devices discovering ...");
		driver.startBLEDevicesDiscovering();
	}

	@Override
	public void onNotConnected(String message) {
		logger.error("[CONNECTION]-Doctor's App is NOT CONNECTED - cause:" + message);

		this.driverConnected = false;
		this.mainWindow.onNotConnected(message);

		// retry the connection
		logger.info("Retry the connection with the BLE stack");
		Timer retryConnectionTimer = new Timer("retryConnectionTimer");
		retryConnectionTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				MainController.this.retryConnection = true;
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						MainController.this.retryConnection();
					}
				});
			}
		}, retryInterval);
	}

	@Override
	public void onDiscoveredBLEDevices(BLEDeviceList deviceList) {
		if (deviceList == null || deviceList.getSize() == 0) {
			logger.debug("[BLE-DISCOVERING]-BLE devices are not available");
			logger.debug("[BLE-DISCOVERING]-COMPLETED");

			// no Amiko devices available
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MainController.this.mainWindow.onDiscoveredBLEDevices(deviceList);
					MainController.this.mainWindow.enableWidgets(false);
				}
			});

		} else if (deviceList.getSize() > 0) {
			logger.debug("[BLE-DISCOVERING]-BLE devices are available");
			logger.debug("[BLE-DISCOVERING]-COMPLETED");

			synchronized (this.deviceList) {
				this.deviceList = deviceList;
				for (String item : this.deviceList.getList()) {
					logger.debug("[BLE-DISCOVERING]-BLE discovered device: " + item);
				}
			}

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MainController.this.mainWindow.onDiscoveredBLEDevices(deviceList);

					// auto reconnect logic
					if (MainController.this.autoReconnect && MainController.this.lastCurrentDevice != null) {
						// auto reconnect
						logger.debug("BLE device auto-reconnect is enabled");
						for (int i = 0; i < MainController.this.deviceList.getSize(); i++) {
							if (MainController.this.mainWindow.amikoDevicesCmb.getItem(i)
									.equalsIgnoreCase(MainController.this.lastCurrentDevice.getName())) {
								// found the last used device
								MainController.this.mainWindow.amikoDevicesCmb.setSelection(i);
								// start the auto-reconnection
								MainController.this.currentDevice = (BLEDevice) MainController.this.deviceList
										.getElementAt(i);
								driver.connectBLEDevice(MainController.this.currentDevice);
								logger.debug("BLE device auto-reconnect with "
										+ MainController.this.lastCurrentDevice.getName());

							}
						}
					}
				}
			});
		}
	}

	@Override
	public void onBLEDeviceConnected(BLEDevice device) {
		if (device != null && this.currentDevice != null) {
			String msg = "Device " + this.currentDevice.toString() + " is connected";
			logger.debug("[BLEDEVICE-CONNECTION]-" + msg);

			// TODO LITE version this.startBatteryCheckTimer();
			// TODO LITE versionthis.startTimerCheckTimer();
			// TODO LITE force the Refresh task
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					onEventRefreshBtnClicked();
				}
			});

			// get the Amiko device Serial Code
			/*
			 * TODO LITE version if (this.currentDevice != null) { logger.debug(
			 * "[SEND-REQUEST]-Send command: GET_SERIAL_CODE"); AmikoCmd cmd =
			 * AmikoCmdFactory.makeGetSerialCodeCmd(); this.driver.sendCmd(cmd);
			 * }
			 */

			// check if we have to continue the records downloading
			if (this.eventAcquisitionStatus == 0x02 && this.autoReconnect) {
				// continue the records downloading
				this.recoveryRecordsDownloading();
			}

			this.connectionIsEnabled = false;
			this.intakesAcquired = false;
			this.lastIntake = null;
			++this.totalNumber;

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MainController.this.mainWindow.onBLEDeviceConnected(msg);
					MainController.this.mainWindow.enableWidgets(true);
					MainController.this.mainWindow.setResult(0);
					MainController.this.mainWindow.setPerformanceLevel(0);
					MainController.this.mainWindow.enableCleanListButton(false);
					// check if download recovery is enabled
					if (MainController.this.startDownloadRecovery) {
						// continue the records downloading
						MainController.this.mainWindow.enableTableButtons(false);
					}
				}
			});
			currentDevice = device;
		}
	}

	@Override
	public void onBLEDeviceDisconnected(String cause) {
		logger.debug("[BLEDEVICE-DISCONNECTED]-Device disconnected cause:" + cause);
		this.lastCurrentDevice = this.currentDevice;
		this.currentDevice = null;

		// clean the driver status
		this.driver.clean();

		// clean all scheduled task
		this.timerCheckerTimer.cancel();
		this.timerCheckerTimer.purge();
		this.timerCheckerTimer = null;
		this.timerCheckerTimer = new Timer("Time Checker Timer");

		// clean all scheduled task
		this.batteryCheckerTimer.cancel();
		this.batteryCheckerTimer.purge();
		this.batteryCheckerTimer = null;
		this.batteryCheckerTimer = new Timer("Battery Checker Timer");

		this.connectionIsEnabled = true;

		// update the UI
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MainController.this.mainWindow.onBLEDeviceDisconnected();
				MainController.this.mainWindow.enableWidgets(false);
				MainController.this.mainWindow.enableCleanListButton(true);
			}
		});

		logger.debug((this.lastCurrentDevice != null) ? this.lastCurrentDevice.getName() + " is disconnected" : "");
		logger.debug("Start Amiko devices discovering");

		// start the BLE devices discovering
		driver.startBLEDevicesDiscovering();
	}

	@Override
	public void onResponse(AmikoCmdResp response) {

		if (response == null) {
			return;
		}

		if (response.getResponseType() == ResponseType.GET_BATTERY_CMD_RESP) {
			logger.debug("[RESPONSE]-Amiko Response data: " + response.toString());

			// read the battery charged level
			int batteryLevel = (int) response.getResult()[0];
			// A = (batteryLevel+730)*3.5/1000 = tensione in mV variabile tra
			// 0..3
			// B = (A*100/3) = valore compreso 0 e 100%
			int _batteryLevel = (int) (((batteryLevel + 730) * 3.5 * 100) / (3 * 1000));

		} else if (response.getResponseType() == ResponseType.GET_TIME_CMD_RESP) {
			logger.debug("[RESPONSE]-Amiko Response data: " + response.toString());

			// device timing
			byte[] result = Utils.subArray(response.getResult(), 0, 3);
			byte[] resultWithZeroFilling = new byte[] { 0x00, 0x00, 0x00, 0x00, result[3], result[2], result[1],
					result[0] };
			long timestampEpoc = Utils.bytesToLong(resultWithZeroFilling);
			timestampEpoc *= 1000;
			Date d = new Date();
			d.setTime(timestampEpoc);
			SimpleDateFormat formatter = new SimpleDateFormat();

		} else if (response.getResponseType() == ResponseType.CLEAR_FS_CMD_RESP) {
			// flash cleaning response
			logger.debug("[RESPONSE]-Amiko Response data: " + response.toString());

			// TODO LITE: cosa fare quando ho terminato di fare clan della
			// flash?
		} else if (this.startDownloadRecovery && response.getResponseType() == ResponseType.GET_INDEX_CMD_RESP) {
			logger.debug("[RECOVERY-RECS-DOWNLOAD]-Amiko Response data: " + response.toString());

			// get index response
			this.eventAcquisitionStatus = 0x01;
			byte[] result = response.getResult();
			byte[] resultWithZeroFilling = new byte[] { 0x00, 0x00, result[1], result[0] };
			int totalRecordsNumber = Utils.bytesToInt(resultWithZeroFilling);

			if (totalRecordsNumber >= this.totalRecordsNumber) {
				// flash memory contains the same or more records respect the
				// previous session
				int delta = (totalRecordsNumber - this.totalRecordsNumber);
				this.recordsNumber += delta;
				logger.debug(
						"[RECOVERY-RECS-DOWNLOAD]-Device contains the same or more records respect the previous session DELTA="
								+ delta + " TOTAL=" + totalRecordsNumber);
				this.totalRecordsNumber = totalRecordsNumber;

				// disabilito i widgets necessari
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						MainController.this.mainWindow.enableTableButtons(false);
					}
				});

				// ricavo l'ultimo record recuperato
				int acquiredRecords = this.tableData.size();
				this.driver.clean();

				// make a GET-RECORD request for each available records
				for (int i = (acquiredRecords); i < this.totalRecordsNumber; i++) {
					AmikoCmd cmd = AmikoCmdFactory.makeGetRecordCmd(i);
					this.driver.sendCmd(cmd);
				}

				this.eventAcquisitionStatus = 0x02;
			} else {
				// flash meory contains less records - we have to interrupt the
				// download auto recovery
				logger.debug("[RECOVERY-RECS-DOWNLOAD]-Download recovery will be stopped");
			}

		} else if (response.getResponseType() == ResponseType.GET_INDEX_CMD_RESP) {
			logger.debug("[RESPONSE]-Amiko Response data: " + response.toString());

			// get index response
			this.eventAcquisitionStatus = 0x01;
			byte[] result = response.getResult();
			byte[] resultWithZeroFilling = new byte[] { 0x00, 0x00, result[1], result[0] };
			recordsNumber = Utils.bytesToInt(resultWithZeroFilling);
			totalRecordsNumber = recordsNumber;
			this.intakesAcquired = false;

			logger.debug("[RESPONSE]-Received total records: " + totalRecordsNumber);

			if (recordsNumber == 0) {

				// udpate the UI
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						MainController.this.mainWindow.enableTableButtons(true);
					}
				});

			}

			// make a GET-RECORD request for each available records
			for (int i = 0; i < recordsNumber; i++) {
				AmikoCmd cmd = AmikoCmdFactory.makeGetRecordCmd(i);
				this.driver.sendCmd(cmd);
			}

			this.eventAcquisitionStatus = 0x02;

		} else if (response.getResponseType() == ResponseType.GET_RECORD_CMD_RESP) {
			logger.debug("[RESPONSE]-Amiko Response data: " + response.toString());

			// get the record value response
			if (this.recordsNumber > 0) {
				--this.recordsNumber;
				byte[] result = response.getResult();
				RecordModel rec = new RecordModel();

				// get the record index
				byte[] recordIndexValue = new byte[] { 0x00, 0x00, result[1], result[0] };
				int indexValue = Utils.bytesToInt(recordIndexValue);
				// TODO LITE
				++globalIndex;
				// TODO LITE rec.setIndex(indexValue);
				rec.setIndex(globalIndex);

				// get the timestamp
				byte[] timeStamiValue = new byte[] { 0x00, 0x00, 0x00, 0x00, result[5], result[4], result[3],
						result[2] };
				rec.setTimestamp(Utils.bytesToLong(timeStamiValue));

				// get event type
				byte[] eventTypeValue = new byte[] { result[6] };
				rec.setEventId((eventTypeValue[0] & 0xFF));

				switch (result[6]) {
				case 0x00:
					// EVENT_GNR_IDLE
					break;
				case 0x10:
					// EVENT_SYS_INIT
					rec.setData0(Utils.bytesToInt(new byte[] { result[8], result[7] }));// ps_tau_min
					rec.setData1(Utils.bytesToInt(new byte[] { result[10], result[9] }));// ps_tau_max
					break;
				case 0x11:
					// EVENT_SYS_WAKE_UP
					rec.setData0(Utils.bytesToInt(new byte[] { result[8], result[7] }));// ps
					break;
				case 0x12:
					// EVENT_SYS_SLEEP
					rec.setData0(Utils.bytesToInt(new byte[] { result[8], result[7] }));// wrk_time
					rec.setData1(Utils.bytesToInt(new byte[] { result[9] }));// batt_chrg
					rec.setData2(Utils.bytesToInt(new byte[] { result[11], result[10] }));// ps
					break;
				case 0x13:
					// EVENT_SYS_OPEN
					rec.setData0(Utils.bytesToInt(new byte[] { result[8], result[7] }));// wrk_time
					break;
				case 0x14:
					// EVENT_SYS_CLOSE
					rec.setData0(Utils.bytesToInt(new byte[] { result[8], result[7] }));// wrk_time
					break;
				case 0x15:
					// EVENT_SYS_INSERT
					break;
				case 0x16:
					// EVENT_SYS_REMOVE
					break;
				case 0x17:
					// EVENT_SYS_CONNECT
					rec.setData0(Utils.bytesToInt(new byte[] { result[8], result[7] }));// wrk_time
					break;
				case 0x70:
					// EVENT_PRO_VIBRATION
					rec.setData0(Utils.bytesToInt(new byte[] { result[8], result[7] }));// wrk_time
					rec.setData1(Utils.bytesToInt(new byte[] { result[9] }));// evt_time
					rec.setData2(Utils.bytesToInt(new byte[] { result[11], result[10] }));// ps_min
					rec.setData3(Utils.bytesToInt(new byte[] { result[13], result[12] }));// ps_max
					rec.setData4(Utils.bytesToInt(new byte[] { result[15], result[14] }));// boreas_call
					break;
				case 0x71:
					// EVENT_PRO_PROXIMITY
					rec.setData0(Utils.bytesToInt(new byte[] { result[8], result[7] }));// wrk_time
					rec.setData1(Utils.bytesToInt(new byte[] { result[9] }));// evt_time
					rec.setData2(Utils.bytesToInt(new byte[] { result[11], result[10] }));// ps_enter
					rec.setData3(Utils.bytesToInt(new byte[] { result[13], result[12] }));// ps_exit
					rec.setData4(Utils.bytesToInt(new byte[] { result[15], result[14] }));// boreas_call
					break;
				case 0x72:
					// EVENT_PRO_BREATH
					rec.setData0(Utils.bytesToInt(new byte[] { result[8], result[7] }));// wrk_time
					rec.setData1(Utils.bytesToInt(new byte[] { result[9] }));// evt_time
					rec.setData2(Utils.bytesToInt(new byte[] { result[11], result[10] }));// orient[0]
					rec.setData3(Utils.bytesToInt(new byte[] { result[13], result[12] }));// orient[1]
					rec.setData4(Utils.bytesToInt(new byte[] { result[15], result[14] }));// orient[2]
					if ((result[11] & 0x80) != 0) {
						// orient[0] is negative
						rec.setData2(rec.getData2() - 65536);
					}
					if ((result[13] & 0x80) != 0) {
						// orient[1] is negative
						rec.setData3(rec.getData3() - 65536);
					}
					if ((result[15] & 0x80) != 0) {
						// orient[2] is negative
						rec.setData4(rec.getData4() - 65536);
					}
					rec.setData5(Utils.bytesToInt(new byte[] { result[16] }));// pif
					break;
				}

				// TODO LITE save only intakes
				if (result[6] == 0x72) {
					// EVENT_PRO_BREATH
					this.tableData.put(indexValue, rec);
					this.intakesAcquired = true;
					this.lastIntake = rec;
				}

				RecordModel generatedRec = intakeRule1.onNewRecordModel(rec);
				if (generatedRec != null) {
					indexValue = generatedRec.getIndex();
					this.tableData.put(indexValue, generatedRec);
					this.intakesAcquired = true;
					this.lastIntake = generatedRec;
					
				}

				logger.debug("[RESPONSE]-Received record " + (indexValue + 1) + "/" + this.totalRecordsNumber + " : "
						+ response.toString());

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						int loaderValue = ((totalRecordsNumber - recordsNumber) * 100 / totalRecordsNumber);
						MainController.this.mainWindow.insertTableRowH(rec, loaderValue);
						// se ho un evento di intake rilevato dalle regole, lo
						// visualizzo
						if (generatedRec != null) {
							MainController.this.mainWindow.insertTableRowH(generatedRec, loaderValue);
						}
					}
				});

				// all records have been acquired
				if (this.recordsNumber == 0) {

					// all records data have been acquired
					this.eventAcquisitionStatus = 0x03;
					this.startDownloadRecovery = false;
					this.deviceName = this.currentDevice.getName();

					// TODO LITE version - force the flash cleaning
					onEventCleanBtnClicked();

					if (this.intakesAcquired) {
						++this.totalIntakes;
						// the intake has been acquired, so show the SUCCESS
						// icon
						// TODO LITE extract the last INTAKE events and get its
						// value
						if (generatedRec != null) {
							// l'ultimo intake è generato, quindi a bassa
							// potenza LOW
							// update the UI
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									int loaderValue = ((totalRecordsNumber - recordsNumber) * 100 / totalRecordsNumber);
									MainController.this.mainWindow.onAllRecordsAquired(loaderValue);
									MainController.this.mainWindow.setResult(1);
									MainController.this.mainWindow.setPerformanceLevel(2);
									float indexPerc = (float) MainController.this.totalIntakes
											/ (float) MainController.this.totalNumber;
									indexPerc *= 100;
									NumberFormat nf = NumberFormat.getInstance();
									nf.setMaximumFractionDigits(2);
									String indexPercStr = nf.format(indexPerc) + "%";																							
									MainController.this.mainWindow.setIndex(MainController.this.totalIntakes + "/"
											+ MainController.this.totalNumber + " - " + indexPercStr, indexPerc, 0 );
								}
							});
						} else {
							// intake ad alta potenza
							// update the UI
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									int loaderValue = ((totalRecordsNumber - recordsNumber) * 100 / totalRecordsNumber);
									MainController.this.mainWindow.onAllRecordsAquired(loaderValue);
									MainController.this.mainWindow.setResult(1);
									if(lastIntake!=null){										
										switch (lastIntake.getData5()) {
										case 0:
											MainController.this.mainWindow.setPerformanceLevel(3);
											break;
										case 1:											
											MainController.this.mainWindow.setPerformanceLevel(1);
											break;
										case 2:
											MainController.this.mainWindow.setPerformanceLevel(1);
											break;
										}
									}
									float indexPerc = (float) MainController.this.totalIntakes
											/ (float) MainController.this.totalNumber;
									indexPerc *= 100;
									NumberFormat nf = NumberFormat.getInstance();
									nf.setMaximumFractionDigits(2);
									String indexPercStr = nf.format(indexPerc) + "%";
									int intakeDuration = 0;
									if(MainController.this.lastIntake!=null){
										intakeDuration = MainController.this.lastIntake.getData1();
									}																
									MainController.this.mainWindow.setIndex(MainController.this.totalIntakes + "/"
											+ MainController.this.totalNumber + " - " + indexPercStr, indexPerc, intakeDuration );
								}
							});
						}
					} else {
						// the intake has not been acquired, so show the ERROR
						// icon
						// update the UI
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								int loaderValue = ((totalRecordsNumber - recordsNumber) * 100 / totalRecordsNumber);
								MainController.this.mainWindow.onAllRecordsAquired(loaderValue);
								MainController.this.mainWindow.setResult(2);
								MainController.this.mainWindow.setPerformanceLevel(0);
								float indexPerc = (float) MainController.this.totalIntakes
										/ (float) MainController.this.totalNumber;
								indexPerc *= 100;
								NumberFormat nf = NumberFormat.getInstance();
								nf.setMaximumFractionDigits(2);
								String indexPercStr = nf.format(indexPerc) + "%";																						
								MainController.this.mainWindow.setIndex(MainController.this.totalIntakes + "/"
										+ MainController.this.totalNumber + " - " + indexPercStr, indexPerc, 0 );
							}
						});
					}
				}
			} else {
				logger.error("[RESPONSE]-Unexpected response: " + response.toString());
			}
		} else if (response.getResponseType() == ResponseType.SET_TIME_CMD_RESP) {
			logger.debug("[RESPONSE]-Amiko Response data: " + response.toString());

			// device timing
			byte[] result = Utils.subArray(response.getResult(), 0, 3);
			byte[] resultWithZeroFilling = new byte[] { 0x00, 0x00, 0x00, 0x00, result[3], result[2], result[1],
					result[0] };
			long timestampEpoc = Utils.bytesToLong(resultWithZeroFilling);
			timestampEpoc *= 1000;
			Date d = new Date();
			d.setTime(timestampEpoc);
			SimpleDateFormat formatter = new SimpleDateFormat();// ("yyyy/MM/dd
																// HH:mm");
		} else if (response.getResponseType() == ResponseType.GET_SERIAL_CMD_RESP) {
			// get serial code response
			logger.debug("[RESPONSE]-Amiko Response data: " + response.toString());

			byte[] serialCode = response.getResult();
			String serialCodeStr;
			try {
				// get the serial code
				serialCodeStr = new String(serialCode, "US-ASCII");

			} catch (UnsupportedEncodingException e) {
				logger.error("[RESPONSE]-" + e.getMessage(), e);
			}
		}
		logger.debug("[RESPONSE]-COMPLETED");
	}

	@Override
	public void onDongleError(String cause) {
		logger.error(cause);
		System.exit(-1);
	}

	/**
	 * EVENT MANAGERs
	 */
	public void onEventConnectBtnClicked() {
		// #CONNEC
		// Connect button has been selected
		// NOTE: if we start a BLE connection the discovery process will
		// be terminated

		if (this.currentDevice != null && this.connectionIsEnabled) {
			logger.debug("[BLEDEVICE-CONNECTION]-Open a new device connection...");
			this.autoReconnect = true;

			// update the UI
			this.mainWindow.enableConnectButton(false);

			driver.connectBLEDevice(this.currentDevice);
		} else if (!this.connectionIsEnabled) {
			// start disconnection
			this.lastCurrentDevice = null;
			this.autoReconnect = false;
			this.eventAcquisitionStatus = 0x00;
			this.startDownloadRecovery = false;

			driver.disconnectBLEDevice(this.currentDevice);
		}
	}
	
	public void onEventCleanListBtnClicked() {
		this.driver.cleanDevicesLists();
	}

	public void onEventShellDisposed() {
		System.exit(0);
	}

	public void onEventSetTimeBtnClicked(String userDateStr) {
		// #SET TIME
		if (this.currentDevice != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			try {
				// get the user date
				Date userDate = formatter.parse(userDateStr);
				long timestamp = userDate.getTime();

				logger.debug("[SEND-REQUEST]-Send command: SET_TIME");

				AmikoCmd cmd = AmikoCmdFactory.makeSetTimeCmd(timestamp);
				this.driver.sendCmd(cmd);
			} catch (ParseException e) {
				this.mainWindow.setStatusMessage("Invalid date or time format", true);
				e.printStackTrace();
			}
		}
	}

	public void onEventCleanBtnClicked() {
		// #CLEAN
		// FLASH CLEAN button has been selected
		if (this.currentDevice != null) {
			logger.debug("[SEND-REQUEST]-Send command: CLEAR_FLASH");

			AmikoCmd cmd = AmikoCmdFactory.makeClearFsCmd();
			this.driver.sendCmd(cmd);
		}
	}

	public void onEventSetTimeNowBtnClicked() {
		// #SET TIME
		if (this.currentDevice != null) {
			Date now = new Date();
			long timestamp = now.getTime();

			logger.debug("[SEND-REQUEST]-Send command: SET_TIME");

			AmikoCmd cmd = AmikoCmdFactory.makeSetTimeCmd(timestamp);
			this.driver.sendCmd(cmd);
		}
	}

	public void onEventSetSerialBtnClicked(String serialCodeStr) {
		// #SET TIME
		if (this.currentDevice != null) {
			logger.debug("[SEND-REQUEST]-Send command: SET_SERIAL");

			AmikoCmd cmd = AmikoCmdFactory.makeSetSerialCmd(serialCodeStr);
			this.driver.sendCmd(cmd);
		}
	}

	public void onEventRefreshBtnClicked() {
		// #REFRESH
		if (this.currentDevice != null) {
			this.eventAcquisitionStatus = 0x00;

			// clean all table data
			// TODO LITE this.tableData.clear();

			this.intakeRule1.resetRuleStatus();

			// update the ui
			this.mainWindow.onRecordsTableRefreshing();

			logger.debug("[SEND-REQUEST]-Send command: GET_INDEX");

			// send the GET INDEX command
			AmikoCmd cmd = AmikoCmdFactory.makeGetIndexCmd();
			this.driver.sendCmd(cmd);
		}
	}

	public void onEventBLEDeviceSelected(int selectedBLEDevice) {
		synchronized (deviceList) {
			// TODO ATTENTION
			if (this.deviceList != null && this.deviceList.getSize() > 0 && selectedBLEDevice > -1) {
				this.currentDevice = (BLEDevice) this.deviceList.getElementAt(selectedBLEDevice);

				// enable the connection button
				this.mainWindow.enableConnectButton(true);
			}
		}

	}

	public void changeTableViewMode() {
		String valueStr = ConfigLoader.getInstance().getProperty("app.devmode.enabled");
		if (valueStr != null) {
			try {
				this.changeTableViewMode(Boolean.parseBoolean(valueStr));
			} catch (NumberFormatException e) {
				logger.error("app.devmode.enabled is not valid: " + valueStr);
			}
		}
	}

	public void changeTableViewMode(boolean devModeEnabled) {
		// clean the table
		this.devModeIsEnabled = devModeEnabled;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MainController.this.mainWindow.setTableViewMode(MainController.this.devModeIsEnabled);
			}
		});

		if (!this.startDownloadRecovery) {
			// all data are available, clear the table and show my data
			/*
			 * TODO LITE no clean the table Display.getDefault().asyncExec(new
			 * Runnable() {
			 * 
			 * @Override public void run() {
			 * MainController.this.mainWindow.cleanTable(); } });
			 */

			for (int i = 0; i < this.tableData.size(); i++) {
				RecordModel rec = this.tableData.get(i);
				if (devModeEnabled) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MainController.this.mainWindow.insertTableRow(rec, -1);
						}
					});
				} else {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MainController.this.mainWindow.insertTableRowH(rec, -1);
						}
					});
				}
			}
		}
	}

	/**
	 * PRIVATE METHODS
	 */

	private void retryConnection() {
		logger.debug("Retry to open the connection with the BLE stack");
		this.retryConnection = true;
		try {
			driver.connect(this, false);
		} catch (BLEDriverConnectionException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void recoveryRecordsDownloading() {
		logger.debug("[RECOVERY-RECS-DOWNLOAD]-Recovery records download");
		this.startDownloadRecovery = true;
		// inizializzo la barra di avanzamento

		// ricavo l'ultimo record recuperato
		int acquiredRecords = this.tableData.size();
		logger.debug(
				"[RECOVERY-RECS-DOWNLOAD]-Downloaded records:" + acquiredRecords + " of " + this.totalRecordsNumber);

		// check if flash has changed
		if (this.currentDevice != null) {
			this.eventAcquisitionStatus = 0x00;

			// update the ui
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MainController.this.mainWindow.enableTableButtons(false);
				}
			});

			// send the GET INDEX command
			AmikoCmd cmd = AmikoCmdFactory.makeGetIndexCmd();
			this.driver.sendCmd(cmd);
		}
	}

	/**
	 * Main method
	 */
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainController controller = new MainController();
			MainWindow window = new MainWindow(controller);
			controller.setMainWindow(window);
			window.open();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
