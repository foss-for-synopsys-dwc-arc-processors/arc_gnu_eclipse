/*     */ package de.innot.avreclipse.ui.preferences;
/*     */ 
/*     */ import de.innot.avreclipse.AVRPlugin;
/*     */ import de.innot.avreclipse.core.avrdude.ProgrammerConfig;
/*     */ import de.innot.avreclipse.core.avrdude.ProgrammerConfigManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.eclipse.core.runtime.IStatus;
/*     */ import org.eclipse.core.runtime.Status;
/*     */ import org.eclipse.jface.dialogs.ErrorDialog;
/*     */ import org.eclipse.jface.preference.FieldEditor;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.osgi.service.prefs.BackingStoreException;
/*     */ 
/*     */ public class ProgConfigListFieldEditor extends FieldEditor
/*     */ {
/*     */   private Table fTableControl;
/*     */   private Composite fButtonComposite;
/*     */   private Button fAddButton;
/*     */   private Button fRemoveButton;
/*     */   private Button fEditButton;
/*     */   private List<ProgrammerConfig> fRemovedConfigs;
/*  81 */   private final ProgrammerConfigManager fCfgManager = ProgrammerConfigManager.getDefault();
/*     */ 
/*     */   public ProgConfigListFieldEditor(String label, Composite parent)
/*     */   {
/*  97 */     super.setLabelText(label);
/*  98 */     createControl(parent);
/*     */   }
/*     */ 
/*     */   protected void adjustForNumColumns(int numColumns)
/*     */   {
/* 109 */     Control control = getLabelControl();
/* 110 */     ((GridData)control.getLayoutData()).horizontalSpan = numColumns;
/* 111 */     ((GridData)this.fTableControl.getLayoutData()).horizontalSpan = (numColumns - 1);
/*     */   }
/*     */ 
/*     */   protected void doFillIntoGrid(Composite parent, int numColumns)
/*     */   {
/* 124 */     Control control = getLabelControl(parent);
/* 125 */     GridData gd = new GridData();
/* 126 */     gd.horizontalSpan = numColumns;
/* 127 */     control.setLayoutData(gd);
/*     */ 
/* 129 */     this.fTableControl = getTableControl(parent);
/* 130 */     gd = new GridData(4, 4, true, true, numColumns - 1, 1);
/* 131 */     this.fTableControl.setLayoutData(gd);
/*     */ 
/* 133 */     this.fButtonComposite = getButtonBoxComposite(parent);
/* 134 */     gd = new GridData();
/* 135 */     gd.verticalAlignment = 1;
/* 136 */     this.fButtonComposite.setLayoutData(gd);
/*     */   }
/*     */ 
/*     */   protected void doLoad()
/*     */   {
/* 146 */     if (this.fTableControl != null) {
/* 147 */       Set allconfigids = this.fCfgManager.getAllConfigIDs();
/* 148 */       for (String configid : allconfigids) {
/* 149 */         if (configid.length() > 0) {
/* 150 */           ProgrammerConfig config = this.fCfgManager.getConfig(configid);
/* 151 */           TableItem item = new TableItem(this.fTableControl, 0);
/* 152 */           item.setText(new String[] { config.getName(), config.getDescription() });
/* 153 */           item.setData(config);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 158 */     this.fRemovedConfigs = new ArrayList();
/*     */   }
/*     */ 
/*     */   protected void doLoadDefault()
/*     */   {
/* 171 */     this.fTableControl.removeAll();
/* 172 */     doLoad();
/*     */   }
/*     */ 
/*     */   protected void doStore()
/*     */   {
/* 185 */     TableItem[] allitems = this.fTableControl.getItems();
/*     */ 
/* 188 */     for (TableItem item : allitems) {
/* 189 */       ProgrammerConfig config = (ProgrammerConfig)item.getData();
/*     */       try {
/* 191 */         this.fCfgManager.saveConfig(config);
/*     */       } catch (BackingStoreException e) {
/* 193 */         IStatus status = new Status(4, "de.innot.avreclipse.core", 
/* 194 */           "Can't save Programmer Configuration [" + config.getName() + 
/* 195 */           "] to the preference storage area", e);
/* 196 */         AVRPlugin.getDefault().log(status);
/* 197 */         ErrorDialog.openError(this.fTableControl.getShell(), "Programmer Configuration Error", 
/* 198 */           null, status);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 203 */     for (ProgrammerConfig config : this.fRemovedConfigs) {
/*     */       try {
/* 205 */         this.fCfgManager.deleteConfig(config);
/*     */       } catch (BackingStoreException e) {
/* 207 */         Object status = new Status(4, "de.innot.avreclipse.core", 
/* 208 */           "Can't delete Programmer Configuration [" + config.getName() + 
/* 209 */           "] from the preference storage area", e);
/* 210 */         AVRPlugin.getDefault().log((IStatus)status);
/* 211 */         ErrorDialog.openError(this.fTableControl.getShell(), "Programmer Configuration Error", 
/* 212 */           null, (IStatus)status);
/*     */       }
/*     */     }
/* 215 */     this.fRemovedConfigs.clear();
/*     */   }
/*     */ 
/*     */   public int getNumberOfControls()
/*     */   {
/* 226 */     return 2;
/*     */   }
/*     */ 
/*     */   public void setFocus()
/*     */   {
/* 236 */     if (this.fTableControl != null)
/* 237 */       this.fTableControl.setFocus();
/*     */   }
/*     */ 
/*     */   public Table getTableControl(Composite parent)
/*     */   {
/* 249 */     if (this.fTableControl == null)
/*     */     {
/* 252 */       this.fTableControl = new Table(parent, 68356);
/*     */ 
/* 254 */       this.fTableControl.setFont(parent.getFont());
/* 255 */       this.fTableControl.setLinesVisible(true);
/* 256 */       this.fTableControl.setHeaderVisible(true);
/*     */ 
/* 258 */       this.fTableControl.addSelectionListener(new SelectionAdapter()
/*     */       {
/*     */         public void widgetSelected(SelectionEvent e)
/*     */         {
/* 266 */           Widget widget = e.widget;
/* 267 */           if (widget == ProgConfigListFieldEditor.this.fTableControl)
/* 268 */             ProgConfigListFieldEditor.this.selectionChanged();
/*     */         }
/*     */       });
/* 273 */       this.fTableControl.addDisposeListener(new DisposeListener()
/*     */       {
/*     */         public void widgetDisposed(DisposeEvent event)
/*     */         {
/* 280 */           ProgConfigListFieldEditor.this.fTableControl = null;
/*     */         }
/*     */       });
/* 284 */       TableColumn column = new TableColumn(this.fTableControl, 0);
/* 285 */       column.setText("Configuration");
/* 286 */       column.setWidth(100);
/* 287 */       column = new TableColumn(this.fTableControl, 0);
/* 288 */       column.setText("Description");
/* 289 */       column.setWidth(200);
/*     */     }
/*     */     else {
/* 292 */       checkParent(this.fTableControl, parent);
/*     */     }
/* 294 */     return this.fTableControl;
/*     */   }
/*     */ 
/*     */   public Composite getButtonBoxComposite(Composite parent)
/*     */   {
/* 305 */     if (this.fButtonComposite == null) {
/* 306 */       this.fButtonComposite = new Composite(parent, 0);
/* 307 */       GridLayout layout = new GridLayout(1, false);
/* 308 */       layout.marginWidth = 0;
/* 309 */       this.fButtonComposite.setLayout(layout);
/* 310 */       createButtons(this.fButtonComposite);
/* 311 */       this.fButtonComposite.addDisposeListener(new DisposeListener()
/*     */       {
/*     */         public void widgetDisposed(DisposeEvent event)
/*     */         {
/* 318 */           ProgConfigListFieldEditor.this.fAddButton = null;
/* 319 */           ProgConfigListFieldEditor.this.fRemoveButton = null;
/* 320 */           ProgConfigListFieldEditor.this.fEditButton = null;
/*     */         } } );
/*     */     }
/*     */     else {
/* 325 */       checkParent(this.fButtonComposite, parent);
/*     */     }
/*     */ 
/* 328 */     selectionChanged();
/* 329 */     return this.fButtonComposite;
/*     */   }
/*     */ 
/*     */   private void createButtons(Composite box)
/*     */   {
/* 339 */     this.fAddButton = createPushButton(box, "Add...");
/* 340 */     this.fEditButton = createPushButton(box, "Edit...");
/* 341 */     this.fRemoveButton = createPushButton(box, "Remove");
/*     */   }
/*     */ 
/*     */   private Button createPushButton(Composite parent, String label)
/*     */   {
/* 354 */     Button button = new Button(parent, 8);
/* 355 */     button.setText(label);
/* 356 */     button.setFont(parent.getFont());
/* 357 */     GridData data = new GridData(768);
/* 358 */     int widthHint = convertHorizontalDLUsToPixels(button, 61);
/* 359 */     data.widthHint = Math.max(widthHint, button.computeSize(-1, -1, true).x);
/* 360 */     button.setLayoutData(data);
/* 361 */     button.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/* 369 */         Widget widget = e.widget;
/* 370 */         if (widget == ProgConfigListFieldEditor.this.fAddButton)
/* 371 */           ProgConfigListFieldEditor.this.editButtonAction(true);
/* 372 */         else if (widget == ProgConfigListFieldEditor.this.fRemoveButton)
/* 373 */           ProgConfigListFieldEditor.this.removeButtonAction();
/* 374 */         else if (widget == ProgConfigListFieldEditor.this.fEditButton)
/* 375 */           ProgConfigListFieldEditor.this.editButtonAction(false);
/*     */       }
/*     */     });
/* 379 */     return button;
/*     */   }
/*     */ 
/*     */   private void selectionChanged()
/*     */   {
/* 393 */     int index = this.fTableControl.getSelectionIndex();
/*     */ 
/* 395 */     this.fRemoveButton.setEnabled(index >= 0);
/* 396 */     this.fEditButton.setEnabled(index >= 0);
/*     */ 
/* 399 */     this.fTableControl.redraw();
/*     */   }
/*     */ 
/*     */   private void removeButtonAction()
/*     */   {
/* 413 */     setPresentsDefaultValue(false);
/* 414 */     int index = this.fTableControl.getSelectionIndex();
/* 415 */     if (index >= 0) {
/* 416 */       TableItem ti = this.fTableControl.getItem(index);
/* 417 */       this.fRemovedConfigs.add((ProgrammerConfig)ti.getData());
/* 418 */       this.fTableControl.remove(index);
/* 419 */       selectionChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void editButtonAction(boolean createnew)
/*     */   {
/* 430 */     setPresentsDefaultValue(false);
/* 431 */     ProgrammerConfig config = null;
/* 432 */     TableItem ti = null;
/*     */ 
/* 437 */     Set allconfigs = new HashSet();
/* 438 */     TableItem[] allitems = this.fTableControl.getItems();
/* 439 */     for (TableItem item : allitems) {
/* 440 */       allconfigs.add(item.getText(0));
/*     */     }
/*     */ 
/* 443 */     if (createnew)
/*     */     {
/* 446 */       String basename = "New Configuration";
/* 447 */       String defaultname = basename;
/* 448 */       int i = 1;
/* 449 */       while (allconfigs.contains(defaultname)) {
/* 450 */         defaultname = basename + " (" + i++ + ")";
/*     */       }
/* 452 */       config = this.fCfgManager.createNewConfig();
/* 453 */       config.setName(defaultname);
/*     */     }
/*     */     else {
/* 456 */       ti = this.fTableControl.getItem(this.fTableControl.getSelectionIndex());
/* 457 */       config = (ProgrammerConfig)ti.getData();
/*     */     }
/*     */ 
/* 463 */     AVRDudeConfigEditor dialog = new AVRDudeConfigEditor(this.fTableControl.getShell(), config, 
/* 464 */       allconfigs);
/* 465 */     if (dialog.open() == 0)
/*     */     {
/* 467 */       ProgrammerConfig newconfig = dialog.getResult();
/*     */ 
/* 469 */       if (createnew) {
/* 470 */         ti = new TableItem(this.fTableControl, 0);
/*     */       }
/*     */ 
/* 474 */       if (ti != null) {
/* 475 */         ti.setText(new String[] { newconfig.getName(), newconfig.getDescription() });
/* 476 */         ti.setData(newconfig);
/*     */       }
/* 478 */       selectionChanged();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\yunluz\Documents\importfile\ARC_GNU_Plugin relevant\other company\avreclipse.2.4.0.final.p2repository\plugins\de.innot.avreclipse.ui_2.4.0.201203041437\
 * Qualified Name:     de.innot.avreclipse.ui.preferences.ProgConfigListFieldEditor
 * JD-Core Version:    0.6.0
 */