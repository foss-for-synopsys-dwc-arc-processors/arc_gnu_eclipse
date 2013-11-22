/*    */ package org.eclipse.cdt.cross.arc.gnu.devkitpro.windows;
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
/* 12 */   private static String ms_sBinSourcery = null;
/*    */   private static final String REGISTRY_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\devkitPro XXX";
/*    */   private static final String PATH_NAME = "InstallLocation";
/*    */   private static final String DELIMITER_WIN = ";";
/*    */ 
/*    */   public String[] resolveBuildPaths(int pathType, String variableName, String variableValue, IConfiguration configuration)
/*    */   {
/* 23 */     System.out.println(PathResolver.class.getName() + 
/* 24 */       " resolveBuildPaths()");
/* 25 */     return variableValue.split(";");
/*    */   }
/*    */ 
/*    */   public static String getBinPath() {
/* 29 */     if (!ms_bChecked)
/* 30 */       checkRegistry();
/* 31 */     return ms_sBinSourcery;
/*    */   }
/*    */ 
/*    */   private static synchronized void checkRegistry()
/*    */   {
/* 40 */     if (ms_bChecked) {
/* 41 */       return;
/*    */     }
/* 43 */     ms_sBinSourcery = null;
/* 44 */     if (!Tools.isWindows()) {
/* 45 */       return;
/*    */     }
/*    */ 
/* 48 */     String sInstallDir = Tools.getLocalMachineValue("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\devkitPro XXX", "InstallLocation");
/* 49 */     if (sInstallDir != null)
/*    */     {
/* 52 */       String sToolPath = sInstallDir + "\\bin";
/* 53 */       File oDir = new File(sToolPath);
/* 54 */       if ((oDir.exists()) && (oDir.isDirectory()))
/*    */       {
/* 57 */         ms_sBinSourcery = sToolPath;
/*    */       }
/*    */     }
/* 60 */     ms_bChecked = true;
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.devkitpro.windows.PathResolver
 * JD-Core Version:    0.6.2
 */