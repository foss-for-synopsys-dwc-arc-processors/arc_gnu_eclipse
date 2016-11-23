/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.dsf.gdb.server;

import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.arc.embeddedcdt.dsf.GdbServerBackend;
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;

public class CustomGdbServerBackend extends GdbServerBackend {

    public CustomGdbServerBackend(DsfSession session, ILaunchConfiguration launchConfiguration) {
        super(session, launchConfiguration);
    }

    @Override
    public String getCommandLine() {
        ConfigurationReader cfgReader = new ConfigurationReader(launchConfiguration);
        String customGdbServerPath = cfgReader.getCustomGdbServerPath();
        String customGdbServerArg = cfgReader.getCustomGdbServerArgs();
        String commandLine = customGdbServerPath + " " + customGdbServerArg;
        return commandLine;
    }

    @Override
    public String getProcessLabel() {
        return "Custom GDBserver";
    }

}
