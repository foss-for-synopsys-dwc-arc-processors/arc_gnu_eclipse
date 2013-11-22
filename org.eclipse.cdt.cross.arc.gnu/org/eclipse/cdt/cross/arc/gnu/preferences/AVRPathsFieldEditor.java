/*     */ package de.innot.avreclipse.ui.preferences;
/*     */ 
/*     */ import de.innot.avreclipse.core.paths.AVRPath;
/*     */ import de.innot.avreclipse.core.paths.AVRPathManager;
/*     */ import de.innot.avreclipse.core.paths.AVRPathManager.SourceType;
/*     */ import org.eclipse.core.runtime.IPath;
/*     */ import org.eclipse.jface.preference.FieldEditor;
/*     */ import org.eclipse.jface.resource.FontRegistry;
/*     */ import org.eclipse.jface.resource.JFaceResources;
/*     */ import org.eclipse.swt.custom.BusyIndicator;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.eclipse.ui.ISharedImages;
/*     */ import org.eclipse.ui.IWorkbench;
/*     */ import org.eclipse.ui.PlatformUI;
/*     */ 
/*     */ public class AVRPathsFieldEditor extends FieldEditor
/*     */ {
/*     */   private Table fTable;
/*     */   private Composite fButtons;
/*     */   private Button fEditButton;
/*     */   private Button fRescanButton;
/*  52 */   private boolean fValid = true;
/*     */   private static final int COLUMN_NAME = 0;
/*     */   private static final int COLUMN_TYPE = 1;
/*     */   private static final int COLUMN_PATH = 2;
/*  61 */   final Font fBoldFont = JFaceResources.getFontRegistry()
/*  61 */     .getBold("org.eclipse.jface.dialogfont");
/*     */ 
/*  63 */   final Font fDialogFont = JFaceResources.getFontRegistry()
/*  63 */     .get("org.eclipse.jface.dialogfont");
/*     */ 
/*     */   public AVRPathsFieldEditor(Composite parent)
/*     */   {
/* 142 */     super("avrpaths", "AVR Paths:", parent);
/*     */   }
/*     */ 
/*     */   protected void adjustForNumColumns(int numColumns)
/*     */   {
/* 155 */     GridData buttonsData = (GridData)this.fButtons.getLayoutData();
/* 156 */     buttonsData.horizontalSpan = 1;
/*     */ 
/* 158 */     GridData tableData = (GridData)this.fTable.getLayoutData();
/* 159 */     tableData.horizontalSpan = (numColumns - 1);
/*     */   }
/*     */ 
/*     */   protected void doFillIntoGrid(Composite parent, int numColumns)
/*     */   {
/* 174 */     this.fTable = new Table(parent, 67588);
/* 175 */     GridData tableData = new GridData(1808);
/* 176 */     tableData.horizontalSpan = (numColumns - 1);
/* 177 */     this.fTable.setLayoutData(tableData);
/* 178 */     this.fTable.addSelectionListener(new TableSelectionListener(null));
/*     */ 
/* 180 */     TableColumn nameColumn = new TableColumn(this.fTable, 16384, 0);
/* 181 */     TableColumn typeColumn = new TableColumn(this.fTable, 16384, 1);
/* 182 */     TableColumn pathColumn = new TableColumn(this.fTable, 16384, 2);
/*     */ 
/* 184 */     nameColumn.setText("Path to");
/* 185 */     typeColumn.setText("Source");
/* 186 */     pathColumn.setText("Current value");
/* 187 */     this.fTable.setHeaderVisible(true);
/*     */ 
/* 191 */     this.fButtons = new Composite(parent, 524288);
/*     */ 
/* 193 */     GridData buttonsData = new GridData(3);
/* 194 */     buttonsData.horizontalSpan = 1;
/* 195 */     buttonsData.horizontalAlignment = 4;
/* 196 */     this.fButtons.setLayoutData(buttonsData);
/*     */ 
/* 198 */     FillLayout buttonsLayout = new FillLayout(512);
/* 199 */     buttonsLayout.spacing = 5;
/* 200 */     this.fButtons.setLayout(buttonsLayout);
/*     */ 
/* 204 */     this.fEditButton = new Button(this.fButtons, 8);
/*     */ 
/* 206 */     this.fEditButton.setText("Edit...");
/* 207 */     this.fEditButton.addSelectionListener(new ButtonSelectionListener(null));
/* 208 */     this.fEditButton.setEnabled(false);
/*     */ 
/* 212 */     this.fRescanButton = new Button(this.fButtons, 8);
/*     */ 
/* 214 */     this.fRescanButton.setText("Rescan");
/* 215 */     this.fRescanButton.addSelectionListener(new ButtonSelectionListener(null));
/* 216 */     this.fRescanButton.setEnabled(false);
/*     */   }
/*     */ 
/*     */   protected void doLoad()
/*     */   {
/* 229 */     AVRPath[] allpaths = AVRPath.values();
/*     */ 
/* 231 */     for (AVRPath current : allpaths)
/*     */     {
/* 234 */       AVRPathManager item = new AVRPathManager(current);
/*     */ 
/* 236 */       TableItem ti = new TableItem(this.fTable, 0);
/* 237 */       ti.setData(item);
/* 238 */       updateTableItem(ti);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doLoadDefault()
/*     */   {
/* 252 */     TableItem[] allitems = this.fTable.getItems();
/*     */ 
/* 254 */     for (TableItem tableitem : allitems)
/*     */     {
/* 256 */       AVRPathManager path = (AVRPathManager)tableitem.getData();
/* 257 */       path.setToDefault();
/* 258 */       updateTableItem(tableitem);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doStore()
/*     */   {
/* 271 */     TableItem[] allitems = this.fTable.getItems();
/*     */ 
/* 273 */     for (TableItem tableitem : allitems)
/*     */     {
/* 276 */       AVRPathManager path = (AVRPathManager)tableitem.getData();
/* 277 */       path.store();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void store()
/*     */   {
/* 292 */     if (getPreferenceStore() == null) {
/* 293 */       return;
/*     */     }
/* 295 */     doStore();
/*     */   }
/*     */ 
/*     */   public int getNumberOfControls()
/*     */   {
/* 306 */     return 2;
/*     */   }
/*     */ 
/*     */   public boolean isValid()
/*     */   {
/* 316 */     return this.fValid;
/*     */   }
/*     */ 
/*     */   protected void refreshValidState()
/*     */   {
/* 326 */     super.refreshValidState();
/*     */ 
/* 334 */     TableItem[] allitems = this.fTable.getItems();
/* 335 */     boolean oldValid = this.fValid;
/* 336 */     boolean newValid = true;
/* 337 */     String invalidPath = null;
/*     */ 
/* 339 */     for (TableItem ti : allitems) {
/* 340 */       AVRPathManager pathitem = (AVRPathManager)ti.getData();
/* 341 */       if ((!pathitem.isValid()) && (!pathitem.isOptional())) {
/* 342 */         newValid = false;
/* 343 */         invalidPath = pathitem.getName();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 348 */     this.fValid = newValid;
/* 349 */     if (!this.fValid)
/* 350 */       showErrorMessage("Path for '" + invalidPath + "' is not valid");
/*     */     else {
/* 352 */       clearErrorMessage();
/*     */     }
/*     */ 
/* 356 */     if (newValid != oldValid)
/* 357 */       fireStateChanged("field_editor_is_valid", oldValid, newValid);
/*     */   }
/*     */ 
/*     */   private void updateTableItem(TableItem item)
/*     */   {
/* 370 */     AVRPathManager path = (AVRPathManager)item.getData();
/*     */ 
/* 373 */     boolean valid = path.isValid();
/* 374 */     boolean optional = path.isOptional();
/* 375 */     boolean empty = path.getPath().isEmpty();
/*     */ 
/* 377 */     if ((valid) && (!empty))
/*     */     {
/* 379 */       item.setImage(null);
/* 380 */     } else if (((valid) && (empty)) || ((!valid) && (optional)))
/*     */     {
/* 382 */       item.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
/* 383 */         "IMG_OBJS_WARN_TSK"));
/*     */     }
/*     */     else {
/* 386 */       item.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
/* 387 */         "IMG_OBJS_ERROR_TSK"));
/*     */     }
/*     */ 
/* 390 */     item.setText(0, path.getName());
/* 391 */     item.setText(1, path.getSourceType().toString());
/* 392 */     item.setText(2, path.getPath().toOSString());
/*     */ 
/* 395 */     switch ($SWITCH_TABLE$de$innot$avreclipse$core$paths$AVRPathManager$SourceType()[path.getSourceType().ordinal()]) {
/*     */     case 2:
/* 397 */       item.setFont(1, this.fDialogFont);
/* 398 */       item.setFont(2, this.fDialogFont);
/* 399 */       item.setForeground(2, this.fTable.getDisplay().getSystemColor(
/* 400 */         16));
/* 401 */       break;
/*     */     case 1:
/* 403 */       item.setFont(1, this.fDialogFont);
/* 404 */       item.setFont(2, this.fDialogFont);
/* 405 */       item.setForeground(2, this.fTable.getDisplay().getSystemColor(
/* 406 */         16));
/* 407 */       break;
/*     */     case 3:
/* 409 */       item.setFont(1, this.fBoldFont);
/* 410 */       item.setFont(2, this.fBoldFont);
/* 411 */       item
/* 412 */         .setForeground(2, this.fTable.getDisplay().getSystemColor(
/* 413 */         2));
/*     */     }
/*     */ 
/* 417 */     this.fTable.getColumn(0).pack();
/* 418 */     this.fTable.getColumn(1).pack();
/* 419 */     this.fTable.getColumn(2).pack();
/* 420 */     this.fTable.layout();
/*     */   }
/*     */ 
/*     */   private class ButtonSelectionListener
/*     */     implements SelectionListener
/*     */   {
/*     */     private ButtonSelectionListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void widgetDefaultSelected(SelectionEvent e)
/*     */     {
/*  73 */       widgetSelected(e);
/*     */     }
/*     */ 
/*     */     public void widgetSelected(SelectionEvent e)
/*     */     {
/*  79 */       TableItem selected = AVRPathsFieldEditor.this.fTable.getSelection()[0];
/*  80 */       AVRPathManager path = (AVRPathManager)selected.getData();
/*     */ 
/*  82 */       if (e.getSource() == AVRPathsFieldEditor.this.fEditButton) {
/*  83 */         PathSettingDialog dialog = new PathSettingDialog(AVRPathsFieldEditor.this.fTable.getShell(), path);
/*  84 */         if (dialog.open() == 0)
/*     */         {
/*  87 */           path = dialog.getResult();
/*     */         }
/*  89 */       } else if (e.getSource() == AVRPathsFieldEditor.this.fRescanButton)
/*     */       {
/*  92 */         AVRPathManager finalpath = path;
/*  93 */         BusyIndicator.showWhile(AVRPathsFieldEditor.this.fTable.getDisplay(), new Runnable(finalpath) {
/*     */           public void run() {
/*  95 */             this.val$finalpath.getSystemPath(true);
/*     */           } } );
/*     */       }
/*  99 */       selected.setData(path);
/* 100 */       AVRPathsFieldEditor.this.updateTableItem(selected);
/* 101 */       AVRPathsFieldEditor.this.refreshValidState();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TableSelectionListener
/*     */     implements SelectionListener
/*     */   {
/*     */     private TableSelectionListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void widgetDefaultSelected(SelectionEvent e)
/*     */     {
/* 114 */       widgetSelected(e);
/*     */     }
/*     */ 
/*     */     public void widgetSelected(SelectionEvent e)
/*     */     {
/* 119 */       AVRPathsFieldEditor.this.fEditButton.setEnabled(true);
/*     */ 
/* 122 */       TableItem selected = AVRPathsFieldEditor.this.fTable.getSelection()[0];
/* 123 */       AVRPathManager path = (AVRPathManager)selected.getData();
/* 124 */       switch ($SWITCH_TABLE$de$innot$avreclipse$core$paths$AVRPathManager$SourceType()[path.getSourceType().ordinal()]) {
/*     */       case 2:
/* 126 */         AVRPathsFieldEditor.this.fRescanButton.setEnabled(true);
/* 127 */         break;
/*     */       default:
/* 129 */         AVRPathsFieldEditor.this.fRescanButton.setEnabled(false);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\yunluz\Documents\importfile\ARC_GNU_Plugin relevant\other company\avreclipse.2.4.0.final.p2repository\plugins\de.innot.avreclipse.ui_2.4.0.201203041437\
 * Qualified Name:     de.innot.avreclipse.ui.preferences.AVRPathsFieldEditor
 * JD-Core Version:    0.6.0
 */