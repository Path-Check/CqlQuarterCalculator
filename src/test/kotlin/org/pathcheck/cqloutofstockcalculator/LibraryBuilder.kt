package org.pathcheck.cqloutofstockcalculator

import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Library

class LibraryBuilder {
  fun assembleFhirLib(
    cqlStr: String?,
    libName: String,
    libVersion: String
  ): Library {

    val attachmentCql =
      cqlStr?.let {
        Attachment().apply {
          contentType = "text/cql"
          data = it.toByteArray()
        }
      }

    return Library().apply {
      id = "$libName-$libVersion"
      name = libName
      version = libVersion
      status = Enumerations.PublicationStatus.ACTIVE
      experimental = true
      url = "http://localhost/Library/$libName|$libVersion"
      attachmentCql?.let { addContent(it) }
    }
  }
}
