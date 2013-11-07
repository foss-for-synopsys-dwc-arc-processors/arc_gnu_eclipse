/*    */ package org.eclipse.cdt.cross.arc.gnu;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.eclipse.cdt.managedbuilder.core.IBuildObject;
/*    */ import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
/*    */ import org.eclipse.cdt.managedbuilder.core.IOption;
/*    */ import org.eclipse.cdt.managedbuilder.core.ManagedOptionValueHandler;
/*    */ import org.eclipse.cdt.managedbuilder.internal.core.FolderInfo;
/*    */ import org.eclipse.cdt.managedbuilder.internal.core.ResourceConfiguration;
/*    */ 
/*    */ public class ARCAdditionalToolsManagedOptionValueHandler extends ManagedOptionValueHandler
/*    */ {
/*    */   public boolean handleValue(IBuildObject configuration, IHoldsOptions holder, IOption option, String extraArgument, int event)
/*    */   {
/* 71 */     if (event == 4) {
/* 72 */  
/* 73 */       if ((configuration instanceof FolderInfo))
/*    */       {
/* 75 */         FolderInfo oFolderInfo = (FolderInfo)configuration;
/*    */ 
/* 79 */         return true;
/* 80 */       }if (!(configuration instanceof ResourceConfiguration))
/*    */       {
/* 83 */         System.out.println("unexpected instanceof configuration " + configuration.getClass().getCanonicalName());
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 88 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.ARMAdditionalToolsManagedOptionValueHandler
 * JD-Core Version:    0.6.2
 */