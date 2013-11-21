/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/


package com.arc.embeddedcdt.gui.buttons;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.arc.embeddedcdt.gui.jtag.IScript;
import com.arc.embeddedcdt.gui.jtag.ITab;

public abstract class FancyText extends FancyButton
{
	private Text textField;
	@Override
	protected void createButtons(Composite comp, String label) {
		super.createButtons(comp, label);
		
		textField = new Text(comp, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		textField.setLayoutData(gd);
		textField.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				buttonChangedEvent();
			}
			
		});
	}

	public FancyText(ITab tab, IScript script, Composite comp, String string, String interface_regexp) {
		super(tab, script, comp, string, interface_regexp);
	}

	

	@Override
	public String getButtonText() {
		return textField.getText();
	}

	

	@Override
	protected void setButtonText(String group) {
		textField.setText(group);
		
	}


	
}