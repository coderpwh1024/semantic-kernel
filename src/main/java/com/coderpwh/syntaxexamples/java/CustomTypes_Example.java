package com.coderpwh.syntaxexamples.java;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.contextvariables.ContextVariableTypeConverter;
import com.microsoft.semantickernel.contextvariables.ContextVariableTypes;
import com.microsoft.semantickernel.contextvariables.converters.ContextVariableJacksonConverter;
import com.microsoft.semantickernel.semanticfunctions.KernelArguments;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomTypes_Example {


    public static void main(String[] args) {

        String AZURE_OPENAI_API_KEY = " ";
        String AZURE_OPENAI_API_ENDPOINT = "";
        String MODEL_ID = "";


        OpenAIAsyncClient client = new OpenAIClientBuilder().credential(new AzureKeyCredential(AZURE_OPENAI_API_KEY))
                .endpoint(AZURE_OPENAI_API_ENDPOINT)
                .buildAsyncClient();

        ChatCompletionService chatCompletionService = OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(client)
                .withModelId(MODEL_ID)
                .build();


        exampleBuildingCustomConverter(chatCompletionService);
//        exampleUsingJackson(chatCompletionService);
        exampleUsingGlobalTypes(chatCompletionService);
    }


    public record Pet(String name, int age, String species) {
        @JsonCreator
        public Pet(@JsonProperty("name") String name, @JsonProperty("age") int age, @JsonProperty("species") String species) {
            this.name = name;
            this.age = age;
            this.species = species;
        }

        @Override
        public String toString() {
            return "Pet{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", species='" + species + '\'' +
                    '}';
        }
    }


    /***
     * 自定义转换器
     * @param chatCompletionService
     */
    private static void exampleBuildingCustomConverter(ChatCompletionService chatCompletionService) {
        Pet sandy = new Pet("Sandy", 3, "Dog");

        Kernel kernel = Kernel.builder().withAIService(ChatCompletionService.class, chatCompletionService).build();

        Function<Pet, String> petToString = pet -> "name:" + pet.name + "\n" + "age:" + pet.age + "\n" + "species:" + pet.species() + "\n";

        Function<String, Pet> stringToPet = prompt -> {
            Map<String, String> properties = Arrays.stream(prompt.split("\n"))
                    .collect(Collectors.toMap(
                            line -> line.split(":")[0].trim(),
                            line -> line.split(":")[1].trim()

                    ));
            return new Pet(properties.get("name"), Integer.parseInt(properties.get("age")), properties.get("species"));
        };


        // 文本(字符串)转换器
        ContextVariableTypeConverter<Pet> typeConverter = ContextVariableTypeConverter.builder(Pet.class)
                .toPromptString(petToString)
                .fromPromptString(stringToPet)
                .build();


        Pet updated = kernel.invokePromptAsync("Change Sandy's name to Daisy:\\n{{$Sandy}}", KernelArguments.builder()
                        .withVariable("Sandy", sandy, typeConverter).build())
                .withTypeConverter(typeConverter)
                .withResultType(Pet.class)
                .block()
                .getResult();

        System.out.println("exampleBuildingCustomConverter方法中-更新后的结果为:" + updated);
    }


    /***
     * 使用Jackson
     * @param chatCompletionService
     */
    public static void exampleUsingJackson(ChatCompletionService chatCompletionService) {

        Pet sandy = new Pet("Sandy", 3, "Dog");

        Kernel kernel = Kernel.builder().withAIService(ChatCompletionService.class, chatCompletionService).build();

        // json 转换器
        ContextVariableTypeConverter<Pet> typeConverter = ContextVariableJacksonConverter.create(Pet.class);


        // 修改提示词，明确要求返回JSON格式
        String prompt = """
        Increase Sandy's age by a year.
        Respond ONLY with a valid JSON object in this exact format:
        {"name": "Sandy", "age": 3, "type": "Dog"}
        Do not include any other text.
        
        Input data:
        {{$Sandy}}
        """;
        try {
            Pet updated = kernel.invokePromptAsync("Increase Sandy's age by a year:\n{{$Sandy}}",
                    KernelArguments.builder()
                            .withVariable("Sandy", sandy, typeConverter)
                            .build()).withTypeConverter(typeConverter).withResultType(Pet.class).block().getResult();

            System.out.println("更新后的结果为:"+updated);
        } catch (Exception e) {
            // 如果JSON解析失败，尝试获取原始响应
            String rawResponse = kernel.invokePromptAsync("Increase Sandy's age by a year:\n{{$Sandy}}",
                    KernelArguments.builder()
                            .withVariable("Sandy", sandy, typeConverter)
                            .build()).block().getResult().toString();

            System.out.println("返回后的实体为:" + rawResponse);
            // 在这里可以添加自定义解析逻辑来处理自然语言响应
        }
    }


    /***
     * 使用全局类型
     * @param chatCompletionService
     */
    public static void exampleUsingGlobalTypes(ChatCompletionService chatCompletionService) {

        Pet sandy = new Pet("Sandy", 3, "Dog");

        Kernel kernel = Kernel.builder().withAIService(ChatCompletionService.class, chatCompletionService).build();

        ContextVariableTypeConverter<Pet> typeConverter = ContextVariableJacksonConverter.create(Pet.class);
        ContextVariableTypes.addGlobalConverter(typeConverter);

        try {
            Pet updated = kernel.invokePromptAsync("Sandy's is actually a cat correct this:\n{{$Sandy}}", KernelArguments.builder()
                    .withVariable("Sandy", sandy)
                    .build())
                    .withResultType(Pet.class)
                    .block()
                    .getResult();
            System.out.println("Sandy's updated record: " + updated);
        } catch (Exception e) {
            Pet updated = kernel.invokePromptAsync("Sandy's is actually a cat correct this:\n{{$Sandy}}",
                    KernelArguments.builder()
                    .withVariable("Sandy", sandy)
                    .build())
                    .withResultType(Pet.class)
                    .block().
                    getResult();
        }




    }


}
