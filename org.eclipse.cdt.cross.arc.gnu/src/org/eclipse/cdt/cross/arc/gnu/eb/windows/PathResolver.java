/*    */ package org.eclipse.cdt.cross.arc.gnu.eb.windows;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.PrintStream;

/*    */ import org.eclipse.cdt.cross.arc.gnu.Tools;
/*    */ import org.eclipse.cdt.managedbuilder.core.IBuildPathResolver;
/*    */ import org.eclipse.cdt.managedbuilder.core.IConfiguration;
/*    */ 
/*    */ public class PathResolver
/*    */   implements IBuildPathResolver
/*    */ {
/* 11 */   private static boolean ms_bChecked = false;
/* 12 */   private static String ms_sBinPath = null;
/*    */   private static final String REGISTRY_KEY = "SOFTWARE\\GNUARC\\4.8.1";
/*    */   private static final String PATH_NAME = "InstallLocation";
/*    */   private static final String DELIMITER_WIN = ";";
/*    */ 
/*    */   public String[] resolveBuildPaths(int pathType, String variableName, String variableValue, IConfiguration configuration)
/*    */   {
/* 24 */     System.out.println(PathResolver.class.getName() + 
/* 25 */       " resolveBuildPaths()");
/* 26 */     return variableValue.split(";");
/*    */   }
/*    */ 
/*    */   public static String getBinPath() {
/* 30 */     if (!ms_bChecked)
/* 31 */       checkRegistry();
/* 32 */     return ms_sBinPath;
/*    */   }
/*    */ 
/*    */   private static synchronized void checkRegistry()
/*    */   {
/* 41 */     if (ms_bChecked) {
/* 42 */       return;
/*    */     }
/* 44 */     ms_sBinPath = null;
/* 45 */     if (!Tools.isWindows()) {
/* 46 */       return;
/*    */     }
/*    */ 
/* 49 */     String sInstallDir = Tools.getLocalMachineValue("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Yagarto XXXX", "InstallLocation");
/* 50 */     if (sInstallDir != null)
/*    */     {
/* 53 */       String sToolPath = sInstallDir + "\\bin";
/* 54 */       File oDir = new File(sToolPath);
/* 55 */       if ((oDir.exists()) && (oDir.isDirectory()))
/*    */       {
/* 58 */         ms_sBinPath = sToolPath;
/*    */       }
/*    */     }
/* 61 */     ms_bChecked = true;
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\org.eclipse.cdt.cross.arm.gnu_0.5.5.201309281715\plugins\org.eclipse.cdt.cross.arm.gnu_0.5.5.201309281715\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.yagarto.windows.PathResolver
 * JD-Core Version:    0.6.0
 */