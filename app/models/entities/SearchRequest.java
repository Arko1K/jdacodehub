package models.entities;


import play.mvc.Http;

public class SearchRequest {

    private int page;
    private String status;
    private String query;


    public static SearchRequest newInstance() {
        return new SearchRequest();
    }

    public static SearchRequest newInstance(Http.Request request) {
        SearchRequest searchRequest = new SearchRequest()
                .setStatus(request.getQueryString("status"))
                .setQuery(request.getQueryString("query"));

        String page = request.getQueryString("page");
        if (page != null)
            searchRequest.setPage(Integer.parseInt(page));

        return searchRequest;
    }


    public int getPage() {
        return page;
    }

    public SearchRequest setPage(int page) {
        this.page = page;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public SearchRequest setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public SearchRequest setQuery(String query) {
        this.query = query;
        return this;
    }
}