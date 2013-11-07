/**
 * 
 */
package com.arc.embeddedcdt.gui.buttons;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.arc.embeddedcdt.gui.jtag.IScript;
import com.arc.embeddedcdt.gui.jtag.ITab;

public abstract class FancyCombo extends FancyButton
{

	public FancyCombo(ITab tab, IScript script, Composite comp, String string, String interfaceRegexp) {
		super(tab, script, comp, string, interfaceRegexp);
	}





	private String getActual(Map<String, String> m, String str)
	{
		String s=m.get(str);
		if (s==null)
			return str;
		return s;
	}
	
	private Combo fDCombo;
	protected void createButtons(Composite comp, String label)
	{
		GridData gd;
		super.createButtons(comp, label);

		fDCombo = new Combo(comp, SWT.DROP_DOWN);
		gd = new GridData();
		fDCombo.setLayoutData(gd);
		fDCombo.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
					buttonChangedEvent();
			}
		});
		fDCombo.setEnabled(true);
	}

	

	

	public void add(String arcJtagDebugger)
	{
		add(arcJtagDebugger, arcJtagDebugger); 
	}
	
	public void setButtonText(String newText) {
		fDCombo.setText(getActual(actualValue, newText));
	}


	public String getButtonText() {
		if (fDCombo.getText().equals(""))
			return "";
		
		return getActual(labelValue, fDCombo.getText());
	}


	Map<String, String> actualValue=new HashMap();
	Map<String, String> labelValue=new HashMap();

	public void add(String string, String string2)
	{
		actualValue.put(string, string2);
		labelValue.put(string2, string);
		fDCombo.add(string2);
	}





	public void clear()
	{
		fDCombo.removeAll();
	}

}
