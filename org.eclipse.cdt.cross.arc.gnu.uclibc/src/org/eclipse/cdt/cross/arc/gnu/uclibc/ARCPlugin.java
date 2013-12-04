package org.eclipse.cdt.cross.arc.gnu.uclibc;

import java.io.PrintStream;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.osgi.framework.BundleContext;

public class ARCPlugin extends Plugin
{
  public static final String PLUGIN_ID = "org.eclipse.cdt.cross.arc.gnu";
  public static final String DEFAULT_LOG = "ARC Eclipse Plugin Log";
  private static ARCPlugin m_oPlugin;

  public void start(BundleContext oContext)
    throws Exception
  {
    super.start(oContext);
    m_oPlugin = this;
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

  public MessageConsole getDefaultConsole() {
    return getConsole("ARC Eclipse Plugin Log");
  }

  public MessageConsole getConsole(String sName)
  {
    IConsoleManager oConMan = ConsolePlugin.getDefault().getConsoleManager();
    IConsole[] aoConsoles = oConMan.getConsoles();
    for (IConsole oConsole : aoConsoles) {
      if (oConsole.getName().equals(sName)) {
        return (MessageConsole)oConsole;
      }
    }

    MessageConsole oNewConsole = new MessageConsole(sName, null);
    oConMan.addConsoles(new IConsole[] { oNewConsole });
    return oNewConsole;
  }
}

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.ARMPlugin
 * JD-Core Version:    0.6.2
 */
