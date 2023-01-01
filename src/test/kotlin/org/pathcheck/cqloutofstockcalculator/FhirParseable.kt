package org.pathcheck.cqloutofstockcalculator

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.hl7.fhir.instance.model.api.IBaseResource

open class FhirParseable: Loadable() {
    private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
    private val jsonParser = fhirContext.newJsonParser()

    fun parse(assetName: String): IBaseResource {
        return jsonParser.parseResource(open(assetName))
    }
}