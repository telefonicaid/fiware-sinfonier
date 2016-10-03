package models.module;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import models.Model;
import models.ui.DdConfig;
import models.ui.OffsetPosition;
import static models.SinfonierConstants.Terminal.*;

import java.util.ArrayList;
import java.util.List;

public class Terminal extends Model {
  protected String name;
  protected List<Integer> direction;
  protected OffsetPosition position;
  protected DdConfig ddConfig;
  protected Integer nMaxWires;

  public Terminal(String name, List<Integer> direction, OffsetPosition position, DdConfig ddConfig, Integer nMaxWires) {
    this.name = name;
    this.direction = direction;
    this.position = position;
    this.ddConfig = ddConfig;
    this.nMaxWires = nMaxWires;
  }

  public Terminal(DBObject o) {
    if (o != null && o.get(FIELD_NAME) != null) {
      name = o.get(FIELD_NAME).toString();
    }

    if (o != null && o.get(FIELD_N_MAX_WIRES) != null) {
      nMaxWires = ((Integer) o.get(FIELD_N_MAX_WIRES));
    }

    if (o != null && o.get(FIELD_DIRECTIONS) != null) {
      BasicDBList dbList = ((BasicDBList) o.get(FIELD_DIRECTIONS));
      direction = new ArrayList<Integer>();

      for (Object d : dbList) {
        direction.add((new Double(d.toString())).intValue());
      }
    }

    if (o != null) {
      position = new OffsetPosition((DBObject) o.get(FIELD_POSITION));
    }

    if (o != null) {
      ddConfig = new DdConfig((DBObject) o.get(FIELD_DD_CONFIG));
    }
  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();

    if (name != null) object.put(FIELD_NAME, name);
    if (nMaxWires != null) object.put(FIELD_N_MAX_WIRES, nMaxWires);

    if (position != null) {
      object.put(FIELD_POSITION, position.toDBObject());
    }

    if (ddConfig != null) {
      object.put(FIELD_DD_CONFIG, ddConfig.toDBObject());
    }

    if (direction != null) {
      BasicDBList dbList = new BasicDBList();

      for (Integer d : direction) {
        dbList.add(d);
      }

      object.put(FIELD_DIRECTIONS, dbList);
    }

    return object;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Integer> getDirection() {
    return direction;
  }

  public void setDirection(List<Integer> direction) {
    this.direction = direction;
  }

  public OffsetPosition getPosition() {
    return position;
  }

  public void setPosition(OffsetPosition position) {
    this.position = position;
  }

  public DdConfig getDdConfig() {
    return ddConfig;
  }

  public void setDdConfig(DdConfig ddConfig) {
    this.ddConfig = ddConfig;
  }

  public Integer getNMaxWires() {
    return nMaxWires;
  }

  public void setNMaxWires(Integer nMaxWires) {
    this.nMaxWires = nMaxWires;
  }
}