/*    */ package org.eclipse.cdt.cross.arc.gnu.scannerconfig;
/*    */ 
/*    */ import org.eclipse.cdt.build.core.scannerconfig.CfgInfoContext;
/*    */ import org.eclipse.cdt.core.model.CoreModel;
/*    */ import org.eclipse.cdt.core.settings.model.ICProjectDescription;
/*    */ import org.eclipse.cdt.make.core.scannerconfig.IScannerInfoCollector;
/*    */ import org.eclipse.cdt.make.core.scannerconfig.InfoContext;
/*    */ import org.eclipse.cdt.make.internal.core.scannerconfig.gnu.GCCScannerInfoConsoleParser;
/*    */ import org.eclipse.cdt.make.internal.core.scannerconfig2.PerProjectSICollector;
/*    */ import org.eclipse.cdt.managedbuilder.core.IConfiguration;
/*    */ import org.eclipse.core.resources.IProject;
/*    */ 
/*    */ public class ARCManagedGCCScannerInfoConsoleParser extends GCCScannerInfoConsoleParser
/*    */ {
/*    */   Boolean m_bManagedBuildOnState;
/*    */ 
/*    */   public boolean processLine(String sLine)
/*    */   {
/* 19 */     if (isManagedBuildOn())
/* 20 */       return false;
/* 21 */     return super.processLine(sLine);
/*    */   }
/*    */ 
/*    */   public void shutdown() {
/* 25 */     if (!isManagedBuildOn()) {
/* 26 */       super.shutdown();
/*    */     }
/* 28 */     this.m_bManagedBuildOnState = null;
/*    */   }
/*    */ 
/*    */   public void startup(IProject oProject, IScannerInfoCollector oCollector) {
/* 32 */     if (isManagedBuildOn())
/* 33 */       return;
/* 34 */     super.startup(oProject, oCollector);
/*    */   }
/*    */ 
/*    */   protected boolean isManagedBuildOn() {
/* 38 */     if (this.m_bManagedBuildOnState == null)
/* 39 */       this.m_bManagedBuildOnState = 
/* 40 */         Boolean.valueOf(doCalcManagedBuildOnState());
/* 41 */     return this.m_bManagedBuildOnState.booleanValue();
/*    */   }
/*    */ 
/*    */   protected boolean doCalcManagedBuildOnState() {
/* 45 */     IScannerInfoCollector oCr = getCollector();
/*    */     InfoContext oC;
/* 47 */     if ((oCr instanceof PerProjectSICollector))
/* 48 */       oC = ((PerProjectSICollector)oCr).getContext();
/*    */     else
/* 50 */       return false;
/*    */     InfoContext oC;
/* 53 */     IProject oProject = oC.getProject();
/* 54 */     ICProjectDescription oDes = CoreModel.getDefault()
/* 55 */       .getProjectDescription(oProject, false);
/* 56 */     CfgInfoContext oCc = CfgInfoContext.fromInfoContext(oDes, oC);
/* 57 */     if (oCc != null) {
/* 58 */       IConfiguration cfg = oCc.getConfiguration();
/* 59 */       return cfg.isManagedBuildOn();
/*    */     }
/* 61 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.scannerconfig.ARMManagedGCCScannerInfoConsoleParser
 * JD-Core Version:    0.6.2
 */