/*    */ package org.eclipse.cdt.cross.arc.gnu.windows;
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
/* 12 */   private static String ms_sBinGNUARC = null;
/*    */   private static final String REGISTRY_KEY = "SOFTWARE\\GNUARC\\4.8.1";
/*    */   private static final String PATH_NAME = "InstallPath";
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
/* 31 */     return ms_sBinGNUARC;
/*    */   }
/*    */ 
/*    */   private static synchronized void checkRegistry()
/*    */   {
/* 40 */     if (ms_bChecked) {
/* 41 */       return;
/*    */     }
/* 43 */     ms_sBinGNUARC = null;
/* 44 */     if (!Tools.isWindows()) {
/* 45 */       return;
/*    */     }
/*    */ 
/* 48 */     String sInstallDir = Tools.getLocalMachineValue("SOFTWARE\\GNUARC\\4.1.1", "InstallPath");
/* 49 */     if (sInstallDir != null)
/*    */     {
/* 52 */       String sToolPath = sInstallDir + "\\bin";
/* 53 */       File oDir = new File(sToolPath);
/* 54 */       if ((oDir.exists()) && (oDir.isDirectory()))
/*    */       {
/* 57 */         ms_sBinGNUARC = sToolPath;
/*    */       }
/*    */     }
/* 60 */     ms_bChecked = true;
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.windows.PathResolver
 * JD-Core Version:    0.6.2
 */