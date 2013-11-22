/*     */ package org.eclipse.cdt.cross.arc.gnu;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import org.eclipse.cdt.managedbuilder.core.BuildException;
/*     */ import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
/*     */ import org.eclipse.cdt.managedbuilder.core.IOption;
/*     */ import org.eclipse.cdt.managedbuilder.core.ITool;
/*     */ import org.eclipse.cdt.managedbuilder.core.IToolChain;
/*     */ import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineGenerator;
/*     */ 
/*     */ public class ARCManagedCommandLineGenerator extends ManagedCommandLineGenerator
/*     */ {
/*     */   private static final String OPTION_SUFIX_PROCESSOR = ".option.target.processor";
/*     */   private static final String OPTION_SUFIX_THUMB = ".option.target.thumb";
/*     */   private static final String OPTION_SUFIX_THUMB_INTERWORK = ".option.target.thumbinterwork";
/*     */   private static final String OPTION_SUFFIX_ENDIANNES = ".option.target.endiannes";
/*     */   private static final String OPTION_SUFFIX_FLOAT_ABI = ".option.target.fpu.abi";
/*     */   private static final String OPTION_SUFFIX_FLOAT_UNIT = ".option.target.fpu.unit";
/*     */   private static final String OPTION_SUFIX_DEBUGGING_LEVEL = ".option.debugging.level";
/*     */   private static final String OPTION_SUFIX_DEBUGGING_FORMAT = ".option.debugging.format";
/*     */   private static final String OPTION_SUFIX_DEBUGGING_OTHER = ".option.debugging.other";
/*     */   private static final String OPTION_SUFIX_DEBUGGING_PROF = ".option.debugging.prof";
/*     */   private static final String OPTION_SUFIX_DEBUGGING_GPROF = ".option.debugging.gprof";
/*     */   private static final boolean DEBUG_LOCAL = false;
/*     */ 
/*     */   public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName, String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName, String[] asInputResources, String sCommandLinePattern)
/*     */   {
/*  39 */     return generateCommandLineInfo(oTool, sCommandName, asFlags, 
/*  40 */       sOutputFlag, sOutputPrefix, sOutputName, asInputResources, 
/*  41 */       sCommandLinePattern, false);
/*     */   }
/*     */ 
/*     */   public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName, String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName, String[] asInputResources, String sCommandLinePattern, boolean bFlag)
/*     */   {
/*  49 */     ArrayList oList = new ArrayList();
/*  50 */     oList.addAll(
/*  51 */       Arrays.asList(asFlags));
/*     */ 
/*  53 */     Object oParent = oTool.getParent();
/*  54 */     while ((oParent != null) && (!(oParent instanceof IToolChain)))
/*     */     {
/*  57 */       Object oSuper = oTool.getSuperClass();
/*  58 */       if ((oSuper != null) && ((oSuper instanceof ITool)))
/*  59 */         oParent = ((ITool)oSuper).getParent();
/*     */       else {
/*  61 */         oParent = null;
/*     */       }
/*     */     }
/*     */ 
/*  65 */     if ((oParent != null) && ((oParent instanceof IToolChain))) {
/*  66 */       IToolChain oToolChain = (IToolChain)oParent;
/*     */ 
/*  68 */       IOption[] aoOptions = oToolChain.getOptions();
/*     */ 
/*  71 */       String sProcessor = null;
/*     */ 
/*  74 */       String sThumb = null;
/*     */ 
/*  77 */       String sThumbInterwork = null;
/*     */ 
/*  80 */       String sProcessorEndiannes = null;
/*     */ 
/*  83 */       String sFloatAbi = null;
/*     */ 
/*  86 */       String sFloatUnit = null;
/*     */ 
/*  89 */       String sDebugLevel = null;
/*     */ 
/*  92 */       String sDebugFormat = null;
/*     */ 
/*  95 */       String sDebugOther = null;
/*     */ 
/*  98 */       String sDebugProf = null;
/*     */ 
/* 101 */       String sDebugGProf = null;
/*     */ 
/* 103 */       for (int i = 0; i < aoOptions.length; i++)
/*     */       {
/* 105 */         IOption oOption = aoOptions[i];
/*     */ 
/* 108 */         String sID = oOption.getId();
/*     */ 
/* 111 */         Object oValue = oOption.getValue();
/*     */ 
/* 114 */         String sCommand = oOption.getCommand();
/*     */ 
/* 116 */         if ((oValue instanceof String)) {
/*     */           String sVal;
/*     */           try {
/* 119 */             sVal = oOption.getStringValue();
/*     */           }
/*     */           catch (BuildException e)
/*     */           {
/*     */             String sVal;
/* 121 */             sVal = null;
/*     */           }
/*     */           String sEnumCommand;
/*     */           try
/*     */           {
/* 126 */             sEnumCommand = oOption.getEnumCommand(sVal);
/*     */           }
/*     */           catch (BuildException e1)
/*     */           {
/*     */             String sEnumCommand;
/* 128 */             sEnumCommand = null;
/*     */           }
/*     */ 
/* 135 */           if ((sID.endsWith(".option.target.processor")) || 
/* 136 */             (sID.indexOf(".option.target.processor.") > 0))
/* 137 */             sProcessor = sEnumCommand;
/* 138 */           else if ((sID.endsWith(".option.target.endiannes")) || 
/* 139 */             (sID.indexOf(".option.target.endiannes.") > 0))
/* 140 */             sProcessorEndiannes = sCommand;
/* 141 */           else if ((sID.endsWith(".option.target.fpu.abi")) || 
/* 142 */             (sID.indexOf(".option.target.fpu.abi.") > 0))
/* 143 */             sFloatAbi = sEnumCommand;
/* 144 */           else if ((sID.endsWith(".option.target.fpu.unit")) || 
/* 145 */             (sID.indexOf(".option.target.fpu.unit.") > 0))
/* 146 */             sFloatUnit = sEnumCommand;
/* 147 */           else if ((sID.endsWith(".option.debugging.level")) || 
/* 148 */             (sID.indexOf(".option.debugging.level.") > 0))
/* 149 */             sDebugLevel = sEnumCommand;
/* 150 */           else if ((sID.endsWith(".option.debugging.format")) || 
/* 151 */             (sID.indexOf(".option.debugging.format.") > 0))
/* 152 */             sDebugFormat = sEnumCommand;
/* 153 */           else if ((sID.endsWith(".option.debugging.other")) || 
/* 154 */             (sID.indexOf(".option.debugging.other.") > 0))
/* 155 */             sDebugOther = sVal;
/*     */         }
/* 157 */         else if ((oValue instanceof Boolean)) {
/*     */           boolean bVal;
/*     */           try {
/* 160 */             bVal = oOption.getBooleanValue();
/*     */           }
/*     */           catch (BuildException e)
/*     */           {
/*     */             boolean bVal;
/* 162 */             bVal = false;
/*     */           }
/*     */ 
/* 169 */           if ((sID.endsWith(".option.target.thumb")) || 
/* 170 */             (sID.indexOf(".option.target.thumb.") > 0)) {
/* 171 */             if (bVal)
/* 172 */               sThumb = sCommand;
/* 173 */           } else if ((sID.endsWith(".option.target.thumbinterwork")) || 
/* 174 */             (sID.indexOf(".option.target.thumbinterwork.") > 0)) {
/* 175 */             if (bVal)
/* 176 */               sThumbInterwork = sCommand;
/* 177 */           } else if ((sID.endsWith(".option.debugging.prof")) || 
/* 178 */             (sID.indexOf(".option.debugging.prof.") > 0)) {
/* 179 */             if (bVal)
/* 180 */               sDebugProf = sCommand;
/* 181 */           } else if (((sID.endsWith(".option.debugging.gprof")) || 
/* 182 */             (sID.indexOf(".option.debugging.gprof.") > 0)) && 
/* 183 */             (bVal)) {
/* 184 */             sDebugGProf = sCommand;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 199 */       if ((sProcessor != null) && (sProcessor.length() > 0))
/* 200 */         oList.add(sProcessor);
/* 201 */       if ((sThumb != null) && (sThumb.length() > 0))
/* 202 */         oList.add(sThumb);
/* 203 */       if ((sThumbInterwork != null) && (sThumbInterwork.length() > 0))
/* 204 */         oList.add(sThumbInterwork);
/* 205 */       if ((sProcessorEndiannes != null) && (sProcessorEndiannes.length() > 0))
/* 206 */         oList.add(sProcessorEndiannes);
/* 207 */       if ((sFloatAbi != null) && (sFloatAbi.length() > 0)) {
/* 208 */         oList.add(sFloatAbi);
/*     */ 
/* 210 */         if ((sFloatUnit != null) && (sFloatUnit.length() > 0))
/* 211 */           oList.add(sFloatUnit);
/*     */       }
/* 213 */       if ((sDebugLevel != null) && (sDebugLevel.length() > 0)) {
/* 214 */         oList.add(sDebugLevel);
/*     */ 
/* 216 */         if ((sDebugFormat != null) && (sDebugFormat.length() > 0))
/* 217 */           oList.add(sDebugFormat);
/*     */       }
/* 219 */       if ((sDebugOther != null) && (sDebugOther.length() > 0))
/* 220 */         oList.add(sDebugOther);
/* 221 */       if ((sDebugProf != null) && (sDebugProf.length() > 0))
/* 222 */         oList.add(sDebugProf);
/* 223 */       if ((sDebugGProf != null) && (sDebugGProf.length() > 0)) {
/* 224 */         oList.add(sDebugGProf);
/*     */       }
/*     */     }
/*     */ 
/* 228 */     return super.generateCommandLineInfo(oTool, sCommandName, 
/* 229 */       (String[])oList
/* 229 */       .toArray(new String[0]), sOutputFlag, sOutputPrefix, 
/* 230 */       sOutputName, asInputResources, sCommandLinePattern);
/*     */   }
/*     */ }

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.ARMManagedCommandLineGenerator
 * JD-Core Version:    0.6.2
 */