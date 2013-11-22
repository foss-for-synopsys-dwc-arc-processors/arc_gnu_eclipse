/*    */ package de.innot.avreclipse.ui.preferences;
/*    */ 
/*    */ import org.eclipse.jface.preference.PreferencePage;
/*    */ import org.eclipse.jface.resource.JFaceResources;
/*    */ import org.eclipse.swt.layout.GridLayout;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.eclipse.ui.IWorkbench;
/*    */ import org.eclipse.ui.IWorkbenchPreferencePage;
/*    */ 
/*    */ public class MainPreferencePage extends PreferencePage
/*    */   implements IWorkbenchPreferencePage
/*    */ {
/*    */   public MainPreferencePage()
/*    */   {
/* 41 */     setDescription("AVR Eclipse Plugin Preferences");
/*    */   }
/*    */ 
/*    */   protected Control createContents(Composite parent)
/*    */   {
/* 47 */     Composite content = new Composite(parent, 0);
/* 48 */     GridLayout layout = new GridLayout();
/* 49 */     layout.numColumns = 1;
/* 50 */     layout.marginHeight = 0;
/* 51 */     layout.marginWidth = 0;
/* 52 */     content.setLayout(layout);
/* 53 */     content.setFont(parent.getFont());
/*    */ 
/* 55 */     Label filler = new Label(content, 0);
/* 56 */     filler.setText("");
/*    */ 
/* 58 */     Label label = createDescriptionLabel(content);
/* 59 */     label
/* 60 */       .setText("Please select one of the sub-pages to change the settings for the AVR plugin.");
/*    */ 
/* 62 */     filler = new Label(content, 0);
/* 63 */     filler.setText("");
/*    */ 
/* 65 */     createNoteComposite(JFaceResources.getDialogFont(), content, "AVRDude:", 
/* 66 */       "Manage the configuration of programmer devices for avrdude.\n");
/*    */ 
/* 68 */     createNoteComposite(JFaceResources.getDialogFont(), content, "Paths:", 
/* 69 */       "Manage the paths to the external tools and files used by the plugin.\n");
/*    */ 
/* 71 */     return content;
/*    */   }
/*    */ 
/*    */   public void init(IWorkbench workbench)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\yunluz\Documents\importfile\ARC_GNU_Plugin relevant\other company\avreclipse.2.4.0.final.p2repository\plugins\de.innot.avreclipse.ui_2.4.0.201203041437\
 * Qualified Name:     de.innot.avreclipse.ui.preferences.MainPreferencePage
 * JD-Core Version:    0.6.0
 */