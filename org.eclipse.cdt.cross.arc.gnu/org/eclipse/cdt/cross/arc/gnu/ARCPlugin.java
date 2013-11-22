/*    */ package org.eclipse.cdt.cross.arc.gnu;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.eclipse.core.runtime.ILog;
/*    */ import org.eclipse.core.runtime.IStatus;
/*    */ import org.eclipse.core.runtime.Plugin;
/*    */ import org.eclipse.core.runtime.Status;
/*    */ import org.eclipse.ui.console.ConsolePlugin;
/*    */ import org.eclipse.ui.console.IConsole;
/*    */ import org.eclipse.ui.console.IConsoleManager;
/*    */ import org.eclipse.ui.console.MessageConsole;
/*    */ import org.osgi.framework.BundleContext;
/*    */ 
/*    */ public class ARCPlugin extends Plugin
/*    */ {
/*    */   public static final String PLUGIN_ID = "org.eclipse.cdt.cross.arc.gnu";
/*    */   public static final String DEFAULT_LOG = "ARC Eclipse Plugin Log";
/*    */   private static ARCPlugin m_oPlugin;
/*    */ 
/*    */   public void start(BundleContext oContext)
/*    */     throws Exception
/*    */   {
/* 33 */     super.start(oContext);
/* 34 */     m_oPlugin = this;
/*    */   }
/*    */ 
/*    */   public void stop(BundleContext oContext) throws Exception {
/* 38 */     m_oPlugin = null;
/* 39 */     super.stop(oContext);
/*    */   }
/*    */ 
/*    */   public static ARCPlugin getDefault() {
/* 43 */     return m_oPlugin;
/*    */   }
/*    */ 
/*    */   public void log(IStatus oStatus) {
/* 47 */     ILog oLog = getLog();
/* 48 */     if (oStatus.getSeverity() >= 2) {
/* 49 */       oLog.log(oStatus);
/*    */     }
/* 51 */     if (isDebugging()) {
/* 52 */       System.err.print("org.eclipse.cdt.cross.arc.gnu: " + oStatus.getMessage());
/* 53 */       if (oStatus.getCode() != 0) {
/* 54 */         System.err.print("(" + oStatus.getCode() + ")");
/*    */       }
/* 56 */       System.out.println("");
/* 57 */       if (oStatus.getException() != null)
/* 58 */         oStatus.getException().printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void log(String sMsg, Exception oException)
/*    */   {
/* 64 */     getDefault().getLog().log(
/* 65 */       new Status(4, "org.eclipse.cdt.cross.arc.gnu", sMsg, 
/* 66 */       oException));
/*    */   }
/*    */ 
/*    */   public MessageConsole getDefaultConsole() {
/* 70 */     return getConsole("ARC Eclipse Plugin Log");
/*    */   }
/*    */ 
/*    */   public MessageConsole getConsole(String sName)
/*    */   {
/* 76 */     IConsoleManager oConMan = ConsolePlugin.getDefault().getConsoleManager();
/* 77 */     IConsole[] aoConsoles = oConMan.getConsoles();
/* 78 */     for (IConsole oConsole : aoConsoles) {
/* 79 */       if (oConsole.getName().equals(sName)) {
/* 80 */         return (MessageConsole)oConsole;
/*    */       }
/*    */     }
/*    */ 
/* 84 */     MessageConsole oNewConsole = new MessageConsole(sName, null);
/* 85 */     oConMan.addConsoles(new IConsole[] { oNewConsole });
/* 86 */     return oNewConsole;
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.ARMPlugin
 * JD-Core Version:    0.6.2
 */