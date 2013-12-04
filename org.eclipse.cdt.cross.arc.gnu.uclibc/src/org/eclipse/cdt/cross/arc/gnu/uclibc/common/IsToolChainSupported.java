/*    */ package org.eclipse.cdt.cross.arc.gnu.uclibc.common;
/*    */ 
/*    */ import org.eclipse.cdt.managedbuilder.core.IManagedIsToolChainSupported;
/*    */ import org.eclipse.cdt.managedbuilder.core.IToolChain;
/*    */ import org.osgi.framework.Version;
/*    */ 
/*    */ public abstract class IsToolChainSupported
/*    */   implements IManagedIsToolChainSupported
/*    */ {
/*    */   static final boolean DEBUG = false;
/*    */ 
/*    */   public String getCompilerName()
/*    */   {
/* 20 */     return "arc-elf32-gcc";
/*    */   }
/*    */ 
/*    */   public String getPlatform() {
/* 24 */     return "linux";
/*    */   }
/*    */ 
/*    */   public boolean isSupportedImpl(IToolChain oToolChain, Version oVersion, String sInstance, IsToolchainData oStaticData)
/*    */   {
/* 37 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.common.IsToolChainSupported
 * JD-Core Version:    0.6.2
 */