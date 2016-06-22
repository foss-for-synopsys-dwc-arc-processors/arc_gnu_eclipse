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

import java.util.Hashtable;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.service.GDBBackend;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.utils.spawner.Spawner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.framework.BundleContext;

import com.arc.embeddedcdt.LaunchPlugin;

/**
 * DSF service containing GDB server-related logic:
 *                      commands to start a server,
 *                      server start and shutdown methods.
 */
public class GdbServerBackend extends GDBBackend {

    private Process process;

    private String[] commandLineArray = new String[] {
            "/home/apologo/2016.03-rc1/ide_linux/bin/openocd", "-d0", "-c", "gdb_port 49105",
            "-f", "/home/apologo/2016.03-rc1/ide_linux/share/openocd/scripts/board/snps_em_sk_v2.1.cfg",
            "-s", "/home/apologo/2016.03-rc1/ide_linux/share/openocd/scripts/" };

    public GdbServerBackend(DsfSession session, ILaunchConfiguration launchConfiguration) {
        super(session, launchConfiguration);
    }

    @Override
    protected BundleContext getBundleContext() {
        return LaunchPlugin.getDefault().getBundle().getBundleContext();
    }

    @Override
    public void initialize(final RequestMonitor requestMonitor) {
        register( new String[]{ GdbServerBackend.class.getName() },
            new Hashtable<String,String>() );
        try {
            process = launchGDBProcess(commandLineArray);
        } catch (CoreException e) {
            e.printStackTrace();
            requestMonitor.done();
        }
        requestMonitor.done();
    }

    @Override
    public void shutdown(RequestMonitor requestMonitor) {
        unregister();
        interrupt();
        requestMonitor.done();
    }

    @Override
    public Process getProcess() {
        return process;
    }

    @Override
    public void interrupt() {
        if (process != null && process instanceof Spawner) {
            Spawner gdbSpawner = (Spawner) process;
            if (getSessionType() == SessionType.REMOTE) {
                gdbSpawner.interrupt();
            } else {
                gdbSpawner.interruptCTRLC();
            }
        }
    }

}
