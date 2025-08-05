package com.coderpwh.plugins.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.List;

public class GitHubPlugin {


    public static final String baseUrl = "https://api.github.com";

    private final String token;

    public GitHubPlugin(String token) {
        this.token = token;
    }


    /***
     * 访问github函数
     * @return
     */

    @DefineKernelFunction(name = "get_user_info", description = "Get user information from GitHub", returnType = "com.coderpwh.plugins.github.GitHubModel$User")
    public Mono<GitHubModel.User> getUserProfileAsync() {
        HttpClient client = createClient();
        return makeRequest(client, "/user")
                .map(json -> {
                    try {
                        return GitHubModel.objectMapper.readValue(json, GitHubModel.User.class);
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to deserialize GitHubUser", e);
                    }
                });
    }


    /***
     * 访问github函数
     * @param organization
     * @param repoName
     * @return
     */
    @DefineKernelFunction(name = "get_repo_info", description = "Get repository infomation from Github", returnType = "com.coderpwh.plugins.github.GitHubModel$Repository")
    public Mono<GitHubModel.Repository> getRepositoryAsync(@KernelFunctionParameter(name = "organization", description = "The name of the repository to retrieve information for") String organization,
                                                           @KernelFunctionParameter(name = "repo_name", description = "The name of the repository to retrieve information for") String repoName) {
        HttpClient client = createClient();
        return makeRequest(client, String.format("/repos/%s%s", organization, repoName))
                .map(json -> {
                    try {
                        return GitHubModel.objectMapper.readValue(json, GitHubModel.Repository.class);
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to deserialize GitHubRepository", e);
                    }
                });
    }


    /***
     * 获取issue信息
     * @param organization
     * @param repoName
     * @param maxResults
     * @param state
     * @param assignee
     * @return
     */
    @DefineKernelFunction(name = "get_issues", description = "Get issues from GitHub", returnType = "java.util.List")
    public Mono<List<GitHubModel.Issue>> getIssuesAsync(@KernelFunctionParameter(name = "organization", description = "The name of the organization to retrieve issues for") String organization,
                                                        @KernelFunctionParameter(name = "repoName", description = "The name of the repository to retrieve issues for") String repoName,
                                                        @KernelFunctionParameter(name = "max_results", description = "The maximum number of issues to retrieve", required = false, defaultValue = "10", type = int.class) int maxResults,
                                                        @KernelFunctionParameter(name = "state", description = "The state of the issues to retrieve", required = false, defaultValue = "open") String state,
                                                        @KernelFunctionParameter(name = "assignee", description = "The assignee of the issues to retrieve", required = false) String assignee) {

        HttpClient client = createClient();
        String query = String.format("/repos/%s/%s/issues", organization, repoName);
        query = buildQueryString(query, "state", state);
        query = buildQueryString(query, "assignee", assignee);
        query = buildQueryString(query, "per_page", String.valueOf(maxResults));

        return makeRequest(client, query).flatMap(json -> {
            try {
                GitHubModel.Issue[] issues = GitHubModel.objectMapper.readValue(json, GitHubModel.Issue[].class);
                return Mono.just(List.of(issues));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to deserialize GitHubIssues", e);
            }

        });
    }


    @DefineKernelFunction(name = "get_issue_detail_info", description = "Get detail information of a single issue from GitHub", returnType = "com.microsoft.semantickernel.samples.plugins.github.GitHubModel$IssueDetail")
    public GitHubModel.IssueDetail getIssueDetailAsync(@KernelFunctionParameter(name = "organization", description = "The name of the repository to retrieve information for") String organization,
                                                       @KernelFunctionParameter(name = "repo_name", description = "The name of the repository to retrieve information for") String repoName,
                                                       @KernelFunctionParameter(name = "issue_number", description = "The issue number to retrieve information for", type = int.class) int issueNumber) {
        HttpClient client = createClient();
        return makeRequest(client, String.format("/repos/%s/%s/issues/%d", organization, repoName, issueNumber))
                .map(json -> {
                    try {
                        return GitHubModel.objectMapper.readValue(json, GitHubModel.IssueDetail.class);
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to deserialize GitHubIssueDetail", e);
                    }
                }).block();
    }


    /***
     * 访问github http工具
     * @return
     */
    private HttpClient createClient() {
        return HttpClient.create()
                .baseUrl(baseUrl)
                .headers(headers -> {
                    headers.add("User-Agent", "request");
                    headers.add("Accept", "application/vnd.github+json");
                    headers.add("Authorization", "Bearer " + token);
                    headers.add("X-GitHub-Api-Version", "2022-11-28");
                });
    }


    private static String buildQueryString(String path, String param, String value) {
        if (value == null || value.isEmpty() || value.equals(KernelFunctionParameter.NO_DEFAULT_VALUE)) {
            return path;
        }
        return path + (path.contains("?") ? "&" : "?") + param + "=" + value;
    }


    private Mono<String> makeRequest(HttpClient client, String path) {
        return client.get()
                .uri(path)
                .responseSingle((res, content) -> {
                    if (res.status().code() != 200) {
                        return Mono.error(new IllegalStateException("Request failed: " + res.status()));
                    }
                    return content.asString();
                });
    }


}
