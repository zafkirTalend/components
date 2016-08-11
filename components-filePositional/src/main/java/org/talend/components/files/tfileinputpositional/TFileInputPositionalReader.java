package org.talend.components.files.tfileinputpositional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.daikon.avro.SchemaConstants;

import com.cedarsoftware.util.io.JsonReader;

/**
 * Simple implementation of a reader.
 */
public class TFileInputPositionalReader extends AbstractBoundedReader<IndexedRecord> {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TFileInputPositionalDefinition.class);

    private RuntimeContainer container;

    private final String filename;

    private boolean started = false;
    private long rowCount = 0;
    private int headers = 0;
    private int footer = 0;
    private int limit;
    private long maxRow = 0;
    private boolean ignoreEmptyRow = true;
    private String lineSeparator = "";
    private boolean dieOnError=true;
    
    private Scanner scan = null;

    private transient IndexedRecord current;
    private String currentLine;
    private List<String> values;
    
    protected TFileInputPositionalProperties properties;
    
    private Result result;

    public TFileInputPositionalReader(RuntimeContainer container, TFileInputPositionalSource source) {
        super(source);
        this.container = container;
        this.properties = source.getProperties();
        this.filename = properties.filename.getValue();
        this.lineSeparator = properties.lineSeparator.getValue();
        this.dieOnError = properties.dieOnError.getValue();;
        
        this.ignoreEmptyRow = properties.ignoreEmptyRow.getValue();
        
        //Create a scanner to read file
        this.scan = createScanner(filename, lineSeparator);
        
        //Manage headers
        Integer headers = properties.header.getValue();
        if(null == headers || "".equals(headers))
        {
        	this.headers = 0;
        }
        else
        {
        	this.headers = headers;
        }
        
        //Get the limit number of line to be readed
        String limit = properties.limit.getValue();
        if(null == limit || "".equals(limit.trim()))
        {
        	limit = "-1";
        }
        this.limit = Integer.parseInt(limit);
        
        //Manage footer : Calculate the max of line to be readed
        this.footer = properties.footer.getValue();
        
        result = new Result();
    }

    @Override
    public boolean start() throws IOException {
        started = true;
        LOGGER.debug("open: " + filename); //$NON-NLS-1$
        //reader = new BufferedReader(new FileReader(filename));
        //current = reader.readLine();
        
        try{

            manageFooter(footer);
            this.scan = createScanner(filename, lineSeparator);
            skipHeader(headers);  
            
            //TODO
            /*if (properties.unzip.getValue()) {
            	unzip(filename);
			}*/
            rowCount = 0;
            readRow();
          
            
        }catch(IOException e)
        {
        	if(dieOnError)
        	{
        		LOGGER.error(e.getMessage());
        		throw e;
        	}
        	else
        	{
        		LOGGER.error(e.getMessage());
        	}
        }
        finally{
        	return currentLine != null; 
        }
        
    }

    @Override
    public boolean advance() throws IOException {
        //current = reader.readLine();
    	
    	if((limit == -1 || rowCount < limit) && rowCount <= maxRow)
    	{
        	readRow();
        	
    	}
    	else
    	{
    		currentLine = null;
    	}
    	   	
        return currentLine != null;
    }

    /*@Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        if (!started) {
            throw new NoSuchElementException();
        }
        
        //GenericData.get().newRecord(null, properties.schema.schema.getValue());
        
        return current;  
    }*/

    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        if (!started) {
            throw new NoSuchElementException();
        }
        current = (IndexedRecord)GenericData.get().newRecord(null, properties.schema.schema.getValue());
        values = readLineFromPattern(properties.pattern.getValue());

        Schema schema = properties.schema.schema.getValue();
        List<String> schemaFields = properties.getFieldNames(properties.schema.schema);
        
        int i=0;
        
        for(Iterator<String> itFields = schemaFields.iterator(); itFields.hasNext();)
        {        	
        	String value = values.get(i);
        	Schema.Field f = schema.getField(itFields.next());
        	
        	addToCurrent(value,f);
        	
        	++i;
        }
        
        result.totalCount ++;
        
        return current;  
    }
    
    @Override
    public void close() throws IOException {
        //reader.close();
    	scan.close();
        LOGGER.debug("close: " + filename); //$NON-NLS-1$
    }

    @Override
    public Map<String, Object> getReturnValues() {
        return result.toMap();
    }

    /**
     * Create a scanner to read the file and with the selected row separator
     * @param filename
     * @param rowSeparator
     * @return
     */
    protected Scanner createScanner(String filename, String rowSeparator)
    {
    	try{
        	File f = new File(filename);
        	Scanner scanner = new Scanner(f);
        	scanner.useDelimiter(rowSeparator);
        	
        	rowCount = 0;
        	
        	return scanner;
        	
    	}catch(FileNotFoundException fnfe)
    	{
    		return null;
    	}
    }
    
    /**
     * Method skipping the headers
     * @param headers
     */
    protected void skipHeader(Integer headers) throws IOException
    {    	
    	checkClosed();

    	if (headers < 0)
    	{
    		return;
    	}
    	
    	for (int i = 0; i < headers; ++i)
    	{
    		if(scan.hasNextLine()){
    			scan.nextLine();
    		}
    	}
    }
    
    /**
     * Read a row from the file
     * @return
     * @throws IOException
     */
    protected boolean readRow() throws IOException
    {
    	checkClosed();
    	
    	if(ignoreEmptyRow)
    	{
    		boolean isEmpty = true;
    		while(isEmpty)
    		{  			
            	if(scan.hasNext())
            	{
            		currentLine = scan.nextLine();
            	}
            	else
            	{
            		currentLine = null;
            	}
            	
            	if (currentLine == null || !currentLine.equals(""))
            	{
            		isEmpty=false;
            		rowCount++;
            	}
    		}
    	}
    	else
    	{
        	if(scan.hasNext())
        	{
        		currentLine = scan.nextLine();
        		rowCount++;
        	}
        	else
        	{
        		currentLine = null;
        	}
    	}
    	
    	return currentLine != null;
    }
    
    /**
     * Check if the reader is still opened
     * @throws IOException
     */
    protected void checkClosed() throws IOException
    {
    	if(started == false)
    	{
    		throw new IOException("This instance of the PositionalFileReader class has already been closed.");
    	}
    }
    
    protected void manageFooter(Integer footer) throws IOException
    {
    	checkClosed();
    	
    	while(scan.hasNext())
    	{
    		//String line = scan.nextLine();
    		//max ++;
    		readRow();
    	}
    	
    	this.maxRow = rowCount - headers - footer;
    	
    	scan.close();
    }
    
    protected boolean checkDate(String date, String pattern)
    {
    	//return ParserUtils.parseTo_Date(date,pattern);
    	return true;
    }
    
    protected void unzip(String filename) throws IOException
    {
    	byte[] buffer = new byte[1024];
    	try{

        	//get the zip file content
        	ZipInputStream zis =
        		new ZipInputStream(new FileInputStream(filename));
        	//get the zipped file list entry
        	//ZipEntry ze = zis.getNextEntry();
        	
        	zis.getNextEntry();
        	zis.getNextEntry();

        	Scanner sc = new Scanner(zis);
        	
            while(sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }
            	
        	zis.close();
            
    	}catch(Exception e){
    		
    	}finally
    	{
    	}
    }
    
    /**
     * Read the line form the file according to the defined pattern
     * @param pattern
     * @return
     */
    protected List<String> readLineFromPattern(String pattern)
    {
    	List<String> list = new ArrayList<String>();
    	int beginPosition = 0;
    	
    	//Split pattern on ","
    	String[] patternValues = pattern.split(","); 
    	for (int i = 0; i < patternValues.length && currentLine.length() > 0; ++i)
    	{
    		int fieldLength = 0;
    		//Check if a star is used
    		//if true, take from actual postion to the end
    		if("*".equals(patternValues[i]))
    		{
    			fieldLength = currentLine.length() - beginPosition;
    		}
    		else
    		{
    			fieldLength = Integer.parseInt(patternValues[i]);
    		}
    		
    		//calculate the end position
    		int endPosition = beginPosition + fieldLength;
    		if(endPosition > currentLine.length())
    		{
    			endPosition = currentLine.length();
    			fieldLength = endPosition - beginPosition;
    		}
    		
    		String fieldValue = currentLine.substring(beginPosition,endPosition);
    		
    		//check if value must be trim
    		if(properties.trim.getValue())
    		{
    			fieldValue = fieldValue.trim();
    		}
    		
    		list.add(fieldValue);
    		beginPosition += fieldLength;
    		
    	}	
    	
    	return list;
    }
    
    /**
     * Add to the current record converting and according to the type defined in the schema
     * @param value
     * @param f
     */
    protected void addToCurrent(String value, Schema.Field f)
    {	 	
    	Type fieldType;
    	boolean isNullable = false;
    	
    	String type=null;
    	String javaClass=null;
    	String pattern=null;
    	
    	if(f.schema().getType() == Type.UNION)
    	{
    		fieldType = f.schema().getTypes().get(0).getType();
    		isNullable = f.schema().getTypes().get(1).getType() == Type.NULL;
        	String sf = f.schema().getTypes().get(0).toString();
        	
        	Map myMap = JsonReader.jsonToMaps(sf);       	
        	type = (String)myMap.get("type");
        	javaClass = (String)myMap.get("java-class");
        	
        	pattern = f.getProp(SchemaConstants.TALEND_COLUMN_PATTERN);
        }
    	else
    	{
    		fieldType = f.schema().getType();
    	}
    	String valueToInsert = value;
    	
    	if (valueToInsert == null)
    	{
    		current.put(f.pos(), null);
    		return;
    	}
    	
    	switch (fieldType){
        case INT:
        	valueToInsert = manageAdvancedSeparator(valueToInsert);
            if(isNullable){
            	current.put(f.pos(), parseToInt(valueToInsert, false));
            }else{
            	current.put(f.pos(), parseToInt(valueToInsert, true));
            }
            break;
        case LONG:
        	if(type == null){
            	valueToInsert = manageAdvancedSeparator(valueToInsert);
                if(isNullable){
                	current.put(f.pos(), parseToLong(valueToInsert, false));
                }else{
                	current.put(f.pos(), parseToLong(valueToInsert, true));
                }
        	}else{
        		current.put(f.pos(), parseTo_Date(valueToInsert, pattern, !properties.validateDate.getValue()));
        	}
            break;
        case BOOLEAN:
            current.put(f.pos(), Boolean.valueOf(valueToInsert));
            break;
        case FLOAT:
        	valueToInsert = manageAdvancedSeparator(valueToInsert);
            current.put(f.pos(), Float.parseFloat(valueToInsert));
            break;
        case DOUBLE:
        	valueToInsert = manageAdvancedSeparator(valueToInsert);
            current.put(f.pos(), Double.parseDouble(valueToInsert));
            break;
        default:
        	if(type == null){
        		current.put(f.pos(), valueToInsert);
        	}else{
        		if("java.math.BigDecimal".equals(javaClass)){
        			current.put(f.pos(), new BigDecimal(valueToInsert));
        		}
        	}
        
          }    	
    }
    
    /**
     * Manage the advanced Number separator :
     * Delete the thousand separator
     * Replace the decimal separator by .
     * @param value
     * @return
     */
    protected String manageAdvancedSeparator(String value)
    {
    	String result = value;
    	if (properties.advancedNumberSeparator.getValue())
    	{
    		String thousandSeparator = properties.thousandSeparator.getValue();
    		String decimalSeparator = properties.decimalSeparator.getValue();
    		
    		if( thousandSeparator != null)
    		{
    			result = result.replace(thousandSeparator, "");
    		}
    		
    		if(decimalSeparator != null)
    		{
    			result = result.replaceAll(decimalSeparator, ".");
    		}
    	}
    	return result;
    }
    
    /**
     * Convert to Int managing primitive type
     * @param s
     * @param isDecode
     * @return
     */
    private Integer parseToInt(String s, boolean isDecode)
    {
    	if(isDecode)
    	{
    		return Integer.decode(s).intValue();
    	}
    	else
    	{
    		return Integer.parseInt(s);
    	}
    }
    
    /**
     * Convert to Long managing primitive type
     * @param s
     * @param isDecode
     * @return
     */
    private Long parseToLong(String s, boolean isDecode)
    {
    	if(isDecode)
    	{
    		return Long.decode(s).longValue();
    	}
    	else
    	{
    		return Long.parseLong(s);
    	}
    }
    
    /**
     * Convert to Date using the pattern
     * @param s
     * @param pattern
     * @return
     */
    private Date parseTo_Date(String s, String pattern, boolean lenient) {
        // check the parameter for supporting " ","2007-09-13"," 2007-09-13 "
        if (s != null) {
            s = s.trim();
        }
        if (s == null || s.length() == 0) {
            return null;
        }
        if (pattern == null) {
            pattern = "dd-MM-yyyy";
        }
        
        Date date = null;

        if (pattern.equals("yyyy-MM-dd'T'HH:mm:ss'000Z'")) {
            if (!s.endsWith("000Z")) {
                throw new RuntimeException("Unparseable date: \"" + s + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
            pattern = "yyyy-MM-dd'T'HH:mm:ss";
            s = s.substring(0, s.lastIndexOf("000Z"));
        }

        SimpleDateFormat dt = new SimpleDateFormat(pattern); 
        try{
        dt.setLenient(lenient);
        date = dt.parse(s);
        
        }catch(ParseException pe){
        	 throw new RuntimeException("Unparseable date: \"" + s + "\"");
        }

        return date;
    }
    
}
