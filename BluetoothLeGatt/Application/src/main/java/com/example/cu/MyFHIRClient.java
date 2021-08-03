package com.example.cu;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import java.util.List;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.util.BundleUtil;

public class MyFHIRClient {
    private static IGenericClient client;
    private static FhirContext ctx;

    public static FhirContext getContext() {
        if (ctx==null) {
            ctx = FhirContext.forR4();
        }
        return ctx;
    }

    public static IGenericClient getClient() {
        if (ctx == null) {
            ctx = getContext();
        }
        if (client == null) {
            client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        }
        return client;
    }
}
