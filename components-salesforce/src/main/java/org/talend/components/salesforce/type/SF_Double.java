package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Double implements SalesforceBaseType<String, Double> {
    @Override
    public String convertFromKnown(Double value) {
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
    public Double convertToKnown(String value) {
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
    public String readValue(SObject app, String key) {
        return app.getChild(key).getValue().toString();
    }

    @Override
    public void writeValue(SObject app, String key, String value) {
        app.setField(key, value);
    }
}
