package org.pathcheck.cqloutofstockcalculator

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.hl7.fhir.instance.model.api.IBaseParameters
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Endpoint
import org.hl7.fhir.r4.model.Parameters
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverterFactory
import org.opencds.cqf.cql.evaluator.builder.Constants
import org.opencds.cqf.cql.evaluator.builder.CqlEvaluatorBuilder
import org.opencds.cqf.cql.evaluator.builder.EndpointConverter
import org.opencds.cqf.cql.evaluator.builder.data.DataProviderFactory
import org.opencds.cqf.cql.evaluator.builder.data.FhirModelResolverFactory
import org.opencds.cqf.cql.evaluator.builder.data.TypedRetrieveProviderFactory
import org.opencds.cqf.cql.evaluator.builder.library.LibrarySourceProviderFactory
import org.opencds.cqf.cql.evaluator.builder.library.TypedLibrarySourceProviderFactory
import org.opencds.cqf.cql.evaluator.builder.terminology.TerminologyProviderFactory
import org.opencds.cqf.cql.evaluator.builder.terminology.TypedTerminologyProviderFactory
import org.opencds.cqf.cql.evaluator.cql2elm.content.fhir.BundleFhirLibrarySourceProvider
import org.opencds.cqf.cql.evaluator.cql2elm.util.LibraryVersionSelector
import org.opencds.cqf.cql.evaluator.engine.retrieve.BundleRetrieveProvider
import org.opencds.cqf.cql.evaluator.engine.terminology.BundleTerminologyProvider
import org.opencds.cqf.cql.evaluator.fhir.adapter.r4.AdapterFactory
import org.opencds.cqf.cql.evaluator.fhir.dal.FhirDal
import org.opencds.cqf.cql.evaluator.library.CqlFhirParametersConverter
import org.opencds.cqf.cql.evaluator.library.LibraryProcessor
import org.pathcheck.cqloutofstockcalculator.InMemoryFhirDal

class LibraryOperator(val database: Bundle) {
  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)

  fun buildProcessor(fhirDal: FhirDal): LibraryProcessor {
    val adapterFactory = AdapterFactory()
    val libraryVersionSelector = LibraryVersionSelector(adapterFactory)
    val fhirTypeConverter = FhirTypeConverterFactory().create(fhirContext.version.version)
    val cqlFhirParametersConverter = CqlFhirParametersConverter(fhirContext, adapterFactory, fhirTypeConverter)

    val fhirModelResolverFactory = FhirModelResolverFactory()

    val librarySourceProviderFactories =
      setOf<TypedLibrarySourceProviderFactory>(
        object : TypedLibrarySourceProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES
          override fun create(url: String, headers: List<String>?) =
            BundleFhirLibrarySourceProvider(
              fhirContext,
              database,
              adapterFactory,
              libraryVersionSelector
            )
        }
      )

    val librarySourceProviderFactory =
      LibrarySourceProviderFactory(
        fhirContext,
        adapterFactory,
        librarySourceProviderFactories,
        libraryVersionSelector
      )

    val retrieveProviderFactories =
      setOf<TypedRetrieveProviderFactory>(
        object : TypedRetrieveProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES
          override fun create(url: String, headers: List<String>?) = BundleRetrieveProvider(fhirContext, database)
        }
      )

    val dataProviderFactory =
      DataProviderFactory(fhirContext, setOf(fhirModelResolverFactory), retrieveProviderFactories)

    val typedTerminologyProviderFactories =
      setOf<TypedTerminologyProviderFactory>(
        object : TypedTerminologyProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES
          override fun create(url: String, headers: List<String>?) = BundleTerminologyProvider(fhirContext, database)
        }
      )

    val terminologyProviderFactory = TerminologyProviderFactory(fhirContext, typedTerminologyProviderFactories)

    val endpointConverter = EndpointConverter(adapterFactory)

    return LibraryProcessor(
      fhirContext,
      cqlFhirParametersConverter,
      librarySourceProviderFactory,
      dataProviderFactory,
      terminologyProviderFactory,
      endpointConverter,
      fhirModelResolverFactory
    ) { CqlEvaluatorBuilder() }
  }

  fun evaluate(libraryUrl: String,
               patientId: String?,
               parameters: Parameters?,
               expressions: Set<String>): IBaseParameters {
    val dataEndpoint =
      Endpoint()
        .setAddress("localhost")
        .setConnectionType(Coding().setCode(Constants.HL7_FHIR_FILES))

    val fhirDal = InMemoryFhirDal()
    fhirDal.addAll(database)

    return buildProcessor(fhirDal).evaluate(
      libraryUrl,
      patientId,
      parameters,
      dataEndpoint,
      dataEndpoint,
      dataEndpoint,
      null,
      expressions
    )
  }
}
