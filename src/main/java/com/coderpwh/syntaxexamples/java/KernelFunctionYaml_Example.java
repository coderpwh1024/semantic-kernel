package com.coderpwh.syntaxexamples.java;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.implementation.EmbeddedResourceLoader;
import com.microsoft.semantickernel.implementation.telemetry.SemanticKernelTelemetry;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.semanticfunctions.KernelArguments;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionYaml;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;

import javax.annotation.Nullable;
import java.io.IOException;

public class KernelFunctionYaml_Example {

    public static String AZURE_OPENAI_API_KEY = "x";

    public static String AZURE_OPENAI_API_ENDPOINT = "x";

    public static String MODEL_ID = "x";


    public static void main(String[] args) throws IOException {
        run(null);
    }


    public static void run(@Nullable SemanticKernelTelemetry telemetry) throws IOException {

        OpenAIAsyncClient client = new OpenAIClientBuilder().credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .buildAsyncClient();

        ChatCompletionService openAIChatCompletion = OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(client)
                .withModelId(MODEL_ID)
                .build();

        Kernel.Builder kernelBuilder = Kernel.builder().withAIService(ChatCompletionService.class, openAIChatCompletion);

        semanticKernelTemplate(kernelBuilder.build(),telemetry);
        handlebarsTemplate(kernelBuilder.build(),telemetry);

    }


    /***
     * 使用Handlebars
     * @param kernel
     * @param telemetry
     * @throws IOException
     */
    private static void handlebarsTemplate(Kernel kernel, @Nullable SemanticKernelTelemetry telemetry) throws IOException {
        String yaml = EmbeddedResourceLoader.readFile("GenerateStoryHandlebars.yaml", KernelFunctionYaml_Example.class);

        KernelFunction<String> function = KernelFunctionYaml.fromPromptYaml(yaml);

        FunctionResult<String> result = function.invokeAsync(kernel).withArguments(KernelArguments
                        .builder()
                        .withVariable("length", 5)
                        .withVariable("topic", "dogs").build())
                .withTelemetry(telemetry).block();

        System.out.println(result.getResult());
    }


    /***
     * 使用KernelFunctionYaml
     * @param kernel
     * @param telemetry
     * @throws IOException
     */
    private static void semanticKernelTemplate(Kernel kernel, @Nullable SemanticKernelTelemetry telemetry) throws IOException {
        String yaml = EmbeddedResourceLoader.readFile("GenerateStoryHandlebars.yaml", KernelFunctionYaml_Example.class);

        KernelFunction<String> function = KernelFunctionYaml.fromPromptYaml(yaml);

        FunctionResult<String> result = function.invokeAsync(kernel).withArguments(KernelArguments.builder()
                        .withVariable("length", 5)
                        .withVariable("topic", "cats").build())
                .withTelemetry(telemetry)
                .block();

        System.out.println(result.getResult());
    }


}
