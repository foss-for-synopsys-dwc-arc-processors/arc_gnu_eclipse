/*******************************************************************************
 * Copyright (c) 2000, 2017 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *     Jonah Graham (Kichwa Coders) - Adapt to declarative tab generation
 *******************************************************************************/

package com.arc.embeddedcdt.gui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class LaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {
    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        // this method does not define tabs, for that see plugin.xml extension
        // of org.eclipse.debug.ui.launchConfigurationTabs
        setTabs(new ILaunchConfigurationTab[0]);
    }

}
