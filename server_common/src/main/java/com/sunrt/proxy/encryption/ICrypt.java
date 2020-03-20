package com.sunrt.proxy.encryption;

import java.io.ByteArrayOutputStream;

/**
 * crypt 加密
 *
 * @author zhaohui
 */
public interface ICrypt {

    void isForUdp(boolean isForUdp);

    void encrypt(byte[] data, ByteArrayOutputStream stream) throws Exception;

    void encrypt(byte[] data, int length, ByteArrayOutputStream stream) throws Exception;

    void decrypt(byte[] data, ByteArrayOutputStream stream) throws Exception;

    void decrypt(byte[] data, int length, ByteArrayOutputStream stream) throws Exception;

}
