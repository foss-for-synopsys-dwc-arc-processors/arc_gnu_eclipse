/*    */ package org.eclipse.cdt.cross.arc.gnu.devkitpro.macosx;
/*    */ 
/*    */ import org.eclipse.cdt.cross.arc.gnu.common.IsToolchainData;
/*    */ import org.eclipse.cdt.managedbuilder.core.IToolChain;
/*    */ import org.osgi.framework.Version;
/*    */ 
/*    */ public class IsToolChainSupported extends org.eclipse.cdt.cross.arc.gnu.devkitpro.IsToolChainSupported
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
/*    */   public String getPlatform()
/*    */   {
/* 25 */     return "macosx";
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.devkitpro.macosx.IsToolChainSupported
 * JD-Core Version:    0.6.2
 */