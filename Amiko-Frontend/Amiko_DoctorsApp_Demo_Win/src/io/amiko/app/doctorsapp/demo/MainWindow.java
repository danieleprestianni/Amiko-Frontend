package io.amiko.app.doctorsapp.demo;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;

import java.text.NumberFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.ibm.icu.text.SimpleDateFormat;

import io.amiko.app.devices.bledriver.BLEDeviceList;

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

public class MainWindow {

	// presentation instance variables (SWT widgets)
	protected Shell appShell;
	private Table table;
	private Button connectBtn;
	private Button cleanListBtn;
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
	private Button setTimeBtn;
	private Button devModeCheckBox;
	private TableColumn tblclmnIndex;
	private TableColumn tblclmnNewColumn_1;
	private TableColumn tblclmnNewColumn_2;
	private TableColumn tblclmnNewColumn_3;
	private TableColumn tblclmnNewColumn_4;
	private TableColumn tblclmnNewColumn_5;
	private TableColumn tblclmnNewColumn_6;
	private TableColumn tblclmnNewColumn_7;
	private TableColumn tblclmnNewColumn_8;
	private TableColumn tblclmnNewColumn_9;
	private TableColumn tblclmnNewColumn_10;
	private TableColumn tblclmnNewColumn_11;
	// TODO usare un setter/getter
	public List amikoDevicesCmb;
	private Label lblNewLabel_1;
	private Label serialLbl;
	private Text serialTxt;
	private Button setSerialBtn;
	private String OS;
	private MainController mainController = null;
	private Logger logger;
	private String appVersion = "##";

	public MainWindow(MainController controller) {

		appVersion = ConfigLoader.getInstance().getProperty("app.version");
		if (appVersion == null) {
			appVersion = "##";
		}

		// business logic
		this.mainController = controller;
		this.logger = Logger.getLogger(MainWindow.class);
		OS = System.getProperty("os.name");
		logger.debug("OS=" + OS);
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		if (OS.toLowerCase().indexOf("windows") != -1) {
			createContents();
		} else if (OS.equals("Linux")) {
			createContentsLinux();
		} else if (OS.equals("Linux-all-ports")) {
			createContentsLinux();
		} else if (OS.toLowerCase().indexOf("qnx") != -1) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		} else if (OS.equals("Irix")) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		} else if (OS.equals("FreeBSD")) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		} else if (OS.equals("NetBSD")) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		} else if (OS.equals("Solaris")) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		} else if (OS.equals("HP-UX")) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		} else if (OS.equals("UnixWare") || OS.equals("OpenUNIX")) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		} else if (OS.equals("OpenServer")) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		} else if (OS.equals("Compaq's Digital UNIX") || OS.equals("OSF1")) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		} else if (OS.equals("BeOS")) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		} else if (OS.equals("Mac OS X")) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		} else if (OS.equals("OpenServer")) {
			logger.error("Unsupported SO " + OS);
			createContentsLinux();
		}
		addAllListeners();
		mainController.changeTableViewMode();		
		
		appShell.open();
		appShell.layout();

		Monitor primary = Display.getDefault().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = appShell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		appShell.setLocation(x, y);

		// NOTE we need to start the controller here, after the shell creating
		// but before the main lop started
		this.mainController.start();

		enableWidgets(false);

		// main loop
		while (!appShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * 
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
		appShell.setText("Amiko - Doctor's App - " + appVersion);
		appShell.setLayout(new FormLayout());

		table = new Table(appShell, SWT.BORDER);
		FormData fd_table = new FormData();
		fd_table.left = new FormAttachment(0, 10);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// no dev mode column
		tblclmnNewColumn_9 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_9.setMoveable(true);
		tblclmnNewColumn_9.setWidth(0);
		tblclmnNewColumn_9.setText("TIME");

		// no dev mode column
		tblclmnNewColumn_10 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_10.setMoveable(true);
		tblclmnNewColumn_10.setWidth(0);
		tblclmnNewColumn_10.setText("ANGLE");

		// no dev mode column
		tblclmnNewColumn_11 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_11.setMoveable(true);
		tblclmnNewColumn_11.setWidth(0);
		tblclmnNewColumn_11.setText("PIF");

		tblclmnIndex = new TableColumn(table, SWT.CENTER);
		tblclmnIndex.setMoveable(true);
		tblclmnIndex.setWidth(30);
		tblclmnIndex.setText("ID");

		tblclmnNewColumn_1 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_1.setMoveable(true);
		tblclmnNewColumn_1.setWidth(120);
		tblclmnNewColumn_1.setText("TIMESTAMP");

		tblclmnNewColumn_2 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_2.setMoveable(true);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("EVENT-ID");

		tblclmnNewColumn_3 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_3.setMoveable(true);
		tblclmnNewColumn_3.setWidth(60);
		tblclmnNewColumn_3.setText("DATA 0");

		tblclmnNewColumn_4 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_4.setMoveable(true);
		tblclmnNewColumn_4.setWidth(60);
		tblclmnNewColumn_4.setText("DATA 1");

		tblclmnNewColumn_5 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_5.setMoveable(true);
		tblclmnNewColumn_5.setWidth(60);
		tblclmnNewColumn_5.setText("DATA 2");

		tblclmnNewColumn_6 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_6.setMoveable(true);
		tblclmnNewColumn_6.setWidth(60);
		tblclmnNewColumn_6.setText("DATA 3");

		tblclmnNewColumn_7 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_7.setMoveable(true);
		tblclmnNewColumn_7.setWidth(60);
		tblclmnNewColumn_7.setText("DATA 4");

		tblclmnNewColumn_8 = new TableColumn(table, SWT.CENTER);
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
		batteryStatusProgBar.setBounds(34, 24, 77, 17);

		Label lblCurrentTime = new Label(deviceInfoGroup, SWT.NONE);
		lblCurrentTime.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblCurrentTime.setBounds(206, 24, 35, 15);
		lblCurrentTime.setText("Time:");

		batteryValueLbl = new Label(deviceInfoGroup, SWT.NONE);
		batteryValueLbl.setBounds(117, 24, 35, 15);
		batteryValueLbl.setText("--%");

		currentTimeLbl = new Label(deviceInfoGroup, SWT.NONE);
		currentTimeLbl.setBounds(247, 24, 120, 15);
		currentTimeLbl.setText("-");// ("----/--/-- --:--");

		connectBtn = new Button(appShell, SWT.CENTER);
		connectBtn.setEnabled(false);
		FormData fd_connectBtn = new FormData();
		fd_connectBtn.top = new FormAttachment(deviceLbl, 0, SWT.TOP);
		connectBtn.setLayoutData(fd_connectBtn);
		connectBtn.setText("Connect");
		
		cleanListBtn = new Button(appShell, SWT.CENTER);
		cleanListBtn.setEnabled(true);
		FormData fd_cleanListBtn = new FormData();
		fd_cleanListBtn.top = new FormAttachment(connectBtn, 6);
		fd_cleanListBtn.left = new FormAttachment(connectBtn, 0, SWT.LEFT);
		cleanListBtn.setLayoutData(fd_cleanListBtn);
		cleanListBtn.setText("Clean");

		Label logoLbl = new Label(appShell, SWT.NONE);
		fd_connectBtn.right = new FormAttachment(logoLbl, -15);
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
		label_1.setBounds(2, 17, 26, 33);

		lblNewLabel_1 = new Label(deviceInfoGroup, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNewLabel_1.setBounds(373, 24, 40, 15);
		lblNewLabel_1.setText("Serial:");

		serialLbl = new Label(deviceInfoGroup, SWT.NONE);
		serialLbl.setBounds(412, 24, 142, 15);
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
		lblTimer.setBounds(10, 25, 39, 15);
		lblTimer.setText("Date:");

		datePicker = new DateTime(utilitiesGroup, SWT.BORDER);
		datePicker.setBounds(55, 25, 86, 24);

		Label lblNewLabel = new Label(utilitiesGroup, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNewLabel.setBounds(10, 57, 39, 15);
		lblNewLabel.setText("Time:");

		timePicker = new DateTime(utilitiesGroup, SWT.BORDER | SWT.TIME);
		timePicker.setBounds(55, 59, 86, 24);

		setTimeBtn = new Button(utilitiesGroup, SWT.NONE);
		setTimeBtn.setBounds(147, 25, 44, 24);
		setTimeBtn.setText("Set");

		cleanBtn = new Button(utilitiesGroup, SWT.CENTER);
		cleanBtn.setImage(SWTResourceManager.getImage(MainWindow.class,
				"/io/amiko/app/resources/48px-Gnome-edit-delete.svg.png"));
		cleanBtn.setBounds(426, 25, 128, 59);
		cleanBtn.setText("Clean Flash");

		setTimeNowBtn = new Button(utilitiesGroup, SWT.NONE);
		setTimeNowBtn.setBounds(147, 58, 44, 25);
		setTimeNowBtn.setText("Now");

		serialTxt = new Text(utilitiesGroup, SWT.BORDER);
		serialTxt.setBounds(225, 28, 174, 21);
		serialTxt.setTextLimit(10);

		setSerialBtn = new Button(utilitiesGroup, SWT.NONE);
		setSerialBtn.setBounds(225, 58, 174, 25);
		setSerialBtn.setText("Set serial");

		Label statusLbl = new Label(appShell, SWT.NONE);
		fd_deviceLbl.right = new FormAttachment(statusLbl, 0, SWT.RIGHT);
		FormData fd_statusLbl = new FormData();
		fd_statusLbl.top = new FormAttachment(table, 45);
		fd_statusLbl.left = new FormAttachment(table, 0, SWT.LEFT);
		statusLbl.setLayoutData(fd_statusLbl);
		statusLbl.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		statusLbl.setText("STATUS:");

		statusValueLbl = new Label(appShell, SWT.NONE);
		FormData fd_statusValueLbl = new FormData();
		fd_statusValueLbl.left = new FormAttachment(statusLbl, 6);
		fd_statusValueLbl.right = new FormAttachment(100, -10);
		statusValueLbl.setLayoutData(fd_statusValueLbl);

		refreshBtn = new Button(appShell, SWT.NONE);
		fd_statusValueLbl.top = new FormAttachment(refreshBtn, 6);
		fd_table.bottom = new FormAttachment(refreshBtn, -6);
		refreshBtn.setImage(
				SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/update-888512_960_720.png"));

		FormData fd_refreshBtn = new FormData();
		fd_refreshBtn.top = new FormAttachment(100, -62);
		fd_refreshBtn.bottom = new FormAttachment(100, -29);
		fd_refreshBtn.left = new FormAttachment(100, -113);
		fd_refreshBtn.right = new FormAttachment(100, -10);
		refreshBtn.setLayoutData(fd_refreshBtn);
		refreshBtn.setText("Refresh");

		amikoDevicesCmb = new List(appShell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		fd_connectBtn.left = new FormAttachment(amikoDevicesCmb, 6);

		FormData fd_amikoDevicesCmb = new FormData();
		fd_amikoDevicesCmb.bottom = new FormAttachment(0, 78);
		fd_amikoDevicesCmb.top = new FormAttachment(0, 10);
		fd_amikoDevicesCmb.left = new FormAttachment(deviceLbl, 6);
		fd_amikoDevicesCmb.right = new FormAttachment(100, -334);
		amikoDevicesCmb.setLayoutData(fd_amikoDevicesCmb);

		exportBtl = new Button(appShell, SWT.NONE);
		exportBtl.setEnabled(false);
		FormData fd_exportBtl = new FormData();
		fd_exportBtl.bottom = new FormAttachment(statusValueLbl, -6);
		fd_exportBtl.top = new FormAttachment(table, 6);
		fd_exportBtl.left = new FormAttachment(refreshBtn, -81, SWT.LEFT);
		fd_exportBtl.right = new FormAttachment(refreshBtn, -12);
		exportBtl.setLayoutData(fd_exportBtl);
		exportBtl.setText("Export CSV");

		loaderBar = new ProgressBar(appShell, SWT.NONE);
		FormData fd_loaderBar = new FormData();
		fd_loaderBar.right = new FormAttachment(table, 170);
		fd_loaderBar.left = new FormAttachment(table, 0, SWT.LEFT);
		fd_loaderBar.top = new FormAttachment(table, 6);
		loaderBar.setLayoutData(fd_loaderBar);

		loaderValueLbl = new Label(appShell, SWT.NONE);
		FormData fd_loaderValueLbl = new FormData();
		fd_loaderValueLbl.width = 50;
		fd_loaderValueLbl.top = new FormAttachment(table, 6);
		fd_loaderValueLbl.left = new FormAttachment(loaderBar, 6);
		loaderValueLbl.setLayoutData(fd_loaderValueLbl);

		devModeCheckBox = new Button(appShell, SWT.CHECK);
		devModeCheckBox.setSelection(false);
		FormData fd_devModeCheckBox = new FormData();
		fd_devModeCheckBox.bottom = new FormAttachment(loaderBar, 0, SWT.BOTTOM);
		fd_devModeCheckBox.right = new FormAttachment(exportBtl, -6);
		devModeCheckBox.setLayoutData(fd_devModeCheckBox);
		devModeCheckBox.setText("Dev mode");

	}

	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContentsLinux() {
		appShell = new Shell();
		appShell.addDisposeListener(this.appShellDisposeListener);
		appShell.setMaximized(false);
		appShell.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/icon_trasp.png"));
		appShell.setMinimumSize(new Point(600, 530));
		appShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		appShell.setTouchEnabled(true);
		appShell.setSize(600, 501);
		appShell.setText("Amiko - Doctor's App - " + appVersion);
		appShell.setLayout(new FormLayout());

		table = new Table(appShell, SWT.BORDER);
		FormData fd_table = new FormData();
		fd_table.left = new FormAttachment(0, 10);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// no dev mode column
		tblclmnNewColumn_9 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_9.setMoveable(true);
		tblclmnNewColumn_9.setWidth(0);
		tblclmnNewColumn_9.setText("TIME");

		// no dev mode column
		tblclmnNewColumn_10 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_10.setMoveable(true);
		tblclmnNewColumn_10.setWidth(0);
		tblclmnNewColumn_10.setText("ANGLE");

		// no dev mode column
		tblclmnNewColumn_11 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_11.setMoveable(true);
		tblclmnNewColumn_11.setWidth(0);
		tblclmnNewColumn_11.setText("PIF");

		tblclmnIndex = new TableColumn(table, SWT.CENTER);
		tblclmnIndex.setMoveable(true);
		tblclmnIndex.setWidth(30);
		tblclmnIndex.setText("ID");

		tblclmnNewColumn_1 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_1.setMoveable(true);
		tblclmnNewColumn_1.setWidth(120);
		tblclmnNewColumn_1.setText("TIMESTAMP");

		tblclmnNewColumn_2 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_2.setMoveable(true);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("EVENT-ID");

		tblclmnNewColumn_3 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_3.setMoveable(true);
		tblclmnNewColumn_3.setWidth(60);
		tblclmnNewColumn_3.setText("DATA 0");

		tblclmnNewColumn_4 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_4.setMoveable(true);
		tblclmnNewColumn_4.setWidth(60);
		tblclmnNewColumn_4.setText("DATA 1");

		tblclmnNewColumn_5 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_5.setMoveable(true);
		tblclmnNewColumn_5.setWidth(60);
		tblclmnNewColumn_5.setText("DATA 2");

		tblclmnNewColumn_6 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_6.setMoveable(true);
		tblclmnNewColumn_6.setWidth(60);
		tblclmnNewColumn_6.setText("DATA 3");

		tblclmnNewColumn_7 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_7.setMoveable(true);
		tblclmnNewColumn_7.setWidth(60);
		tblclmnNewColumn_7.setText("DATA 4");

		tblclmnNewColumn_8 = new TableColumn(table, SWT.CENTER);
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
		fd_deviceLbl.left = new FormAttachment(0, 10);
		fd_deviceLbl.bottom = new FormAttachment(deviceInfoGroup, -34);
		fd_deviceLbl.top = new FormAttachment(0, 10);
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
		
		cleanListBtn = new Button(appShell, SWT.CENTER);
		cleanListBtn.setEnabled(true);
		FormData fd_cleanListBtn = new FormData();
		fd_cleanListBtn.top = new FormAttachment(connectBtn, 6);
		fd_cleanListBtn.left = new FormAttachment(connectBtn, 0, SWT.LEFT);
		cleanListBtn.setLayoutData(fd_cleanListBtn);
		cleanListBtn.setText("Clean");

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

		setTimeBtn = new Button(utilitiesGroup, SWT.NONE);
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
		fd_connectBtn.left = new FormAttachment(amikoDevicesCmb, 6);
		fd_deviceLbl.right = new FormAttachment(amikoDevicesCmb, -6);

		FormData fd_amikoDevicesCmb = new FormData();
		fd_amikoDevicesCmb.bottom = new FormAttachment(deviceInfoGroup, -6);
		fd_amikoDevicesCmb.top = new FormAttachment(0, 10);
		fd_amikoDevicesCmb.left = new FormAttachment(0, 72);
		fd_amikoDevicesCmb.right = new FormAttachment(100, -363);
		amikoDevicesCmb.setLayoutData(fd_amikoDevicesCmb);

		exportBtl = new Button(appShell, SWT.NONE);
		exportBtl.setEnabled(false);
		FormData fd_exportBtl = new FormData();
		// fd_exportBtl.left = new FormAttachment(table, 358, SWT.LEFT);
		// fd_exportBtl.right = new FormAttachment(table, 471);
		// fd_exportBtl.top = new FormAttachment(table, 6);
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

		devModeCheckBox = new Button(appShell, SWT.CHECK);
		FormData fd_devModeCheckBox = new FormData();
		fd_devModeCheckBox.bottom = new FormAttachment(loaderBar, 0, SWT.BOTTOM);
		fd_devModeCheckBox.right = new FormAttachment(exportBtl, -6);
		devModeCheckBox.setSelection(false);
		devModeCheckBox.setLayoutData(fd_devModeCheckBox);
		devModeCheckBox.setText("Dev mode");
	}

	private void addAllListeners() {
		// add all listeners
		connectBtn.addListener(SWT.Selection, this.connectBtnListener);
		cleanListBtn.addListener(SWT.Selection, this.cleanListBtnListener);
		setTimeBtn.addListener(SWT.Selection, this.setTimeBtnListener);
		cleanBtn.addListener(SWT.Selection, this.cleanBtnListener);
		setTimeNowBtn.addListener(SWT.Selection, this.setTimeNowBtnListener);
		setSerialBtn.addListener(SWT.Selection, this.setSerialBtnListener);
		refreshBtn.addListener(SWT.Selection, this.refreshBtnListener);
		amikoDevicesCmb.addSelectionListener(this.amikoDevicesCmbSelectionListener);
		exportBtl.addListener(SWT.Selection, this.exportBtlListener);
		devModeCheckBox.addSelectionListener(devModeChckBoxListener);

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
		this.cleanListBtn.setEnabled(true);
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
	
	public void enableCleanListButton(boolean value){
		this.cleanListBtn.setEnabled(value);
	}

	public void enableTableButtons(boolean value) {
		this.refreshBtn.setEnabled(value);
		this.cleanBtn.setEnabled(value);
		this.devModeCheckBox.setEnabled(value);
	}

	public void enableWidgets(boolean value) {
		enableTableButtons(value);
		this.setSerialBtn.setEnabled(value);
		this.setTimeNowBtn.setEnabled(value);
		this.setTimeBtn.setEnabled(value);
	}

	public void insertTableRow(RecordModel rec, int loaderValue) {
		if(rec==null || rec.getIndex()<0){			
			return;
		}
		
		if(loaderValue!=-1){
			this.loaderBar.setSelection(loaderValue);
			this.loaderValueLbl.setText(loaderValue + "%");			
		}
		
		TableItem tableItem = new TableItem(table, SWT.NONE);

		// change timestamp view
		Date d = new Date();
		d.setTime(rec.getTimestamp() * 1000);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// formatter.format(d)
		tableItem.setText(new String[] { "", "", "", rec.getIndex() + "", formatter.format(d) + "", rec.getEventIdH(),
				rec.getData0() + "", rec.getData1() + "", rec.getData2() + "", rec.getData3() + "", rec.getData4() + "",
				rec.getData5() + "" });

	}

	/**
	 * 
	 * @param rec
	 * @param loaderValue
	 */
	public void insertTableRowH(RecordModel rec, int loaderValue) {		
		if(rec==null){
			return;
		}
		
		if(loaderValue!=-1){
			this.loaderBar.setSelection(loaderValue);
			this.loaderValueLbl.setText(loaderValue + "%");			
		}
		
		// take only the EVENT_PRO_BREATH events
		if (rec.getEventId()==114) {
			TableItem tableItem = new TableItem(table, SWT.NONE);

			// change timestamp view
			Date d = new Date();
			d.setTime(rec.getTimestamp() * 1000);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			// get the pif value
			String pifValue = "";
			switch (rec.getData5()) {
			case 0:
				pifValue = "LOW";
				break;
			case 1:
				pifValue = "MEDIUM";
				break;
			case 2:
				pifValue = "HIGH";
				break;
			}
			pifValue = "GOOD";
			// get the angle
			int orient0 = rec.getData2();
			int orient1 = rec.getData3();
			int orient2 = rec.getData4();
			double angle = Math.acos(-(double)orient0/Math.sqrt((double)(orient0*orient0+orient1*orient1+orient2*orient2)));
			angle*=180/Math.PI;
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			String angleValue = nf.format(angle) + "°";
			
			// get breath event time
			double breathTimeValue = ((rec.getData1()) * 64.0 / 1000.0);

			// formatter.format(d)
			tableItem.setText(new String[] { breathTimeValue + " sec", angleValue, pifValue, rec.getIndexH() + "",
					formatter.format(d) + "", rec.getEventIdH(), rec.getData0() + "", rec.getData1() + "",
					rec.getData2() + "", rec.getData3() + "", rec.getData4() + "", rec.getData5() + "" });
		}else if(rec.getCustomEventId()!=null){
			TableItem tableItem = new TableItem(table, SWT.NONE);

			// change timestamp view
			Date d = new Date();
			d.setTime(rec.getTimestamp() * 1000);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			// get the pif value
			String pifValue = "";
			switch (rec.getData5()) {
			case 0:
				pifValue = "LOW";
				break;
			case 1:
				pifValue = "MEDIUM";
				break;
			case 2:
				pifValue = "HIGH";
				break;
			}

			// get breath event time
			double breathTimeValue = ((rec.getData1()) * 64.0 / 1000.0);

			// formatter.format(d)
			tableItem.setText(new String[] { breathTimeValue + " sec", "", pifValue, rec.getIndexH() + "",
					formatter.format(d) + "", rec.getEventIdH(), rec.getData0() + "", rec.getData1() + "",
					rec.getData2() + "", rec.getData3() + "", rec.getData4() + "", rec.getData5() + "" });
		}
	}

	public void onAllRecordsAquired(int loaderValue) {
		this.loaderBar.setSelection(loaderValue);
		this.loaderValueLbl.setText(loaderValue + "%");
		this.refreshBtn.setEnabled(true);
		this.exportBtl.setEnabled(true);
		this.cleanBtn.setEnabled(true);
		this.devModeCheckBox.setEnabled(true);
	}

	public void onRecordsTableRefreshing() {
		this.refreshBtn.setEnabled(false);
		this.devModeCheckBox.setEnabled(false);
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

	public void cleanTable(){
		table.removeAll();
	}
	
	public void setTableViewMode(boolean devModeEnabled) {
		this.devModeCheckBox.setSelection(devModeEnabled);
		if (devModeEnabled) {
			tblclmnIndex.setWidth(30);
			tblclmnNewColumn_1.setWidth(120);
			tblclmnNewColumn_2.setWidth(100);
			tblclmnNewColumn_3.setWidth(60);
			tblclmnNewColumn_4.setWidth(60);
			tblclmnNewColumn_5.setWidth(60);
			tblclmnNewColumn_6.setWidth(60);
			tblclmnNewColumn_7.setWidth(60);
			tblclmnNewColumn_8.setWidth(60);
			tblclmnNewColumn_9.setWidth(0);
			tblclmnNewColumn_10.setWidth(0);
			tblclmnNewColumn_11.setWidth(0);
		} else {
			tblclmnIndex.setWidth(0);
			tblclmnNewColumn_1.setWidth(120);
			tblclmnNewColumn_2.setWidth(0);
			tblclmnNewColumn_3.setWidth(0);
			tblclmnNewColumn_4.setWidth(0);
			tblclmnNewColumn_5.setWidth(0);
			tblclmnNewColumn_6.setWidth(0);
			tblclmnNewColumn_7.setWidth(0);
			tblclmnNewColumn_8.setWidth(0);
			tblclmnNewColumn_9.setWidth(120);
			tblclmnNewColumn_10.setWidth(120);
			tblclmnNewColumn_11.setWidth(120);
		}
	}

	/**
	 * UI listeners
	 */
	public Listener connectBtnListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			if (connectBtn.getText().equalsIgnoreCase("Connect")) {
				logger.debug("[BLEDEVICE-CONNECTION]-Connect button has been clicked");
			} else {
				logger.debug("[BLEDEVICE-DISCONNECTION]-Disconnect button has been clicked");
			}
			mainController.onEventConnectBtnClicked();
		}
	};
	
	public Listener cleanListBtnListener = new Listener() {
		@Override
		public void handleEvent(Event event) {			
			mainController.onEventCleanListBtnClicked();
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
			String userDateStr = MainWindow.this.datePicker.getYear() + "-"
					+ (MainWindow.this.datePicker.getMonth() + 1) + "-" + MainWindow.this.datePicker.getDay() + " "
					+ MainWindow.this.timePicker.getHours() + ":" + MainWindow.this.timePicker.getMinutes();

			mainController.onEventSetTimeBtnClicked(userDateStr);
		}
	};

	public Listener cleanBtnListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			CleanFlashDialog dialog = new CleanFlashDialog(appShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			boolean result = dialog.open();
			if (result) {
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
			String serialCodeStr = MainWindow.this.serialTxt.getText();

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

	SelectionListener devModeChckBoxListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent arg0) {
			mainController.changeTableViewMode(MainWindow.this.devModeCheckBox.getSelection());
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {

		}
	};

}
