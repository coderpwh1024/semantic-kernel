package com.coderpwh.plugins;

import com.coderpwh.syntaxexamples.LocaleParser;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimePlugin {

    public static final String DAY_MONTH_DAY_YEAR = "EEEE, MMMM d, yyyy";


    public ZonedDateTime now() {
        return ZonedDateTime.now(ZoneId.systemDefault());
    }


    @DefineKernelFunction(name = "date", description = "Get the current date")
    public String date(@KernelFunctionParameter(name = "locale", description = "Locale to use when formatting the date", required = false) String locale) {
        return DateTimeFormatter.ofPattern(DAY_MONTH_DAY_YEAR)
                .withLocale(parseLocale(locale))
                .format(now());
    }


    @DefineKernelFunction(name = "time", description = "Get the current time")
    public String time(@KernelFunctionParameter(name = "locale", description = "Locale to use when formatting the date", required = false) String locale) {
        return DateTimeFormatter.ofPattern("hh:mm:ss a")
                .withLocale(parseLocale(locale))
                .format(now());
    }


    @DefineKernelFunction(name = "utcNow", description = "Get the current UTC date and time")
    public String utcNow(@KernelFunctionParameter(name = "locale", description = "Locale to use when formatting the date") String locale) {
        return DateTimeFormatter.ofPattern(DAY_MONTH_DAY_YEAR + "h:mm a")
                .withLocale(parseLocale(locale))
                .format(now().withZoneSameLocal(ZoneOffset.UTC));
    }


    @DefineKernelFunction(name = "today", description = "Get the current date")
    public String today(@KernelFunctionParameter(name = "locale", description = "Locale to use when formatting the date", required = false) String locale) {
        return date(locale);
    }


    @DefineKernelFunction(name = "now", description = "Get the current date and time in the local time zone")
    public String now(@KernelFunctionParameter(name = "locale", description = "Locale to use when formatting the date", required = false) String locale){
        return  DateTimeFormatter.ofPattern(DAY_MONTH_DAY_YEAR + "h:mm a")
                .withLocale(parseLocale(locale))
                .format(now());
    }


    @DefineKernelFunction(name = "year", description = "Get the current year")
    public String year(@KernelFunctionParameter(name = "locale",description = "Locale to use when formatting the date",required = false) String locale){
        return DateTimeFormatter.ofPattern("yyyy").withLocale(parseLocale(locale)).format(now());
    }

    @DefineKernelFunction(name = "month", description = "Get the current month name")
    public String month(
            @KernelFunctionParameter(name = "locale", description = "Locale to use when formatting the date", required = false) String locale) {
        return DateTimeFormatter.ofPattern("MMMM").withLocale(parseLocale(locale)).format(now());
    }


    @DefineKernelFunction(name = "monthNumber", description = "Get the current month number")
    public String monthNumber(@KernelFunctionParameter(name = "locale",description = "Locale to use when formatting the date",required = false) String locale){
        return  DateTimeFormatter.ofPattern("MM").withLocale(parseLocale(locale)).format(now());
    }

    @DefineKernelFunction(name = "day", description = "Get the current day of the month")
    public String day(@KernelFunctionParameter(name ="locale",description = "Locale to use when formatting the date",required = false) String locale){
        return  DateTimeFormatter.ofPattern("d").withLocale(parseLocale( locale)).format(now());
    }


    @DefineKernelFunction(name = "dayOfWeek", description = "Get the current day of the week")
    public String dayOfWeek(@KernelFunctionParameter(name = "locale",description = "Locale to use when formatting the date",required = false) String locale){
        return  DateTimeFormatter.ofPattern("EEEE").withLocale(parseLocale(locale)).format(now());
    }

    @DefineKernelFunction(name = "hour", description = "Get the current hour")
    public String hour(@KernelFunctionParameter(name = "locale",description = "Locale to use when formatting the date",required = false) String  locale){
        return DateTimeFormatter.ofPattern("h a").withLocale(parseLocale(locale)).format(now());
    }









    protected Locale parseLocale(String locale) {
        return LocaleParser.parseLocale(locale);
    }


}
