// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.scannerconfig;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.cross.arc.gnu.ARCPlugin;
import org.eclipse.cdt.cross.arc.gnu.common.CommandInfo;
import org.eclipse.cdt.make.internal.core.scannerconfig2.GCCSpecsRunSIProvider;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

@SuppressWarnings("restriction")
public final class ArcScannerInfoRunProvider extends GCCSpecsRunSIProvider
{
    @Override
    protected boolean initialize()
    {
        if (!super.initialize()) {
            return false;
        }

        this.fCompileCommand = expandCommand(
            getCompilerTool(resource.getProject())
                .map(ITool::getToolCommand)
                .<IPath>map(Path::new)
                .orElse(fCompileCommand));
        return true;
    }

    private Optional<ITool> getCompilerTool(IProject project)
    {
        boolean isCPlusPlus = false;
        if (project.exists() && project.isOpen()) {
            try {
                isCPlusPlus = project.hasNature(CCProjectNature.CC_NATURE_ID);
            } catch (CoreException err) {
                ARCPlugin.log("Failed to get project nature.", err);
                return Optional.empty();
            }
        }
        String toolId = isCPlusPlus ? ".cpp.compiler." : ".c.compiler.";

        var projectDescription = CoreModel.getDefault().getProjectDescription(project);
        var configDescription = projectDescription.getActiveConfiguration();
        var configuration = ManagedBuildManager.getConfigurationForDescription(configDescription);

        return Arrays.stream(configuration.getTools())
            .filter(t -> t.getId().contains(toolId))
            .findFirst();
    }

    /**
     * If given {@command} exists in the GNU IDE installation path, then expand command to include
     * an absolute path to the tool.
     */
    private IPath expandCommand(IPath command)
    {
        assert command != null;

        /* Do nothing if command is already an absolute path. */
        if (!command.isAbsolute()) {
            var osCommand = CommandInfo.normalizeCommand(command.toOSString());
            var predefinedPath = CommandInfo.getGnuIdeBinPath().resolve(osCommand);
            if (Files.isExecutable(predefinedPath)) {
                return new Path(predefinedPath.toString());
            }
        }
        return command;
    }
}
