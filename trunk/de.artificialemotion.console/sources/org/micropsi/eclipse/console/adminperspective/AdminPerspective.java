package org.micropsi.eclipse.console.adminperspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AdminPerspective implements IPerspectiveFactory {
	
	IPageLayout layout;

	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		this.layout = layout;
		defineActions();
		defineLayout();
  	}
 
	private void defineLayout() {
    
		String editorArea = this.layout.getEditorArea();
		this.layout.setEditorAreaVisible(false);
    
		IFolderLayout one = this.layout.createFolder("one", IPageLayout.RIGHT, 0.5f, editorArea);
		one.addView("org.micropsi.eclipse.console.adminperspective.rawcomview");

		IFolderLayout two = this.layout.createFolder("two", IPageLayout.BOTTOM, 1f, "org.micropsi.eclipse.console.adminperspective.rawcomview");
		two.addView("org.micropsi.eclipse.console.adminperspective.logview");

	}
	
	private void defineActions() {
	}

}
