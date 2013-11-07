/*    */ package org.eclipse.cdt.cross.arc.gnu.windows;
/*    */ 
/*    */ import org.eclipse.cdt.cross.arc.gnu.Tools;
/*    */ import org.eclipse.cdt.managedbuilder.core.IConfiguration;
/*    */ import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
/*    */ import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
/*    */ import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
/*    */ import org.eclipse.cdt.managedbuilder.internal.envvar.BuildEnvVar;
/*    */ 
/*    */ public class ConfigurationEnvironmentSupplier
/*    */   implements IConfigurationEnvironmentVariableSupplier
/*    */ {
/*    */   static final String VARNAME_PATH = "PATH";
/*    */   static final String DELIMITER_UNIX = ":";
/*    */   static final String PROPERTY_DELIMITER = "path.separator";
/*    */ 
/*    */   public IBuildEnvironmentVariable getVariable(String variableName, IConfiguration configuration, IEnvironmentVariableProvider provider)
/*    */   {
/* 20 */     if (!Tools.isWindows()) {
/* 21 */       return null;
/*    */     }
/* 23 */     if (variableName == null) {
/* 24 */       return null;
/*    */     }
/* 26 */     if (!"PATH".equalsIgnoreCase(variableName)) {
/* 27 */       return null;
/*    */     }
/* 29 */     String p = PathResolver.getBinPath();
/* 30 */     if (p != null)
/*    */     {
/* 33 */       String sDelimiter = System.getProperty("path.separator", ":");
/*    */ 
/* 36 */       String sPath = p.replace('/', '\\');
/*    */ 
/* 38 */       return new BuildEnvVar("PATH", sPath, 
/* 39 */         3, sDelimiter);
/*    */     }
/* 41 */     return null;
/*    */   }
/*    */ 
/*    */   public IBuildEnvironmentVariable[] getVariables(IConfiguration configuration, IEnvironmentVariableProvider provider)
/*    */   {
/* 47 */     IBuildEnvironmentVariable[] tmp = new IBuildEnvironmentVariable[1];
/* 48 */     tmp[0] = getVariable("PATH", configuration, provider);
/* 49 */     if (tmp[0] != null)
/* 50 */       return tmp;
/* 51 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.windows.ConfigurationEnvironmentSupplier
 * JD-Core Version:    0.6.2
 */