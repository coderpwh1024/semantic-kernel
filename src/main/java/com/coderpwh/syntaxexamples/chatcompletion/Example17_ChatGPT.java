package com.coderpwh.syntaxexamples.chatcompletion;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;

public class Example17_ChatGPT {


    private static String AZURE_OPENAI_API_KEY = " ";
    private static String AZURE_OPENAI_API_ENDPOINT = "";
    private static String MODEL_ID = "";

    public static void main(String[] args) {

        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .buildAsyncClient();


        ChatCompletionService chat = OpenAIChatCompletion.builder()
                .withModelId(MODEL_ID)
                .withOpenAIAsyncClient(client)
                .build();

        System.out.println(":");
        System.out.println("-------------------------------------------");

        ChatHistory chatHistory = new ChatHistory("You are a librarian, expert about books");
        chatHistory.addUserMessage("Hi, I'm looking for book suggestions");
        messageOutput(chatHistory);

        reply(chat, chatHistory);
        messageOutput(chatHistory);

        chatHistory.addUserMessage("I love history and philosophy, I'd like to learn something new about Greece, any suggestion");
        messageOutput(chatHistory);


        reply(chat,chatHistory);
        messageOutput(chatHistory);

    }

    /***
     * 输出消息
     * @param chatHistory
     */
    private static void messageOutput(ChatHistory chatHistory) {
        var messages = chatHistory.getLastMessage().get();
        System.out.println(messages.getAuthorRole() + ":" + messages.getContent());
        System.out.println("-------------------------------------------");

    }


    /***
     * 回复
     * @param chatCompletionService
     * @param chatHistory
     */
    private static void reply(ChatCompletionService chatCompletionService, ChatHistory chatHistory) {
        var reply = chatCompletionService.getChatMessageContentsAsync(chatHistory, null, null).block();

        StringBuilder message = new StringBuilder();
        reply.forEach(content -> message.append(content.getContent()));
        chatHistory.addAssistantMessage(message.toString());
    }


}
