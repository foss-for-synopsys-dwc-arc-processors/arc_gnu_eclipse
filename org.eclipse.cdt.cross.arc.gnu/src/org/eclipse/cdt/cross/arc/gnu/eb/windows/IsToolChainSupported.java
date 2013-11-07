/*    */ package org.eclipse.cdt.cross.arc.gnu.eb.windows;
/*    */ 
/*    */ import org.eclipse.cdt.cross.arc.gnu.common.IsToolchainData;
/*    */ import org.eclipse.cdt.managedbuilder.core.IToolChain;
/*    */ import org.osgi.framework.Version;
/*    */ 
/*    */ public class IsToolChainSupported extends org.eclipse.cdt.cross.arc.gnu.eb.IsToolChainSupported
/*    */ {
/* 11 */   static IsToolchainData ms_oData = null;
/*    */ 
/*    */   public boolean isSupported(IToolChain oToolChain, Version oVersion, String sInstance)
/*    */   {
/* 16 */     if (ms_oData == null) {
/* 17 */       ms_oData = new IsToolchainData();
/*    */     }
/* 19 */     return isSupportedImpl(oToolChain, oVersion, sInstance, 
/* 20 */       ms_oData);
/*    */   }
/*    */ 
/*    */   public String getPlatform() {
/* 24 */     return "windows";
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\org.eclipse.cdt.cross.arm.gnu_0.5.5.201309281715\plugins\org.eclipse.cdt.cross.arm.gnu_0.5.5.201309281715\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.yagarto.windows.IsToolChainSupported
 * JD-Core Version:    0.6.0
 */