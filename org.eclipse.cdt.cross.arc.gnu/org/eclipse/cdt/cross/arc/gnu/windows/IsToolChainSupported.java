/*    */ package org.eclipse.cdt.cross.arc.gnu.windows;
/*    */ 
/*    */ import org.eclipse.cdt.cross.arc.gnu.common.IsToolchainData;
/*    */ import org.eclipse.cdt.managedbuilder.core.IToolChain;
/*    */ import org.osgi.framework.Version;
/*    */ 
/*    */ public class IsToolChainSupported extends org.eclipse.cdt.cross.arc.gnu.common.IsToolChainSupported
/*    */ {
/* 11 */   static IsToolchainData ms_oData = null;
/*    */ 
/*    */   public boolean isSupported(IToolChain oToolChain, Version oVersion, String sInstance)
/*    */   {
/* 16 */     if (ms_oData == null) {
/* 17 */       ms_oData = new IsToolchainData();
/*    */     }
/* 19 */     if (ms_oData.m_sBinPath == null) {
/* 20 */       ms_oData.m_sBinPath = PathResolver.getBinPath();
/*    */     }
/* 22 */     return isSupportedImpl(oToolChain, oVersion, sInstance, 
/* 23 */       ms_oData);
/*    */   }
/*    */ 
/*    */   public String getPlatform()
/*    */   {
/* 28 */     return "windows";
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.windows.IsToolChainSupported
 * JD-Core Version:    0.6.2
 */