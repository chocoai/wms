package com.xyy.http.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.xyy.util.Base64Util;

public class EncrypAES {
	static {
		// 安全提供者
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
	}
	// SecretKey 负责保存对称密钥
	private SecretKey secretKey;
	// Cipher负责完成加密或解密工作
	private Cipher cipher;

	
	/**
	 * 利用base64Key构造EncrypAES对象
	 *
	 */
	public EncrypAES(String base64Key) throws NoSuchAlgorithmException,
			NoSuchPaddingException {

		this.secretKey = getSecretKeyFromBase64(base64Key);
		// 生成Cipher对象,指定其支持的AES算法
		this.cipher = Cipher.getInstance("AES");
	}

	/**
	 * 利用加密后的base64字符串构建SecretKey
	 * 
	 * @param base64Key
	 * @return
	 */
	public static SecretKey getSecretKeyFromBase64(String base64Key) {
		return getSecretKey(Base64Util.BytesFromBase64(base64Key));

	}

	/**
	 * 利用普通字符串构建SecretKey
	 * 
	 * @param key
	 * @return
	 */
	public static SecretKey getSecretKey(String key) {
		return getSecretKey(key.getBytes());

	}

	/**
	 * 根据输入的keys生成对应的key
	 * 
	 * @param keys
	 * @return
	 */
	public static SecretKey getSecretKey(byte[] keys) {
		SecretKeySpec secretKey = new SecretKeySpec(keys, "AES");
		return secretKey;
	}

	/**
	 * 产生随机的Base64Key
	 * 
	 * @return
	 */
	public static String generatorBase64Key() {
		byte[] keys = generatorKey();
		if (keys != null) {
			return Base64Util.Bytes2Base64(keys);
		}
		return null;
	}

	/**
	 * 产生随机的keys
	 * 
	 * @return
	 */
	public static byte[] generatorKey() {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			return keygen.generateKey().getEncoded();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	/**
	 * 对字符串加密,返回加密后的base64字符串
	 */
	public String Encrytor(String str) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		// 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式
		this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
		byte[] src = str.getBytes();
		// 加密，结果保存进cipherByte
		return Base64Util.Bytes2Base64(this.cipher.doFinal(src));
	}

	/**
	 * 对字符串解密，返回解密后的原始字符串
	 */
	public String Decryptor(String str) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		// 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式
		this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
		return new String(this.cipher.doFinal(Base64Util.BytesFromBase64(str)));

	}

	private static void createSecretKeyFile(String apiKey, File PropertiesFile) {
		Properties properties = new Properties();
		properties.setProperty("apiKey", apiKey);
		properties.setProperty("key", EncrypAES.generatorBase64Key());
		// properties.setProperty("key", EncrypAES.generatorBase64Key());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(PropertiesFile);
			properties.store(fos, "www.ybm100.com");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.flush();
					fos.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static EncrypAES _clientEncrypAES = null;
	/**
	 * 获取客户端EncrypAES对象
	 * 				客户端的安全密钥存储在：security.key文件中
	 * @return
	 */
	public static EncrypAES getCliEncrypAES() {
		if (_clientEncrypAES == null) {
			synchronized (EncrypAES.class) {
				if (_clientEncrypAES == null) {
					InputStream is = EncrypAES.class
							.getResourceAsStream("/security.key");
					try {

						Properties properties = new Properties();
						properties.load(is);
						String key = properties.getProperty("key");
						_clientEncrypAES = new EncrypAES(key);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return _clientEncrypAES;
	}

	
	//获取服务器端的
	private static final ConcurrentMap<String, EncrypAES> _cncrypMap = new ConcurrentHashMap<String, EncrypAES>();

	/**
	 * 获取服务器EncrypAES对象
	 * 	服务器端的EncrypAES对以APIkEY为单位进行保存，一个APIKEY代表一类客户端
	 * @return
	 */
	public static EncrypAES getSrvEncrypAES(String apiKey) {
		if (apiKey == null)
			return null;
		if (!_cncrypMap.containsKey(apiKey)) {
			synchronized (EncrypAES.class) {
				if (!_cncrypMap.containsKey(apiKey)) {
					InputStream is = null;
					try {
						 is = EncrypAES.class
								.getResourceAsStream("/" + apiKey + ".key");
						Properties properties = new Properties();
						properties.load(is);
						String key = properties.getProperty("key");
						EncrypAES encrypAES = new EncrypAES(key);
						_cncrypMap.put(apiKey, encrypAES);
						return encrypAES;
					} catch (Exception ex) {
						ex.printStackTrace();
						return null;
					} finally {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return _cncrypMap.get(apiKey);
	}

	public static void main(String[] args) {
		createSecretKeyFile("security", new File("e:/security.key"));
	}

}
