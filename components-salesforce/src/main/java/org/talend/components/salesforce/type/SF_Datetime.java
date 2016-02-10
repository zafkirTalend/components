package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;
import org.talend.daikon.schema.type.utils.FastDateParser;
import org.talend.daikon.schema.type.utils.ParserUtils;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Datetime implements SalesforceBaseType<String, Date> {

    public static final String DATETIME_PARTTERN = "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

    @Override
    public String convertFromKnown(Date value) {
        DateFormat format = FastDateParser.getInstance(DATETIME_PARTTERN);
        return format.format(value);
    }

    @Override
    public Date convertToKnown(String value) {
        return ParserUtils.parseTo_Date(value, DATETIME_PARTTERN);
    }

    @Override
    public String readValue(SObject app, String key) {
        return app.getChild(key).getValue().toString();
    }

    @Override
    public void writeValue(SObject app, String key, String value) {
        app.setField(key, value);
    }
}
