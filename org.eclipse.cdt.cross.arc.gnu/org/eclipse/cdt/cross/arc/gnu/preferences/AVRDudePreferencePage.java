/*     */ package de.innot.avreclipse.ui.preferences;
/*     */ 
/*     */ import de.innot.avreclipse.core.preferences.AVRDudePreferences;
/*     */ import org.eclipse.jface.preference.BooleanFieldEditor;
/*     */ import org.eclipse.jface.preference.FieldEditorPreferencePage;
/*     */ import org.eclipse.jface.preference.FileFieldEditor;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.ui.IWorkbench;
/*     */ import org.eclipse.ui.IWorkbenchPreferencePage;
/*     */ 
/*     */ public class AVRDudePreferencePage extends FieldEditorPreferencePage
/*     */   implements IWorkbenchPreferencePage
/*     */ {
/*     */   private FileFieldEditor fFileEditor;
/*     */   private ProgConfigListFieldEditor fConfigEditor;
/*     */ 
/*     */   public AVRDudePreferencePage()
/*     */   {
/*  46 */     super(1);
/*     */ 
/*  49 */     setPreferenceStore(AVRDudePreferences.getPreferenceStore());
/*  50 */     setDescription("AVRDude Global Settings");
/*     */   }
/*     */ 
/*     */   public void createFieldEditors()
/*     */   {
/*  60 */     Composite parent = getFieldEditorParent();
/*     */ 
/*  63 */     BooleanFieldEditor useconsole = new BooleanFieldEditor(
/*  64 */       "avrdudeUseConsole", 
/*  65 */       "Log internal AVRDude output to console", parent);
/*  66 */     addField(useconsole);
/*     */ 
/*  69 */     Label separator = new Label(parent, 258);
/*  70 */     separator.setLayoutData(new GridData(4, 0, true, false, 3, 1));
/*     */ 
/*  73 */     MyBooleanFieldEditor usecustomconfig = new MyBooleanFieldEditor(
/*  74 */       "customconfigfile", 
/*  75 */       "Use custom configuration file for AVRDude", parent);
/*  76 */     addField(usecustomconfig);
/*     */ 
/*  78 */     this.fFileEditor = 
/*  79 */       new FileFieldEditor("avrdudeconf", "AVRDude config file", 
/*  79 */       parent);
/*  80 */     addField(this.fFileEditor);
/*     */ 
/*  83 */     separator = new Label(parent, 258);
/*  84 */     separator.setLayoutData(new GridData(4, 0, true, false, 3, 1));
/*     */ 
/*  87 */     this.fConfigEditor = new ProgConfigListFieldEditor("Programmer configurations", parent);
/*  88 */     addField(this.fConfigEditor);
/*     */   }
/*     */ 
/*     */   public void init(IWorkbench workbench)
/*     */   {
/*     */   }
/*     */ 
/*     */   private class MyBooleanFieldEditor extends BooleanFieldEditor
/*     */   {
/*     */     private Composite fParent;
/*     */ 
/*     */     public MyBooleanFieldEditor(String name, String label, Composite parent)
/*     */     {
/* 121 */       super(label, parent);
/* 122 */       this.fParent = parent;
/*     */     }
/*     */ 
/*     */     protected void valueChanged(boolean oldValue, boolean newValue)
/*     */     {
/* 133 */       super.valueChanged(oldValue, newValue);
/* 134 */       enableConfigFileEditor(newValue);
/*     */     }
/*     */ 
/*     */     protected void doLoad()
/*     */     {
/* 144 */       super.doLoad();
/* 145 */       enableConfigFileEditor(getBooleanValue());
/*     */     }
/*     */ 
/*     */     protected void doLoadDefault()
/*     */     {
/* 155 */       super.doLoadDefault();
/* 156 */       enableConfigFileEditor(getBooleanValue());
/*     */     }
/*     */ 
/*     */     private void enableConfigFileEditor(boolean newValue)
/*     */     {
/* 167 */       if (AVRDudePreferencePage.this.fFileEditor != null)
/* 168 */         if (newValue)
/* 169 */           AVRDudePreferencePage.this.fFileEditor.setEnabled(true, this.fParent);
/*     */         else
/* 171 */           AVRDudePreferencePage.this.fFileEditor.setEnabled(false, this.fParent);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\yunluz\Documents\importfile\ARC_GNU_Plugin relevant\other company\avreclipse.2.4.0.final.p2repository\plugins\de.innot.avreclipse.ui_2.4.0.201203041437\
 * Qualified Name:     de.innot.avreclipse.ui.preferences.AVRDudePreferencePage
 * JD-Core Version:    0.6.0
 */