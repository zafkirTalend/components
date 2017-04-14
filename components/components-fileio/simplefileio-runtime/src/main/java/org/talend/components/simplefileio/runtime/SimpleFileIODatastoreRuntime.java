// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.simplefileio.runtime;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.UUID;

import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.common.datastore.runtime.DatastoreRuntime;
import org.talend.components.simplefileio.SimpleFileIODatastoreProperties;
import org.talend.components.simplefileio.runtime.ugi.UgiDoAs;
import org.talend.daikon.properties.ValidationResult;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.HeadBucketRequest;

public class SimpleFileIODatastoreRuntime implements DatastoreRuntime<SimpleFileIODatastoreProperties> {

    /**
     * The datastore instance that this runtime is configured for.
     */
    private SimpleFileIODatastoreProperties properties = null;

    /**
     * Helper method for any runtime to get the appropriate {@link UgiDoAs} for executing.
     * 
     * @param properties datastore properties, containing credentials for the cluster.
     * @return An object that can be used to execute actions with the correct credentials.
     */
    public static UgiDoAs getUgiDoAs(SimpleFileIODatastoreProperties properties) {
        if (properties.useKerberos.getValue())
            return UgiDoAs.ofKerberos(properties.kerberosPrincipal.getValue(), properties.kerberosKeytab.getValue());
        else if (properties.userName.getValue() != null && !properties.userName.getValue().isEmpty())
            return UgiDoAs.ofSimple(properties.userName.getValue());
        else
            return UgiDoAs.ofNone();
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, SimpleFileIODatastoreProperties properties) {
        this.properties = properties;
        return ValidationResult.OK;
    }

    @Override
    public Iterable<ValidationResult> doHealthChecks(RuntimeContainer container) {
        if (properties.useS3.getValue()) {
            try {
                AWSCredentialsProvider basicCredentials = new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(properties.s3AccessKey.getValue(), properties.s3SecretKey.getValue()));
                AmazonS3 conn = AmazonS3ClientBuilder.standard().withCredentials(basicCredentials)
                        .withRegion(Regions.fromName(properties.s3Region.getValue())).build();
                try {
                    conn.headBucket(new HeadBucketRequest(UUID.randomUUID().toString()));
                } catch (AmazonServiceException ase) {
                    // it means access successfully, so ignore
                    if (ase.getStatusCode() != Constants.NO_SUCH_BUCKET_STATUS_CODE) {
                        throw ase;
                    }
                }
                return Arrays.asList(ValidationResult.OK);
            } catch (Exception e) {
                ValidationResult vr = new ValidationResult();
                vr.setMessage(e.getMessage());
                vr.setStatus(ValidationResult.Result.ERROR);
                return Arrays.asList(vr);
            }
        }
        return emptyList();
    }
}
