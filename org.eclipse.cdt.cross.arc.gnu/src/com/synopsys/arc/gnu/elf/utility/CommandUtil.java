// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.utility;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.cdt.utils.PathUtil;
import org.eclipse.core.runtime.Platform;

public final class CommandUtil
{
    /**
     * Return a path to the {@code bin} folder that is the sibling of the {@code eclipse} folder.
     * This is the predefined location to search for ARC GNU tools.
     */
    public static Path getGnuIdeBinPath()
    {
        try {
            var eclipseHome = Platform.getInstallLocation().getURL().toURI();
            return Path.of(eclipseHome).getParent().resolve("bin");
        } catch (URISyntaxException e) {
            return Path.of(Platform.getLocation().toOSString()).getParent().resolve("bin");
        }
    }

    /**
     * Determine whether given {@code command} is a valid command in the current environment.
     */
    public static boolean isValidCommand(String command)
    {
        return resolveCommand(command).isPresent();
    }

    /**
     * Return a valid path to a GNU tool with the specified {@code command}.
     *
     * @return
     *         <ol>
     *         <li>{@code command} if it is an absolute path and file exists;</li>
     *         <li>an absolute path to a command if it exists in the GNU IDE installation path;</li>
     *         <li>{@code command} if it exists in the {@code PATH}.
     *         </ol>
     *         An {@code .exe} extension is appended on Windows, unless it is already present in
     *         {@code command}.
     * @see PathUtil#findProgramLocation(String, String)
     */
    public static Optional<Path> resolveCommand(String command)
    {
        if (command == null) {
            return Optional.empty();
        }

        // If path is absolute then there is no need to resolve it further. But if it doesn't exist,
        // then null should be returned.
        var commandPath = Path.of(command);
        if (commandPath.isAbsolute()) {
            return Optional.of(commandPath).filter(Files::isExecutable);
        }

        // Attempt to resolve the path, first via relative path, then in the PATH.
        var cmd = Optional.of(command).map(CommandUtil::appendExe).map(Path::of);

        return cmd
            .map(getGnuIdeBinPath()::resolve)
            .filter(Files::isExecutable)
            .or(() -> cmd.filter(CommandUtil::isInSystemPath));
    }

    /**
     * Add {@code .exe} file extension on Windows hosts if it is not already present in
     * {@code command}.
     *
     * @param command The path to an executable, can be absolute or relative.
     */
    private static String appendExe(String command)
    {
        if (command != null
            && Platform.WS_WIN32.equals(Platform.getOS())
            && !command.endsWith(".exe")) {
            return command + ".exe";
        } else {
            return command;
        }
    }

    private static boolean isInSystemPath(Path exeCommand)
    {
        return exeCommand != null && PathUtil.findProgramLocation(exeCommand.toString()) != null;
    }

    private CommandUtil()
    {
    }
}
