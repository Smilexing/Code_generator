package com.dexcode.cli.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import picocli.CommandLine.Option;

public class ReflectArgs {
    public String[] execute(String[] args) {
        List<String> argumentList = new ArrayList<>(Arrays.asList(args));
        checkOptionArgs(this, argumentList);
        args = argumentList.toArray(new String[0]);
        return args;
    }

    private void checkOptionArgs(Object obj, List<String> argumentList) {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Option.class)) {
                Option option = field.getAnnotation(Option.class);  // interactive()属性则指示是否在命令行中直接交互式地询问此选项的值。
                if (option.interactive() && !isOptionPresent(argumentList, option.names())) {
                    argumentList.add(option.names()[0]);
                }
            }
        }
    }

    private boolean isOptionPresent(List<String> argumentList, String[] optionNames) {
        for (String optionName : optionNames) {
            if (argumentList.contains(optionName)) {
                return true;
            }
        }
        return false;
    }
}