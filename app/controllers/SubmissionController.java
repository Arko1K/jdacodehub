package controllers;


import models.SubmissionModel;
import models.entities.SearchRequest;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SubmissionController extends Controller {

    public CompletionStage<Result> getSubmissions() {
        return CompletableFuture.supplyAsync(() -> new SubmissionModel().getSubmissions(SearchRequest.newInstance(request())))
                .thenApply(result -> ok(Json.toJson(result)));
    }
}