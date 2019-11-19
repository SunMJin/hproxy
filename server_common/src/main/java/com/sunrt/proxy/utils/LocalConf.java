package com.sunrt.proxy.utils;

import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LocalConf {
    public static int localPort;
    public static String remoteHost;
    public static int remotePort;
    public static String user;
    public static String password;
    public static int thread;
    static {
        String filename="local_server.json";
        File file=new File(filename);
        try {
            InputStream in;
            if(file.exists()){
                in=new FileInputStream(file);
            }else{
                in= LocalConf.class.getClassLoader().getResourceAsStream("local_server.json");
            }
            byte[] buff = in.readAllBytes();
            JSONObject json=JSONObject.fromObject(new String(buff,"utf-8"));
            localPort=json.getInt("localPort");
            remoteHost=json.getString("serverHost");
            remotePort=json.getInt("serverPort");
            user=json.getString("user");
            password=json.getString("password");
            thread=json.getInt("thread");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
