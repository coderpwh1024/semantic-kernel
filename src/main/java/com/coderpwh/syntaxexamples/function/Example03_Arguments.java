package com.coderpwh.syntaxexamples.function;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.semanticfunctions.KernelArguments;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import reactor.core.publisher.Mono;

import java.util.Locale;

public class Example03_Arguments {


    public static void main(String[] args) {

        Kernel kernel = Kernel.builder().build();

        KernelPlugin kernelFunctions = KernelPluginFactory.createFromObject(new StaticTextPlugin(),"text");

        KernelArguments arguments = KernelArguments.builder().withInput("Today is:").withVariable("day","Monday").build();


        FunctionResult<?> resultValue = kernel.invokeAsync(kernelFunctions.get("AppendDay")).withArguments(arguments).block();
        System.out.println(resultValue.getResult());

    }


    /***
     * 静态插件
     */
    public static class StaticTextPlugin {

        /***
         * 静态方法
         * @param text
         * @return
         */
        @DefineKernelFunction(description = "Change all string chars to uppercase.", name = "uppercase", returnType = "java.lang.String")
        public static Mono<String> uppercase(@KernelFunctionParameter(name = "input", description = "Text to uppercase") String text) {
            return Mono.just(text.toUpperCase(Locale.ROOT));
        }


        /***
         * 动态方法
         * @param input
         * @param day
         * @return
         */
        @DefineKernelFunction(description = "Append current day to a string", name = "appendDay", returnType = "java.lang.String")
        public Mono<String> appendDay(@KernelFunctionParameter(description = "Text to append to", name = "input") String input,
                                      @KernelFunctionParameter(description = "Current day", name = "day") String day) {
            return Mono.just(input + day);
        }

    }
}
