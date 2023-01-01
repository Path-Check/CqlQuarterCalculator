package org.pathcheck.cqloutofstockcalculator

import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.MetadataResource
import org.hl7.fhir.r4.model.Resource
import org.opencds.cqf.cql.evaluator.fhir.dal.FhirDal

class InMemoryFhirDal : FhirDal {
  private val cacheById = mutableMapOf<String, IBaseResource>()
  private val cacheByURL = mutableMapOf<String, MutableList<IBaseResource>>()
  private val cacheByType = mutableMapOf<String, MutableList<IBaseResource>>()

  private fun toKey(resource: IIdType) = "${resource.resourceType}/${resource.idPart}"

  private fun putIntoCache(resource: Resource) {
    println("Add $resource")
    cacheById[toKey(resource.idElement)] = resource
    cacheByType.getOrPut(resource.resourceType.name) { mutableListOf() }.add(resource)
    if (resource is MetadataResource && resource.url != null) {
      cacheByURL.getOrPut(resource.url) { mutableListOf() }.add(resource)
    }
  }

  fun addAll(resource: Resource) {
    when (resource) {
      is Bundle -> resource.entry.forEach { addAll(it.resource) }
      else -> putIntoCache(resource)
    }
  }

  override fun read(id: IIdType): IBaseResource? {
    println("Read $id")
    return cacheById[toKey(id)]
  }

  override fun create(resource: IBaseResource) {}
  override fun update(resource: IBaseResource) {}
  override fun delete(id: IIdType) {}

  override fun search(resourceType: String): MutableList<IBaseResource>? {
    println("Search $resourceType")
    return cacheByType[resourceType]
  }
  override fun searchByUrl(resourceType: String, url: String) : List<IBaseResource>? {
    println("Search By URL $url")
    return cacheByURL[url]?.filter { it.idElement.resourceType == resourceType }
  }

}
