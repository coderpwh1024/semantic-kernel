package com.coderpwh.syntaxexamples.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.contextvariables.ContextVariableTypeConverter;
import com.microsoft.semantickernel.semanticfunctions.KernelArguments;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomTypes_Example {


    public static void main(String[] args) {

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


        ContextVariableTypeConverter<Pet> typeConverter = ContextVariableTypeConverter.builder(Pet.class)
                .toPromptString(petToString)
                .fromPromptString(stringToPet)
                .build();


        Pet updated = kernel.invokePromptAsync("Change Sandy's name to Daisy:\\n{{$Sandy}}", KernelArguments.builder()
                .withVariable("Sandy", sandy, typeConverter).build()).withTypeConverter(typeConverter).withResultType(Pet.class).block().getResult();

        System.out.println("Sandy's updated record: " + updated);
    }


}
