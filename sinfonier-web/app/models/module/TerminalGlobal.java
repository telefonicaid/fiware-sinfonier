package models.module;

import models.ui.DdConfig;
import models.ui.OffsetPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerminalGlobal extends Terminal {
  private static final String NAME = "out";
  private static final String INPUT_TYPE = "variable";
  private static final String ALLOWED_TYPE = "field";
  private static final List<String> ALLOWED_TYPE_LIST = new ArrayList<String>(Arrays.asList(new String[]{ALLOWED_TYPE}));


  public TerminalGlobal(List<Integer> direction, OffsetPosition position, Integer nMaxWires) {
    super(NAME, direction, position, new DdConfig(INPUT_TYPE, ALLOWED_TYPE_LIST), nMaxWires);
  }
}
