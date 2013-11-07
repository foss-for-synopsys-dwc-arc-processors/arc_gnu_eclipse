/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/

package com.arc.embeddedcdt;

import org.eclipse.cdt.debug.mi.core.cdi.model.Target;
import org.eclipse.cdt.debug.mi.core.cdi.model.TargetConfiguration;

public class Configuration extends TargetConfiguration {
	public Configuration(Target target) {
		super(target);
	}

	public boolean supportsRestart() {
		return false;
	}

	public boolean supportsSharedLibrary() {
		return false;
	}

}
