package com.coderpwh.plugins;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.contextvariables.ContextVariableTypes;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.semanticfunctions.KernelArguments;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import com.microsoft.semantickernel.text.TextChunker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class ConversationSummaryPlugin {


    private static final int MaxTokens = 1024;

    private KernelFunction<String> summarizeConversationFunction;

    private KernelFunction<String> conversationActionItemsFunction;

    private KernelFunction<String> conversationTopicsFunction;


    public ConversationSummaryPlugin() {
        PromptExecutionSettings settings = PromptExecutionSettings.builder()
                .withMaxTokens(MaxTokens)
                .withTemperature(0.1)
                .withTopP(0.5)
                .build();

        /**
         *  创建一个总结对话的函数
         */
        this.summarizeConversationFunction = KernelFunction.<String>createFromPrompt(PromptFunctionConstants.SummarizeConversationDefinition)
                .withDefaultExecutionSettings(settings)
                .withName("summarizeConversation")
                .withDescription("Given a section of a conversation transcript, summarize the part of the conversation.")
                .build();

        this.conversationActionItemsFunction = KernelFunction.<String>createFromPrompt(PromptFunctionConstants.GetConversationActionItemsDefinition)
                .withDefaultExecutionSettings(settings)
                .withName("conversationActionItems")
                .withDescription("Given a section of a conversation transcript, identify action items.")
                .build();


        this.conversationTopicsFunction = KernelFunction.<String>createFromPrompt(PromptFunctionConstants.GetConversationTopicsDefinition)
                .withDefaultExecutionSettings(settings)
                .withName("conversationTopics")
                .withDescription("Analyze a conversation transcript and extract key topics worth remembering")
                .build();
    }


    /***
     * 处理异步
     * @param func
     * @param input
     * @param kernel
     * @return
     */
    private static Mono<String> processAsync(KernelFunction<String> func, String input, Kernel kernel) {
        List<String> lines = TextChunker.splitMarkDownLines(input, MaxTokens);
        List<String> paragraphs = TextChunker.splitMarkdownParagraphs(lines, MaxTokens);

        return Flux.fromIterable(paragraphs).concatMap(paragraph -> {
            return func.invokeAsync(kernel).withArguments(KernelArguments.builder().withInput(paragraph).build())
                    .withResultType(ContextVariableTypes.getGlobalVariableTypeForClass(String.class));

        }).reduce("", (acc, next) -> {
            return acc + "\n" + next.getResult();
        });
    }


    /***
     * 摘要函数
     * @param input
     * @param kernel
     * @return
     */
    @DefineKernelFunction(description = "Given a long conversation transcript, summarize the conversation.", name = "SummarizeConversation", returnType = "java.lang.String")
    public Mono<String> SummarizeConversationAsync(@KernelFunctionParameter(name = "input", description = "A long conversation transcript.") String input, Kernel kernel) {
        return processAsync(this.summarizeConversationFunction, input, kernel);
    }


    /***
     * action函数
     * @param input
     * @param kernel
     * @return
     */
    @DefineKernelFunction(description = "Given a long conversation transcript, identify action items.", name = "GetConversationActionItems", returnType = "java.lang.String")
    public Mono<String> GetConversationActionItemsAsync(@KernelFunctionParameter(name = "input", description = "A long conversation transcript.") String input, Kernel kernel) {
        return processAsync(this.conversationActionItemsFunction, input, kernel);
    }


    /***
     * topic函数
     * @param input
     * @param kernel
     * @return
     */
    @DefineKernelFunction(description = "Given a long conversation transcript, identify topics worth remembering.", name = "GetConversationTopics", returnType = "java.lang.String")
    public Mono<String> GetConversationTopicsAsync(@KernelFunctionParameter(name = "input", description = "A long conversation transcript.") String input, Kernel kernel) {
        return processAsync(this.conversationTopicsFunction, input, kernel);
    }


}
