package core;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Password encryptor
 */
final class Crypto {
	
	/**
	 * Encryption Key for AES algorithm 
	 */
	private static final String encryptionKey = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
	 
	/**
	 * Converts hex numbers in String format to array of bytes
	 * 
	 * @param s given hex numbers in String format
	 * @return given String converted to array of bytes
	 */
	private static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;	
		}
		return b;
	}
	

	/**
	 * Password AES decryptor
	 * 
	 * @param encriptedPassword given encrypted password
	 * @return decrypted password
	 */
	static String passwordDecryptor(String encriptedPassword) {
		if (encriptedPassword == null || encriptedPassword.length() == 0) return "";
		else if (!encriptedPassword.matches("[a-fA-F0-9]+") || !encryptionKey.matches("[a-fA-F0-9]+")) 
			throw new IllegalArgumentException();
		
		String decryptedPassword = "";
		try {
			byte[] key = hexStringToByteArray(encryptionKey);
			Cipher cipher = Cipher.getInstance("AES");
			SecretKeySpec sk = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.DECRYPT_MODE, sk);
			decryptedPassword = new String(cipher.doFinal(hexStringToByteArray(encriptedPassword)));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} 
		return decryptedPassword;
	}


}
