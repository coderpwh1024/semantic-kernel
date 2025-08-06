package com.coderpwh.syntaxexamples.java;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;

public class FunctionsWithinPrompts_Example {

    public static void main(String[] args) {


        String AZURE_OPENAI_API_KEY = " ";

        String AZURE_OPENAI_API_ENDPOINT = "";

        String MODEL_ID = "";


        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .buildAsyncClient();

        ChatCompletionService chatCompletionService = OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(client)
                .withModelId(MODEL_ID)
                .build();




    }


}
