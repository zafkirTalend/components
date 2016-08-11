package org.talend.components.files;

public enum Encoding {

	ISO_8859_15("ISO-8859-15"),UTF_8("UTF-8"),CUSTOM("CUSTOM");
	
	private String encoding;
	
	Encoding(String encoding)
	{
		this.encoding = encoding;
	}
	
	public String getEncoding() {
		return encoding;
	}
}
