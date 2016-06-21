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
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;

public class MainWindow {

	// presentation instance variables (SWT widgets)
	protected Shell appShell;
	private Table table;
	private Button connectBtn;
	private Button cleanListBtn;
	private Label statusValueLbl;
	private Button refreshBtn;
	private ProgressBar loaderBar;
	private Label loaderValueLbl;
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
		appShell.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/update-888512_960_720.png"));
		appShell.setMinimumSize(new Point(1200, 800));
		appShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		appShell.setTouchEnabled(true);
		appShell.setSize(600, 501);
		appShell.setText("Amiko - Doctor's App - " + appVersion);
		appShell.setLayout(new FormLayout());

		table = new Table(appShell, SWT.BORDER);
		FormData fd_table = new FormData();
		fd_table.left = new FormAttachment(0, 10);
		fd_table.right = new FormAttachment(100, -10);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// no dev mode column
		tblclmnNewColumn_9 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_9.setMoveable(true);
		tblclmnNewColumn_9.setWidth(200);
		tblclmnNewColumn_9.setText("TIME");

		// no dev mode column
		tblclmnNewColumn_10 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_10.setMoveable(true);
		tblclmnNewColumn_10.setWidth(200);
		tblclmnNewColumn_10.setText("ANGLE");

		// no dev mode column
		tblclmnNewColumn_11 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_11.setMoveable(true);
		tblclmnNewColumn_11.setWidth(200);
		tblclmnNewColumn_11.setText("PIF");

		tblclmnIndex = new TableColumn(table, SWT.CENTER);
		tblclmnIndex.setMoveable(true);
		tblclmnIndex.setWidth(230);
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

		Label deviceLbl = new Label(appShell, SWT.WRAP);
		deviceLbl.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		deviceLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_deviceLbl = new FormData();
		fd_deviceLbl.bottom = new FormAttachment(table, -377);
		fd_deviceLbl.top = new FormAttachment(0, 10);
		fd_deviceLbl.left = new FormAttachment(0, 10);
		deviceLbl.setLayoutData(fd_deviceLbl);
		deviceLbl.setText("Amiko device:");

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
		fd_table.top = new FormAttachment(logoLbl, 349);
		fd_connectBtn.right = new FormAttachment(logoLbl, -15);
		logoLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		logoLbl.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/Amiko_logo.png"));
		FormData fd_logoLbl = new FormData();
		fd_logoLbl.bottom = new FormAttachment(0, 78);
		fd_logoLbl.right = new FormAttachment(100, -10);
		fd_logoLbl.left = new FormAttachment(100, -244);
		fd_logoLbl.top = new FormAttachment(0, 10);
		logoLbl.setLayoutData(fd_logoLbl);

		Label statusLbl = new Label(appShell, SWT.NONE);
		statusLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		fd_deviceLbl.right = new FormAttachment(statusLbl, 0, SWT.RIGHT);
		FormData fd_statusLbl = new FormData();
		fd_statusLbl.left = new FormAttachment(0, 10);
		statusLbl.setLayoutData(fd_statusLbl);
		statusLbl.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		statusLbl.setText("STATUS:");

		statusValueLbl = new Label(appShell, SWT.NONE);
		statusValueLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_statusValueLbl = new FormData();
		fd_statusValueLbl.left = new FormAttachment(statusLbl, 6);
		fd_statusValueLbl.right = new FormAttachment(100, -10);
		statusValueLbl.setLayoutData(fd_statusValueLbl);

		refreshBtn = new Button(appShell, SWT.NONE);
		fd_table.bottom = new FormAttachment(refreshBtn, -6);
		fd_statusValueLbl.top = new FormAttachment(refreshBtn, 6);

		FormData fd_refreshBtn = new FormData();
		fd_refreshBtn.top = new FormAttachment(100, -62);
		fd_refreshBtn.bottom = new FormAttachment(100, -29);
		fd_refreshBtn.left = new FormAttachment(100, -113);
		fd_refreshBtn.right = new FormAttachment(100, -10);
		refreshBtn.setLayoutData(fd_refreshBtn);
		refreshBtn.setText("Clean");

		amikoDevicesCmb = new List(appShell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		fd_connectBtn.left = new FormAttachment(amikoDevicesCmb, 6);

		FormData fd_amikoDevicesCmb = new FormData();
		fd_amikoDevicesCmb.bottom = new FormAttachment(0, 78);
		fd_amikoDevicesCmb.top = new FormAttachment(0, 10);
		fd_amikoDevicesCmb.left = new FormAttachment(deviceLbl, 6);
		fd_amikoDevicesCmb.right = new FormAttachment(100, -334);
		amikoDevicesCmb.setLayoutData(fd_amikoDevicesCmb);

		loaderBar = new ProgressBar(appShell, SWT.NONE);
		fd_statusLbl.top = new FormAttachment(loaderBar, 22);
		FormData fd_loaderBar = new FormData();
		fd_loaderBar.top = new FormAttachment(table, 6);
		fd_loaderBar.left = new FormAttachment(0, 10);
		loaderBar.setLayoutData(fd_loaderBar);

		loaderValueLbl = new Label(appShell, SWT.NONE);
		fd_loaderBar.right = new FormAttachment(loaderValueLbl, -6);
		loaderValueLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_loaderValueLbl = new FormData();
		fd_loaderValueLbl.top = new FormAttachment(table, 6);
		fd_loaderValueLbl.width = 50;
		fd_loaderValueLbl.left = new FormAttachment(0, 186);
		loaderValueLbl.setLayoutData(fd_loaderValueLbl);
		
		checkLbl = new Label(appShell, SWT.NONE);
		checkLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		checkLbl.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/result_empty.png"));
		
		FormData fd_checkLbl = new FormData();
		fd_checkLbl.left = new FormAttachment(0, 10);
		fd_checkLbl.bottom = new FormAttachment(table, -42);
		fd_checkLbl.top = new FormAttachment(amikoDevicesCmb, 6);
		fd_checkLbl.height = 100;
		checkLbl.setLayoutData(fd_checkLbl);
		
		performanceLbl = new Label(appShell, SWT.NONE);
		fd_checkLbl.right = new FormAttachment(performanceLbl, -6);
		performanceLbl.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/performance_empty.png"));
		performanceLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_performanceLbl = new FormData();
		fd_performanceLbl.right = new FormAttachment(0, 844);
		fd_performanceLbl.left = new FormAttachment(0, 327);
		fd_performanceLbl.bottom = new FormAttachment(table, -42);
		fd_performanceLbl.top = new FormAttachment(amikoDevicesCmb, 6);
		performanceLbl.setLayoutData(fd_performanceLbl);
		
		indexLbl = new Label(appShell, SWT.NONE);
		indexLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		indexLbl.setFont(SWTResourceManager.getFont("@Yu Gothic UI Semibold", 44, SWT.BOLD));
		
		durationLbl = new Label(appShell, SWT.NONE);
		durationLbl.setFont(SWTResourceManager.getFont("@Yu Gothic UI Semibold", 44, SWT.BOLD));
		durationLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));		
		
		indexBar = new ProgressBar(appShell, SWT.BORDER | SWT.SMOOTH | SWT.VERTICAL);
		FormData fd_indexBar = new FormData();
		//fd_indexBar.right = new FormAttachment(indexLbl, 0);
		fd_indexBar.top = new FormAttachment(checkLbl, -250);
		fd_indexBar.bottom = new FormAttachment(checkLbl, 0, SWT.BOTTOM);
		fd_indexBar.left = new FormAttachment(performanceLbl, 50, SWT.RIGHT);
		fd_indexBar.right = new FormAttachment(performanceLbl, 100, SWT.RIGHT);
		indexBar.setLayoutData(fd_indexBar);

		FormData fd_indexLbl = new FormData();
		fd_indexLbl.top = new FormAttachment(logoLbl, 54);
		fd_indexLbl.right = new FormAttachment(100, -50);
		fd_indexLbl.left = new FormAttachment(indexBar, 100);
		indexLbl.setLayoutData(fd_indexLbl);
		
		FormData fd_durationLbl = new FormData();
		fd_durationLbl.top = new FormAttachment(logoLbl, 200);
		fd_durationLbl.right = new FormAttachment(100, -50);
		fd_durationLbl.left = new FormAttachment(indexBar, 100);
		durationLbl.setLayoutData(fd_durationLbl);
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

		Label lblCurrentTime = new Label(deviceInfoGroup, SWT.NONE);
		lblCurrentTime.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblCurrentTime.setBounds(188, 5, 35, 15);
		lblCurrentTime.setText("Time:");

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

		Label lblNewLabel = new Label(utilitiesGroup, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNewLabel.setBounds(10, 42, 39, 15);
		lblNewLabel.setText("Time:");

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

		FormData fd_exportBtl = new FormData();
		// fd_exportBtl.left = new FormAttachment(table, 358, SWT.LEFT);
		// fd_exportBtl.right = new FormAttachment(table, 471);
		// fd_exportBtl.top = new FormAttachment(table, 6);
		fd_exportBtl.bottom = new FormAttachment(100, -36);
		fd_exportBtl.top = new FormAttachment(table, 6);
		fd_exportBtl.left = new FormAttachment(refreshBtn, -120, SWT.LEFT);
		fd_exportBtl.right = new FormAttachment(refreshBtn, -12);

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

		FormData fd_devModeCheckBox = new FormData();
		fd_devModeCheckBox.bottom = new FormAttachment(loaderBar, 0, SWT.BOTTOM);
	}

	private void addAllListeners() {
		// add all listeners
		connectBtn.addListener(SWT.Selection, this.connectBtnListener);
		cleanListBtn.addListener(SWT.Selection, this.cleanListBtnListener);
		refreshBtn.addListener(SWT.Selection, this.refreshBtnListener);
		amikoDevicesCmb.addSelectionListener(this.amikoDevicesCmbSelectionListener);
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

	public void enableTableButtons(boolean value) {
		//this.refreshBtn.setEnabled(value);
	}

	public void enableWidgets(boolean value) {
		enableTableButtons(value);
	}
	
	public void enableCleanListButton(boolean value){
		this.cleanListBtn.setEnabled(value);
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
			this.loaderBar.setMinimum(0);
			this.loaderBar.setMaximum(100);
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
		
		// auto-scroll table
		table.select(table.getItemCount() - 1);
		table.showSelection();

	}

	public void onAllRecordsAquired(int loaderValue) {
		this.loaderBar.setSelection(loaderValue);
		this.loaderValueLbl.setText(loaderValue + "%");
		this.refreshBtn.setEnabled(true);
	}

	public void onRecordsTableRefreshing() {
		this.refreshBtn.setEnabled(false);
		this.loaderBar.setSelection(0);
		this.loaderValueLbl.setText("0%");
		// clean the current table
		//TODO LITE this.table.removeAll();
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
			tblclmnNewColumn_1.setWidth(220);
			tblclmnNewColumn_2.setWidth(0);
			tblclmnNewColumn_3.setWidth(0);
			tblclmnNewColumn_4.setWidth(0);
			tblclmnNewColumn_5.setWidth(0);
			tblclmnNewColumn_6.setWidth(0);
			tblclmnNewColumn_7.setWidth(0);
			tblclmnNewColumn_8.setWidth(0);
			tblclmnNewColumn_9.setWidth(220);
			tblclmnNewColumn_10.setWidth(220);
			tblclmnNewColumn_11.setWidth(220);
		}
	}
	
	/**
	 * 
	 * @param value 0 - empty, 1 - HIGH, 2 - LOW, 3 - MODERATE
	 */
	public void setPerformanceLevel(int value){
		switch(value){
		case 0:
			performanceLbl.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/performance_empty.png"));
			break;
		case 1:
			performanceLbl.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/high_level_small.png"));
			break;
		case 2:
			performanceLbl.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/low_level_small.png"));
			break;
		case 3:
			performanceLbl.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/medium_level_small.png"));
			break;
		}
	}

	/**
	 * 
	 * @param value 0 - empty, 1 - SUCCESS, 2 - ERROR  
	 */
	public void setResult(int value){
		switch(value){
		case 0:
			checkLbl.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/result_empty.png"));				break;
		case 1:
			checkLbl.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/success-check.png"));
			break;
		case 2:
			checkLbl.setImage(SWTResourceManager.getImage(MainWindow.class, "/io/amiko/app/resources/error-check.png"));	
			break;
		}
	}
	
	public void setIndex(String indexStr, float indexValue, int duration){
		this.indexLbl.setText(indexStr);		
		// get breath event time
		double breathTimeValue = ((duration) * 64.0 / 1000.0);
		System.out.println(duration);
		System.out.println("ma che storia");
		if(duration!=0){
			this.durationLbl.setText(breathTimeValue + " sec.");
		}else{
			this.durationLbl.setText("");
		}
		this.indexBar.setSelection((int)Math.floor(indexValue));
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

	public Listener refreshBtnListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			//mainController.onEventRefreshBtnClicked();
			table.removeAll();
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
	
	private Label checkLbl;
	private Label performanceLbl;
	private Label indexLbl;
	private ProgressBar indexBar;
	private Label durationLbl;
}
