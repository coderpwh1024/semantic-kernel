package com.coderpwh.syntaxexamples.function;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;

public class Example05_InlineFunctionDefinition {

    private static String AZURE_OPENAI_API_KEY = " ";
    private static String AZURE_OPENAI_API_ENDPOINT = "";
    private static String MODEL_ID = " ";

    public static void main(String[] args) {


        // 构建 OpenAIAsyncClient
        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .buildAsyncClient();


        // 构建 ChatCompletionService
        ChatCompletionService chatCompletionService = OpenAIChatCompletion
                .builder()
                .withOpenAIAsyncClient(client)
                .withModelId(MODEL_ID)
                .build();


        Kernel kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();

         System.out.println("======== Inline Function Definition ========");

    }
}
