package cn.randeejia.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	public static String getMD5Str(String str) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(
					str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		} catch (Exception e) {
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);

		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString().toLowerCase();
	}

	// public static String md5(String input) {
	// String result = input;
	// if (input != null) {
	// MessageDigest md;
	// try {
	// md = MessageDigest.getInstance("MD5");
	// md.update(input.getBytes("UTF_8"));
	// BigInteger hash = new BigInteger(1, md.digest());
	// result = hash.toString(16);
	// if ((result.length() % 2) != 0) {
	// result = "0" + result;
	// }
	// } catch (NoSuchAlgorithmException e) {
	// e.printStackTrace();
	// return null;
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// return null;
	// }
	// return result.toUpperCase();
	// }
	//
	// return null;
	// }
	/**
	 * 返回的大写
	 * 
	 * @param input
	 * @return
	 */
	public static String md5(String input) {
		if (getMD5Str(input) != null) {
			return getMD5Str(input).toUpperCase();
		}
		return null;
	}
}
