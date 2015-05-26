/*******************************************************************************
 * Copyright (c) 2005-2012 Synopsys, Incorporated
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Synopsys, Inc - Initial implementation 
 *******************************************************************************/
package org.eclipse.cdt.cross.arc.gnu.common;

import java.io.File;

import org.eclipse.core.runtime.Platform;


/**
 * A method for determining if a command exists under JRE 1.5 or later.
 * <P>
 * CUSTOMIZATION
 * <P>
 * @author David Pickens
 */
public class CommandInfo {

    /**
     * Return whether or not a command exists.
     * <P>
     * Called to determine if a toolchain is supported.
     * @param cmd the command
     * @return whether or not a command exists.
     */
    public static boolean commandExists (String cmd) {
        // There may be arguments so only grab up to the whitespace
        if (cmd.indexOf(' ') > 0) {
            cmd = cmd.substring(0, cmd.indexOf(' '));
        }
        if (isWindows() && !cmd.toLowerCase().endsWith(".exe"))
            cmd = cmd + ".exe";
        File f = new File(cmd);
        if (f.isAbsolute())
            return f.exists();
        //Checking for compiler presence in PATH,
        String path = System.getenv("PATH");
        //Checking for compiler presence in location ../bin? Relative to eclipse.exe. So IDE releases will work even when PATH is not configured
        String eclipsehome= Platform.getInstallLocation().getURL().toString();
		eclipsehome=eclipsehome.substring(eclipsehome.lastIndexOf("file:/")+6, eclipsehome.length());
		File predefined_path_dir = new File(eclipsehome).getParentFile();
        String predefined_path=predefined_path_dir+"\\bin";

        path=predefined_path+";"+path;
        if (path == null)
            return true; // punt
        String paths[] = path.split(File.pathSeparator);
        for (String p : paths) {
            if (new File(p, cmd).isFile())
                return true;
        }
        return false;
    }
    
    /**
     * Determine whether or not we're running on Microsoft Windows.
     * @return true if we're running under Microsoft Windows.
     */
    public static boolean isWindows(){
        return System.getProperty("os.name").indexOf("indow") > 0;
    }

}
