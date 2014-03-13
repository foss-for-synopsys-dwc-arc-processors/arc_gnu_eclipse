/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/
   
package org.eclipse.cdt.cross.arc.gnu;
   
   import java.util.ArrayList;
   import java.util.Arrays;
   import org.eclipse.cdt.managedbuilder.core.BuildException;
   import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
   import org.eclipse.cdt.managedbuilder.core.IOption;
   import org.eclipse.cdt.managedbuilder.core.ITool;
   import org.eclipse.cdt.managedbuilder.core.IToolChain;
   import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineGenerator;
   
   public class ARCManagedCommandLineGenerator extends ManagedCommandLineGenerator
   {
     private static final String OPTION_SUFIX_PROCESSOR = ".option.target.processor";
     private static final String OPTION_SUFIX_CORE = ".option.target.core700";                     //yunluz add for core 700
     private static final String OPTION_SUFIX_BARRELSHIFTER = ".option.target.barrelshifter";      //yunluz add for core barrelshifter
     private static final String OPTION_SUFIX_CODEDENSITY = ".option.target.codedensity";          //yunluz add for core codedensity
     private static final String OPTION_SUFIX_DIVIDE = ".option.target.divide";                    //yunluz add for core divide
     private static final String OPTION_SUFIX_NORMALIZE = ".option.target.normalize";              //yunluz add for core normalize
     private static final String OPTION_SUFIX_SWAP = ".option.target.swap";                 //yunluz add for core swap
     private static final String OPTION_SUFIX_SPFP_COMFP = ".option.target.spfp";                 //yunluz add for core sSpfp
     private static final String OPTION_SUFIX_EA = ".option.target.ea";                 //yunluz add for core ea
     private static final String OPTION_SUFIX_THUMB = ".option.target.thumb";
     private static final String OPTION_SUFIX_THUMB_INTERWORK = ".option.target.thumbinterwork";
     private static final String OPTION_SUFFIX_ENDIANNES = ".option.target.endiannes";
     private static final String OPTION_SUFFIX_FLOAT_ABI = ".option.warnings.syntax";
     private static final String OPTION_SUFFIX_FLOAT_UNIT = ".option.target.fpu.unit";
     private static final String OPTION_SUFIX_DEBUGGING_LEVEL = ".option.debugging.flevel";
     private static final String OPTION_SUFIX_DEBUGGING_FORMAT = ".option.debugging.format";
     private static final String OPTION_SUFIX_DEBUGGING_OTHER = ".option.debugging.other";
     private static final String OPTION_SUFIX_DEBUGGING_PROF = ".option.debugging.prof";
     private static final String OPTION_SUFIX_DEBUGGING_GPROF = ".option.debugging.gprof";
     private static final boolean DEBUG_LOCAL = false;
   
     public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName, String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName, String[] asInputResources, String sCommandLinePattern)
     {
     return generateCommandLineInfo(oTool, sCommandName, asFlags,sOutputFlag, sOutputPrefix, sOutputName, asInputResources, sCommandLinePattern, false);
     }
   
     public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName, String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName, String[] asInputResources, String sCommandLinePattern, boolean bFlag)
     {
     ArrayList oList = new ArrayList();
     oList.addAll(Arrays.asList(asFlags));

   
     Object oParent = oTool.getParent();
     while ((oParent != null) && (!(oParent instanceof IToolChain)))
       {
       Object oSuper = oTool.getSuperClass();
       if ((oSuper != null) && ((oSuper instanceof ITool)))
         oParent = ((ITool)oSuper).getParent();
         else {
         oParent = null;
         }
       }
   
     if ((oParent != null) && ((oParent instanceof IToolChain))) {
       IToolChain oToolChain = (IToolChain)oParent;
   
       IOption[] aoOptions = oToolChain.getOptions();
   
       String sProcessor = null;
                
       String sCore700 = null;        //yunluz add for core 700
       String sBarrelshifter = null;  //yunluz add for Barrelshifter
       String sCodedensity = null;    //yunluz add for codedensity
       String sDivide = null;         //yunluz add for divide
       String sNormalize = null;      //yunluz add for normalize
       String sSwap = null;           //yunluz add for swap
       String sSpfp = null;           //yunluz add for spfp
       String sEa = null;           //yunluz add for ea
       String sThumb = null;
   
       String sThumbInterwork = null;
   
       String sProcessorEndiannes = null;
   
       String sSyntaxonly = null;
   
       String sFPU = null;
       String smpy= null;
   
       String sDebugLevel = null;
   
       String sDebugFormat = null;
   
       String sDebugOther = null;
   
       String sDebugProf = null;
   
       String sDebugGProf = null;
       String sshiftassist= null; 
       String sdivrem = null;
       String satomic = null;
       String sll64 = null;
       String smdpfp= null;
   
       for (int i = 0; i < aoOptions.length; i++)
         {
         IOption oOption = aoOptions[i];
   
         String sID = oOption.getId();
   
         Object oValue = oOption.getValue();
   
         String sCommand = oOption.getCommand();
   
         if ((oValue instanceof String)) {
             String sVal;
             try {
             sVal = oOption.getStringValue();
             }
             catch (BuildException e)
             {
               // yunluz comment String sVal;
             sVal = null;
             }
             String sEnumCommand;
             try
             {
             sEnumCommand = oOption.getEnumCommand(sVal);
             }
             catch (BuildException e1)
             {
               //yunluz String sEnumCommand;
             sEnumCommand = null;
             }
   
           if ((sID.endsWith(".option.target.processor")) || 
             (sID.indexOf(".option.target.processor.") > 0))
             sProcessor = sEnumCommand;
           else if ((sID.endsWith(".option.target.core700")) ||     //yunluz add for core 700
             (sID.indexOf(".option.target.core700.") > 0))          //yunluz add for core 700
             sCore700 = sEnumCommand;                               //yunluz add for core 700
           else if ((sID.endsWith(".option.target.endiannes")) || 
             (sID.indexOf(".option.target.endiannes.") > 0))
             sProcessorEndiannes = sEnumCommand;
           else if ((sID.endsWith(".option.warnings.syntax")) ||  //yunluz add for fsyntax-only
             (sID.indexOf(".option.warnings.syntax") > 0))
             sSyntaxonly = sEnumCommand;
           else if ((sID.endsWith(".option.target.fpu.")) || 
             (sID.indexOf(".option.target.fpu.") > 0))
             sFPU = sEnumCommand;
           else if ((sID.endsWith(".option.target.mpy.")) || 
                   (sID.indexOf(".option.target.mpy.") > 0))
             smpy = sEnumCommand;
           else if ((sID.endsWith(".option.debugging.level")) || 
             (sID.indexOf(".option.debugging.level.") > 0))
             sDebugLevel = sEnumCommand;
           else if ((sID.endsWith(".option.debugging.format")) || 
             (sID.indexOf(".option.debugging.format.") > 0))
             sDebugFormat = sEnumCommand;
           else if ((sID.endsWith(".option.debugging.other")) || 
             (sID.indexOf(".option.debugging.other.") > 0))
             sDebugOther = sVal;
           }
           else if ((oValue instanceof Boolean)) {
             boolean bVal;
             try {
             bVal = oOption.getBooleanValue();
             }
             catch (BuildException e)
             {
               //yunluz boolean bVal;
             bVal = false;
             }
   
           if ((sID.endsWith(".option.target.thumb")) || 
             (sID.indexOf(".option.target.thumb.") > 0)) {
             if (bVal)
               sThumb = sCommand;
           } else if ((sID.endsWith(".option.target.thumbinterwork")) || 
             (sID.indexOf(".option.target.thumbinterwork.") > 0)) {
             if (bVal)
               sThumbInterwork = sCommand;
           } else if ((sID.endsWith(".option.debugging.prof")) || 
             (sID.indexOf(".option.debugging.prof.") > 0)) {
             if (bVal)
               sDebugProf = sCommand;
           } else if ((sID.endsWith(".option.target.barrelshifter")) ||  //yunluz add for barrelshifter
             (sID.indexOf(".option.target.barrelshifter.") > 0)) {       //yunluz add for barrelshifter
             if (bVal)                                                   //yunluz add for barrelshifter
               sBarrelshifter = sCommand;                                //yunluz add for barrelshifter
           } else if ((sID.endsWith(".option.target.codedensity")&&((sProcessor.equalsIgnoreCase("-mcpu=ARCv2EM"))||(sProcessor.equalsIgnoreCase("-mcpu=ARCv2HS"))) ||    //yunluz add for codedensity
             (sID.indexOf(".option.target.codedensity.") > 0)&&((sProcessor.equalsIgnoreCase("-mcpu=ARCv2EM"))||(sProcessor.equalsIgnoreCase("-mcpu=ARCv2HS"))))) {         //yunluz add for codedensity
              if (bVal)                                                  //yunluz add for codedensity
              sCodedensity = sCommand;                                   //yunluz add for codedensity
           } else if ((sID.endsWith(".option.target.divide")) ||         //yunluz add for divide
             (sID.indexOf(".option.target.divide.") > 0)) {              //yunluz add for divide
             if (bVal)                                                   //yunluz add for divide
             sDivide = sCommand;                                         //yunluz add for divide
           } else if ((sID.endsWith(".option.target.normalize")) ||      //yunluz add for normalize
            (sID.indexOf(".option.target.normalize.") > 0)) {            //yunluz add for normalize
             if (bVal)                                                   //yunluz add for normalize
             sNormalize = sCommand;                                      //yunluz add for normalize
           } else if ((sID.endsWith(".option.target.swap")&&((sProcessor.equalsIgnoreCase("-mcpu=ARCv2EM"))||(sProcessor.equalsIgnoreCase("-mcpu=ARCv2HS"))) ||           //yunluz add for swap
             (sID.indexOf(".option.target.swap.") > 0)&&((sProcessor.equalsIgnoreCase("-mcpu=ARCv2EM"))||(sProcessor.equalsIgnoreCase("-mcpu=ARCv2HS"))))) {                //yunluz add for swap
             if (bVal)                                                   //yunluz add for swap
              sSwap = sCommand;                                          //yunluz add for swap
           } else if ((sID.endsWith(".option.target.spfp")) ||           //yunluz add for spfp 
              (sID.indexOf(".option.target.spfp.") > 0)) {               //yunluz add for spfp
              if (bVal)                                                   //yunluz add for spfp
              sSpfp = sCommand;                                          //yunluz add for ea
           } else if ((sID.endsWith(".option.target.ea")&&sProcessor.equalsIgnoreCase("-mA7")) ||           //yunluz add for ea
                   (sID.indexOf(".option.target.ea.") > 0)&&sProcessor.equalsIgnoreCase("-mA7")) {               //yunluz add for ea
                   if (bVal)                                                   //yunluz add for ea
                   sEa = sCommand;                                          //yunluz add for ea
           } else if ((sID.endsWith(".option.target.shiftassist")) ||  
                   (sID.indexOf(".option.target.shiftassist.") > 0)) {       
               if (bVal)                                                  
                 sshiftassist = sCommand;                                
           } 
           else if ((sID.endsWith(".option.target.divrem")) ||  
                   (sID.indexOf(".option.target.divrem.") > 0)) {       
               if (bVal)                                                  
                 sdivrem = sCommand;                                
           } 
           else if ((sID.endsWith(".option.target.atomic")) ||  
                   (sID.indexOf(".option.target.atomic.") > 0)) {       
               if (bVal)                                                  
            	   satomic = sCommand;                                
           } 
           else if ((sID.endsWith(".option.target.ll64")) ||  
                   (sID.indexOf(".option.target.ll64.") > 0)) {       
               if (bVal)                                                  
                 sll64 = sCommand;                                
           } 
           else if ((sID.endsWith(".option.target.dpfp")) ||  
                   (sID.indexOf(".option.target.dpfp.") > 0)) {       
               if (bVal)                                                  
            	   smdpfp = sCommand;                                
           } 
           
           
           
           
           else if (((sID.endsWith(".option.debugging.gprof")) || 
             (sID.indexOf(".option.debugging.gprof.") > 0)) && 
             (bVal)) {
             sDebugGProf = sCommand;
             }
   
           }
         
   
         }
   
       if ((sProcessor != null) && (sProcessor.length() > 0))
         oList.add(sProcessor);
       if ((sCore700 != null) && (sCore700.length() > 0))
         oList.add(sCore700);
       if ((sThumb != null) && (sThumb.length() > 0))
         oList.add(sThumb);
       if ((sThumbInterwork != null) && (sThumbInterwork.length() > 0))
         oList.add(sThumbInterwork);
       if ((sProcessorEndiannes != null) && (sProcessorEndiannes.length() > 0))
         oList.add(sProcessorEndiannes);
       if ((sSyntaxonly != null) && (sSyntaxonly.length() > 0)) {
         oList.add(sSyntaxonly);
       }
       if ((sFPU != null) && (sFPU.length() > 0))
           oList.add(sFPU);
       if ((smpy != null) && (smpy.length() > 0))
           oList.add(smpy);
       if ((sDebugLevel != null) && (sDebugLevel.length() > 0)) {
         oList.add(sDebugLevel);
       if ((sDebugFormat != null) && (sDebugFormat.length() > 0))
           oList.add(sDebugFormat);
         }
       if ((sDebugOther != null) && (sDebugOther.length() > 0))
         oList.add(sDebugOther);
       if ((sDebugProf != null) && (sDebugProf.length() > 0))
         oList.add(sDebugProf);
       if ((sDebugGProf != null) && (sDebugGProf.length() > 0)) {
         oList.add(sDebugGProf);
         }
       if ((sBarrelshifter != null) && (sBarrelshifter.length() > 0)) {//yunluz add for barrelshifter
         oList.add(sBarrelshifter);                                    //yunluz add for barrelshifter
         }
       if ((sCodedensity != null) && (sCodedensity.length() > 0)) {    //yunluz add for codedensity
           oList.add(sCodedensity);                                    //yunluz add for codedensity
           }
       if ((sDivide != null) && (sDivide.length() > 0)) {              //yunluz add for divide
           oList.add(sDivide);                                         //yunluz add for divide
           }
       if ((sNormalize != null) && (sNormalize.length() > 0)) {           //yunluz add for normalize
           oList.add(sNormalize);                                         //yunluz add for normalize
           }
       if ((sSwap != null) && (sSwap.length() > 0)) {                 //yunluz add for swap
           oList.add(sSwap);                                         //yunluz add for swap     
           }
       if ((sSpfp != null) && (sSpfp.length() > 0)) {                 
           oList.add(sSpfp);                                            
           }  
       if ((sEa != null) && (sEa.length() > 0)) {                 
           oList.add(sEa);                                              
           } 
       if ((sshiftassist != null) && (sshiftassist.length() > 0)) {           
           oList.add(sshiftassist);                                         
           }
       if ((sdivrem != null) && (sdivrem.length() > 0)) {                
           oList.add(sdivrem);                                             
           }
       if ((satomic != null) && (satomic.length() > 0)) {                
           oList.add(satomic);                                             
           }  
       if ((sll64 != null) && (sll64.length() > 0)) {                
           oList.add(sll64);                                             
           } 
       if ((smdpfp != null) && (smdpfp.length() > 0)) {                
           oList.add(smdpfp);                                             
           } 
 
       
       }
     
     return super.generateCommandLineInfo(oTool, sCommandName, (String[])oList.toArray(new String[0]), sOutputFlag, sOutputPrefix, sOutputName, asInputResources, sCommandLinePattern);
     }
   }

