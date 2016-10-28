package utils;

public class PageElement {
  private int pageNumber;
  private String pageLinkText;
  
  public PageElement(int pageNumber, String pageLinkText) {
    super();
    this.pageNumber = pageNumber;
    this.pageLinkText = pageLinkText;
  }
  
  public int getPageNumber() {
    return pageNumber;
  }
  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }
  public String getPageLinkText() {
    return pageLinkText;
  }
  public void setPageLinkText(String pageLinkText) {
    this.pageLinkText = pageLinkText;
  }
  
}
