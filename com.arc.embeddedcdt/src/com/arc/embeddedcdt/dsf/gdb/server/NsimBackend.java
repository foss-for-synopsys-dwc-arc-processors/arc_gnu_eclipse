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
import org.eclipse.ui.statushandlers.StatusManager;

import com.arc.embeddedcdt.LaunchPlugin;
import com.arc.embeddedcdt.common.InvalidDirectoryPathException;
import com.arc.embeddedcdt.dsf.GdbServerBackend;
import com.arc.embeddedcdt.dsf.utils.Configuration;
import com.arc.embeddedcdt.gui.ARCWorkingDirectoryBlock;

public class NsimBackend extends GdbServerBackend {

    private String commandLineTemplate = "%s"
            + " -port %s"
            + " -gdb";

    public NsimBackend(DsfSession session, ILaunchConfiguration launchConfiguration) {
        super(session, launchConfiguration);
    }

    @Override
    public String getCommandLine() {

        String nsimPath = Configuration.getNsimPath(launchConfiguration);
        String gdbServerPort = Configuration.getGdbServerPort(launchConfiguration);
        StringBuffer commandLine = new StringBuffer();
        commandLine.append(String.format(commandLineTemplate, nsimPath, gdbServerPort));

        boolean simulateMemoryExceptions = Configuration
                .getNsimSimulateMemoryExceptions(launchConfiguration);
        if (!simulateMemoryExceptions) {
            commandLine.append(" -off memory_exception_interrupt");
        }
        boolean simulateExceptions = Configuration.getNsimSimulateExceptions(launchConfiguration);
        if (!simulateExceptions) {
            commandLine.append(" -off enable_exceptions");
        }
        boolean simulateInvalidInstExceptions = Configuration
                .getNsimSimulateInvalidInstructionExceptions(launchConfiguration);
        if (!simulateInvalidInstExceptions) {
            commandLine.append(" -off invalid_instruction_interrupt");
        }
        boolean useJit = Configuration.getNsimUseJit(launchConfiguration);
        if (useJit) {
            String jitThreads = Configuration.getNsimJitThreads(launchConfiguration);
            commandLine.append(" -on nsim_fast");
            if (!jitThreads.equals("1")) {
                commandLine.append(" -p nsim_fast-num-threads=").append(jitThreads);
            }
        }
        boolean useHostLink = Configuration.getNsimUseNsimHostlink(launchConfiguration);
        if (useHostLink) {
            commandLine.append(" -on nsim_emt");
        }

        boolean useTcf = Configuration.getNsimUseTcf(launchConfiguration);
        if (useTcf) {
            String tcfPath = Configuration.getNsimTcfPath(launchConfiguration);
            commandLine.append(" -tcf ").append(tcfPath);
        }
        boolean useProps = Configuration.getNsimUseProps(launchConfiguration);
        if (useProps) {
            String propsPath = Configuration.getNsimPropsPath(launchConfiguration);
            commandLine.append(" -propsfile ").append(propsPath);
        }
        return commandLine.toString();
    }

    @Override
    public String getProcessLabel() {
        return "nSIM GDBserver";
    }

    @Override
    public File getWorkingDirectory() {
        String workingDirectoryPath = Configuration
                .getNsimWorkingDirectoryPath(launchConfiguration);
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
