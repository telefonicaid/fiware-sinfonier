package models;

import static models.SinfonierConstants.ModelCollection.FIELD_ID;
import models.factory.MongoFactory;
import models.module.Module;
import models.module.ModuleVersion;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bson.types.ObjectId;
import org.eclipse.jdt.core.dom.ThisExpression;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import exceptions.SinfonierException;

public abstract class ModelCollection extends Model implements Comparable<ModelCollection> {
  
  protected static String collectionName;
  protected String id;

  public abstract void save() throws SinfonierException;
  public abstract void remove();
  
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;

    if (o == null || getClass() != o.getClass())
      return false;

    ModelCollection modelCollection = (ModelCollection) o;

    return new EqualsBuilder().append(id, modelCollection.id).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(id).toHashCode();
  }

  @Override
  public int compareTo(ModelCollection modelCollection) {
    if (this.equals(modelCollection)) {
      return 0;
    } else {
      return this.id.compareTo(modelCollection.getId());
    }
  }

  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
}
