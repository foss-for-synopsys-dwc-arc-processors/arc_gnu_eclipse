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

import java.io.File;

import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.arc.embeddedcdt.common.FtdiCore;
import com.arc.embeddedcdt.common.FtdiDevice;
import com.arc.embeddedcdt.dsf.GdbServerBackend;
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;

public class OpenOcdBackend extends GdbServerBackend {

    private String commandLineTemplate = "%s"
            + " -d0"
            + " -c \"gdb_port %s\""
            + " -f %s"
            + " -s %s";

    public OpenOcdBackend(DsfSession session, ILaunchConfiguration launchConfiguration) {
        super(session, launchConfiguration);
    }

    /*
     * On AXS10x OpenOCD opens connection for each CPU core on target. For the first core it listens
     * on the port specified in launch configuration, for the second core -- on the next port and so
     * on. OpenOCD discovers cores in reverse order to core position in JTAG chain, so core order
     * for OpenOCD is the following: for AXS101: ARC 770D, ARC EM, AS221#2, AS221#1; for AXS102: ARC
     * HS36, ARC HS34; for AXS103 HS38x2: ARC HS38#1, ARC HS38#0.
     * 
     * Compute the port we should listen on from the value of FtdiCore.
     */
    @Override
    protected String getPortToConnect() {
        int gdbPort = Integer.parseInt(super.getPortToConnect());
        ConfigurationReader cfgReader = new ConfigurationReader(launchConfiguration);
        FtdiDevice ftdiDevice = cfgReader.getFtdiDevice();
        FtdiCore ftdiCore = cfgReader.getFtdiCore();

        if ((ftdiDevice == FtdiDevice.AXS101 && ftdiCore == FtdiCore.EM6)
                || (ftdiDevice == FtdiDevice.AXS102 && ftdiCore == FtdiCore.HS34)
                || (ftdiDevice == FtdiDevice.AXS103 && ftdiCore == FtdiCore.HS38_0)
                || (ftdiDevice == FtdiDevice.AXS103 && ftdiCore == FtdiCore.HS48_0)
                || (ftdiDevice == FtdiDevice.HSDK && ftdiCore == FtdiCore.HS38_3)) {
            gdbPort += 1;
        } else if ((ftdiDevice == FtdiDevice.AXS101 && ftdiCore == FtdiCore.AS221_2)
                || (ftdiDevice == FtdiDevice.HSDK && ftdiCore == FtdiCore.HS38_2)) {
            gdbPort += 2;
        } else if ((ftdiDevice == FtdiDevice.AXS101 && ftdiCore == FtdiCore.AS221_1)
                || (ftdiDevice == FtdiDevice.HSDK && ftdiCore == FtdiCore.HS38_1)) {
            gdbPort += 3;
        }
        return String.valueOf(gdbPort);
    }

    @Override
    public String getCommandLine() {

        ConfigurationReader cfgReader = new ConfigurationReader(launchConfiguration);
        String openOcdPath = cfgReader.getOpenOcdPath();
        String gdbPort = cfgReader.getGdbServerPort();
        String openOcdConfig = cfgReader.getOpenOcdConfig();

        final File rootDir = new File(openOcdPath).getParentFile().getParentFile();
        final File scriptsDir = new File(rootDir,
                "share" + File.separator + "openocd" + File.separator + "scripts");

        String commandLine = String.format(commandLineTemplate, openOcdPath, gdbPort, openOcdConfig,
                scriptsDir.getAbsolutePath());
        return commandLine;
    }

    @Override
    public String getProcessLabel() {
        return "OpenOCD";
    }

}
