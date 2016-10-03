package models.module;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import exceptions.SinfonierException;
import models.Model;
import models.user.MyTool;
import models.user.User;

import java.util.ArrayList;
import java.util.List;

public class MyTools extends Model {
  private List<MyTool> myTools;

  public MyTools(List<MyTool> myTools) {
    this.myTools = myTools;
  }

  public MyTools() {
    this(new ArrayList<MyTool>());
  }

  public MyTools(BasicDBList o) throws SinfonierException {
    this();

    if (o != null) {
      for (Object o1 : o) {
        add(new MyTool(((DBObject) o1)));
      }
    }
  }

  public boolean hasAdded(User user) {
    return myTools.contains(new MyTool(user));
  }

  public List<MyTool> getMyTools() {
    return myTools;
  }

  public void setMyTools(List<MyTool> myTools) {
    this.myTools = myTools;
  }

  public void add(MyTool myTool) {
    myTools.add(myTool);
  }
  
  public int size() {
    return myTools.size();
  }

  public void remove(MyTool myTool) {
    myTools.remove(myTool);
  }

  public DBObject toDBObject() {
    BasicDBList obj = new BasicDBList();

    for (MyTool myTool : myTools) {
      obj.add(myTool.toDBObject());
    }

    return obj;
  }
}
