package models.topology.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class LogData {

  private String filename;
  private Long start;
  private Long length;
  
  public LogData() {
		super();
	}
	public LogData(String filename, Long start, Long length) {
    this.filename = filename;
    this.start = start;
    this.length = length;
  }
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Long getStart() {
		return start;
	}
	public void setStart(Long start) {
		this.start = start;
	}
	public Long getLength() {
		return length;
	}
	public void setLength(Long length) {
		this.length = length;
	}
}
