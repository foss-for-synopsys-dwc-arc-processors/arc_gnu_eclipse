/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.dsf;

import org.eclipse.cdt.dsf.debug.service.command.ICommandControl;
import org.eclipse.cdt.dsf.gdb.service.GdbDebugServicesFactory;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.dsf.gdb.server.AshlingBackend;
import com.arc.embeddedcdt.dsf.gdb.server.CustomGdbServerBackend;
import com.arc.embeddedcdt.dsf.gdb.server.NsimBackend;
import com.arc.embeddedcdt.dsf.gdb.server.OpenOcdBackend;
import com.arc.embeddedcdt.dsf.gdb.server.ExternallyStartedGdbServerBackend;
import com.arc.embeddedcdt.dsf.utils.Configuration;

public class ArcDebugServicesFactory extends GdbDebugServicesFactory {

    public ArcDebugServicesFactory(String version) {
        super(version);
    }

    public <V> V createService(Class<V> clazz, DsfSession session,
            Object... optionalArguments) {
    if (GdbServerBackend.class.isAssignableFrom(clazz)) {
            for (Object arg : optionalArguments) {
                    if (arg instanceof ILaunchConfiguration) {
                            return (V) createGdbServerBackendService(session,
                                            (ILaunchConfiguration) arg);
                    }
            }
    }
    return super.createService(clazz, session, optionalArguments);
}
    @Override
    protected ICommandControl createCommandControl(DsfSession session, ILaunchConfiguration config) {
        return new ArcGdbControl(session, config, new CommandFactory());
    }

    protected GdbServerBackend createGdbServerBackendService(DsfSession session, ILaunchConfiguration arg) {
        ArcGdbServer gdbServer = Configuration.getGdbServer(arg);
        switch (gdbServer) {
        case JTAG_OPENOCD:
            return new OpenOcdBackend(session, arg);
        case JTAG_ASHLING:
            return new AshlingBackend(session, arg);
        case NSIM:
            return new NsimBackend(session, arg);
        case CUSTOM_GDBSERVER:
            return new CustomGdbServerBackend(session, arg);
        case GENERIC_GDBSERVER:
            return new ExternallyStartedGdbServerBackend(session, arg);
        default:
            throw new IllegalArgumentException(
                    "Can not create a backend for GDB server: " + gdbServer.toString());
        }
    }

}
