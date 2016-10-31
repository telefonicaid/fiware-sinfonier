<div class="row">
  <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
    <div class="text-center">
      %{
        int previousPage = 0;
        pages = utils.SinfonierPaginationExtensions.buildPossiblePages(_currentPage, _total, _pageSize);
      }%
      #{if pages.size() > 1}
        #{list items:pages, as:'page'}
          #{if (page.getPageNumber() - previousPage > 1)} <span>...</span> #{/if}
            <a class="btn-bezel primary #{if page.getPageNumber() == _currentPage} active #{/if}" href="${utils.SinfonierPaginationExtensions.buildUrl(_url, _params, page.getPageNumber())}">${page.getPageLinkText()}</a>
          %{previousPage = page.getPageNumber()}%
        #{/list}
      #{/if}
    </div>
  </div>
</div>