// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf;

import java.util.Arrays;

import org.eclipse.cdt.cross.arc.gnu.common.CommandInfo;
import org.eclipse.cdt.managedbuilder.core.IManagedIsToolChainSupported;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.osgi.framework.Version;

public final class IsToolchainSupported implements IManagedIsToolChainSupported
{
    @Override
    public boolean isSupported(IToolChain toolChain, Version version, String instance)
    {
        // Toolchain is supported if all the tools are present.
        return Arrays.stream(toolChain.getTools())
            .map(ITool::getToolCommand)
            .allMatch(CommandInfo::isValidCommand);
    }
}
