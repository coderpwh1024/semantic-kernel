package com.coderpwh.syntaxexamples.chatcompletion;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;

public class Example44_MultiChatCompletion {


    private static String AZURE_OPENAI_API_KEY = "";
    private static String AZURE_OPENAI_API_ENDPOINT = "";
    private static String MODEL_ID = "";


    public static void main(String[] args) {
        System.out.println("==========Open AI - Multiple Chat Completion ==============");

        // 创建OpenAI客户端
        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .buildAsyncClient();

        // 创建ChatCompletion服务
        ChatCompletionService chatCompletionService = OpenAIChatCompletion.builder().withModelId(MODEL_ID).withOpenAIAsyncClient(client).build();


    }


}
