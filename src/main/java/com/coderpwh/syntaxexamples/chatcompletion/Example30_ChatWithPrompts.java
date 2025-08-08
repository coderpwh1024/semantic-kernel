package com.coderpwh.syntaxexamples.chatcompletion;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.implementation.EmbeddedResourceLoader;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;

import java.io.FileNotFoundException;

public class Example30_ChatWithPrompts {


    private static String AZURE_OPENAI_API_KEY = "";
    private static String AZURE_OPENAI_API_ENDPOINT = "";
    private static String MODEL_ID = "";


    public static void main(String[] args) throws FileNotFoundException {


        OpenAIAsyncClient client = new OpenAIClientBuilder()
                 .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                 .endpoint(AZURE_OPENAI_API_ENDPOINT)
                 .buildAsyncClient();


        ChatCompletionService openAIChatCompletion= OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(client)
                .withModelId(MODEL_ID)
                .build();

        Kernel kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, openAIChatCompletion)
                .build();

        System.out.println("===============================提示词部分=======================================");

        var systemPromptTemplate= EmbeddedResourceLoader.readFile("", Example30_ChatWithPrompts.class);
        var selectedText = EmbeddedResourceLoader.readFile("30-user-context.txt", Example30_ChatWithPrompts.class);
        var userPromptTemplate = EmbeddedResourceLoader.readFile("30-user-prompt.txt", Example30_ChatWithPrompts.class);








    }

}
