package com.arc.embeddedcdt.gui.jtag;

import com.arc.embeddedcdt.gui.buttons.IFancyCombo;


public interface IScript
{

	String getText();

	void setText(String text);

	/** Notify these that script changed */
	void add(IFancyCombo fancyButton);

	/**
	 * When the GUI is created or the script changed, copy values from script into GUI components 
	 */
	void scriptChangedEvent();

	void changeScript(IFirstExpression fancyButton, String regexp, String text);

}
