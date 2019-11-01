/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.cdt.toolchain.tcf;

import java.nio.file.Path;
import java.text.MessageFormat;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;

import com.arc.cdt.toolchain.ArcCpu;
import com.synopsys.arc.gnu.elf.ArcGnuElfPlugin;
import com.synopsys.arc.gnu.elf.utility.BuildUtils;

public class TcfValueHandler implements IManagedOptionValueHandler
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

            var tcfArch = tcfContent.getCpuFamily();
            var expectedArch = BuildUtils.getCurrentCpu(configuration, holder)
                .map(ArcCpu::fromCommand)
                .map(ArcCpu::getToolChain);
            if (!tcfArch.get().equals(expectedArch.get())) {
                ArcGnuElfPlugin.getDefault()
                    .showError(MessageFormat.format(
                        "TCF describes {} architecture, but selected tool chain is for {}.",
                        tcfArch,
                        expectedArch));
                return false;
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
