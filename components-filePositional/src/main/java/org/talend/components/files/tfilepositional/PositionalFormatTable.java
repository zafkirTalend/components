package org.talend.components.files.tfilepositional;

import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

public class PositionalFormatTable extends ComponentPropertiesImpl {

	public enum Align {
		Gauche,
		Droite,
		Centre;
		}
	
    /**
     * 
     */
    private static final TypeLiteral<List<String>> LIST_STRING_TYPE = new TypeLiteral<List<String>>() {// empty
    };
    
    /**
     * 
     */
    private static final TypeLiteral<List<Integer>> LIST_INTEGER_TYPE = new TypeLiteral<List<Integer>>() {// empty
    };
    
    /**
     * 
     */
    private static final TypeLiteral<List<Character>> LIST_CHARACTER_TYPE = new TypeLiteral<List<Character>>() {// empty
    };
    
    /**
     * 
     */
    private static final TypeLiteral<List<Align>> LIST_ALIGN_TYPE = new TypeLiteral<List<Align>>() {// empty
    };
    
    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public PositionalFormatTable(String name) {
        super(name);
    }
    
    public Property<List<String>> columnName = newProperty(LIST_STRING_TYPE, "columnName");
    
    public Property<List<Integer>> size = newProperty(LIST_INTEGER_TYPE, "size");
    
    public Property<List<Character>> paddingChar = newProperty(LIST_CHARACTER_TYPE, "paddingChar");
    
    public Property<List<Align>> align = newProperty(LIST_ALIGN_TYPE, "align");
    
    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        //mainForm.addColumn(new Widget(columnName).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addColumn(columnName);
        mainForm.addColumn(size);
        mainForm.addColumn(paddingChar);
        //mainForm.addColumn(new Widget(align).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addColumn(align).getWidget("align").setWidgetType(Widget.ENUMERATION_WIDGET_TYPE);
        //mainForm.addColumn(align);
    }
    
    @Override
    public void setupProperties() {
        super.setupProperties();
        // Code for property initialization goes here      
        
    } 
    
    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
    }
    
    public void setDefaultAlign()
    {
    	//this.align.setValue(Align.Gauche);
    }
    
    public void initValues()
    {
    	List<Character> paddingChars = new ArrayList<Character>();
    	List<Align> aligns = new ArrayList<Align>();
    	for(Iterator<String> itSchema = columnName.getValue().iterator(); itSchema.hasNext();)
    	{
    		String columnName = itSchema.next();
    		paddingChars.add(' ');
    		aligns = Arrays.asList(Align.values());
    		
    		
    	}
    	
    	paddingChar.setValue(paddingChars);
    	align.setValue(aligns);
    	
    	//System.out.println("### ADDED : "+paddingChars + " - " + Arrays.asList(Align.values()));
    	
    }
}
