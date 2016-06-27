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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.arc.embeddedcdt.gui.jtag.IFirstExpression;
import com.arc.embeddedcdt.gui.jtag.IScript;
import com.arc.embeddedcdt.gui.jtag.ITab;

public abstract class FancyButton implements IFancyCombo, IFirstExpression
{

	protected String regexp;
	protected Label interfaceFaceLabel;
	private IScript script;
	ITab tab;
	
	public FancyButton(ITab tab, IScript script, Composite comp, String string,
			String interface_regexp) 
	{
		this.tab=tab;
		this.script=script;
		regexp=interface_regexp;
		createButtons(comp, string);
		script.add(this);

	}

	protected void createButtons(Composite comp, String label) {
		interfaceFaceLabel = new Label(comp, SWT.NONE);
		interfaceFaceLabel.setText(label); //$NON-NLS-1$
		GridData gd = new GridData();
		interfaceFaceLabel.setLayoutData(gd);
	}

	public void scriptChangedEvent(IScript source) 
	{
		scriptChangedEvent(script, regexp);
	}

	protected void scriptChangedEvent(IScript source, String regexp)
	{
		// here we need to find the commands in the script and update them. 
		// If they do not exist, we add them.
		String s=source.getText();
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(s);	
		if (m.matches())
		{				
			setButtonText(m.group(1));
		}
	}

	protected void updateButton()
	{
		// here we need to find the commands in the script and update them. 
		// If they do not exist, we add them.
		String s=script.getText();
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(s);	
		if (m.matches())
		{				
			setButtonText(m.group(1));
		}
	}

	protected abstract void setButtonText(String group);

	protected abstract String getButtonText();
	
	String defaultValue="";
	
	public String getValue()
	{
		if (getButtonText().equals(""))
			return getDefaultValue();
		return getButtonText();
	}

	
	/** The button changed,  update the script */
	public void buttonChangedEvent() 
	{
		updateScript();
	}

	protected void updateScript()
	{
		script.changeScript(this, regexp, getButtonText());
		tab.updateIt();
	}


	
	public String appendLine(String t)
	{
		if (t.endsWith(System.getProperty("line.separator")))
		{
			return t;
		}
		return t+System.getProperty("line.separator");
	}

	public String createFirstEntry(String script, String newText)
	{
		String t=appendLine(script);
		t+=createFirstEntry(newText);
		t=appendLine(t);
		return t;
	}

	protected String createFirstEntry(String newText)
	{
		throw new RuntimeException("Not implemented");
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}
}