package com.coderpwh.syntaxexamples.java;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.coderpwh.plugins.ConversationSummaryPlugin;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.semanticfunctions.KernelArguments;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionFromPrompt;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.message.ChatMessageTextContent;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FunctionsWithinPrompts_Example {

    public static InputStream INPUT = System.in;

    public static void main(String[] args) {


        String AZURE_OPENAI_API_KEY = "";

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

        KernelPlugin plugin = KernelPluginFactory.createFromObject(new ConversationSummaryPlugin(), "ConversationSummaryPlugin");

        Kernel kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .withPlugin(plugin)
                .build();

        List<String> choices = Arrays.asList("ContinueConversation", "EndConversation");

        List<ChatHistory> fewShortExample = Arrays.asList(
                new ChatHistory(Arrays.asList(
                        ChatMessageTextContent.userMessage("请尽快给营销团队发送一份批复"),
                        ChatMessageTextContent.systemMessage("Intent"),
                        ChatMessageTextContent.assistantMessage("继续对话")
                )),
                new ChatHistory(Arrays.asList(
                        ChatMessageTextContent.userMessage("Thats all"),
                        ChatMessageTextContent.systemMessage("Intent"),
                        ChatMessageTextContent.assistantMessage("结束对话")
                ))
        );

        var getIntent = KernelFunctionFromPrompt.<String>createFromPrompt("""
                         <message role="system">Instructions: What is the intent of this request?
                                            Do not explain the reasoning, just reply back with the intent. If you are unsure, reply with {{choices.[0]}}.
                                            Choices: {{choices}}.</message>
                                        
                                            {{#each fewShotExamples}}
                                                {{#each this}}
                                                    <message role="{{role}}">{{content}}</message>
                                                {{/each}}
                                            {{/each}}
                                        
                                            <message role="user">{{request}}</message>
                                            <message role="system">Intent:</message>
                        """.stripIndent())
                .withTemplateFormat("handlebars")
                .build();

        var chat = KernelFunctionFromPrompt.<String>createFromPrompt("""
                 Answer the users question below taking into account the conversation so far.
                                
                            [START SUMMARY OF CONVERSATION SO FAR]
                                {{ConversationSummaryPlugin.SummarizeConversation $history}}
                            [END SUMMARY OF CONVERSATION SO FAR]
                                
                            User: {{$request}}
                            Assistant:
                """.stripIndent()).build();

        Scanner scanner = new Scanner(INPUT);

        ChatHistory history = new ChatHistory();
        while (true) {
            System.out.println("User>");
            var request = scanner.nextLine();

            String historyString = history.getMessages()
                    .stream()
                    .map(message -> message.getAuthorRole() + ":" + message.getContent())
                    .collect(Collectors.joining("\n"));


            var intent = kernel.invokeAsync(getIntent).withArguments(KernelArguments.builder()
                            .withVariable("request", request)
                            .withVariable("choices", choices)
                            .withVariable("history", historyString)
                            .withVariable("fewShotExamples", fewShortExample)
                            .build()).withToolCallBehavior(ToolCallBehavior.allowOnlyKernelFunctions(true, plugin.get("SummarizeConversation")))
                    .block();

            if ("EndConversation" .equalsIgnoreCase(intent.getResult())) {
                break;
            }

            var chatResult = kernel.invokeAsync(chat).withArguments(KernelArguments.builder()
                    .withVariable("request", request)
                    .withVariable("history", historyString)
                    .build()).block();

            String message = chatResult.getResult();
            System.out.println("Assistant: " + message);
            System.out.println();

            history.addUserMessage(request);
            history.addAssistantMessage(message);

        }


    }


}
