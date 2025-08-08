package com.coderpwh.syntaxexamples.java;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.semanticfunctions.KernelArguments;
import com.microsoft.semantickernel.semanticfunctions.KernelPromptTemplateFactory;
import com.microsoft.semantickernel.semanticfunctions.PromptTemplateConfig;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionsHandlebars_Example {
    public static void main(String[] args) {
        Kernel kernel = Kernel.builder().withPlugin(KernelPluginFactory.createFromObject(new StringHelper(), "StringHelper")).build();
        List<String> choices = Arrays.asList("ContinueConversation", "EndConversation");

        var promptTemplate = new KernelPromptTemplateFactory().tryCreate(PromptTemplateConfig.builder().withTemplate(
                """
                        Choices: {{choices}}.
                        Screaming Snake Case: {{StringHelper-toScreamingSnakeCase (StringHelper-toListString [choices])}}
                        """).withTemplateFormat("handlebars").build());

        var renderedPrompt = promptTemplate.renderAsync(kernel, KernelArguments.builder().withVariable("choices", choices).build(), null);

        System.out.println("结果为:" + renderedPrompt);
    }


    public static class StringHelper {
        @DefineKernelFunction(name = "toScreamingSnakeCase", description = "Converts a string to screaming snake case")
        public static String toScreamingSnakeCase(@KernelFunctionParameter(name = "in", description = "The string to screaming snake") String in) {
            Pattern p = Pattern.compile("([^,])([A-Z])([^A-Z]+)");

            Matcher m = p.matcher(in);
            StringBuilder builder = new StringBuilder();
            while (m.find()) {
                m.appendReplacement(builder, m.group(1) + "_" + m.group(2) + m.group(3));
            }

            return builder.toString().toUpperCase(Locale.ROOT);
        }

        @DefineKernelFunction(name = "toListString", description = "Converts a string to list")
        public static String toListString(@KernelFunctionParameter(name = "in", description = "The string to convert to a list", type = java.util.List.class) List<String> in) {
            return String.join(",", in);
        }

    }


}
