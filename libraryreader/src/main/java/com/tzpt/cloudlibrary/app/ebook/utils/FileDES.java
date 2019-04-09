package com.tzpt.cloudlibrary.app.ebook.utils;

import com.zhy.http.okhttp.utils.L;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Shaw on 15/4/29.
 * DES文件加密&解密  <br>
 * 可以实现android和window的文件互通
 */
public class FileDES {

    /**
     * 加密解密的key
     */
    private Key mKey;
    /**
     * 解密的密码
     */
    private Cipher mDecryptCipher;
    /**
     * 加密的密码
     */
    private Cipher mEncryptCipher;

    public FileDES(String key) throws Exception {
        initKey(key);
        initCipher();
    }

    /**
     * 创建一个加密解密的key
     *
     * @param keyRule
     */
    public void initKey(String keyRule) {
        //TODO 不给初始值会空指针
        keyRule = "我去";
        byte[] keyByte = keyRule.getBytes();
        // 创建一个空的八位数组,默认情况下为0
        byte[] byteTemp = new byte[8];
        // 将用户指定的规则转换成八位数组
        for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        mKey = new SecretKeySpec(byteTemp, "DES");
    }

    /**
     * 初始化加载密码
     *
     * @throws Exception
     */
    private void initCipher() throws Exception {
        mEncryptCipher = Cipher.getInstance("DES");
        mEncryptCipher.init(Cipher.ENCRYPT_MODE, mKey);

        mDecryptCipher = Cipher.getInstance("DES");
        mDecryptCipher.init(Cipher.DECRYPT_MODE, mKey);
    }

    /**
     * 加密文件
     *
     * @param in
     * @param savePath 加密后保存的位置
     */
    public void doEncryptFile(InputStream in, String savePath) {
        if (in == null) {
            //L.e("加密文件", "文件流是空的");
            return;
        }
        try {
            CipherInputStream cin = new CipherInputStream(in, mEncryptCipher);
            OutputStream os = new FileOutputStream(savePath);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = cin.read(bytes)) > 0) {
                os.write(bytes, 0, len);
                os.flush();
            }
            os.close();
            cin.close();
            in.close();
            //L.e("加密文件", "加密成功");
        } catch (Exception e) {
            //L.e("加密文件", "加密失败");
            e.printStackTrace();
        }
    }

    /**
     * 加密文件
     *
     * @param filePath 需要加密的文件路径
     * @param savePath 加密后保存的位置
     * @throws FileNotFoundException
     */
    public void doEncryptFile(String filePath, String savePath) throws FileNotFoundException {
        doEncryptFile(new FileInputStream(filePath), savePath);
    }

    /**
     * 解密文件
     *
     * @param in
     * @return InputStream 返回一个文件读取流
     */
    public InputStream doDecryptFile(InputStream in) {
        if (in == null) {
            //L.e("解密文件", "文件流是空的");
            return null;
        }
        CipherInputStream cin = null;
        try {
            cin = new CipherInputStream(in, mDecryptCipher);
            //L.e("解密文件", "解密成功");
        } catch (Exception e) {
            //L.e("解密文件", "解密成功");
            e.printStackTrace();
        }
        return cin;
    }

    /**
     * 解密文件
     *
     * @param filePath 文件路径
     * @return String 返回html字符串
     * @throws Exception
     */
    public String doDecryptFile(String filePath) throws Exception {
        if (!new File(filePath).exists()) {
            L.e("解密文件文件不存在");
            return null;
        }
        try {
            CipherInputStream cin = new CipherInputStream(new FileInputStream(new File(filePath)), mDecryptCipher);
            BufferedReader reader = new BufferedReader(new InputStreamReader(cin));
            String htmlString = "";
            String line;
            while ((line = reader.readLine()) != null) {
                htmlString += line.trim() + "\n";
            }
            reader.close();
            cin.close();
            L.e("解密文件解密成功");
            return htmlString;
        } catch (Exception e) {
            L.e("解密文件解密失败");
            e.printStackTrace();
            return "";
        }
    }
}
