package com.coderpwh.syntaxexamples.configuration;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.sun.jdi.PrimitiveValue;

public class Example41_HttpClientUsage {


    private static String AZURE_OPENAI_API_KEY = " ";
    private static String AZURE_OPENAI_API_ENDPOINT = "";
    private static String MODEL_ID = "";

    public static void main(String[] args) {
        useDefaultHttpClient();
        useCustomHttpClient();

    }


    /***
     * 默认的工具
     */
    private static void useDefaultHttpClient() {
        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .buildAsyncClient();

        var kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, OpenAIChatCompletion.builder()
                        .withOpenAIAsyncClient(client)
                        .withModelId(MODEL_ID)
                        .build())
                .build();
    }


    /***
     * 自定义工具
     */
    private static void useCustomHttpClient() {
        HttpClient customHttpClient = HttpClient.createDefault();

        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .httpClient(customHttpClient)
                .endpoint("https://localhost:5000")
                .credential(new AzureKeyCredential("BAD KEY"))
                .buildAsyncClient();

        ChatCompletionService openAIChatCompletion = OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(client)
                .withModelId("gpt-35-turbo")
                .build();

        Kernel kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, openAIChatCompletion)
                .build();
    }

}
