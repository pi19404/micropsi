/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.3demotion/sources/LibLoader.java,v 1.4 2005/11/15 20:40:49 vuine Exp $ 
 */

public class LibLoader {

	static {
		System.loadLibrary("devil");
		System.loadLibrary("ilu");
		System.loadLibrary("ilut");
		System.loadLibrary("HTTPLib");
		System.loadLibrary("d3dx9_27");

		System.loadLibrary("3demotion");
	}

}
