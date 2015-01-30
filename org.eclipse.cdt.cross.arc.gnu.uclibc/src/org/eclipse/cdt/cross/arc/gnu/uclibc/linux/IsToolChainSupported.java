/*    */ package org.eclipse.cdt.cross.arc.gnu.uclibc.linux;
/*    */ import org.eclipse.cdt.cross.arc.gnu.uclibc.common.IsToolchainData;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
/*    */ import org.osgi.framework.Version;
/*    */ 
/*    */ public class IsToolChainSupported extends org.eclipse.cdt.cross.arc.gnu.uclibc.common.IsToolChainSupported
/*    */ {
/* 10 */   static IsToolchainData ms_oData = null;
/*    */ 
/*    */   public boolean isSupported(IToolChain oToolChain, Version oVersion, String sInstance)
/*    */   {
/* 15 */     if (ms_oData == null) {
/* 16 */       ms_oData = new IsToolchainData();
/*    */     }
/* 18 */     return isSupportedImpl(oToolChain, oVersion, sInstance, 
/* 19 */       ms_oData);
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.linux.IsToolChainSupported
 * JD-Core Version:    0.6.2
 */