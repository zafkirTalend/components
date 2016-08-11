package org.talend.components.files.tfileinputpositional;

import static org.talend.daikon.properties.presentation.Widget.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.components.files.EncodingProperties;
import org.talend.components.files.PositionalFormatTable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

/**
 * The ComponentProperties subclass provided by a component stores the 
 * configuration of a component and is used for:
 * 
 * <ol>
 * <li>Specifying the format and type of information (properties) that is 
 *     provided at design-time to configure a component for run-time,</li>
 * <li>Validating the properties of the component at design-time,</li>
 * <li>Containing the untyped values of the properties, and</li>
 * <li>All of the UI information for laying out and presenting the 
 *     properties to the user.</li>
 * </ol>
 * 
 * The TestInputProperties has two properties:
 * <ol>
 * <li>{code filename}, a simple property which is a String containing the 
 *     file path that this component will read.</li>
 * <li>{code schema}, an embedded property referring to a Schema.</li>
 * </ol>
 */
public class TFileInputPositionalProperties extends FixedConnectorsComponentProperties {
	
	public ISchemaListener schemaListener;
	//
	// MAIN
	//
	public Property<Boolean> useExistingDynamic = PropertyFactory.newBoolean("useExistingDynamic");
	
    public Property<String> filename = PropertyFactory.newString("filename"); //$NON-NLS-1$
	
    public Property<String> lineSeparator = PropertyFactory.newString("lineSeparator"); //$NON-NLS-1$
    
	public Property<Boolean> useOctetLength = PropertyFactory.newBoolean("useOctetLength");
	
	public Property<Boolean> customize = PropertyFactory.newBoolean("customize");
	public Property<String> pattern = PropertyFactory.newString("pattern");
	public PositionalFormatTable positionalFormatTable = new PositionalFormatTable("positionalFormatTable");
	
	public Property<Boolean> ignoreEmptyRow = PropertyFactory.newBoolean("ignoreEmptyRow");
	
	public Property<Boolean> unzip = PropertyFactory.newBoolean("unzip");
	
	public Property<Boolean> dieOnError = PropertyFactory.newBoolean("dieOnError");
    
    public Property<Integer> header = PropertyFactory.newInteger("header"); 
    public Property<Integer> footer = PropertyFactory.newInteger("footer"); 
    public Property<String> limit = PropertyFactory.newString("limit"); 
    
    public SchemaProperties schema = new SchemaProperties("schema"); //$NON-NLS-1$
    
    //
    // ADVANCED
    //
    public Property<Boolean> required = PropertyFactory.newBoolean("required");
	public Property<Boolean> advancedNumberSeparator = PropertyFactory.newBoolean("advancedNumberSeparator");
	public Property<String> thousandSeparator = PropertyFactory.newString("thousandSeparator");
	public Property<String> decimalSeparator = PropertyFactory.newString("decimalSeparator");
	public Property<Boolean> trim = PropertyFactory.newBoolean("trim");
	public Property<Boolean> validateDate = PropertyFactory.newBoolean("validateDate");
	public EncodingProperties encoding = new EncodingProperties("encoding");
    
    protected transient PropertyPathConnector mainConnector = new PropertyPathConnector(Connector.MAIN_NAME, "schema");
 
    public TFileInputPositionalProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        // Code for property initialization goes here
        pattern.setValue("\"5,4,5\"");
        header.setValue(0);
        footer.setValue(0);
        lineSeparator.setValue("\"\\n\"");
        positionalFormatTable.setDefaultAlign();
        ignoreEmptyRow.setValue(true);
        thousandSeparator.setValue(".");
        decimalSeparator.setValue(",");
        validateDate.setValue(false);

    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN);
        form.addRow(useExistingDynamic);
        form.addRow(widget(filename).setWidgetType(Widget.FILE_WIDGET_TYPE));
        form.addRow(lineSeparator);
        form.addRow(useOctetLength);
        //form.addRow(customize);
        //form.addColumn(pattern);
        form.addRow(pattern);
        //form.addRow(positionalFormatTable);
        form.addRow(widget(positionalFormatTable).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        form.addRow(ignoreEmptyRow);
        form.addColumn(unzip);
        form.addRow(dieOnError);
        form.addColumn(header);
        form.addColumn(footer);
        form.addColumn(limit);
        
        form.addRow(schema.getForm(Form.REFERENCE));
        
        Form advancedForm = Form.create(this, Form.ADVANCED);
        advancedForm.addRow(required);
        advancedForm.addRow(advancedNumberSeparator);
        advancedForm.addColumn(thousandSeparator);
        advancedForm.addColumn(decimalSeparator);
        advancedForm.addRow(trim);
        advancedForm.addColumn(validateDate);
        advancedForm.addRow(encoding.getForm(Form.MAIN));
        
        form.getWidget(pattern.getName()).setHidden(false);
        form.getWidget(positionalFormatTable.getName()).setHidden(true);
        advancedForm.getWidget(thousandSeparator.getName()).setHidden(true);
        advancedForm.getWidget(decimalSeparator.getName()).setHidden(true); 
    }

    public void afterCustomize(){
    	//If Customize is checked, show the table to manage it and hide the pattern line
    	if(customize.getValue() == true)
    	{
    		getForm(Form.MAIN).getWidget(positionalFormatTable.getName()).setHidden(false);
    		getForm(Form.MAIN).getWidget(pattern.getName()).setHidden(true);
    	}
    	else
    	{
    		getForm(Form.MAIN).getWidget(positionalFormatTable.getName()).setHidden(true);
    		getForm(Form.MAIN).getWidget(pattern.getName()).setHidden(false);
    	}
    	//positionalFormatTable.columnName.setValue(getFieldNames(schema.schema));
    	//positionalFormatTable.initValues();
    }
    
    
    public void afterAdvancedNumberSeparator(){
    	//If Customize is checked, show the table to manage it and hide the pattern line
    	if(advancedNumberSeparator.getValue() == true)
    	{
    		//getForm(Form.MAIN).getWidget(userPassword.getName()).setHidden(false);
    		getForm(Form.ADVANCED).getWidget(thousandSeparator.getName()).setHidden(false);
    		getForm(Form.ADVANCED).getWidget(decimalSeparator.getName()).setHidden(false);
    	}
    	else
    	{
    		getForm(Form.ADVANCED).getWidget(thousandSeparator.getName()).setHidden(true);
    		getForm(Form.ADVANCED).getWidget(decimalSeparator.getName()).setHidden(true);
    	}
    	
    }
    
    public void beforePositionalFormatTable() {
        //positionalFormatTable.columnName.setValue(getFieldNames(schema.schema));
    }
    
    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputComponent) {
        if (isOutputComponent) {
            return Collections.singleton(mainConnector);
        }
        return Collections.emptySet();
    }
    
    public void afterSchema() {
    	//beforePositionalFormatTable();
    }
    
    protected List<String> getFieldNames(Property schema) {
        String sJson = schema.getStringValue();
        Schema s = new Schema.Parser().parse(sJson);
        List<String> fieldNames = new ArrayList<>();
        for (Schema.Field f : s.getFields()) {
            fieldNames.add(f.name());
        }
        return fieldNames;
        
    }

}
