package io.amiko.app.doctorsapp.demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class CleanFlashDialog extends Dialog {

	protected boolean result;
	protected Shell cleanFlashDialog;
	private Label lblAllDataWill;
	private Label lblNewLabel;
	private Button btnNewButton;
	private Button btnNewButton_1;
	private Display display;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CleanFlashDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");		
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public boolean open() {
		createContents();
		cleanFlashDialog.open();
		cleanFlashDialog.layout();
		Monitor primary = Display.getDefault().getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = cleanFlashDialog.getBounds();
	    
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    cleanFlashDialog.setLocation(x, y);
	    
		display = getParent().getDisplay();		
		while (!cleanFlashDialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		cleanFlashDialog = new Shell(getParent(), SWT.DIALOG_TRIM
		        | SWT.APPLICATION_MODAL);		
		cleanFlashDialog.setMinimumSize(new Point(127, 29));
		cleanFlashDialog.setSize(300, 171);
		cleanFlashDialog.setText("Clean flash task");
		cleanFlashDialog.setLayout(new FormLayout());
		
		lblAllDataWill = new Label(cleanFlashDialog, SWT.NONE);
		lblAllDataWill.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		FormData fd_lblAllDataWill = new FormData();
		lblAllDataWill.setLayoutData(fd_lblAllDataWill);
		lblAllDataWill.setText("All data will be delete, are you sure?");
		
		lblNewLabel = new Label(cleanFlashDialog, SWT.NONE);
		fd_lblAllDataWill.top = new FormAttachment(lblNewLabel, 6);
		fd_lblAllDataWill.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setImage(SWTResourceManager.getImage(CleanFlashDialog.class, "/io/amiko/app/resources/Emblem-important.svg.png"));
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		fd_lblNewLabel.bottom = new FormAttachment(100, -84);
		fd_lblNewLabel.right = new FormAttachment(0, 284);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		
		btnNewButton = new Button(cleanFlashDialog, SWT.NONE);
		btnNewButton.setSelection(true);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				result = false;				
				cleanFlashDialog.dispose();
			}
		});
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.bottom = new FormAttachment(100, -10);
		fd_btnNewButton.right = new FormAttachment(100, -78);
		fd_btnNewButton.left = new FormAttachment(0, 153);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("No");
		
		btnNewButton_1 = new Button(cleanFlashDialog, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				result = true;
				cleanFlashDialog.dispose();
			}
		});
		FormData fd_btnNewButton_1 = new FormData();
		fd_btnNewButton_1.right = new FormAttachment(100, -9);
		fd_btnNewButton_1.left = new FormAttachment(btnNewButton, 6);
		fd_btnNewButton_1.top = new FormAttachment(btnNewButton, 0, SWT.TOP);
		btnNewButton_1.setLayoutData(fd_btnNewButton_1);
		btnNewButton_1.setText("Yes");

	}
}
