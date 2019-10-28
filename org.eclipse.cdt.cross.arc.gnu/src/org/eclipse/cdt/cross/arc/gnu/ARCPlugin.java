/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/

package org.eclipse.cdt.cross.arc.gnu;

import org.eclipse.cdt.cross.arc.gnu.common.StateListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

 public class ARCPlugin extends Plugin
 {
   public static final String PLUGIN_ID = "org.eclipse.cdt.cross.arc.gnu";
   private static ARCPlugin m_oPlugin;

   public static String safeVariableExpansion(String expression) {
     IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
     try {
       return manager.performStringSubstitution(expression);
     } catch (CoreException e) {
       StatusManager.getManager().handle(e, PLUGIN_ID);
       return expression;
     }
   }

   public void start(BundleContext oContext)
     throws Exception
   {
     super.start(oContext);
     m_oPlugin = this;
     ResourcesPlugin.getWorkspace().addResourceChangeListener(StateListener.getInstance());
   }
 
   public void stop(BundleContext oContext) throws Exception {
     m_oPlugin = null;
     super.stop(oContext);
   }
 
   public static ARCPlugin getDefault() {
     return m_oPlugin;
   }
 
   public void log(IStatus oStatus) {
     ILog oLog = getLog();
     if (oStatus.getSeverity() >= 2) {
       oLog.log(oStatus);
     }
     if (isDebugging()) {
       System.err.print("org.eclipse.cdt.cross.arc.gnu: " + oStatus.getMessage());
       if (oStatus.getCode() != 0) {
         System.err.print("(" + oStatus.getCode() + ")");
       }
       System.out.println("");
       if (oStatus.getException() != null)
         oStatus.getException().printStackTrace();
     }
   }
 
   public static void log(String sMsg, Exception oException)
   {
     getDefault().getLog().log(
       new Status(4, "org.eclipse.cdt.cross.arc.gnu", sMsg, 
       oException));
   }
 }
