package com.coderpwh.plugins;

import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;

public class ConversationSummaryPlugin {


    private static final int MaxTokens = 1024;

    private KernelFunction<String> summarizeConversationFunction;

    private  KernelFunction<String>  conversationActionItemsFunction;

    private  KernelFunction<String>  conversationTopicsFunction;


    public ConversationSummaryPlugin(){

        PromptExecutionSettings settings = PromptExecutionSettings.builder()
                .withMaxTokens(MaxTokens)
                .withTemperature(0.1)
                .withTopP(0.5)
                .build();






    }


}
