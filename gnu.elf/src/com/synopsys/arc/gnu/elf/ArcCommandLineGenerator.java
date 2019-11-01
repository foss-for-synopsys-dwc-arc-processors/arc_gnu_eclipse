// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineGenerator;
import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineGenerator;

import com.synopsys.arc.gnu.elf.tcf.TcfContent;
import com.synopsys.arc.gnu.elf.tcf.TcfContentException;
import com.synopsys.arc.gnu.elf.utility.BuildUtils;

@SuppressWarnings("restriction")
public final class ArcCommandLineGenerator implements IManagedCommandLineGenerator
{
    private final ManagedCommandLineGenerator parent = new ManagedCommandLineGenerator() {};

    @Override
    public IManagedCommandLineInfo generateCommandLineInfo(
        ITool tool,
        String commandName,
        String[] flags,
        String outputFlag,
        String outputPrefix,
        String outputName,
        String[] inputResources,
        String commandLinePattern)
    {
        var newFlags = BuildUtils.getParentToolchain(Optional.of(tool))
            .map(toolchain -> getTargetFlags(tool, toolchain))
            .map(options -> Stream.concat(options, Arrays.stream(flags)))
            .map(options -> options.toArray(String[]::new));
        newFlags.map(Arrays::toString)
            .ifPresent(ArcGnuElfPlugin.getDefault()::log);

        return parent.generateCommandLineInfo(
            tool,
            commandName,
            newFlags.orElse(flags),
            outputFlag,
            outputPrefix,
            outputName,
            inputResources,
            commandLinePattern);
    }

    private Stream<String> getTargetFlags(ITool tool, IToolChain toolchain)
    {
        var tcfPath = BuildUtils.getTcfPath(toolchain);
        if (tcfPath.isPresent()) {
            return getTcfFlags(tool, toolchain, tcfPath.get());
        }

        return getProjectFlags(toolchain);
    }

    private Stream<String> getProjectFlags(IToolChain toolchain)
    {
        IConfiguration configuration = toolchain.getParent();
        return Arrays.stream(toolchain.getOptions())
            .filter(BuildUtils::isArcTargetOption)
            .filter(opt -> BuildUtils.isOptionVisible(configuration, toolchain, opt))
            .filter(opt -> BuildUtils.isOptionUsedInCommandLine(configuration, toolchain, opt))
            .map(option -> {
                try {
                    if (IOption.BOOLEAN == option.getBasicValueType()
                        && !option.getBooleanValue()) {
                        return option.getCommandFalse();
                    } else if (IOption.ENUMERATED == option.getBasicValueType()) {
                        return option.getEnumCommand(option.getStringValue());
                    } else {
                        return option.getCommand();
                    }
                } catch (BuildException err) {
                    ArcGnuElfPlugin.getDefault()
                        .showError("Failed to handle toolchain option.", err);
                    return "";
                }
            })
            .filter(s -> !(s == null || s.isBlank()));
    }

    /**
     * Read GCC options from the TCF.
     */
    private Stream<String> getTcfFlags(
        ITool tool,
        IToolChain toolchain,
        Path tcfPath)
    {
        Optional<TcfContent> tcf;
        try {
            tcf = Optional.of(TcfContent.readFile(tcfPath));
        } catch (TcfContentException err) {
            ArcGnuElfPlugin.getDefault().showError("Failed to parse TCF.", err);
            return Stream.empty();
        }

        var compilerOptions = tcf
            .map(TcfContent::getGccOptions)
            .map(Arrays::stream)
            .orElse(Stream.empty())
            // Filter out endianness options.
            .filter(option -> !option.equals("-mlittle-endian"))
            .filter(option -> !option.equals("-mbig-endian"));

        var buildPath = BuildUtils.getProjectBuildPath(toolchain);
        if (buildPath == null) {
            return compilerOptions;
        }

        // C includes file.
        if ((BuildUtils.isCompilerTool(tool) || BuildUtils.isAssemblerTool(tool))
            && BuildUtils.useTcfCinclude(toolchain)) {
            compilerOptions = Stream.concat(compilerOptions,
                tcf.flatMap(
                    t -> BuildUtils.createTcfFile(t, TcfContent.C_DEFINES_SECTION, buildPath))
                    .map(path -> Stream.of("-include ", path.toString()))
                    .orElse(Stream.empty()));
        }
        // memory.x
        if (BuildUtils.isLinkerTool(tool) && BuildUtils.useTcfMemoryX(toolchain)) {
            compilerOptions = Stream.concat(compilerOptions,
                tcf.flatMap(
                    t -> BuildUtils.createTcfFile(t, TcfContent.LINKER_MEMORY_MAP_SECTION,
                        buildPath))
                    .map(path -> Stream.of("-Wl,-marcv2elfx -L ", path.getParent().toString()))
                    .orElse(Stream.empty()));
        }

        return compilerOptions;
    }
}
