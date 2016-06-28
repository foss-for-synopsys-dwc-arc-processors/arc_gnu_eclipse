package com.arc.embeddedcdt.gui;

import java.awt.Toolkit;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;

import com.arc.embeddedcdt.common.ArcGdbServer;

public class FirstlaunchDialog extends Dialog {
	public static String[] value= new String[2];;
	public Combo fFirstlaunchPrgmArgumentsComCom;// this variable is for getting user's
											// input COM port
	protected Label fPrgmArgumentsLabelCom;

	/**
	 * @param parent
	 */
	public FirstlaunchDialog(Shell parent) {
		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public FirstlaunchDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Makes the dialog visible.
	 * 
	 * @return
	 */
	public String[] open() {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL);
		shell.setLocation(Display.getCurrent().getClientArea().width / 2
				- shell.getShell().getSize().x / 2, Display.getCurrent()
				.getClientArea().height / 2 - shell.getSize().y / 2);

		shell.setText("Before Debug as Configuration");

		shell.setLayout(new GridLayout(2, true));

		Label label = new Label(shell, SWT.NULL);
		label.setText("ARC GDB Server:");

		Combo fPrgmArgumentsComboInit = new Combo(shell, SWT.SINGLE
				| SWT.BORDER);
		fPrgmArgumentsComboInit.add(ArcGdbServer.JTAG_OPENOCD.toString());
		fPrgmArgumentsComboInit.add(ArcGdbServer.JTAG_ASHLING.toString());
		fPrgmArgumentsComboInit.add(ArcGdbServer.NSIM.toString());

		fPrgmArgumentsLabelCom = new Label(shell, SWT.NULL);
		fPrgmArgumentsLabelCom.setText("COM  Ports:"); //$NON-NLS-1$

		fFirstlaunchPrgmArgumentsComCom = new Combo(shell, SWT.SINGLE | SWT.BORDER);

		List COM = ARCTerminalTab.COMserialport();
		for (int ii = 0; ii < COM.size(); ii++) {
			String currentcom = (String) COM.get(ii);
			fFirstlaunchPrgmArgumentsComCom.add(currentcom);
		}

		fFirstlaunchPrgmArgumentsComCom.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				Combo combo = (Combo) evt.widget;
				value[1] = combo.getText();
			}

		});

		fPrgmArgumentsComboInit.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				Combo combo = (Combo) evt.widget;
				value[0] = combo.getText();
				ArcGdbServer gdbServer;
				try {
				    gdbServer = ArcGdbServer.fromString(combo.getText());
				} catch (IllegalArgumentException e) {
				    gdbServer = ArcGdbServer.DEFAULT_GDB_SERVER;
				}
				if (gdbServer == ArcGdbServer.NSIM || gdbServer == ArcGdbServer.GENERIC_GDBSERVER) {
					fFirstlaunchPrgmArgumentsComCom.setVisible(false);
					fPrgmArgumentsLabelCom.setVisible(false);
				}
				else {
					fFirstlaunchPrgmArgumentsComCom.setVisible(true);
					fPrgmArgumentsLabelCom.setVisible(true);
				}
			}
		});
		final Button buttonOK = new Button(shell, SWT.PUSH);
		buttonOK.setText("     Ok     ");
		buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		Button buttonCancel = new Button(shell, SWT.PUSH);
		buttonCancel.setText("Cancel");

		buttonOK.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.dispose();
			}
		});

		buttonCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				value[0] = "";
				value[1] = "";
				shell.dispose();
			}
		});

		shell.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE)
					event.doit = false;
			}
		});
		fFirstlaunchPrgmArgumentsComCom.setText("");
		fPrgmArgumentsComboInit.setText("");
		shell.pack();
		shell.open();

		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		System.out.println("gdbserver: \""+value[0]+"\" COM serial port: \""+value[1]+"\"");
		return value;

	}

	public static void main(String[] args) {
		Shell shell = new Shell();
		FirstlaunchDialog dialog = new FirstlaunchDialog(shell);
		dialog.open();
	}
}
