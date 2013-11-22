/*    */ package org.eclipse.cdt.cross.arc.gnu.scannerconfig;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.eclipse.cdt.make.core.scannerconfig.IScannerInfoCollector3;
/*    */ import org.eclipse.cdt.make.core.scannerconfig.InfoContext;
/*    */ import org.eclipse.cdt.make.core.scannerconfig.ScannerInfoTypes;
/*    */ import org.eclipse.cdt.make.internal.core.scannerconfig.util.CygpathTranslator;
/*    */ import org.eclipse.cdt.make.internal.core.scannerconfig2.PerProjectSICollector;
/*    */ import org.eclipse.cdt.managedbuilder.scannerconfig.IManagedScannerInfoCollector;
/*    */ import org.eclipse.core.resources.IProject;
/*    */ 
/*    */ public class ARCGnuWinScannerInfoCollector extends PerProjectSICollector
/*    */   implements IScannerInfoCollector3, IManagedScannerInfoCollector
/*    */ {
/*    */   private IProject m_oProject;
/*    */ 
/*    */   public void contributeToScannerConfig(Object oResource, Map oScannerInfo)
/*    */   {
/* 28 */     List oIncludes = (List)oScannerInfo.get(ScannerInfoTypes.INCLUDE_PATHS);
/*    */ 
/* 32 */     List oTranslatedIncludes = CygpathTranslator.translateIncludePaths(this.m_oProject, oIncludes);
/* 33 */     Iterator oPathIter = oTranslatedIncludes.listIterator();
/* 34 */     while (oPathIter.hasNext()) {
/* 35 */       String sConvertedPath = (String)oPathIter.next();
/*    */ 
/* 37 */       if (sConvertedPath.startsWith("/")) {
/* 38 */         oPathIter.remove();
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 44 */     oScannerInfo.put(ScannerInfoTypes.INCLUDE_PATHS, oTranslatedIncludes);
/*    */ 
/* 55 */     super.contributeToScannerConfig(oResource, oScannerInfo);
/*    */   }
/*    */ 
/*    */   public void setProject(IProject oProject) {
/* 59 */     this.m_oProject = oProject;
/* 60 */     super.setProject(oProject);
/*    */   }
/*    */ 
/*    */   public void setInfoContext(InfoContext oContext) {
/* 64 */     this.m_oProject = oContext.getProject();
/* 65 */     super.setInfoContext(oContext);
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.scannerconfig.ARMGnuWinScannerInfoCollector
 * JD-Core Version:    0.6.2
 */