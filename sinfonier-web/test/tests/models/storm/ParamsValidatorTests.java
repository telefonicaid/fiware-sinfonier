package tests.models.storm;

import exceptions.SinfonierException;
import models.storm.ParamsValidator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import play.test.PlayJUnitRunner;
import tests.BaseTest;

import java.util.Map;
import java.util.TreeMap;

@RunWith(PlayJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParamsValidatorTests extends BaseTest {
  private static ParamsValidator validator;
  private static final String VALID_PARAM_INTEGER_1 = "TOPOLOGY_WORKERS";
  private static final String VALID_PARAM_INTEGER_2 = "TOPOLOGY_MAX_TASK_PARALLELISM";
  private static final String VALID_PARAM_INTEGER_3 = "TOPOLOGY_MAX_SPOUT_PENDING";
  private static final String VALID_PARAM_STRING = "TOPOLOGY_MAX_TASK_TEST_STRING";
  private static final String VALID_PARAM_BOOLEAN = "TOPOLOGY_MAX_TASK_TEST_BOOLEAN";
  private static final String INVALID_PARAM = "WRONG_KEY";

  private static final String VALID_PARAM_STRING_VALUE = "paramString1";
  private static final String INVALID_PARAM_STRING_VALUE = "bad-*param_String";
  private static final String VALID_PARAM_INTEGER_VALUE = "30";
  private static final String INVALID_PARAM_INTEGER_VALUE = "3a5";
  private static final String VALID_PARAM_BOOLEAN_VALUE = "false";
  private static final String INVALID_PARAM_BOOLEAN_VALUE = "flase";

  @BeforeClass
  public static void runBeforeClass() throws SinfonierException {
    validator = ParamsValidator.getInstance();
  }

  @Test
  public void hasParams01Test() throws Exception {
    assertTrue(validator.hasParams(VALID_PARAM_INTEGER_1));
  }

  @Test
  public void validateValidDataTest() throws Exception {
    assertTrue(validator.validate(VALID_PARAM_INTEGER_1, VALID_PARAM_INTEGER_VALUE));
    assertTrue(validator.validate(VALID_PARAM_INTEGER_2, VALID_PARAM_INTEGER_VALUE));
    assertTrue(validator.validate(VALID_PARAM_INTEGER_3, VALID_PARAM_INTEGER_VALUE));
    assertTrue(validator.validate(VALID_PARAM_STRING, VALID_PARAM_STRING_VALUE));
    assertTrue(validator.validate(VALID_PARAM_BOOLEAN, VALID_PARAM_BOOLEAN_VALUE));
  }

  @Test
  public void validateInvalidDataTest() throws Exception {
    assertFalse(validator.validate(VALID_PARAM_INTEGER_1, INVALID_PARAM_INTEGER_VALUE));
    assertFalse(validator.validate(VALID_PARAM_STRING, INVALID_PARAM_STRING_VALUE));
    assertFalse(validator.validate(VALID_PARAM_BOOLEAN, INVALID_PARAM_BOOLEAN_VALUE));
  }

  @Test
  public void validateListTest() throws Exception {
    Map<String, Object> values = new TreeMap<String, Object>();
    values.put(VALID_PARAM_INTEGER_1, VALID_PARAM_INTEGER_VALUE);
    values.put(VALID_PARAM_INTEGER_2, VALID_PARAM_INTEGER_VALUE);
    values.put(VALID_PARAM_INTEGER_3, VALID_PARAM_INTEGER_VALUE);
    values.put(VALID_PARAM_STRING, VALID_PARAM_STRING_VALUE);
    values.put(VALID_PARAM_BOOLEAN, VALID_PARAM_BOOLEAN_VALUE);

    assertTrue(validator.validate(values));

    values.put(INVALID_PARAM, INVALID_PARAM_INTEGER_VALUE);
    assertFalse(validator.validate(values));
  }

  @AfterClass
  public static void runAfterClass() throws SinfonierException {
    validator.reset();
  }
}
