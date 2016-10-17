package models.module;

import models.ui.DdConfig;
import models.ui.OffsetPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerminalIn extends Terminal {
  private static final String NAME = "in[]";
  private static final String INPUT_TYPE = "input";
  private static final String ALLOWED_TYPE = "output";
  private static final List<String> ALLOWED_TYPE_LIST = new ArrayList<String>(Arrays.asList(new String[]{ALLOWED_TYPE}));

  public TerminalIn(List<Integer> direction, OffsetPosition position, Integer nMaxWires) {
    super(NAME, direction, position, new DdConfig(INPUT_TYPE, ALLOWED_TYPE_LIST), nMaxWires);
  }
}
