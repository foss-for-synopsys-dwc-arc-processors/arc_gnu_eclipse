/*    */ package de.innot.avreclipse.ui.preferences;
/*    */ 
/*    */ import de.innot.avreclipse.core.preferences.AVRPathsPreferences;
/*    */ import org.eclipse.jface.preference.BooleanFieldEditor;
/*    */ import org.eclipse.jface.preference.FieldEditorPreferencePage;
/*    */ import org.eclipse.jface.preference.IPreferenceStore;
/*    */ import org.eclipse.jface.resource.JFaceResources;
/*    */ import org.eclipse.swt.layout.GridData;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.eclipse.ui.IWorkbench;
/*    */ import org.eclipse.ui.IWorkbenchPreferencePage;
/*    */ 
/*    */ public class PathsPreferencePage extends FieldEditorPreferencePage
/*    */   implements IWorkbenchPreferencePage
/*    */ {
/* 47 */   private IPreferenceStore fPreferenceStore = null;
/*    */ 
/*    */   public PathsPreferencePage() {
/* 50 */     super(1);
/*    */ 
/* 53 */     this.fPreferenceStore = AVRPathsPreferences.getPreferenceStore();
/* 54 */     setPreferenceStore(this.fPreferenceStore);
/* 55 */     setDescription("Path Settings for the AVR Eclipse Plugin");
/*    */   }
/*    */ 
/*    */   public void createFieldEditors()
/*    */   {
/* 70 */     Composite parent = getFieldEditorParent();
/*    */ 
/* 72 */     Label filler = new Label(parent, 0);
/* 73 */     filler.setLayoutData(new GridData(4, 0, true, false, 2, 1));
/*    */ 
/* 77 */     BooleanFieldEditor autoScanBoolean = new BooleanFieldEditor(
/* 78 */       "NoScanAtStartup", 
/* 79 */       "Disable search for system paths at startup", 0, parent);
/* 80 */     addField(autoScanBoolean);
/*    */ 
/* 82 */     Composite note = createNoteComposite(JFaceResources.getDialogFont(), parent, "Note:", 
/* 83 */       "If disabled, a manual rescan may be required when a new avr-gcc toolchain has been installed.\n");
/* 84 */     note.setLayoutData(new GridData(4, 0, true, false, 2, 1));
/*    */ 
/* 86 */     filler = new Label(parent, 0);
/* 87 */     filler.setLayoutData(new GridData(4, 0, true, false, 2, 1));
/*    */ 
/* 91 */     AVRPathsFieldEditor pathEditor = new AVRPathsFieldEditor(parent);
/* 92 */     addField(pathEditor);
/*    */   }
/*    */ 
/*    */   public void init(IWorkbench workbench)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Documents\importfile\ARC_GNU_Plugin relevant\other company\avreclipse.2.4.0.final.p2repository\plugins\de.innot.avreclipse.ui_2.4.0.201203041437\
 * Qualified Name:     de.innot.avreclipse.ui.preferences.PathsPreferencePage
 * JD-Core Version:    0.6.0
 */