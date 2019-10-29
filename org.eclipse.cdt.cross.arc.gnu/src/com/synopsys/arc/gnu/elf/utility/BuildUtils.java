// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.utility;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;

import com.synopsys.arc.gnu.elf.ArcGnuElfPlugin;

public final class BuildUtils
{
    private static final String ARC_TARGET_CATEGORY = "arc.gnu.elf.category.target";
    private static final String ARCEM_CPU_OPTION = "arc.gnu.elf.option.target.cpuem";
    private static final String ARCHS_CPU_OPTION = "arc.gnu.elf.option.target.cpuhs";
    private static final String ARC700_CPU_OPTION = "arc.gnu.elf.option.target.cpu700";
    private static final String ARC600_CPU_OPTION = "arc.gnu.elf.option.target.cpu600";

    /**
     * Return GCC option that represents current CPU choice, or {@link Optional#empty()} in case of
     * failure.
     */
    public static Optional<String> getCurrentCpu(
        IBuildObject configuration,
        IHoldsOptions optionHolder)
    {
        if (configuration == null || optionHolder == null) {
            return Optional.empty();
        }

        /* Out of four CPU options, only one will be visible at a time. */
        return Arrays.stream(optionHolder.getOptions())
            .filter(BuildUtils::isCpuOption)
            .filter(option -> isOptionVisible(configuration, optionHolder, option))
            .findAny()
            .map(o -> {
                try {
                    return o.getEnumCommand(o.getSelectedEnum());
                } catch (BuildException err) {
                    ArcGnuElfPlugin.getDefault()
                        .logError("Failed to get current CPU of the toolchain.", err);
                    return null;
                }
            });
    }

    /**
     * Return a toolchain that owns the given tool.
     *
     * @param tool The tool for which to return the owning toolchain.
     * @return An {@link Optional} with the parent toolchain of the {code tool} or
     *         {@link Optional#empty()} if tool is not owned by a toolchain.
     */
    public static Optional<IToolChain> getParentToolchain(Optional<ITool> tool)
    {
        return tool.map(ITool::getParent)
            .filter(parent -> parent instanceof IToolChain)
            .map(parent -> (IToolChain) parent);
    }

    /**
     * A predicate whether the given {@code option} is an ARC "Target" option and should be passed
     * to the GCC as an {@code -m} flag.
     *
     * @param option The option for which to perform the check.
     * @return {@code true} if option is not null and is owned by the ARC Target category.
     */
    public static boolean isArcTargetOption(IOption option)
    {
        return option != null && option.getCategory().getBaseId().equals(ARC_TARGET_CATEGORY);
    }

    /**
     * Return true if give {@code option} is one of the valid CPU options.
     */
    public static boolean isCpuOption(IOption option)
    {
        if (option == null) {
            return false;
        }

        return option.getBaseId().equals(ARCEM_CPU_OPTION)
            || option.getBaseId().equals(ARCHS_CPU_OPTION)
            || option.getBaseId().equals(ARC700_CPU_OPTION)
            || option.getBaseId().equals(ARC600_CPU_OPTION);
    }

    /**
     * Whether the given {@code option} is visible in the given context.
     */
    public static boolean isOptionVisible(
        IBuildObject configuration,
        IHoldsOptions holder,
        IOption option)
    {
        if (configuration == null || holder == null || option == null) {
            return false;
        }

        return option.getApplicabilityCalculator().isOptionVisible(configuration, holder, option);
    }

    /**
     * Whether the given {@code option} should be used in command line of the given context.
     */
    public static boolean isOptionUsedInCommandLine(
        IBuildObject configuration,
        IHoldsOptions holder,
        IOption option)
    {
        if (configuration == null || holder == null || option == null) {
            return false;
        }

        return option.getApplicabilityCalculator()
            .isOptionUsedInCommandLine(configuration, holder, option);
    }

    /* Static class. */
    private BuildUtils()
    {
    }
}
