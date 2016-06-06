package models;


import common.Global;
import models.entities.Response;
import models.entities.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubmissionModel {

    private static final int PAGE_SIZE = 4;
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_LANGUAGE = "language";
    private static final String FIELD_LEVEL = "metadata.level";
    private static final String FIELD_TITLE_TERM = "title.term";
    private static final String FIELD_LANGUAGE_TERM = "language.term";


    public Response getSubmissions(SearchRequest searchRequest) {
        Response response = new Response();
        try {
            SearchRequestBuilder searchRequestBuilder = Global.getElasticTransportClient()
                    .prepareSearch(Global.getEsIndexSubmission())
                    .setTypes(Global.getEsTypeSubmission())
                    .setFrom(searchRequest.getPage() * PAGE_SIZE)
                    .setSize(PAGE_SIZE);

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (searchRequest.getStatus() != null)
                boolQueryBuilder.filter(QueryBuilders.matchQuery(FIELD_STATUS, searchRequest.getStatus()));
            if (searchRequest.getQuery() != null) {
                boolQueryBuilder
                        .should(QueryBuilders.matchQuery(FIELD_TITLE, searchRequest.getQuery()).boost(2))
                        .should(QueryBuilders.matchQuery(FIELD_LANGUAGE, searchRequest.getQuery()).boost(2))
                        .should(QueryBuilders.matchQuery(FIELD_LEVEL, searchRequest.getQuery()).boost(2))
                        .should(QueryBuilders.matchQuery(FIELD_TITLE_TERM, searchRequest.getQuery()))
                        .should(QueryBuilders.matchQuery(FIELD_LANGUAGE_TERM, searchRequest.getQuery()));
            }
            if (boolQueryBuilder.hasClauses())
                searchRequestBuilder.setQuery(boolQueryBuilder);

            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            List<Map> results = new ArrayList<>();
            for (SearchHit searchHit : searchResponse.getHits().getHits())
                results.add(searchHit.getSource());
            response.setData(results);
            response.setSuccess(true);
        } catch (Exception ex) {
            response.setError(ex.getMessage());
        }
        return response;
    }
}