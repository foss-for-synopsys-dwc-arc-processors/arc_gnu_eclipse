/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.common;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.arc.embeddedcdt.LaunchConfigurationConstants;

public enum ArcGdbServer {
    JTAG_OPENOCD("JTAG via OpenOCD", LaunchConfigurationConstants.DEFAULT_OPENOCD_PORT),
    JTAG_ASHLING("JTAG via Opella-XD", LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT),
    NSIM("nSIM", LaunchConfigurationConstants.DEFAULT_NSIM_PORT),
    GENERIC_GDBSERVER("Connect to running GDB server", null),
    CUSTOM_GDBSERVER("Custom GDB server", LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT);
    
    private final String stringRepresentation;
    public static final ArcGdbServer DEFAULT_GDB_SERVER = JTAG_OPENOCD;
    private Group guiGroup;
    private final String defaultPortNumber;
    private String portNumber = "";
    protected Text gdbServerPortNumberText;
    
    private ArcGdbServer(final String text, final String defaultPortNumber) {
        stringRepresentation = text;
        this.defaultPortNumber = defaultPortNumber;
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

    public static ArcGdbServer fromString(final String string) {
        for (ArcGdbServer server : ArcGdbServer.values()) {
            if (server.toString().equals(string))
                return server;
        }
        throw new IllegalArgumentException("String does not correspond to any ARC GDB Server.");
    }
}
