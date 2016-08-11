package org.talend.components.files;

import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class EncodingProperties extends PropertiesImpl {

	/**
	 *  Encoding list
	 */
	public Property<Encoding> encoding = PropertyFactory.newEnum("encoding", Encoding.class);
	
	public Property<String> customEncoding = PropertyFactory.newString("customEncoding");
	
    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public EncodingProperties(String name) {
        super(name);
    }
	
    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(encoding);
        mainForm.addColumn(customEncoding);
        
        mainForm.getWidget(customEncoding.getName()).setHidden(true);
    }
    
    @Override
    public void setupProperties() {
        super.setupProperties();
        // Code for property initialization goes here      
        encoding.setValue(Encoding.ISO_8859_15);
        customEncoding.setValue("");
    } 
   
    public void afterEncoding()
    {
    	if(encoding.getValue().equals(Encoding.CUSTOM))
    	{
    		getForm(Form.MAIN).getWidget(customEncoding.getName()).setHidden(false);
    	}
    	else
    	{
    		getForm(Form.MAIN).getWidget(customEncoding.getName()).setHidden(true);
    	}
    }
}
