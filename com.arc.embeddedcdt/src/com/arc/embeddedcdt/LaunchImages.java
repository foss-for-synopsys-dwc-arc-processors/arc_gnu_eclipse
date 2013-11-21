/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;


/**
 */
public class LaunchImages {

	private static final String NAME_PREFIX= "com.arc.embeddedcdt" + '.';
	private static final int NAME_PREFIX_LENGTH= NAME_PREFIX.length();
	
	// The plugin registry
	private static ImageRegistry imageRegistry = new ImageRegistry();

	// Subdirectory (under the package containing this class) where 16 color images are
	private static URL fgIconBaseURL;
	static {
		fgIconBaseURL= Platform.getBundle(com.arc.embeddedcdt.LaunchPlugin.getUniqueIdentifier()).getEntry("/icons/"); //$NON-NLS-1$
	}	

	private static final String T_TABS = "tabs/"; //$NON-NLS-1$
	//private static final String T_OBJS = "obj16/"; //$NON-NLS-1$

	public static String IMG_VIEW_COMMANDS_TAB = NAME_PREFIX + "commands_tab.gif"; //$NON-NLS-1$

	public static final ImageDescriptor DESC_TAB_COMMANDS = createManaged(T_TABS, IMG_VIEW_COMMANDS_TAB);


	public static void initialize() {
	}
	
	private static ImageDescriptor createManaged(String prefix, String name) {
		return createManaged(imageRegistry, prefix, name);
	}
	
	private static ImageDescriptor createManaged(ImageRegistry registry, String prefix, String name) {
		ImageDescriptor result= ImageDescriptor.createFromURL(makeIconFileURL(prefix, name.substring(NAME_PREFIX_LENGTH)));
		registry.put(name, result);
		return result;
	}
	
	public static Image get(String key) {
		return imageRegistry.get(key);
	}

	
	private static URL makeIconFileURL(String prefix, String name) {
		StringBuffer buffer= new StringBuffer(prefix);
		buffer.append(name);
		try {
			return new URL(fgIconBaseURL, buffer.toString());
		} catch (MalformedURLException e) {
			LaunchPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID, IStatus.ERROR, "Error", e));
		}
		return null;
	}
	
	/**
	 * Helper method to access the image registry from the JavaPlugin class.
	 */
	static ImageRegistry getImageRegistry() {
		return imageRegistry;
	}

}
