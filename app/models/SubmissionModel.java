package models;


import common.Global;
import models.entities.Response;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubmissionModel {

    public Response getSubmissions() {
        Response response = new Response();
        try {
            SearchResponse searchResponse = Global.getElasticTransportClient()
                    .prepareSearch(Global.getEsIndexSubmission())
                    .setTypes(Global.getEsTypeSubmission())
                    .setFrom(0)
                    .setSize(20)
                    .execute()
                    .actionGet();
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