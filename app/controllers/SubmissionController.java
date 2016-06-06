package controllers;


import com.google.inject.Inject;
import models.SubmissionModel;
import models.entities.SearchRequest;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SubmissionController extends Controller {

    @Inject
    HttpExecutionContext httpExecutionContext;


    public CompletionStage<Result> getSubmissions() {
        return CompletableFuture.supplyAsync(() -> new SubmissionModel()
                        .getSubmissions(SearchRequest.newInstance(request())),
                httpExecutionContext.current())
                .thenApply(result -> ok(Json.toJson(result)));
    }

    public CompletionStage<Result> getSubmissionStatistics() {
        return CompletableFuture.supplyAsync(() -> new SubmissionModel().getSubmissionStatistics(),
                httpExecutionContext.current())
                .thenApply(result -> ok(Json.toJson(result)));
    }

    public CompletionStage<Result> getSubmissionStatuses() {
        return CompletableFuture.supplyAsync(() -> new SubmissionModel().getSubmissionStatuses(),
                httpExecutionContext.current())
                .thenApply(result -> ok(Json.toJson(result)));
    }
}