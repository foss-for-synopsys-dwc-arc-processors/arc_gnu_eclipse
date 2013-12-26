 /*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/

package org.eclipse.cdt.cross.arc.gnu.eb.windows;

import java.io.File;

import org.eclipse.cdt.cross.arc.gnu.Tools;
import org.eclipse.cdt.managedbuilder.core.IBuildPathResolver;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;

 public class PathResolver
   implements IBuildPathResolver
 {
   private static boolean ms_bChecked = false;
   private static String ms_sBinPath = null;
   private static final String REGISTRY_KEY = "SOFTWARE\\GNUARC\\4.8.1";
   private static final String PATH_NAME = "InstallLocation";
   private static final String DELIMITER_WIN = ";";
 
   public String[] resolveBuildPaths(int pathType, String variableName, String variableValue, IConfiguration configuration)
   {
     System.out.println(PathResolver.class.getName() + 
       " resolveBuildPaths()");
     return variableValue.split(";");
   }
 
   public static String getBinPath() {
     if (!ms_bChecked)
       checkRegistry();
     return ms_sBinPath;
   }
 
   private static synchronized void checkRegistry()
   {
     if (ms_bChecked) {
       return;
     }
     ms_sBinPath = null;
     if (!Tools.isWindows()) {
       return;
     }
 
     String sInstallDir = Tools.getLocalMachineValue("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\ XXXX", "InstallLocation");
     if (sInstallDir != null)
     {
       String sToolPath = sInstallDir + "\\bin";
       File oDir = new File(sToolPath);
       if ((oDir.exists()) && (oDir.isDirectory()))
       {
         ms_sBinPath = sToolPath;
       }
     }
     ms_bChecked = true;
   }
 }

