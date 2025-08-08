package com.coderpwh.syntaxexamples.java;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.implementation.telemetry.SemanticKernelTelemetry;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.semanticfunctions.KernelArguments;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import io.opentelemetry.context.Scope;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FunctionTelemetry_Example {

    public static final String AZURE_OPENAI_API_KEY = "";

    public static final String AZURE_OPENAI_API_ENDPOINT = "";

    public static final String MODEL_ID = "";


    public static void main(String[] args) {
        requestsWithSpanContext();

        requestsWithScope();

        testNestedCalls();


    }


    private static void requestsWithSpanContext() {
        Span fakeRequest = GlobalOpenTelemetry.getTracer("Custom")
                .spanBuilder("GET /requestsWithSpanContext")
                .setSpanKind(SpanKind.SERVER)
                .setAttribute("http.request.method", "GET")
                .setAttribute("url.path", "/requestsWithSpanContext")
                .setAttribute("url.scheme", "http")
                .startSpan();

        SemanticKernelTelemetry telemetry = new SemanticKernelTelemetry(GlobalOpenTelemetry.getTracer("Custom"), fakeRequest.getSpanContext());

        sequentialFunctionCalls(telemetry);

        fakeRequest.setStatus(StatusCode.OK);
        fakeRequest.end();
    }


    public static void requestsWithScope() {
        Span fakeRequest = GlobalOpenTelemetry.getTracer("Custom")
                .spanBuilder("GET /requestsWithScope")
                .setSpanKind(SpanKind.SERVER)
                .setAttribute("http.request.method", "GET")
                .setAttribute("url.path", "/requestsWithScope")
                .setAttribute("url.scheme", "http")
                .startSpan();

        SemanticKernelTelemetry telemetry = new SemanticKernelTelemetry();

        try (Scope scope = fakeRequest.makeCurrent()) {
            sequentialFunctionCalls(telemetry);
        }

        fakeRequest.setStatus(StatusCode.OK);
        fakeRequest.end();

    }


    /***
     *
     * Sequential Function Calls
     * @param telemetry
     */
    public static void sequentialFunctionCalls(SemanticKernelTelemetry telemetry) {
        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .buildAsyncClient();

        ChatCompletionService chat = OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(client)
                .withModelId(MODEL_ID)
                .build();


        var plugin = KernelPluginFactory.createFromObject(new PetPlugin(), "PetPlugin");

        var kernel = Kernel.builder().withAIService(ChatCompletionService.class, chat).withPlugin(plugin).build();

        var chatHistory = new ChatHistory();
        chatHistory.addUserMessage("What is the name and type of the pet with id ca2fc6bc-1307-4da6-a009-d7bf88dec37b?");

        var messages = chat.getChatMessageContentsAsync(chatHistory, kernel,
                InvocationContext.builder()
                        .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                        .withReturnMode(InvocationReturnMode.FULL_HISTORY)
                        .withTelemetry(telemetry).build()).block();

        chatHistory = new ChatHistory(messages);
        System.out.println("THE NAME AND TYPE IS: " + chatHistory.getLastMessage().get().getContent());
    }


    public static void testNestedCalls() {

        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .buildAsyncClient();


        ChatCompletionService chat = OpenAIChatCompletion.builder()
                .withModelId(MODEL_ID)
                .withOpenAIAsyncClient(client)
                .build();


        var plugin = KernelPluginFactory.createFromObject(new TextAnalysisPlugin(), "TextAnalysisPlugin");

        var kernel = Kernel.builder().withAIService(ChatCompletionService.class, chat).withPlugin(plugin).build();


        SemanticKernelTelemetry telemetry = new SemanticKernelTelemetry();

        Span span = GlobalOpenTelemetry.getTracer("Test")
                .spanBuilder("testNestedCalls span")
                .setSpanKind(SpanKind.SERVER)
                .startSpan();


        try (Scope scope = span.makeCurrent()) {
            String analysed = kernel.invokePromptAsync("""
                      Analyse the following text:
                      Hello There
                    """, KernelArguments.builder().build(),InvocationContext
                    .builder()
                    .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                    .withReturnMode(InvocationReturnMode.NEW_MESSAGES_ONLY)
                    .withTelemetry(telemetry)
                    .build()).withResultType(String.class)
                    .map(result->{
                        return result.getResult();
                    }).block();
            System.out.println(analysed);
        }finally {
            span.end();
        }
    }


    public static class TextAnalysisPlugin {
        @DefineKernelFunction(name = "uppercase", description = "Change all string chars to uppercase.")
        public String uppercase(@KernelFunctionParameter(description = "Text to uppercase", name = "input") String text) {
            return text.toUpperCase();
        }


        @DefineKernelFunction(name = "sha256sum", description = "Calculates a sha256 of the input", returnType = "string")
        public Mono<String> sha256sum(@KernelFunctionParameter(description = "The input to checksum", name = "input", type = String.class) String input, Kernel kernel, SemanticKernelTelemetry telemetry) throws NoSuchAlgorithmException {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String hashStr = new BigInteger(1, hash).toString(16);

            return kernel.invokePromptAsync("""
                     Uppercase the following text:
                                            === BEGIN TEXT ===
                                            %s
                                            === END TEXT ===
                    """.formatted(hashStr).stripIndent(), null, InvocationContext
                    .builder()
                    .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                    .withReturnMode(InvocationReturnMode.NEW_MESSAGES_ONLY)
                    .withTelemetry(telemetry).build()).withResultType(String.class).map(result -> {
                return result.getResult();
            });

        }


    }


    /***
     * Pet Plugin
     * @return
     */
    public static class PetPlugin {
        @DefineKernelFunction(name = "getPetName", description = "Retrieves the pet for a given ID.")
        public String getPetName(
                @KernelFunctionParameter(name = "petId", description = "The pets id") String id) {
            if (id.equals("ca2fc6bc-1307-4da6-a009-d7bf88dec37b")) {
                return "Snuggles";
            }

            throw new RuntimeException("Pet not found");
        }

        @DefineKernelFunction(name = "getPetType", description = "Retrieves the type of pet for a given ID.")
        public String getPetType(
                @KernelFunctionParameter(name = "petId", description = "The pets id") String id) {
            if (id.equals("ca2fc6bc-1307-4da6-a009-d7bf88dec37b")) {
                return "cat";
            }

            throw new RuntimeException("Pet not found");
        }
    }


}
