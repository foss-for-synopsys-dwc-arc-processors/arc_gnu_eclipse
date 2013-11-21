/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/
package com.arc.embeddedcdt.gui.jtag;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.arc.embeddedcdt.gui.buttons.IFancyCombo;

public abstract class ScriptModifierBase implements IScript
{
	private List<IFancyCombo> fancyCombos = new LinkedList<IFancyCombo>();

	private boolean reenter;

	public void add(IFancyCombo fancyButton)
	{
		fancyCombos.add(fancyButton);

	}
	
	IScript me=this;

	private IFancyCombo allCombo = new IFancyCombo()
	{

		public void scriptChangedEvent(IScript source)
		{
			for (IFancyCombo f : fancyCombos)
				f.scriptChangedEvent(me);

		}
	};

	public void scriptChangedEvent()
	{
		if (reenter)
			return;
		try
		{
			reenter = true;
			allCombo.scriptChangedEvent(this);
		} finally
		{
			reenter = false;
		}

	}

	/**
	 * Modify script, which will cause buttons to update. The buttons changed
	 * event will then cause a change script which is stopped w/a reenter check
	 * lest we get stack overflow.
	 */
	public void changeScript(IFirstExpression fancyButton, String regexp,
			String newText)
	{
		if (reenter)
			return;
		try
		{
			reenter = true;
			if (newText.equals(""))
				return;
			String s = getText();
			Pattern p = Pattern.compile(regexp);
			Matcher m = p.matcher(s);
			if (m.matches())
			{
				String t = getText().substring(0, m.start(1));
				t += newText;
				t += getText().substring(m.end(1));
				setText(t);
			} else
			{
				String t = getText();
				t = fancyButton.createFirstEntry(t, newText);
				setText(t);
			}
		} finally
		{
			reenter = false;
		}

	}
}