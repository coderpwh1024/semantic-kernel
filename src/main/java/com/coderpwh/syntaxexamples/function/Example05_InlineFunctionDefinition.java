package com.coderpwh.syntaxexamples.function;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.semanticfunctions.KernelArguments;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionFromPrompt;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class Example05_InlineFunctionDefinition {

    private static String AZURE_OPENAI_API_KEY = "";
    private static String AZURE_OPENAI_API_ENDPOINT = "";
    private static String MODEL_ID = "";

    public static void main(String[] args) {


        // 构建 OpenAIAsyncClient
        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .buildAsyncClient();


        // 构建 ChatCompletionService
        ChatCompletionService chatCompletionService = OpenAIChatCompletion
                .builder()
                .withOpenAIAsyncClient(client)
                .withModelId(MODEL_ID)
                .build();


        Kernel kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();

        System.out.println("======== Inline Function Definition ========");


        String promptTemplate = """
                    Generate a creative reason or excuse for the given event.
                    Be creative and be funny. Let your imagination run wild.

                    Event: I am running late.
                    Excuse: I was being held ransom by giraffe gangsters.

                    Event: I haven't been to the gym for a year
                    Excuse: I've been too busy training my pet dragon.

                    Event: {{$input}}
                """.stripIndent();


        var excuseFunction = KernelFunctionFromPrompt.builder()
                .withTemplate(promptTemplate)
                .withDefaultExecutionSettings(
                        PromptExecutionSettings.builder()
                                .withTemperature(0.4)
                                .withTopP(1)
                                .withMaxTokens(100)
                                .build())
                .build();


        var result = kernel
                .invokeAsync(excuseFunction)
                .withArguments(
                        KernelArguments.builder()
                                .withInput("I missed the F1 final race")
                                .build())
                .block();

        System.out.println(result.getResult());

        result = kernel.invokeAsync(excuseFunction)
                .withArguments(
                        KernelArguments.builder()
                                .withInput("sorry I forgot your birthday")
                                .build())
                .block();
        System.out.println(result.getResult());


        var date = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC)
                .format(Instant.ofEpochSecond(1));
        var message = "Translate this date " + date + " to French format";
        var fixedFunction = KernelFunction.createFromPrompt(message)
                .withDefaultExecutionSettings(
                        PromptExecutionSettings.builder()
                                .withMaxTokens(100)
                                .build())
                .build();

        FunctionResult<?> fixedFunctionResult = kernel
                .invokeAsync(fixedFunction)
                .block();
        System.out.println(fixedFunctionResult.getResult());


    }
}
