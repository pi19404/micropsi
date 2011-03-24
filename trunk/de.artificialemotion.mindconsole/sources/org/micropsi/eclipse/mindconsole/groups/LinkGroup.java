package org.micropsi.eclipse.mindconsole.groups;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.LinkST;
import org.micropsi.nodenet.LinkTypesIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.NetIntegrityException;

/**
 * 
 * 
 * 
 */
public class LinkGroup {
	
	private Text weight;
	private Text confidence;
	
	private Text t;
	private Text x;
	private Text y;
	private Text z;
	
	private boolean weightChanged = false;
	private boolean confidenceChanged = false;
	private boolean tChanged = false;
	private boolean xChanged = false;
	private boolean yChanged = false;
	private boolean zChanged = false;
	
	private Link link;
//	private NetFacadeIF net;
	
	public LinkGroup(Composite parent, NetFacadeIF net, Link link) {
		this.link = link;
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
		label.setText("from");
		
		NetEntity linking = link.getLinkingEntity(); 
		label = new Label(topLevel, SWT.NONE);
		label.setText(
			linking.getID()+(linking.hasName() ? " ("+linking.getEntityName()+")" : "")
		);
		
		label = new Label(topLevel, SWT.NONE);
		label.setText("to");
		
		try {
			NetEntity linked = link.getLinkedEntity();
			label = new Label(topLevel, SWT.NONE);
			label.setText(
				linked.getID()+(linked.hasName() ? " ("+linked.getEntityName()+")" : "")
			);
		} catch (NetIntegrityException e) {
			// that shouldn't happen here
			throw new RuntimeException(e);
		}
		
		label = new Label(topLevel, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.verticalSpan = 3;
		label.setLayoutData(data);		
		
		label = new Label(topLevel, SWT.NONE);
		label.setText("weight");
		
		weight = new Text(topLevel, SWT.NONE);
		weight.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		weight.setText(Double.toString(link.getWeight()));
		weight.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				weightChanged = true;
			}
		});
		
		label = new Label(topLevel, SWT.NONE);
		label.setText("conf");
		
		confidence = new Text(topLevel, SWT.NONE);
		confidence.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		confidence.setText(Double.toString(link.getConfidence()));
		confidence.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				confidenceChanged = true;
			}
		});

		
		label = new Label(topLevel, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.verticalSpan = 3;
		label.setLayoutData(data);
		
		LinkST stl = null;
		if(link.getType() == LinkTypesIF.LINKTYPE_SPACIOTEMPORAL)
			stl = (LinkST)link;

		label = new Label(topLevel, SWT.NONE);
		label.setText("t");
		
		t = new Text(topLevel, SWT.NONE);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		t.setEnabled(link.getType() == LinkTypesIF.LINKTYPE_SPACIOTEMPORAL);
		if(stl != null) t.setText(Double.toString(stl.getT()));
		t.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				tChanged = true;
			}
		});


		label = new Label(topLevel, SWT.NONE);
		label.setText("x");
		
		x = new Text(topLevel, SWT.NONE);
		x.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		x.setEnabled(link.getType() == LinkTypesIF.LINKTYPE_SPACIOTEMPORAL);
		if(stl != null) x.setText(Double.toString(stl.getX()));
		x.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				xChanged = true;
			}
		});

		
		label = new Label(topLevel, SWT.NONE);
		label.setText("y");
		
		y = new Text(topLevel, SWT.NONE);
		y.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		y.setEnabled(link.getType() == LinkTypesIF.LINKTYPE_SPACIOTEMPORAL);
		if(stl != null) y.setText(Double.toString(stl.getY()));
		y.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				yChanged = true;
			}
		});


		label = new Label(topLevel, SWT.NONE);
		label.setText("z");
		
		z = new Text(topLevel, SWT.NONE);
		z.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		z.setEnabled(link.getType() == LinkTypesIF.LINKTYPE_SPACIOTEMPORAL);
		if(stl != null) z.setText(Double.toString(stl.getZ()));
		z.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				zChanged = true;
			}
		});

		
	}
	
	public double getWeight() {
		try {
			return Double.parseDouble(weight.getText());
		} catch (Exception e) {
			return link.getWeight();
		}
	}
	
	public double getConfidence() {
		try {
			return Double.parseDouble(confidence.getText());
		} catch (Exception e) {
			return link.getConfidence();
		}
	}
	
	public double getX() {
		try {
			return Double.parseDouble(x.getText());
		} catch (Exception e) {
			return ((LinkST)link).getX();
		}
	}

	public double getY() {
		try {
			return Double.parseDouble(y.getText());
		} catch (Exception e) {
			return ((LinkST)link).getY();
		}
	}

	public double getZ() {
		try {
			return Double.parseDouble(z.getText());
		} catch (Exception e) {
			return ((LinkST)link).getZ();
		}
	}
	
	public double getT() {
		try {
			return Double.parseDouble(t.getText());
		} catch (Exception e) {
			return ((LinkST)link).getT();
		}
	}
	
	

	/**
	 * Returns the confidenceChanged.
	 * @return boolean
	 */
	public boolean isConfidenceChanged() {
		return confidenceChanged;
	}

	/**
	 * Returns the tChanged.
	 * @return boolean
	 */
	public boolean isTChanged() {
		return tChanged;
	}

	/**
	 * Returns the weightChanged.
	 * @return boolean
	 */
	public boolean isWeightChanged() {
		return weightChanged;
	}

	/**
	 * Returns the xChanged.
	 * @return boolean
	 */
	public boolean isXChanged() {
		return xChanged;
	}

	/**
	 * Returns the yChanged.
	 * @return boolean
	 */
	public boolean isYChanged() {
		return yChanged;
	}

	/**
	 * Returns the zChanged.
	 * @return boolean
	 */
	public boolean isZChanged() {
		return zChanged;
	}

}
