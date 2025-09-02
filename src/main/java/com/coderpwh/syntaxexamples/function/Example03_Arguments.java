package com.coderpwh.syntaxexamples.function;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import reactor.core.publisher.Mono;

import java.util.Locale;

public class Example03_Arguments {


    public static void main(String[] args) {

    }


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


        public Mono<String> appendDay() {
            return  Mono.just("");

        }

    }
}
