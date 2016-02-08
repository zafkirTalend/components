package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;
import org.talend.daikon.schema.type.utils.FastDateParser;
import org.talend.daikon.schema.type.utils.ParserUtils;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Datetime extends SalesforceBaseType<String, Date> {

    public static final String DATETIME_PARTTERN = "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

    @Override
    protected String convert2AType(Date value) {
        DateFormat format = FastDateParser.getInstance(DATETIME_PARTTERN);
        return format.format(value);
    }

    @Override
    protected Date convert2TType(String value) {
        return ParserUtils.parseTo_Date(value, DATETIME_PARTTERN);
    }

    @Override
    protected String getAppValue(SObject app, String key) {
        return app.getChild(key).getValue().toString();
    }

    @Override
    protected void setAppValue(SObject app, String key, String value) {
        app.setField(key, value);
    }
}
