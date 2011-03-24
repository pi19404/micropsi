package org.micropsi.eclipse.mindconsole.groups;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.NodeSpaceModule;

/**
 * 
 * 
 * 
 */
public class SpaceGroup {
	
	private Text assoConst;
	private Text dissoConst;
	private Text strengthConst;
	private Button decayAllowed;
	
	private boolean assoChanged = false;
	private boolean dissoChanged = false;
	private boolean strengthChanged = false;
	private boolean decayAllowedChanged = false;
	
	private NodeSpaceModule space;
//	private NetFacadeIF net;
	
	public SpaceGroup(Composite parent, NetFacadeIF net, NodeSpaceModule space) {
		this.space = space;
//		this.net = net;
		createControls(parent);
	}
	
	protected void createControls(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		topLevel.setLayout(layout);
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());

		Label label = new Label(topLevel, SWT.NONE);
		label.setText("ID");
		
		label = new Label(topLevel, SWT.NONE);
		label.setText(space.getID()+(space.hasName() ? " ("+space.getEntityName()+")" : ""));
				
		label = new Label(topLevel, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.verticalSpan = 3;
		label.setLayoutData(data);		
		
		label = new Label(topLevel, SWT.NONE);
		label.setText("AssoConst");
		
		assoConst = new Text(topLevel, SWT.NONE);
		assoConst.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		assoConst.setText(Double.toString(space.getLearningConstant()));
		assoConst.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				assoChanged = true;
			}
		});
		
		label = new Label(topLevel, SWT.NONE);
		label.setText("DissoConst");
		
		dissoConst = new Text(topLevel, SWT.NONE);
		dissoConst.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dissoConst.setText(Double.toString(space.getDissociationConstant()));
		dissoConst.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dissoChanged = true;
			}
		});

		label = new Label(topLevel, SWT.NONE);
		label.setText("StrengtheningConst");
		
		strengthConst = new Text(topLevel, SWT.NONE);
		strengthConst.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		strengthConst.setText(Double.toString(space.getStrengtheningConstant()));
		strengthConst.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				strengthChanged = true;
			}
		});

		label = new Label(topLevel, SWT.NONE);
		label.setText("DecayAllowed");
		
		decayAllowed = new Button(topLevel, SWT.CHECK);
		decayAllowed.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		decayAllowed.setSelection(space.isDecayAllowed());
		decayAllowed.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				decayAllowedChanged = true;
			}			
		});
		
	}
	
	public double getAssoConst() {
		try {
			return Double.parseDouble(assoConst.getText());
		} catch (Exception e) {
			return space.getLearningConstant();
		}
	}
	
	public double getDissoConst() {
		try {
			return Double.parseDouble(dissoConst.getText());
		} catch (Exception e) {
			return space.getDissociationConstant();
		}
	}

	public double getStrengtheningConst() {
		try {
			return Double.parseDouble(strengthConst.getText());
		} catch (Exception e) {
			return space.getStrengtheningConstant();
		}
	}
	
	public boolean getDecayAllowed() {
		return decayAllowed.getSelection();
	}
	
	/**
	 * @return boolean
	 */
	public boolean isAssoChanged() {
		return assoChanged;
	}

	/**
	 * @return boolean
	 */
	public boolean isDissoChanged() {
		return dissoChanged;
	}

	/**
	 * @return boolean
	 */
	public boolean isStrengtheningChanged() {
		return strengthChanged;
	}
	
	public boolean isDecayAllowedChanged() {
		return decayAllowedChanged;
	}

}
