/*******************************************************************************
F* This program and the accompanying materials 
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
     public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName, String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName, String[] asInputResources, String sCommandLinePattern)
     {
     return generateCommandLineInfo(oTool, sCommandName, asFlags,sOutputFlag, sOutputPrefix, sOutputName, asInputResources, sCommandLinePattern, false);
     }
   
     public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName, String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName, String[] asInputResources, String sCommandLinePattern, boolean bFlag)
     {
     ArrayList<String> oList = new ArrayList<String>();
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
                
       String sCore700 = null;        //Customized for ARC GNU core 700
       String sBarrelshifter = null;  //Customized for ARC GNU Barrelshifter
       String sCodedensity = null;    //Customized for ARC GNU codedensity
       String sDivide = null;         //Customized for ARC GNU divide
       String sNormalize = null;      //Customized for ARC GNU normalize
       String sMPY=null;
       String sSwap = null;           //Customized for ARC GNU swap
       String sEa = null;           //Customized for ARC GNU ea
       String sThumb = null;
   
       String sThumbInterwork = null;
   
       String sProcessorEndiannes = null;
   
       String sSyntaxonly = null;
   
       String sFPUEM = null;
       String sFPUHS = null;
       String smpyhs= null;
       String smpyem= null;
   
       String sDebugLevel = null;
   
       String sDebugFormat = null;
   
       String sDebugOther = null;
   
       String sDebugProf = null;
   
       String sDebugGProf = null;
       String sshiftassist= null; 
       String satomic = null;
       String sll64 = null;
       String smfpi= null;
       String smno_dpfp_lrsr= null;
       String smul3216= null;
       String smxy= null;
       String smlock= null;
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
           else if ((sID.endsWith(".option.target.core700")) ||     //Customized for ARC GNU core 700
             (sID.indexOf(".option.target.core700.") > 0))          //Customized for ARC GNU core 700
             sCore700 = sEnumCommand;                               //Customized for ARC GNU core 700
           else if ((sID.endsWith(".option.target.endiannes")) || 
             (sID.indexOf(".option.target.endiannes.") > 0))
             sProcessorEndiannes = sEnumCommand;
           else if ((sID.endsWith(".option.warnings.syntax")) ||  //Customized for ARC GNU fsyntax-only
             (sID.indexOf(".option.warnings.syntax") > 0))
             sSyntaxonly = sEnumCommand;
           else if ((sID.endsWith(".option.target.fpuem")&&(sProcessor.equalsIgnoreCase("-mEM"))) || 
             (sID.indexOf(".option.target.fpuem.") > 0)&&(sProcessor.equalsIgnoreCase("-mEM")))
             sFPUEM = sEnumCommand;
           else if ((sID.endsWith(".option.target.fpuhs"))&&(sProcessor.equalsIgnoreCase("-mHS")) || 
                   (sID.indexOf(".option.target.fpuhs.") > 0&&(sProcessor.equalsIgnoreCase("-mHS"))))
                   sFPUHS = sEnumCommand;
           else if ((sID.endsWith(".option.target.mpyhs"))&&(sProcessor.equalsIgnoreCase("-mHS")) || 
                   (sID.indexOf(".option.target.mpyhs") > 0)&&(sProcessor.equalsIgnoreCase("-mHS")))
             smpyhs = sEnumCommand;
           else if ((sID.endsWith(".option.target.mpyem"))&&(sProcessor.equalsIgnoreCase("-mEM")) || 
                   (sID.indexOf(".option.target.mpyem") > 0)&&(sProcessor.equalsIgnoreCase("-mEM")))
             smpyem = sEnumCommand;
           else if ((sID.endsWith(".option.target.fpi")&&(sProcessor.equalsIgnoreCase("-mEM")||sProcessor.equalsIgnoreCase("-mA6")||sProcessor.equalsIgnoreCase("-mA7"))) ||           //Customized for ARC GNU spfp 
                   (sID.indexOf(".option.target.fpi") > 0)&&(sProcessor.equalsIgnoreCase("-mEM")||sProcessor.equalsIgnoreCase("-mA6")||sProcessor.equalsIgnoreCase("-mA7")))               //Customized for ARC GNU spfp
             smfpi = sEnumCommand;                                          //Customized for ARC GNU 
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
           } else if ((sID.endsWith(".option.target.barrelshifter")) ||  //Customized for ARC GNU barrelshifter
             (sID.indexOf(".option.target.barrelshifter.") > 0)) {       //Customized for ARC GNU barrelshifter
             if (bVal)                                                   //Customized for ARC GNU barrelshifter
               sBarrelshifter = sCommand;                                //Customized for ARC GNU barrelshifter
           } else if ((sID.endsWith(".option.target.codedensity")&&((sProcessor.equalsIgnoreCase("-mEM"))||(sProcessor.equalsIgnoreCase("-mHS"))) ||    //Customized for ARC GNU codedensity
             (sID.indexOf(".option.target.codedensity.") > 0)&&((sProcessor.equalsIgnoreCase("-mEM"))||(sProcessor.equalsIgnoreCase("-mHS"))))) {         //Customized for ARC GNU codedensity
              if (bVal)                                                  //Customized for ARC GNU codedensity
              sCodedensity = sCommand;                                   //Customized for ARC GNU codedensity
           } else if ((sID.endsWith(".option.target.divide")) ||         //Customized for ARC GNU divide
             (sID.indexOf(".option.target.divide.") > 0)) {              //Customized for ARC GNU divide
             if (bVal)                                                   //Customized for ARC GNU divide
             sDivide = sCommand;                                         //Customized for ARC GNU divide
           } else if ((sID.endsWith(".option.target.normalize")) ||      //Customized for ARC GNU normalize
            (sID.indexOf(".option.target.normalize.") > 0)) {            //Customized for ARC GNU normalize
             if (bVal)                                                   //Customized for ARC GNU normalize
             sNormalize = sCommand;                                      //Customized for ARC GNU normalize
           } else if ((sID.endsWith(".option.target.mpy")) ||      //Customized for ARC GNU mpy
                   (sID.indexOf(".option.target.mpy.") > 0)) {            //Customized for ARC GNU mpy
                    if (bVal)                                                   //Customized for ARC GNU mpy
                    sMPY = sCommand;  
           } else if ((sID.endsWith(".option.target.swap")) ||           //Customized for ARC GNU swap
             (sID.indexOf(".option.target.swap.") > 0)) {                //Customized for ARC GNU swap
             if (bVal)                                                   //Customized for ARC GNU swap
              sSwap = sCommand;                                          //Customized for ARC GNU swap
           } else if ((sID.endsWith(".option.target.ea")&&(sProcessor.equalsIgnoreCase("-mA7")||sProcessor.equalsIgnoreCase("-mA6"))) ||           //Customized for ARC GNU ea
                   (sID.indexOf(".option.target.ea.") > 0)&&(sProcessor.equalsIgnoreCase("-mA7")||sProcessor.equalsIgnoreCase("-mA6"))) {               //Customized for ARC GNU ea
                   if (bVal)                                                   //Customized for ARC GNU ea
                   sEa = sCommand;                                          //Customized for ARC GNU ea
           } else if ((sID.endsWith(".option.target.mul3216")&&(sProcessor.equalsIgnoreCase("-mA7")||sProcessor.equalsIgnoreCase("-mA6"))) ||           //Customized for ARC GNU ea
                   (sID.indexOf(".option.target.mul3216.") > 0)&&(sProcessor.equalsIgnoreCase("-mA7")||sProcessor.equalsIgnoreCase("-mA6"))) {               //Customized for ARC GNU ea
                   if (bVal)                                                   //Customized for ARC GNU ea
                   smul3216 = sCommand; 
           } else if ((sID.endsWith(".option.target.xy")&&(sProcessor.equalsIgnoreCase("-mA7")||sProcessor.equalsIgnoreCase("-mA6"))) ||           //Customized for ARC GNU ea
                   (sID.indexOf(".option.target.xy.") > 0)&&(sProcessor.equalsIgnoreCase("-mA7")||sProcessor.equalsIgnoreCase("-mA6"))) {               //Customized for ARC GNU ea
                   if (bVal)                                                   //Customized for ARC GNU ea
                   smxy = sCommand; 
           } else if ((sID.endsWith(".option.target.lock")&&sProcessor.equalsIgnoreCase("-mA7")) ||           //Customized for ARC GNU ea
                   (sID.indexOf(".option.target.lock.") > 0)&&sProcessor.equalsIgnoreCase("-mA7")) {               //Customized for ARC GNU ea
                   if (bVal)                                                   //Customized for ARC GNU ea
                   smxy = sCommand; 
           } else if ((sID.endsWith(".option.target.shiftassist")) ||  
                   (sID.indexOf(".option.target.shiftassist.") > 0)) {       
               if (bVal)                                                  
                 sshiftassist = sCommand;                                
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
           else if ((sID.endsWith(".option.target.mno-dpfp-lrsr")&&sProcessor.equalsIgnoreCase("-mEM")) ||  
                   (sID.indexOf(".option.target.mno-dpfp-lrsr.") > 0)&&sProcessor.equalsIgnoreCase("-mEM")) {       
               if (bVal)                                                  
            	   smno_dpfp_lrsr = sCommand;                                
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
       if ((sFPUEM != null) && (sFPUEM.length() > 0))
           oList.add(sFPUEM);
       if ((sFPUHS != null) && (sFPUHS.length() > 0))
           oList.add(sFPUHS);
       if ((smpyhs != null) && (smpyhs.length() > 0))
           oList.add(smpyhs);
       if ((smpyem != null) && (smpyem.length() > 0))
           oList.add(smpyem);
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
       if ((sBarrelshifter != null) && (sBarrelshifter.length() > 0)) {//Customized for ARC GNU barrelshifter
         oList.add(sBarrelshifter);                                    //Customized for ARC GNU barrelshifter
         }
       if ((sCodedensity != null) && (sCodedensity.length() > 0)) {    //Customized for ARC GNU codedensity
           oList.add(sCodedensity);                                    //Customized for ARC GNU codedensity
           }
       if ((sDivide != null) && (sDivide.length() > 0)) {              //Customized for ARC GNU divide
           oList.add(sDivide);                                         //Customized for ARC GNU divide
           }
       if ((sNormalize != null) && (sNormalize.length() > 0)) {           //Customized for ARC GNU normalize
           oList.add(sNormalize);                                         //Customized for ARC GNU normalize
           }
       if ((sMPY != null) && (sMPY.length() > 0)) {           //Customized for ARC GNU mpy
           oList.add(sMPY);                                         //Customized for ARC GNU mpy
           }
       if ((sSwap != null) && (sSwap.length() > 0)) {                 //Customized for ARC GNU swap
           oList.add(sSwap);                                         //Customized for ARC GNU swap     
           }
       
       if ((smfpi != null) && (smfpi.length() > 0)) {                 
           oList.add(smfpi);                                            
           }  
       if ((smno_dpfp_lrsr != null) && (smno_dpfp_lrsr.length() > 0)) {                 
           oList.add(smno_dpfp_lrsr);                                            
           }  
       
       if ((sEa != null) && (sEa.length() > 0)) {                 
           oList.add(sEa);                                              
           } 
       if ((smul3216 != null) && (smul3216.length() > 0)) {                 
           oList.add(smul3216);                                              
           } 
       if ((smlock != null) && (smlock.length() > 0)) {                 
           oList.add(smlock);                                              
           } 
       if ((smxy != null) && (smxy.length() > 0)) {                 
           oList.add(smxy);                                              
           } 
       if ((sshiftassist != null) && (sshiftassist.length() > 0)) {           
           oList.add(sshiftassist);                                         
           }
       if ((satomic != null) && (satomic.length() > 0)) {                
           oList.add(satomic);                                             
           }  
       if ((sll64 != null) && (sll64.length() > 0)) {                
           oList.add(sll64);                                             
           }  
       if((sProcessor != null)){
    	   if (sProcessor.equalsIgnoreCase("-mA7")&&oList.indexOf(sMPY)<0)
           {
        	   oList.add("-mno-mpy");
        	   
           }
           if (sProcessor.equalsIgnoreCase("-mA7")&&oList.indexOf(sNormalize)<0)
           {
        	   oList.add("-mno-norm");
        	   
           }
       }  
       
//       if (sProcessor.equalsIgnoreCase("-mHS"))
//       {
//    	   oList.add("-mbarrel-shifter");
//    	   oList.add("-mshift-assist");
//    	   oList.add("-mcode-density");
//    	   oList.add("-mswap");
//    	   oList.add("-mnorm");
//    	   
//       }
       
       }
     
     return super.generateCommandLineInfo(oTool, sCommandName, (String[])oList.toArray(new String[0]), sOutputFlag, sOutputPrefix, sOutputName, asInputResources, sCommandLinePattern);
     }
   }

