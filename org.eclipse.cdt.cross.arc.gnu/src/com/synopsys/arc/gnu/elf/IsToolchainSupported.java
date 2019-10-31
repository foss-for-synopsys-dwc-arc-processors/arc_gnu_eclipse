// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf;

import org.eclipse.cdt.managedbuilder.core.IManagedIsToolChainSupported;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.osgi.framework.Version;

import com.synopsys.arc.gnu.elf.utility.CommandUtil;

public final class IsToolchainSupported implements IManagedIsToolChainSupported
{
    @Override
    public boolean isSupported(IToolChain toolChain, Version version, String instance)
    {
        // Toolchain is supported if all the tools are present. This code updates command if needed.
        for (var tool : toolChain.getTools()) {
            var command = tool.getToolCommand();
            var resolvedCommand = CommandUtil.resolveCommand(command);
            if (resolvedCommand.isEmpty()) {
                return false;
            }
            tool.setToolCommand(resolvedCommand.get().toString());
        }
        return true;
    }
}
