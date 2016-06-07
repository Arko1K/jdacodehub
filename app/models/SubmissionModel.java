package models;


import common.Global;
import common.Utils;
import models.entities.Response;
import models.entities.SearchData;
import models.entities.SearchRequest;
import models.entities.StatisticsData;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmissionModel {

    private static final int PAGE_SIZE = 4;
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_STATUS_SHORT = "statusShort";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_LANGUAGE = "language";
    private static final String FIELD_LEVEL = "metadata.level";
    private static final String FIELD_TITLE_TERM = "title.term";
    private static final String FIELD_LANGUAGE_TERM = "language.term";
    private static final String FIELD_TITLE_ORIG = "title.orig";
    private static final String FIELD_LANGUAGE_ORIG = "language.orig";


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
                boolQueryBuilder.filter(QueryBuilders.matchQuery(FIELD_STATUS_SHORT, searchRequest.getStatus().split(",")));
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
            List<Map> result = new ArrayList<>();
            for (SearchHit searchHit : searchResponse.getHits().getHits())
                result.add(searchHit.getSource());
            response.setData(new SearchData(result, searchResponse.getHits().totalHits()));
            response.setSuccess(true);
        } catch (Exception ex) {
            response.setError(ex.getMessage());
        }
        return response;
    }

    public Response getSubmissionStatistics() {
        Response response = new Response();
        try {
            SearchResponse searchResponse = Global.getElasticTransportClient()
                    .prepareSearch(Global.getEsIndexSubmission())
                    .setTypes(Global.getEsTypeSubmission())
                    .addAggregation(AggregationBuilders.terms(FIELD_LANGUAGE).field(FIELD_LANGUAGE_ORIG).size(5))
                    .addAggregation(AggregationBuilders.terms(FIELD_TITLE).field(FIELD_TITLE_ORIG).size(2))
                    .addAggregation(AggregationBuilders.terms(FIELD_LEVEL).field(FIELD_LEVEL).size(0))
                    .setSize(0)
                    .execute()
                    .actionGet();

            StatisticsData statisticsData = new StatisticsData();
            statisticsData.setTotal(searchResponse.getHits().getTotalHits());
            for (Aggregation aggregation : searchResponse.getAggregations()) {
                HashMap<String, Long> counts = new HashMap<>();
                for (Terms.Bucket bucket : ((StringTerms) aggregation).getBuckets())
                    counts.put(Utils.capitalize(bucket.getKeyAsString()), bucket.getDocCount());
                switch (aggregation.getName()) {
                    case FIELD_LANGUAGE:
                        statisticsData.setLanguage(counts);
                        break;
                    case FIELD_TITLE:
                        statisticsData.setTitle(counts);
                        break;
                    case FIELD_LEVEL:
                        statisticsData.setLevel(counts);
                        break;
                }
            }
            response.setData(statisticsData);
            response.setSuccess(true);
        } catch (Exception ex) {
            response.setError(ex.getMessage());
        }
        return response;
    }

    public Response getSubmissionStatuses() {
        Response response = new Response();
        try {
            SearchResponse searchResponse = Global.getElasticTransportClient()
                    .prepareSearch(Global.getEsIndexSubmission())
                    .setTypes(Global.getEsTypeSubmission())
                    .addAggregation(AggregationBuilders.terms(FIELD_STATUS).field(FIELD_STATUS_SHORT).size(0))
                    .setSize(0)
                    .execute()
                    .actionGet();
            Map<String, Long> submissionStatuses = new HashMap<>();
            for (Terms.Bucket bucket : ((StringTerms) searchResponse.getAggregations().get(FIELD_STATUS)).getBuckets())
                submissionStatuses.put(Utils.capitalize(bucket.getKeyAsString()), bucket.getDocCount());
            response.setData(submissionStatuses);
            response.setSuccess(true);
        } catch (Exception ex) {
            response.setError(ex.getMessage());
        }
        return response;
    }
}