package com.coderpwh.syntaxexamples;

import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;

import java.util.Locale;

public class LocaleParser {



    private LocaleParser() {

    }

    @SuppressWarnings("StringSplitter")
    public static final Locale parseLocale(String locale) {
        Locale parsedLocale = null;

        if (locale == null
                || "".equals(locale.trim())
                || KernelFunctionParameter.NO_DEFAULT_VALUE.equals(locale)) {
            return Locale.getDefault();
        } else if (locale.indexOf("-") > -1) {
            parsedLocale = Locale.forLanguageTag(locale);
        } else if (locale.indexOf("_") > -1) {
            String[] parts = locale.split("_");
            parsedLocale = new Locale(parts[0], parts[1]);
        } else {
            parsedLocale = new Locale(locale);
        }

        return parsedLocale;
    }
}
