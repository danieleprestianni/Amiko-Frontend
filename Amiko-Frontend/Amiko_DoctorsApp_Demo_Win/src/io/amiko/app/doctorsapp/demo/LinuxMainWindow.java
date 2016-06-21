package io.amiko.app.doctorsapp.demo;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import io.amiko.app.devices.bledriver.BLEDeviceList;
import io.amiko.app.devices.bledriver.BLEDriver;
import io.amiko.app.devices.bledriver.bluegiga.BluegigaBLEDriver;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Text;

public class LinuxMainWindow {

	// interval between two consecutive connection attempts to the DONGLE
	// BLED112
	public final static int RETRY_INTERVAL = 2000;
	public final static String APP_VERSION = "Beta.0.3";

	// presentation instance variables (SWT widgets)
	protected Shell appShell;
	private Table table;
	private Button connectBtn;
	private Label statusValueLbl;
	private Label batteryValueLbl;
	private ProgressBar batteryStatusProgBar;
	private Label currentTimeLbl;
	private Button cleanBtn;
	private Button refreshBtn;
	private Button exportBtl;
	private DateTime datePicker;
	private DateTime timePicker;
	private Button setTimeNowBtn;
	private ProgressBar loaderBar;
	private Label loaderValueLbl;
	// TODO usare un setter/getter
	public List amikoDevicesCmb;
	private Label lblNewLabel_1;
	private Label serialLbl;
	private Text serialTxt;
	private Button setSerialBtn;

	private MainController mainController = null;

	private Logger logger;	

	public LinuxMainWindow(MainController controller) {
		// business logic
		this.mainController = controller;
		this.logger = Logger.getLogger(MainWindow.class);		
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		appShell.open();
		appShell.layout();
		
		Monitor primary = Display.getDefault().getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = appShell.getBounds();
	    
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    appShell.setLocation(x, y);
		
		//NOTE we need to start the controller here, after the shell creating but before the main lop started
		this.mainController.start();

		// main loop
		while (!appShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		appShell = new Shell();
		appShell.addDisposeListener(this.appShellDisposeListener);
		appShell.setMaximized(false);
		appShell.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/icon_trasp.png"));		
		appShell.setMinimumSize(new Point(600, 530));
		appShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		appShell.setTouchEnabled(true);
		appShell.setSize(600, 501);
		appShell.setText("Amiko - Doctor's App - " + APP_VERSION);
		appShell.setLayout(new FormLayout());

		table = new Table(appShell, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fd_table = new FormData();
		fd_table.left = new FormAttachment(0, 10);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnIndex = new TableColumn(table, SWT.CENTER);
		tblclmnIndex.setMoveable(true);
		tblclmnIndex.setWidth(60);
		tblclmnIndex.setText("INDEX");

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_1.setMoveable(true);
		tblclmnNewColumn_1.setWidth(130);
		tblclmnNewColumn_1.setText("TIMESTAMP");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_2.setMoveable(true);
		tblclmnNewColumn_2.setWidth(60);
		tblclmnNewColumn_2.setText("EVENT-ID");
		
		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_3.setMoveable(true);
		tblclmnNewColumn_3.setWidth(60);
		tblclmnNewColumn_3.setText("DATA 0");
		
		TableColumn tblclmnNewColumn_4 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_4.setMoveable(true);
		tblclmnNewColumn_4.setWidth(60);
		tblclmnNewColumn_4.setText("DATA 1");
		
		TableColumn tblclmnNewColumn_5 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_5.setMoveable(true);
		tblclmnNewColumn_5.setWidth(60);
		tblclmnNewColumn_5.setText("DATA 2");
		
		TableColumn tblclmnNewColumn_6 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_6.setMoveable(true);
		tblclmnNewColumn_6.setWidth(60);
		tblclmnNewColumn_6.setText("DATA 3");

		TableColumn tblclmnNewColumn_7 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_7.setMoveable(true);
		tblclmnNewColumn_7.setWidth(60);
		tblclmnNewColumn_7.setText("DATA 4");

		TableColumn tblclmnNewColumn_8 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_8.setMoveable(true);
		tblclmnNewColumn_8.setWidth(60);
		tblclmnNewColumn_8.setText("DATA 5");

		Group deviceInfoGroup = new Group(appShell, SWT.NONE);
		fd_table.right = new FormAttachment(deviceInfoGroup, 0, SWT.RIGHT);
		deviceInfoGroup.setText("Amiko Info");
		FormData fd_deviceInfoGroup = new FormData();
		fd_deviceInfoGroup.left = new FormAttachment(0, 10);
		fd_deviceInfoGroup.right = new FormAttachment(100, -10);
		deviceInfoGroup.setLayoutData(fd_deviceInfoGroup);					

		Label deviceLbl = new Label(appShell, SWT.WRAP);
		deviceLbl.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		deviceLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		FormData fd_deviceLbl = new FormData();
		fd_deviceLbl.bottom = new FormAttachment(deviceInfoGroup, -34);
		fd_deviceLbl.top = new FormAttachment(0, 10);
		fd_deviceLbl.left = new FormAttachment(0, 10);
		deviceLbl.setLayoutData(fd_deviceLbl);
		deviceLbl.setText("Amiko device:");
		
		batteryStatusProgBar = new ProgressBar(deviceInfoGroup, SWT.BORDER | SWT.SMOOTH);
		batteryStatusProgBar.setForeground(SWTResourceManager.getColor(124, 252, 0));
		batteryStatusProgBar.setTouchEnabled(true);
		batteryStatusProgBar.setBounds(43, 10, 77, 6);
		
		Label lblCurrentTime = new Label(deviceInfoGroup, SWT.NONE);
		lblCurrentTime.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblCurrentTime.setBounds(188, 5, 35, 15);
		lblCurrentTime.setText("Time:");		

		batteryValueLbl = new Label(deviceInfoGroup, SWT.NONE);
		batteryValueLbl.setBounds(126, 1, 35, 24);
		batteryValueLbl.setText("--%");
		
		currentTimeLbl = new Label(deviceInfoGroup, SWT.NONE);
		currentTimeLbl.setBounds(229, 1, 120, 24);
		currentTimeLbl.setText("-");// ("----/--/-- --:--");
		
		connectBtn = new Button(appShell, SWT.CENTER);
		connectBtn.setEnabled(false);
		FormData fd_connectBtn = new FormData();
		fd_connectBtn.top = new FormAttachment(0, 10);
		connectBtn.setLayoutData(fd_connectBtn);
		connectBtn.setText("Connect");		

		Label logoLbl = new Label(appShell, SWT.NONE);
		fd_connectBtn.right = new FormAttachment(logoLbl, -17);
		fd_deviceInfoGroup.top = new FormAttachment(logoLbl, 6);
		logoLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		logoLbl.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/Amiko_logo.png"));
		FormData fd_logoLbl = new FormData();
		fd_logoLbl.bottom = new FormAttachment(0, 78);
		fd_logoLbl.right = new FormAttachment(100, -10);
		fd_logoLbl.left = new FormAttachment(100, -244);
		fd_logoLbl.top = new FormAttachment(0, 10);
		logoLbl.setLayoutData(fd_logoLbl);			

		Group utilitiesGroup = new Group(appShell, SWT.NONE);
		fd_deviceInfoGroup.bottom = new FormAttachment(utilitiesGroup, -7);
		
		Label statusDeviceLbl = new Label(deviceInfoGroup, SWT.NONE);
		statusDeviceLbl.setBounds(519, 10, 35, 39);
		
		Label label_1 = new Label(deviceInfoGroup, SWT.NONE);
		label_1.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/battery.png"));
		label_1.setBounds(10, 0, 26, 33);

		lblNewLabel_1 = new Label(deviceInfoGroup, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNewLabel_1.setBounds(366, 5, 40, 15);
		lblNewLabel_1.setText("Serial:");			

		serialLbl = new Label(deviceInfoGroup, SWT.NONE);
		serialLbl.setBounds(412, 1, 142, 24);
		serialLbl.setText("-");		

		fd_table.top = new FormAttachment(utilitiesGroup, 6);
		utilitiesGroup.setText("Utilities");
		FormData fd_utilitiesGroup = new FormData();
		fd_utilitiesGroup.bottom = new FormAttachment(0, 240);
		fd_utilitiesGroup.top = new FormAttachment(0, 147);
		fd_utilitiesGroup.left = new FormAttachment(0, 10);
		fd_utilitiesGroup.right = new FormAttachment(100, -10);
		utilitiesGroup.setLayoutData(fd_utilitiesGroup);

		Label lblTimer = new Label(utilitiesGroup, SWT.NONE);
		lblTimer.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblTimer.setBounds(10, 12, 39, 15);
		lblTimer.setText("Date:");			

		datePicker = new DateTime(utilitiesGroup, SWT.NONE);
		datePicker.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
		datePicker.setBounds(54, 8, 150, 24);
		
		Label lblNewLabel = new Label(utilitiesGroup, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNewLabel.setBounds(10, 42, 39, 15);
		lblNewLabel.setText("Time:");				

		timePicker = new DateTime(utilitiesGroup, SWT.TIME);
		timePicker.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
		timePicker.setBounds(54, 37, 150, 24);
		
		Button setTimeBtn = new Button(utilitiesGroup, SWT.NONE);
		setTimeBtn.setBounds(210, 9, 59, 24);
		setTimeBtn.setText("Set");

		cleanBtn = new Button(utilitiesGroup, SWT.CENTER);
		cleanBtn.setImage(SWTResourceManager.getImage(MainWindow.class,
				"/io/amiko/app/resources/48px-Gnome-edit-delete.svg.png"));
		cleanBtn.setBounds(470, 2, 96, 59);
		cleanBtn.setText("Clean");

		setTimeNowBtn = new Button(utilitiesGroup, SWT.NONE);
		setTimeNowBtn.setBounds(210, 37, 59, 25);
		setTimeNowBtn.setText("Now");

		serialTxt = new Text(utilitiesGroup, SWT.NONE);
		serialTxt.setBounds(287, 10, 161, 24);
		serialTxt.setTextLimit(10);

		setSerialBtn = new Button(utilitiesGroup, SWT.NONE);
		setSerialBtn.setBounds(287, 39, 161, 25);
		setSerialBtn.setText("Set serial");

		Label statusLbl = new Label(appShell, SWT.NONE);
		FormData fd_statusLbl = new FormData();
		fd_statusLbl.bottom = new FormAttachment(100, -7);
		fd_statusLbl.right = new FormAttachment(0, 65);
		statusLbl.setLayoutData(fd_statusLbl);
		statusLbl.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		statusLbl.setText("STATUS:");
		
		statusValueLbl = new Label(appShell, SWT.NONE);
		statusValueLbl.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.NORMAL));

		FormData fd_statusValueLbl = new FormData();
		fd_statusValueLbl.top = new FormAttachment(100, -23);
		fd_statusValueLbl.right = new FormAttachment(100, -10);
		fd_statusValueLbl.left = new FormAttachment(0, 72);
		fd_statusValueLbl.bottom = new FormAttachment(100, -2);
		statusValueLbl.setLayoutData(fd_statusValueLbl);

		refreshBtn = new Button(appShell, SWT.NONE);
		fd_table.bottom = new FormAttachment(100, -89);
		refreshBtn.setImage(
				SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/update-888512_960_720.png"));

		FormData fd_refreshBtn = new FormData();
		fd_refreshBtn.top = new FormAttachment(table, 6);
		fd_refreshBtn.right = new FormAttachment(table, 0, SWT.RIGHT);
		fd_refreshBtn.bottom = new FormAttachment(100, -36);
		fd_refreshBtn.left = new FormAttachment(100, -113);
		refreshBtn.setLayoutData(fd_refreshBtn);
		refreshBtn.setText("Refresh");
		
		amikoDevicesCmb = new List(appShell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		fd_deviceLbl.right = new FormAttachment(amikoDevicesCmb, -6);
		fd_connectBtn.left = new FormAttachment(amikoDevicesCmb, 6);		
		
		FormData fd_amikoDevicesCmb = new FormData();
		fd_amikoDevicesCmb.bottom = new FormAttachment(deviceInfoGroup, -6);
		fd_amikoDevicesCmb.top = new FormAttachment(0, 10);
		fd_amikoDevicesCmb.left = new FormAttachment(0, 72);
		fd_amikoDevicesCmb.right = new FormAttachment(100, -345);
		amikoDevicesCmb.setLayoutData(fd_amikoDevicesCmb);
		
		exportBtl = new Button(appShell, SWT.NONE);
		exportBtl.setEnabled(false);
		FormData fd_exportBtl = new FormData();
//		fd_exportBtl.left = new FormAttachment(table, 358, SWT.LEFT);
	//	fd_exportBtl.right = new FormAttachment(table, 471);
		//fd_exportBtl.top = new FormAttachment(table, 6);
		fd_exportBtl.bottom = new FormAttachment(100, -36);
		fd_exportBtl.top = new FormAttachment(table, 6);
		fd_exportBtl.left = new FormAttachment(refreshBtn, -120, SWT.LEFT);
		fd_exportBtl.right = new FormAttachment(refreshBtn, -12);

		exportBtl.setLayoutData(fd_exportBtl);
		exportBtl.setText("Export CSV");

		loaderBar = new ProgressBar(appShell, SWT.NONE);
		FormData fd_loaderBar = new FormData();
		fd_loaderBar.left = new FormAttachment(0, 10);
		fd_loaderBar.top = new FormAttachment(table, 14);
		fd_loaderBar.right = new FormAttachment(100, -414);
		loaderBar.setLayoutData(fd_loaderBar);		

		loaderValueLbl = new Label(appShell, SWT.NONE);
		FormData fd_loaderValueLbl = new FormData();
		fd_loaderValueLbl.bottom = new FormAttachment(table, 27, SWT.BOTTOM);
		fd_loaderValueLbl.top = new FormAttachment(table, 6);
		fd_loaderValueLbl.left = new FormAttachment(loaderBar, 6);
		fd_loaderValueLbl.width = 50;
		loaderValueLbl.setLayoutData(fd_loaderValueLbl);

		// add all listeners
		connectBtn.addListener(SWT.Selection, this.connectBtnListener);
		setTimeBtn.addListener(SWT.Selection, this.setTimeBtnListener);
		cleanBtn.addListener(SWT.Selection, this.cleanBtnListener);
		setTimeNowBtn.addListener(SWT.Selection, this.setTimeNowBtnListener);
		setSerialBtn.addListener(SWT.Selection, this.setSerialBtnListener);
		refreshBtn.addListener(SWT.Selection, this.refreshBtnListener );
		amikoDevicesCmb.addSelectionListener(this.amikoDevicesCmbSelectionListener);
		exportBtl.addListener(SWT.Selection, this.exportBtlListener);

	}

	public void onConnected() {
		this.statusValueLbl.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		this.statusValueLbl.setText("Connected to the BLE stack");
	}

	/**
	 * Invoked after each connection attempt
	 */
	public void onNotConnected(String message) {
		this.statusValueLbl.setText(message + " - please check if your DONGLE is installed");
		this.statusValueLbl.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
	}


	public void onBLEDeviceConnected(String msg) {
		logger.debug("[BLEDEVICE-CONNECTION]-COMPLETED");
		this.statusValueLbl.setForeground(SWTResourceManager.getColor(0, 0, 0));
		this.statusValueLbl.setText(msg);
		this.connectBtn.setText("Disconnect");
		this.connectBtn.setEnabled(true);
	}

	public void onBLEDeviceDisconnected() {
		this.statusValueLbl.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		this.statusValueLbl.setText("BLE device has been lost");
		this.connectBtn.setText("Connect");
		this.batteryStatusProgBar.setSelection(0);
		this.batteryValueLbl.setText("--%");
		this.currentTimeLbl.setText("-");// ("----/--/--
											// --:--");
		this.serialLbl.setText("-");
	}
	
	public void onDiscoveredBLEDevices(BLEDeviceList deviceList) {
		if (deviceList == null || deviceList.getSize() == 0) {
			this.statusValueLbl.setForeground(SWTResourceManager.getColor(255, 0, 0));
			this.statusValueLbl.setText("BLE devices not available");
			this.connectBtn.setEnabled(false);
			this.amikoDevicesCmb.removeAll();
		} else if (deviceList.getSize() > 0) {
			this.statusValueLbl.setForeground(SWTResourceManager.getColor(0, 0, 0));
			this.statusValueLbl.setText("BLE devices available");
			this.connectBtn.setEnabled(false);
			synchronized (deviceList) {
				this.amikoDevicesCmb.setItems(deviceList.getList());
			}
		}
	}

	public void updateBatteryInfo(int batteryLevel) {
		this.batteryStatusProgBar.setSelection(batteryLevel);
		this.batteryValueLbl.setText(batteryLevel + "%");
	}

	public void resetBatteryInfo() {
		this.batteryStatusProgBar.setSelection(0);
		this.batteryValueLbl.setText("--%");
	}

	public void updateCurrentTimeInfo(String timeInfo) {
		this.currentTimeLbl.setText(timeInfo);
	}

	public void resetCurrentTimeInfo() {
		this.currentTimeLbl.setText("-");
	}

	public void enableCleanButton(boolean value) {
		this.cleanBtn.setEnabled(value);
	}

	public void enableTableButtons(boolean value) {
		this.refreshBtn.setEnabled(value);
		this.cleanBtn.setEnabled(value);
	}

	public void insertTableRow(RecordModel rec, int loaderValue) {
		this.loaderBar.setSelection(loaderValue);
		this.loaderValueLbl.setText(loaderValue + "%");
		TableItem tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(new String[] { rec.getIndex() + "", rec.getTimestamp() + "", rec.getEventId() + "",
				rec.getData0() + "", rec.getData1() + "", rec.getData2() + "", rec.getData3() + "",
				rec.getData4() + "", rec.getData5() + "" });

	}

	public void onAllRecordsAquired(int loaderValue) {
		this.loaderBar.setSelection(loaderValue);
		this.loaderValueLbl.setText(loaderValue + "%");
		this.refreshBtn.setEnabled(true);
		this.exportBtl.setEnabled(true);
		this.cleanBtn.setEnabled(true);
	}
	
	public void onRecordsTableRefreshing(){
		this.refreshBtn.setEnabled(false);
		this.exportBtl.setEnabled(false);
		this.cleanBtn.setEnabled(false);
		this.loaderBar.setSelection(0);
		this.loaderValueLbl.setText("0%");
		// clean the current table
		this.table.removeAll();				
	}

	public void updateSerialCode(String serialValue) {
		this.serialLbl.setText(serialValue);
		this.cleanBtn.setEnabled(true);
	}

	public void enableConnectButton(boolean status) {
		this.connectBtn.setEnabled(status);
	}

	public void setStatusMessage(String text, boolean isError) {
		if (isError) {
			this.statusValueLbl.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		} else {
			this.statusValueLbl.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		}
		this.statusValueLbl.setText(text);
	}

	/**
	 * UI listeners
	 */
	public Listener connectBtnListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			if(connectBtn.getText().equalsIgnoreCase("Connect")){
				logger.debug("[BLEDEVICE-CONNECTION]-Connect button has been clicked");				
			}else{
				logger.debug("[BLEDEVICE-DISCONNECTION]-Disconnect button has been clicked");								
			}
			mainController.onEventConnectBtnClicked();
		}
	};

	public DisposeListener appShellDisposeListener = new DisposeListener() {
		@Override
		public void widgetDisposed(DisposeEvent arg0) {
			mainController.onEventShellDisposed();
		}
	};

	public Listener setTimeBtnListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			String userDateStr = LinuxMainWindow.this.datePicker.getYear() + "-"
					+ (LinuxMainWindow.this.datePicker.getMonth() + 1) + "-" + LinuxMainWindow.this.datePicker.getDay() + " "
					+ LinuxMainWindow.this.timePicker.getHours() + ":" + LinuxMainWindow.this.timePicker.getMinutes();

			mainController.onEventSetTimeBtnClicked(userDateStr);
		}
	};

	public Listener cleanBtnListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			CleanFlashDialog dialog = new CleanFlashDialog(appShell, SWT.DIALOG_TRIM
			        | SWT.APPLICATION_MODAL);			
			boolean result = dialog.open();
			if(result){
				mainController.onEventCleanBtnClicked();
			}
		}
	};

	public Listener setTimeNowBtnListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			mainController.onEventSetTimeNowBtnClicked();
		}
	};

	public Listener setSerialBtnListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			// get the serial code
			String serialCodeStr = LinuxMainWindow.this.serialTxt.getText();

			mainController.onEventSetSerialBtnClicked(serialCodeStr);
		}
	};
	
	public Listener refreshBtnListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			mainController.onEventRefreshBtnClicked();
		}
	};

	public SelectionListener amikoDevicesCmbSelectionListener = new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			int selectedBLEDevice = ((List) arg0.getSource()).getSelectionIndex();
			mainController.onEventBLEDeviceSelected(selectedBLEDevice);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {

		}
	};
	
	public Listener exportBtlListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			mainController.onEventExportBtnCicked();
		}
	};

}
