package org.pathcheck.cqlquartercalculator

import org.cqframework.cql.elm.execution.ExpressionDef
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.opencds.cqf.cql.engine.elm.execution.ExpressionDefEvaluator
import org.opencds.cqf.cql.engine.execution.Context
import org.testng.ITest
import org.testng.annotations.DataProvider
import org.testng.annotations.Factory
import org.testng.annotations.Test

class QuarterCalculatorTest @Factory(dataProvider = "dataMethod") constructor(
    private val ctx: Context,
    private val expression: ExpressionDef
) : ITest {

    @Test
    fun test() {
        MatcherAssert.assertThat(expression.expression.evaluate(ctx), Matchers.`is`(true))
    }

    override fun getTestName(): String {
        return expression.name
    }

    companion object {
        @DataProvider
        @JvmStatic
        fun dataMethod(): Array<Array<Any>> {
            val c = CqlCompiler().evaluator("QuarterCalculatorTest-1.0.0.cql")

            return c.currentLibrary.statements.def
                .filterIsInstance<ExpressionDefEvaluator>()
                .filter { it.name.contains("Test") }
                .map {
                    arrayOf(c, it)
                }.toTypedArray()
        }
    }
}