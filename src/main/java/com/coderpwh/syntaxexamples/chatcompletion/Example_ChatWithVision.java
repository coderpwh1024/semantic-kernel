package com.coderpwh.syntaxexamples.chatcompletion;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.implementation.CollectionUtil;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.message.ChatMessageImageContent;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Example_ChatWithVision {


    private static String AZURE_OPENAI_API_KEY = " ";
    private static String AZURE_OPENAI_API_ENDPOINT = "";
    private static String MODEL_ID = "";

    public static void main(String[] args) throws MalformedURLException {


        // 构建 OpenAIAsyncClient
        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .buildAsyncClient();


        // 创建 ChatCompletionService
        ChatCompletionService chatCompletionService = OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(client)
                .withModelId(MODEL_ID)
                .build();

        describeUrl(chatCompletionService);
//        describeImage(chatCompletionService);
    }


    private static void describeImage(ChatCompletionService chatGPT) throws MalformedURLException {

        try (InputStream duke = Example_ChatWithVision.class.getResourceAsStream("duke.png")) {
            byte[] image = duke.readAllBytes();

            ChatHistory chatHistory = new ChatHistory(
                    "You look at images and answer questions about them");

            // First user message
            chatHistory.addUserMessage(
                    "This image is a cartoon drawing of the Java Duke character riding a dinosaur. What type of dinosaur is it?");
            chatHistory.addMessage(
                    ChatMessageImageContent.builder()
                            .withImage("png", image)
                            .build());

            var reply = chatGPT.getChatMessageContentsAsync(chatHistory, null, null);

            String message = reply
                    .mapNotNull(CollectionUtil::getLastOrNull)
                    .map(ChatMessageContent::getContent)
                    .block();

            System.out.println("\n------------------------");
            System.out.print(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void describeUrl(ChatCompletionService chatGPT) throws MalformedURLException {
        ChatHistory chatHistory = new ChatHistory("You look at images and describe them");

        // First user message
        chatHistory.addUserMessage("Describe the following image");
        chatHistory.addMessage(
                ChatMessageImageContent.builder()
                        .withImageUrl(new URL("https://cr.openjdk.org/~jeff/Duke/jpg/Welcome.jpg"))
                        .build());

        var reply = chatGPT.getChatMessageContentsAsync(chatHistory, null, null);

        String message = reply
                .mapNotNull(CollectionUtil::getLastOrNull)
                .map(ChatMessageContent::getContent)
                .block();

        System.out.println("\n------------------------");
        System.out.print(message);
    }


}
