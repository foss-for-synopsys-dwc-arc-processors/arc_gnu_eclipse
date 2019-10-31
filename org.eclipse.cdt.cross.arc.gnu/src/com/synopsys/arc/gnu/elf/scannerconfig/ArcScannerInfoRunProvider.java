// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.scannerconfig;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.cross.arc.gnu.common.CommandInfo;
import org.eclipse.cdt.make.internal.core.scannerconfig2.GCCSpecsRunSIProvider;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.utils.CommandLineUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.synopsys.arc.gnu.elf.ArcGnuElfPlugin;

@SuppressWarnings("restriction")
public final class ArcScannerInfoRunProvider extends GCCSpecsRunSIProvider
{
    @Override
    protected boolean initialize()
    {
        if (!super.initialize()) {
            return false;
        }

        var tool = getCompilerTool(this.resource.getProject());
        this.fCompileCommand = tool.map(ITool::getToolCommand)
            .flatMap(CommandInfo::resolveCommand)
            .<IPath>map(p -> new Path(p.toString()))
            .orElse(this.fCompileCommand);
        this.fCompileArguments = Stream.concat(
            Arrays.stream(this.fCompileArguments),
            getCompilerOptions(tool.orElse(null)))
            .toArray(String[]::new);

        return true;
    }

    /**
     * Return a stream of the architecture specific options of the compiler selected in the project.
     *
     * @param compilerTool The compiler description, that will be used to generate command line
     *        options. May be null.
     */
    private Stream<String> getCompilerOptions(ITool compilerTool)
    {
        if (compilerTool == null) {
            return Stream.empty();
        }

        try {
            // Input and output files can be null or sometimes empty strings - generated command
            // line isn't used directly to invoke compiler, we just need some of the options.
            var cmdGenerator = compilerTool.getCommandLineGenerator();
            var cmdInfo = cmdGenerator.generateCommandLineInfo(
                compilerTool,
                compilerTool.getToolCommand(),
                compilerTool.getToolCommandFlags(null, null),
                compilerTool.getOutputFlag(),
                compilerTool.getOutputPrefix(),
                "",
                null,
                compilerTool.getCommandLinePattern());

            // Filter out options that don't affect code generation.
            return Arrays.stream(CommandLineUtil.argumentsToArray(cmdInfo.getFlags()))
                .filter(opt -> opt.startsWith("-m") || opt.startsWith("-f"));
        } catch (BuildException err) {
            err.printStackTrace();
            return Stream.empty();
        }
    }

    private Optional<ITool> getCompilerTool(IProject project)
    {
        boolean isCPlusPlus = false;
        if (project.exists() && project.isOpen()) {
            try {
                isCPlusPlus = project.hasNature(CCProjectNature.CC_NATURE_ID);
            } catch (CoreException err) {
                ArcGnuElfPlugin.getDefault().logError("Failed to get project nature.", err);
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
}
