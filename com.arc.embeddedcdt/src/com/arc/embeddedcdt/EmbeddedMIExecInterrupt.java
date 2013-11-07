package com.arc.embeddedcdt;

import org.eclipse.cdt.debug.mi.core.command.MIExecInterrupt;


public class EmbeddedMIExecInterrupt extends MIExecInterrupt {

	public EmbeddedMIExecInterrupt(String miVersion) {
		super(miVersion);
		setOperation(((char)3)+"-exec-interrupt");
	}


}
