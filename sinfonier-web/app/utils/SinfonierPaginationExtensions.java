package utils;

import ext.PaginationExtensions;

import play.i18n.Messages;
import java.util.*;

public class SinfonierPaginationExtensions extends PaginationExtensions {

  public static List<PageElement> buildPossiblePages(int page, int results, int pageSize) {
    int totalPages = Math.max(1, (int) Math.ceil((double) results / pageSize));
    List<PageElement> pages = new ArrayList<PageElement>();
    if (page == 1) {
      for (int i = page; i <= Math.min(page + 2, totalPages); i++) {
        pages.add(new PageElement(i,Integer.toString(i)));
      }
    } else if (page == totalPages) {
      for (int i = Math.max(totalPages - 2, 1); i <= totalPages; i++) {
         pages.add(new PageElement(i,Integer.toString(i)));
      }
    } else {
      for (int i = page - 1; i <= Math.min(page + 1, totalPages); i++) {
        pages.add(new PageElement(i,Integer.toString(i)));
      }
    }
    if (pages.size() == 3 && pages.get(2).getPageNumber() < totalPages) {
      pages.add(new PageElement(totalPages, Messages.get("Utils.pagination.last")));
    }
    if (pages.size() >= 3 && pages.get(0).getPageNumber() > 1) {
      pages.add(0, new PageElement(1, Messages.get("Utils.pagination.first")));
    }
    return pages;
  }
}
