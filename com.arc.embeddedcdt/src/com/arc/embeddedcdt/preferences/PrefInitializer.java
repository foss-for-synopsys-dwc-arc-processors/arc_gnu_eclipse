/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/

package com.arc.embeddedcdt.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.arc.embeddedcdt.LaunchPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PrefInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = LaunchPlugin.getDefault().getPreferenceStore();
		store.setDefault(PrefConstants.P_DEBUGGER_NAME, LaunchPlugin
				.getResourceString(PrefConstants.P_RES_DEBUGGER_NAME));
		store.setDefault(PrefConstants.P_DEBUGGER_INIT, LaunchPlugin
				.getResourceString(PrefConstants.P_RES_DEBUGGER_INIT));
	
	}
}
