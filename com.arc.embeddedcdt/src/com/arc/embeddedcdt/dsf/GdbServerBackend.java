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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.IGdbDebugConstants;
import org.eclipse.cdt.dsf.gdb.service.GDBBackend;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.utils.spawner.Spawner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.osgi.framework.BundleContext;

import com.arc.embeddedcdt.LaunchPlugin;

/**
 * DSF service containing GDB server-related logic:
 *                      commands to start a server,
 *                      server start and shutdown methods,
 *                      creating console for the server.
 */
public class GdbServerBackend extends GDBBackend {

    private Process process;
    private DsfSession session;

    private String[] commandLineArray = new String[] {
            "/home/apologo/2016.03-rc1/ide_linux/bin/openocd", "-d0", "-c", "gdb_port 49105",
            "-f", "/home/apologo/2016.03-rc1/ide_linux/share/openocd/scripts/board/snps_em_sk_v2.1.cfg",
            "-s", "/home/apologo/2016.03-rc1/ide_linux/share/openocd/scripts/" };

    public GdbServerBackend(DsfSession session, ILaunchConfiguration launchConfiguration) {
        super(session, launchConfiguration);
        this.session = session;
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

    /**
     * Add server process to the launch and assign to the process a label and a command line.
     * 
     * @throws CoreException
     */
    public void initializeServerConsole() throws CoreException {
        IProcess newProcess = addServerProcess("GDB Server");
        newProcess.setAttribute(IProcess.ATTR_CMDLINE, "openOCD");
    }

    /**
     * Add server process to the launch: show it in the debug tree and add its console.
     * 
     * @param label
     *            Label to assign to the process
     * @return process added to the launch
     * @throws CoreException
     */
    private IProcess addServerProcess(String label) throws CoreException {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(IGdbDebugConstants.PROCESS_TYPE_CREATION_ATTR,
                IGdbDebugConstants.GDB_PROCESS_CREATION_VALUE);

        ILaunch launch = (ILaunch) session.getModelAdapter(ILaunch.class);
        IProcess newProcess = null;
        Process serverProc = getProcess();
        if (serverProc != null) {
            newProcess = DebugPlugin.newProcess(launch, serverProc, label, attributes);
        }
        return newProcess;
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
