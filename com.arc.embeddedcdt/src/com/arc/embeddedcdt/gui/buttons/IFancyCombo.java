package com.arc.embeddedcdt.gui.buttons;

import com.arc.embeddedcdt.gui.jtag.IScript;

public interface IFancyCombo
{


	/** Notification that the script changed 
	 * @param source The script that changed  
	 **/
	public abstract void scriptChangedEvent(IScript source);

}