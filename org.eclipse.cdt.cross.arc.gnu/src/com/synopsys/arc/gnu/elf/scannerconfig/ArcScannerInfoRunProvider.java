// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.scannerconfig;

import java.nio.file.Files;

import org.eclipse.cdt.cross.arc.gnu.common.CommandInfo;
import org.eclipse.cdt.make.internal.core.scannerconfig2.GCCSpecsRunSIProvider;
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

        fCompileCommand = expandCommand(fCompileCommand);

        return true;
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
