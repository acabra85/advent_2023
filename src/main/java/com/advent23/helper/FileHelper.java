package com.advent23.helper;


public class FileHelper {
    public static boolean exists(String name) {
        return null != FileHelper.class.getClassLoader().getResourceAsStream(name);
    }
}
