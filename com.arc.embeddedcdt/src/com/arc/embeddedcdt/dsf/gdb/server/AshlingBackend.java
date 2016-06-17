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
import com.arc.embeddedcdt.dsf.utils.Configuration;

public class AshlingBackend extends GdbServerBackend {

    private String commandLineTemplate = "%s"
            + " --jtag-frequency %s"
            + " --device arc"
            + " --gdb-port %s"
            + " --arc-reg-file %s";

    public AshlingBackend(DsfSession session, ILaunchConfiguration launchConfiguration) {
        super(session, launchConfiguration);
    }

    @Override
    public String getCommandLine() {

        String ashlingPath = Configuration.getAshlingPath(launchConfiguration);
        String gdbServerPort = Configuration.getGdbServerPort(launchConfiguration);
        String ashlingXmlFile = Configuration.getAshlingXmlPath(launchConfiguration);
        String jtagFrequency = Configuration.getAshlingJtagFrequency(launchConfiguration);

        String commandLine = String.format(commandLineTemplate, ashlingPath, jtagFrequency,
                gdbServerPort, ashlingXmlFile);
        return commandLine;
    }

    @Override
    public String getProcessLabel() {
        return "Ashling GDBserver";
    }

    @Override
    public String getCommandToConnect() {
        return "set tdesc filename " + Configuration.getAshlingTDescPath(launchConfiguration) + "\n"
                + super.getCommandToConnect();
    }
}
