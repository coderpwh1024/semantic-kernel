package com.coderpwh.syntaxexamples.chatcompletion;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;

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

        System.out.println("聊天内容:");
        System.out.println("------------------------------------------------------");
        ChatHistory chatHistory = new ChatHistory("你作为一名图书管理员，对书籍非常精通");

        var executionSettings  = PromptExecutionSettings.builder().withMaxTokens(1024).withTemperature(1).withResultsPerPrompt(2).build();

        chatHistory.addUserMessage("Hi,我正在找3中不同的书籍关于sci-fi");
        messageOutput(chatHistory);

        GPTReply(chatCompletionService, chatHistory, executionSettings);


    }


    /***
     *  输出消息
     * @param chatHistory
     */
    private  static void messageOutput(ChatHistory chatHistory){
        var message = chatHistory.getLastMessage().get();
        System.out.println(message.getAuthorRole()+":"+message.getContent());
        System.out.println("------------------------------------------------------");
    }

    private static void GPTReply(ChatCompletionService chatGPT, ChatHistory chatHistory, PromptExecutionSettings executionSettings) {

        var  invocationContent = InvocationContext.builder().withPromptExecutionSettings(executionSettings).build();

        var reply = chatGPT.getChatMessageContentsAsync(chatHistory,null,invocationContent).block();

        reply.forEach(streamingChatMessage -> {
            chatHistory.addAssistantMessage(streamingChatMessage.getContent());
            messageOutput(chatHistory);
        });

    }


}
