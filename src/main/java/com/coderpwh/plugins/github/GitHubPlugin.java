package com.coderpwh.plugins.github;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

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

    @DefineKernelFunction(name = "get_user_info", description = "Get user information from GitHub",returnType = "com.coderpwh.plugins.github.GitHubModel$User")
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


    private static String builderQueryString(String path, String param, String value) {
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
