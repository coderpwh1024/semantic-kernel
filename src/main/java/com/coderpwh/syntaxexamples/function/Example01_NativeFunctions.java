package com.coderpwh.syntaxexamples.function;

import com.coderpwh.plugins.text.TextPlugin;

public class Example01_NativeFunctions {

    public static void main(String[] args) {

        TextPlugin textPlugin = new TextPlugin();

//        String result = textPlugin.concat("Hello", "World");

        // Use function without kernel
        String result = textPlugin.uppercase("ciao!");

        System.out.println(result);

    }
}
