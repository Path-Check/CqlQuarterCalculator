package org.pathcheck.cqloutofstockcalculator

import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Resource
import org.testng.annotations.Test
import java.sql.Date
import java.time.LocalDate
import kotlin.test.assertEquals

class StockCalculatorCase1Test: FhirParseable() {

    private val databaseFileNames = listOf(
        "consume-tablet.fhir.json",
        "encounter-close-flag-A.fhir.json",
        "encounter-close-flag-B.fhir.json",
        "encounter-close-flag-C.fhir.json",
        //"encounter-close-flag-D.fhir.json",
        "encounter-create-flag-A.fhir.json",
        "encounter-create-flag-B.fhir.json",
        "encounter-create-flag-C.fhir.json",
        //"encounter-create-flag-D.fhir.json",
        "flag-A.fhir.json",
        "flag-B.fhir.json",
        "flag-C.fhir.json",
        //"flag-D.fhir.json",
        "practioner.fhir.json",
        "practionerRole.fhir.json",
        "restock-Tablets.fhir.json",
        "Zinc-Sulphate-tablets.fhir.json"
    )

    private val library = LibraryBuilder().assembleFhirLib(
        load("StockCalculator-1.0.0.cql"),
        "StockCalculator",
        "1.0.0"
    )

    private val operator = LibraryOperator(Bundle().apply {
        databaseFileNames
            .map { parse(it) }
            .forEach {
                addEntry().resource = it as Resource
            }

        addEntry().resource = library
    })

    private val evaluateParamsJan = Parameters().apply {
        addParameter().apply {
            name = "Measurement Period"
            value = Period().apply {
                start = Date.valueOf(LocalDate.parse("2023-01-01"))
                end = Date.valueOf(LocalDate.parse("2023-01-31"))
            }
        }
    }

    private val evaluateParamsDec = Parameters().apply {
        addParameter().apply {
            name = "Measurement Period"
            value = Period().apply {
                start = Date.valueOf(LocalDate.parse("2022-12-01"))
                end = Date.valueOf(LocalDate.parse("2022-12-31"))
            }
        }
    }

    @Test
    fun testIfFlagsThatIntersectWithMeasurementPeriodJan() {
        val params = operator.evaluate(library.url, null, evaluateParamsJan, setOf("Flags that Intersect with Measurement Period")) as Parameters

        assertEquals(3, params.getParameters("Flags that Intersect with Measurement Period").size)
    }

    @Test
    fun testDaysOutOfStockJan() {
        val params = operator.evaluate(library.url, null, evaluateParamsJan, setOf("Days Out Of Stock")) as Parameters

        assertEquals(23, (params.getParameter("Days Out Of Stock") as IntegerType).value)
    }

    @Test
    fun testIfFlagsThatIntersectWithMeasurementPeriodDec() {
        val params = operator.evaluate(library.url, null, evaluateParamsDec, setOf("Flags that Intersect with Measurement Period")) as Parameters

        println(params)

        assertEquals(1, params.getParameters("Flags that Intersect with Measurement Period").size)
    }

    @Test
    fun testDaysOutOfStockDec() {
        val params = operator.evaluate(library.url, null, evaluateParamsDec, setOf("Days Out Of Stock")) as Parameters

        assertEquals(12, (params.getParameter("Days Out Of Stock") as IntegerType).value)
    }

}