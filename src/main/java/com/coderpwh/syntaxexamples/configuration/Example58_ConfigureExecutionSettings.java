package com.coderpwh.syntaxexamples.configuration;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionFromPrompt;
import com.microsoft.semantickernel.semanticfunctions.OutputVariable;
import com.microsoft.semantickernel.semanticfunctions.PromptTemplateConfig;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;

public class Example58_ConfigureExecutionSettings {

    private static String AZURE_OPENAI_API_KEY = "";
    private static String AZURE_OPENAI_API_ENDPOINT = "";
    private static String MODEL_ID = "";


    public static void main(String[] args) {

        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .buildAsyncClient();


        ChatCompletionService chatCompletionService = OpenAIChatCompletion
                .builder()
                .withModelId(MODEL_ID)
                .withOpenAIAsyncClient(client)
                .build();

        var kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();


        var prompt = "Hello AI, what can you do for me?";

        var result = kernel.invokeAsync(
                        KernelFunctionFromPrompt.builder()
                                .withTemplate(prompt)
                                .withDefaultExecutionSettings(
                                        PromptExecutionSettings.builder()
                                                .withMaxTokens(60)
                                                .withTemperature(0.7)
                                                .build())
                                .withOutputVariable(new OutputVariable<>("result", String.class))
                                .build())
                .block();



        System.out.println(result.getResult());


        String configPayload = """
              {
              "schema": 1,
              "name": "HelloAI",
              "description": "Say hello to an AI",
              "type": "completion",
              "completion": {
                "max_tokens": 256,
                "temperature": 0.5,
                "top_p": 0.0,
                "presence_penalty": 0.0,
                "frequency_penalty": 0.0
              }
            }""".stripIndent();


        var  promptConfig = PromptTemplateConfig.parseFromJson(configPayload).copy().withTemplate(prompt).build();


        var func = KernelFunction.createFromPrompt(promptConfig).build();


        var result2 = kernel.invokeAsync(func).block();

        System.out.println(result2.getResult());






    }


}
