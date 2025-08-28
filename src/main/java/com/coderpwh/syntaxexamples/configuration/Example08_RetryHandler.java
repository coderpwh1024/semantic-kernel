package com.coderpwh.syntaxexamples.configuration;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.policy.ExponentialBackoffOptions;
import com.azure.core.http.policy.RetryOptions;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionFromPrompt;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;

import java.time.Duration;

public class Example08_RetryHandler {


    private static String AZURE_OPENAI_API_KEY = " ";
    private static String AZURE_OPENAI_API_ENDPOINT = "";
    private static String MODEL_ID = "";

    public static void main(String[] args) {

        RetryOptions retryOptions = new RetryOptions(new ExponentialBackoffOptions()
                .setMaxDelay(Duration.ofSeconds(10))
                .setBaseDelay(Duration.ofSeconds(2))
                .setMaxRetries(3));


        OpenAIAsyncClient client = new OpenAIClientBuilder().retryOptions(retryOptions)
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .buildAsyncClient();

        ChatCompletionService openAIChatCompletion = OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(client)
                .withModelId(MODEL_ID)
                .build();


        Kernel kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, openAIChatCompletion)
                .build();

        String question = "Polly 图书馆有多受欢迎?";


        KernelFunction<String> function = KernelFunctionFromPrompt.<String>builder()
                .withTemplate(question)
                .build();

        try {
            var result = kernel.invokeAsync(function).block();
            System.out.println("结果为:" + result.getResult());
        } catch (Exception e) {
            System.out.println("重试");
        }


    }


}
