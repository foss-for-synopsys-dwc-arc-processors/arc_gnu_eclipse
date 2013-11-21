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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.arc.embeddedcdt.gui.jtag.IScript;
import com.arc.embeddedcdt.gui.jtag.ITab;

public abstract class FancyCheckButton extends FancyButton
{
	protected Button checkButton;

	public FancyCheckButton(ITab tab, IScript script, Composite comp,
			String string, String interface_regexp)
	{
		super(tab, script, comp, string, interface_regexp);
	}

	@Override
	protected void createButtons(Composite comp, String label)
	{
		super.createButtons(comp, label);
		checkButton = new Button(comp, SWT.CHECK);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		checkButton.setLayoutData(gd);


		checkButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				buttonChangedEvent();
			}
			
		});
	}

}