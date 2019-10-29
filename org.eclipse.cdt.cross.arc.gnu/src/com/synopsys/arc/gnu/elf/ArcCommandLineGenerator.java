// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf;

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
            .map(this::getTargetFlags)
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

    private Stream<String> getTargetFlags(IToolChain toolchain)
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
}
