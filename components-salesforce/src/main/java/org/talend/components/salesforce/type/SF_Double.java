package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Double extends SalesforceBaseType<String, Double> {
    @Override
    protected String convert2AType(Double value) {
        if (value == Double.NaN) {
            return "NaN";
        } else if (value == Double.POSITIVE_INFINITY) {
            return "INF";
        } else if (value == Double.NEGATIVE_INFINITY) {
            return "-INF";
        } else {
            return String.valueOf(value);
        }
    }

    @Override
    protected Double convert2TType(String value) {
        if ("NaN".equals(value)) {
            return Double.NaN;
        } else if ("INF".equals(value)) {
            return Double.POSITIVE_INFINITY;
        } else if ("-INF".equals(value)) {
            return Double.NEGATIVE_INFINITY;
        } else {
            return Double.parseDouble(value);
        }
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
