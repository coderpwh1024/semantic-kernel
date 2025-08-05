package com.coderpwh.syntaxexamples.agent;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;

/**
 * @author coderpwh
 */


public class CompletionAgent {

    public static void main(String[] args) {

        String AZURE_OPENAI_API_KEY = "";
        String AZURE_OPENAI_API_ENDPOINT = "";
        String AZURE_OPENAI_API_VERSION = "2023-05-15";
        String MODEL_ID = "";


        OpenAIAsyncClient client = new OpenAIClientBuilder().credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .endpoint(AZURE_OPENAI_API_ENDPOINT).buildAsyncClient();


        ChatCompletionService chatCompletion = OpenAIChatCompletion.builder()
                .withModelId(MODEL_ID)
                .withOpenAIAsyncClient(client)
                .build();


/*        Kernel kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletion)*/



    }


}
