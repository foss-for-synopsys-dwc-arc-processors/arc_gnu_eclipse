/*******************************************************************************
 * This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package org.eclipse.cdt.cross.arc.gnu.uclibc.common;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.cross.arc.gnu.uclibc.common.CommandInfo;
import org.eclipse.cdt.cross.arc.gnu.uclibc.common.IsToolchainData;
import org.eclipse.cdt.managedbuilder.core.IManagedIsToolChainSupported;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

public abstract class IsToolChainSupported implements
        IManagedIsToolChainSupported {
    static final boolean DEBUG = false;

    public String getCompilerName() {
        return "arc-linux-gcc";
    }

    public String getPlatform() {
        return "linux";
    }

    public boolean isSupportedImpl(IToolChain oToolChain, Version oVersion,
            String sInstance, IsToolchainData oStaticData) {
        ITool[] tools = oToolChain.getTools();
        for (ITool tool : tools) {
            String extensions[] = tool.getAllOutputExtensions();
            List<String> extList = Arrays.asList(extensions);
            if (extList.contains("o") || extList.contains("obj")) {
                // We assume this tool is the compiler if its output
                // is .o or .obj file.
                // If the compiler doesn't exist in the search path,
                // then we don't support the tool.
                String cmd = tool.getToolCommand();
                if (cmd != null && cmd.length() > 0) {
                    if (!CommandInfo.commandExists(cmd))
                        return false;
                }
            }

            String current_tool_command = tool.getToolCommand();
            if (CommandInfo.commandExistsInPredefinedPath(current_tool_command)) {
                String eclipsehome = Platform.getInstallLocation().getURL().getPath();
                File predefined_path_dir = new File(eclipsehome).getParentFile();
                String predefined_path = predefined_path_dir + File.separator
                        + "bin" + File.separator;
                if (current_tool_command.indexOf(predefined_path) < 0) {
                    tool.setToolCommand(predefined_path + current_tool_command);
                    current_tool_command = tool.getToolCommand();
                }
            }

            if (current_tool_command.endsWith("-gcc")) {
                boolean gccForArcHs;
                try {
                    gccForArcHs = CommandInfo.isGccForArcHs(current_tool_command);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                boolean toolchainForArcHs = oToolChain.getName().indexOf("ARC HS") > -1;
                if (gccForArcHs != toolchainForArcHs)
                    return false;
            }
        }

        return true;
    }
}
