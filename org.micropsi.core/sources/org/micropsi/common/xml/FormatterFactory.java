/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/xml/FormatterFactory.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.xml;

public class FormatterFactory {
	
	public static final int FORMATTER_HUMAN_READABLE = 0;
	public static final int FORMATTER_OPTIMIZED = 1;
	
	class HumanReadableFormatter implements XMLFormatterIF {
		
		private final String crlf = System.getProperty("line.separator");
		private final String tab = "  ";
		
		public String getBreak() {
			return crlf;
		}
		
		public String getTab() {
			return tab;
		}
	
		public int getIndentation() {
			return 1;
		}
	
		public boolean breakValues() {
			return false;
		}
		
		public boolean breakTags() {
			return true;
		}
		
		public boolean surroundTextWithSpaces() {
			return true;
		}
	}

	class OptimizedFormatter implements XMLFormatterIF {
		
		private static final String empty = "";
		
		public String getBreak() {
			return empty;
		}
		
		public String getTab() {
			return empty;
		}
	
		public int getIndentation() {
			return 0;
		}

		public boolean breakValues() {
			return false;
		}
		
		public boolean breakTags() {
			return false;
		}	

		public boolean surroundTextWithSpaces() {
			return false;
		}

	}

	private static FormatterFactory instance = new FormatterFactory();
	
	public static XMLFormatterIF getFormatter(int type) {
		return instance.getFormatterInstance(type);
	}
	
    private XMLFormatterIF getFormatterInstance(int type) {
		switch(type) {
			case FORMATTER_OPTIMIZED: return new OptimizedFormatter();
			default: return new HumanReadableFormatter();
		}
	}

}
