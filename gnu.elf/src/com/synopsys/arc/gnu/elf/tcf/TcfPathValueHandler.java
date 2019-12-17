// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.tcf;

import java.nio.file.Path;
import java.text.MessageFormat;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;

import com.synopsys.arc.gnu.elf.ArcGnuElfPlugin;
import com.synopsys.arc.gnu.elf.utility.BuildUtils;

public class TcfPathValueHandler implements IManagedOptionValueHandler
{
    @Override
    public boolean handleValue(
        IBuildObject configuration,
        IHoldsOptions holder,
        IOption option,
        String extraArgument,
        int event)
    {
        if (event == IManagedOptionValueHandler.EVENT_APPLY
            && option.getApplicabilityCalculator().isOptionEnabled(configuration, holder, option)) {

            TcfContent tcfContent;
            try {
                var tcfPath = ArcGnuElfPlugin.safeVariableExpansion(option.getStringValue());
                tcfContent = TcfContent.readFile(Path.of(tcfPath));
            } catch (TcfContentException | BuildException err) {
                ArcGnuElfPlugin.getDefault().showError("Failed to parse TCF.", err);
                return false;
            }

            var tcfArch = tcfContent.getCpuFamily().get();
            var expectedArch = BuildUtils.getCurrentCpu(configuration, holder)
                .flatMap(ArcCpuFamily::fromMcpuOption)
                .get();
            if (!tcfArch.equals(expectedArch)) {
                ArcGnuElfPlugin.getDefault()
                    .showError(MessageFormat.format(
                        "TCF describes {0} architecture, but selected tool chain is for {1}.",
                        tcfArch,
                        expectedArch));
                return false;
            }

            // Rebuild the target.
            if (holder instanceof IToolChain) {
                ((IToolChain) holder).getParent().setRebuildState(true);
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean isDefaultValue(
        IBuildObject configuration,
        IHoldsOptions holder,
        IOption option,
        String extraArgument)
    {
        return false;
    }

    @Override
    public boolean isEnumValueAppropriate(
        IBuildObject configuration,
        IHoldsOptions holder,
        IOption option,
        String extraArgument,
        String enumValue)
    {
        return false;
    }
}
