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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.statushandlers.StatusManager;

import com.arc.embeddedcdt.ILaunchPreferences;
import com.arc.embeddedcdt.LaunchPlugin;
import com.arc.embeddedcdt.common.InvalidDirectoryPathException;
import com.arc.embeddedcdt.dsf.GdbServerBackend;
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;
import com.arc.embeddedcdt.gui.ARCWorkingDirectoryBlock;

public class NsimBackend extends GdbServerBackend {

    private boolean pass_reconnect = false;

    private String commandLineTemplate = "%s"
            + " -port %s"
            + " -gdb";

    public NsimBackend(DsfSession session, ILaunchConfiguration launchConfiguration) {
        super(session, launchConfiguration);

        IPreferenceStore prefs = LaunchPlugin.getDefault().getPreferenceStore();
        this.pass_reconnect = prefs.getBoolean(ILaunchPreferences.NSIM_PASS_RECONNECT_OPTION);
    }

    @Override
    public String getCommandLine() {

        ConfigurationReader cfgReader = new ConfigurationReader(launchConfiguration);
        String nsimPath = cfgReader.getNsimPath();
        String gdbServerPort = cfgReader.getGdbServerPort();
        StringBuffer commandLine = new StringBuffer();
        commandLine.append(String.format(commandLineTemplate, nsimPath, gdbServerPort));

        if (this.pass_reconnect) {
            commandLine.append(" -reconnect");
        }

        boolean simulateMemoryExceptions = cfgReader.getNsimSimulateMemoryExceptions();
        if (!simulateMemoryExceptions) {
            commandLine.append(" -off memory_exception_interrupt");
        }
        boolean simulateExceptions = cfgReader.getNsimSimulateExceptions();
        if (!simulateExceptions) {
            commandLine.append(" -off enable_exceptions");
        }
        boolean simulateInvalidInstExceptions = cfgReader
                .getNsimSimulateInvalidInstructionExceptions();
        if (!simulateInvalidInstExceptions) {
            commandLine.append(" -off invalid_instruction_interrupt");
        }
        boolean useJit = cfgReader.getNsimUseJit();
        if (useJit) {
            String jitThreads = cfgReader.getNsimJitThreads();
            commandLine.append(" -on nsim_fast");
            if (!jitThreads.equals("1")) {
                commandLine.append(" -p nsim_fast-num-threads=").append(jitThreads);
            }
        }
        boolean useHostLink = cfgReader.getNsimUseNsimHostlink();
        if (useHostLink) {
            commandLine.append(" -on nsim_emt");
        }

        boolean useTcf = cfgReader.getNsimUseTcf();
        if (useTcf) {
            String tcfPath = cfgReader.getNsimTcfPath();
            commandLine.append(" -tcf ").append(tcfPath);
        }
        boolean useProps = cfgReader.getNsimUseProps();
        if (useProps) {
            String propsPath = cfgReader.getNsimPropsPath();
            commandLine.append(" -propsfile ").append(propsPath);
        }
        return commandLine.toString();
    }

    @Override
    protected boolean hasReconnectSupport() {
        return this.pass_reconnect;
    }

    @Override
    public String getProcessLabel() {
        return "nSIM GDBserver";
    }

    @Override
    public File getWorkingDirectory() {
        ConfigurationReader cfgReader = new ConfigurationReader(launchConfiguration);
        String workingDirectoryPath = cfgReader.getNsimWorkingDirectoryPath();
        File workingDir = new File(System.getProperty("user.dir"));

        try {
            workingDirectoryPath = ARCWorkingDirectoryBlock
                    .resolveDirectoryPath(workingDirectoryPath);
            workingDir = new File(workingDirectoryPath);
        } catch (InvalidDirectoryPathException e) {
            String message = "Working directory for nSIM is not specified or incorrect:\n"
                    + e.getMessage() + "\n\nUsing directory \'" + workingDir.getPath()
                    + "\' instead.";
            StatusManager.getManager().handle(
                    new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID, message),
                    StatusManager.BLOCK);
        }

        return workingDir;
    }

}
