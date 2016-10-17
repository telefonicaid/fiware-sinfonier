package utils;

import ext.PaginationExtensions;

import java.util.*;

public class SinfonierPaginationExtensions extends PaginationExtensions {

  public static List<Integer> buildPossiblePages(int page, int results, int pageSize) {
    int totalPages = Math.max(1, (int) Math.ceil((double) results / pageSize));
    List<Integer> pages = new ArrayList<Integer>();
    if (page == 1) {
      for (int i = page; i <= Math.min(page + 2, totalPages); i++) {
        pages.add(i);
      }
    } else if (page == totalPages) {
      for (int i = Math.max(totalPages - 2, 1); i <= totalPages; i++) {
         pages.add(i);
      }
    } else {
      for (int i = page - 1; i <= Math.min(page + 1, totalPages); i++) {
        pages.add(i);
      }
    }
    if (pages.size() == 3 && pages.get(2) < totalPages) {
      pages.add(totalPages);
    }
    return pages;
  }

}
