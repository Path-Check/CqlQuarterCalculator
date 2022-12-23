package org.pathcheck.cqlquartercalculator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.cqframework.cql.elm.execution.ExpressionDef;
import org.fhir.ucum.UcumException;
import org.opencds.cqf.cql.engine.elm.execution.ExpressionDefEvaluator;
import org.opencds.cqf.cql.engine.execution.Context;
import org.testng.ITest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class QuarterCalculatorTest implements ITest {
  private ExpressionDef expression;
  private Context ctx;
  @Factory(dataProvider = "dataMethod")
  public QuarterCalculatorTest(Context context, ExpressionDef expression) {
    this.ctx = context;
    this.expression = expression;
  }

  @DataProvider
  public static Object[][] dataMethod() throws UcumException, IOException, URISyntaxException {
    List<Object[]> testsToRun = new ArrayList<>();

    Context c = new CqlCompilerHelper().evaluator("QuarterCalculatorTest-1.0.0.cql");

    for (ExpressionDef exp : c.getCurrentLibrary().getStatements().getDef()) {
      if (exp instanceof ExpressionDefEvaluator && exp.getName().contains("Test")) {
        testsToRun.add(new Object[]{ c, exp });
      }
    }

    return testsToRun.toArray(new Object[testsToRun.size()][]);
  }

  @Test
  public void test() {
    assertThat(expression.getExpression().evaluate(ctx), is(true));
  }

  @Override
  public String getTestName() {
    return expression.getName();
  }
}
