package com.coderpwh.syntaxexamples.chatcompletion;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.implementation.CollectionUtil;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;

public class Example33_Chat {

    private static String AZURE_OPENAI_API_KEY = "";
    private static String AZURE_OPENAI_API_ENDPOINT = "";
    private static String MODEL_ID = "";


    public static void main(String[] args) {

        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .buildAsyncClient();

        ChatCompletionService chatCompletionService = OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(client)
                .withModelId(MODEL_ID)
                .build();

        System.out.println("聊天内容:");
        System.out.println("------------------------------------------------------");

        ChatHistory chatHistory = new ChatHistory("你作为一名图书管理员，对书籍非常精通");
        chatHistory.addUserMessage("Hi,我正在寻找一些书籍推荐");

        replay(chatCompletionService,chatHistory);


        chatHistory.addUserMessage("我喜欢历史和哲学，我想了解一下有关希腊的新知识，有什么建议吗？");
        messageOutput(chatHistory);


        replay(chatCompletionService,chatHistory);
        messageOutput(chatHistory);

    }


    /**
     * 输出消息
     * @param chatHistory
     */
    private static void messageOutput(ChatHistory chatHistory) {
        var messages = chatHistory.getLastMessage().get();
        System.out.println(messages.getAuthorRole() + ":" + messages.getContent());
        System.out.println("---------------------------------------------");
    }


     /**
     * 聊天回复
     * @param chat
     * @param chatHistory
     */
    private static void replay(ChatCompletionService chat, ChatHistory chatHistory) {
        var reply = chat.getChatMessageContentsAsync(chatHistory, null, null);
        System.out.println(AuthorRole.ASSISTANT + ":");

        String message = reply.mapNotNull(CollectionUtil::getLastOrNull)
                .doOnNext(streamingChatMessage -> {
                    String content = streamingChatMessage.getContent();
                    System.out.println(content);
                }).map(ChatMessageContent::getContent).block();

        System.out.println();
        System.out.println("------------------------------------------------------");
        chatHistory.addAssistantMessage(message);
    }


}
