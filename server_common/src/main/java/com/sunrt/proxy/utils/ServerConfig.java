package com.sunrt.proxy.utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;

public class ServerConfig {

    public int serverPort;
    public String password;
    public String method;

    @Override
    public String toString() {
        return "ServerConfig{" +
                "serverPort=" + serverPort +
                ", password='" + password + '\'' +
                ", method='" + method + '\'' +
                '}';
    }

    public static ServerConfig load(String file) throws Exception {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            JsonReader reader;
            reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            ServerConfig config = new Gson().fromJson(reader, ServerConfig.class);
            reader.close();
            return config;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
