package models;

import exceptions.SinfonierException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
