package com.coderpwh.syntaxexamples.chatcompletion;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionFromPrompt;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;

import java.util.concurrent.CountDownLatch;

public class Example63_ChatCompletionPrompts {


    private static String AZURE_OPENAI_API_KEY = " ";
    private static String AZURE_OPENAI_API_ENDPOINT = "";
    private static String MODEL_ID = " ";


    public static void main(String[] args) throws InterruptedException {

        // 创建OpenAI客户端
        OpenAIAsyncClient client = new OpenAIClientBuilder().credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY)).endpoint(AZURE_OPENAI_API_ENDPOINT).buildAsyncClient();


        // 创建ChatCompletion服务
        ChatCompletionService openAIChatCompletion = OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(client)
                .withModelId(MODEL_ID)
                .build();


        // 创建Kernel
        Kernel kernel = Kernel.builder().withAIService(ChatCompletionService.class, openAIChatCompletion).build();

        // 提示词
        String chatPrompt = """
                <message role="user">What is Seattle?</message>
                <message role="system">Respond with JSON.</message>
                """.stripIndent();

        // 创建Kernel函数
        var chatSemanticFunction = KernelFunctionFromPrompt.<String>builder()
                .withTemplate(chatPrompt)
                .build();

        // 执行函数的结果
        var chatPromptResult = kernel.invoke(chatSemanticFunction);

        System.out.println("提示词为:");
        System.out.println(chatPrompt);
        System.out.println("聊天提示词的结果为:");
        System.out.println(chatPromptResult.getResult());

        CountDownLatch  cd1 = new CountDownLatch(1);
        System.out.println("结果为:");
        kernel.invokeAsync(chatSemanticFunction)
                .doFinally(x->cd1.countDown())
                .subscribe(result-> System.out.println(result.getResult()));
        cd1.await();;

    }


}
