/*     */ package org.eclipse.cdt.cross.arc.gnu;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
/*     */ import org.eclipse.cdt.managedbuilder.core.IConfiguration;
/*     */ import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
/*     */ import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
/*     */ import org.eclipse.cdt.managedbuilder.gnu.ui.GnuUIPlugin;
/*     */ import org.eclipse.cdt.utils.WindowsRegistry;
/*     */ import org.eclipse.cdt.utils.spawner.ProcessFactory;
/*     */ import org.eclipse.core.runtime.Status;
/*     */ 
/*     */ public class Tools
/*     */ {
/*     */   private static final String PROPERTY_OS_NAME = "os.name";
/*     */   public static final String PROPERTY_OS_VALUE_WINDOWS = "windows";
/*     */   public static final String PROPERTY_OS_VALUE_LINUX = "linux";
/*     */ 
/*     */   public static boolean isPlatform(String sPlatform)
/*     */   {
/*  25 */     return System.getProperty("os.name").toLowerCase()
/*  26 */       .startsWith(sPlatform);
/*     */   }
/*     */ 
/*     */   public static boolean isWindows() {
/*  30 */     return System.getProperty("os.name").toLowerCase()
/*  31 */       .startsWith("windows");
/*     */   }
/*     */ 
/*     */   public static boolean isLinux() {
/*  35 */     return System.getProperty("os.name").toLowerCase()
/*  36 */       .startsWith("linux");
/*     */   }
/*     */ 
/*     */ 
/*     */   public static String getManualInstallPath(String check) {
/*  45 */     String installPath = null;
/*  46 */     if ((check == null) || (check.isEmpty()))
/*  47 */       return installPath;
/*     */     try
/*     */     {
/*  50 */       String sysPath = null;
/*  51 */       sysPath = System.getenv("PATH");
/*     */ 
/*  53 */       String delim = System.getProperty("path.separator");
/*  54 */       if ((delim != null) && (delim.length() > 0) && (sysPath != null) && (sysPath.length() > 0)) {
/*  55 */         String[] paths = sysPath.split(delim);
/*  56 */         if ((paths != null) && (paths.length > 0))
/*  57 */           for (String p : paths)
/*     */           {
/*  59 */             if (p.contains(check)) {
/*  60 */               int start = p.indexOf(check);
/*  61 */               installPath = p.substring(0, start + check.length());
/*  62 */               GnuUIPlugin.getDefault();
/*  63 */               GnuUIPlugin.getDefault().log(new Status(0, "org.eclipse.cdt.cross.arc.gnu", "getManualInstallPath(): " + installPath));
/*  64 */               break;
/*     */             }
/*     */           }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*  70 */       GnuUIPlugin.getDefault().log(e);
/*     */     }
/*  72 */     return installPath;
/*     */   }
/*     */ 
/*     */   public static String[] exec(String cmd, IConfiguration cfg, String sBinPath)
/*     */   {
/*     */     try
/*     */     {
/*  79 */       IEnvironmentVariable[] vars = 
/*  80 */         ManagedBuildManager.getEnvironmentVariableProvider().getVariables(cfg, true);
/*  81 */       String[] env = new String[vars.length];
/*  82 */       for (int i = 0; i < env.length; i++) {
/*  83 */         env[i] = (vars[i].getName() + "=");
/*  84 */         String value = vars[i].getValue();
/*  85 */         if (value != null)
/*     */         {
/*     */           int tmp76_74 = i;
/*     */           String[] tmp76_72 = env; tmp76_72[tmp76_74] = (tmp76_72[tmp76_74] + value);
/*     */         }
/*     */       }
/*  88 */       Process proc = ProcessFactory.getFactory().exec(cmd.split(" "), env);
/*  89 */       if (proc != null)
/*     */       {
/*  91 */         InputStream ein = proc.getInputStream();
/*  92 */         BufferedReader d1 = new BufferedReader(new InputStreamReader(
/*  93 */           ein));
/*  94 */         ArrayList ls = new ArrayList(10);
/*     */         String s;
/*  96 */         while ((s = d1.readLine()) != null)
/*     */         {
/*     */           //yunluz comment String s;
/*  97 */           ls.add(s);
/*     */         }
/*  99 */         ein.close();
/* 100 */         return (String[])ls.toArray(new String[0]);
/*     */       }
/*     */     } catch (IOException e) {
/* 103 */       GnuUIPlugin.getDefault().log(e);
/*     */     }
/* 105 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getLocalMachineValue(String sKey, String sName) {
/* 109 */     WindowsRegistry registry = WindowsRegistry.getRegistry();
/* 110 */     if (registry != null)
/*     */     {
/* 112 */       String s = registry.getLocalMachineValue(sKey, sName);
/*     */ 
/* 114 */       if (s != null) {
/* 115 */         return s;
/*     */       }
/*     */     }
/* 118 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.Tools
 * JD-Core Version:    0.6.2
 */