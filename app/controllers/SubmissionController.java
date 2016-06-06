package controllers;


import models.SubmissionModel;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class SubmissionController extends Controller {

    private static final String QUERY_FROM = "from";
    private static final String QUERY_SIZE = "size";
    private static final String QUERY_TYPES = "types";
    private static final String QUERY_QUERY = "q";
    private static final String QUERY_SORT = "sort";
    private static final String QUERY_ORDER = "order";


    public F.Promise<Result> getSubmissions() {
        return F.Promise.promise(() -> new SubmissionModel().getSubmissions())
                .thenApply(result -> ok(Json.toJson(result)));
    }
}