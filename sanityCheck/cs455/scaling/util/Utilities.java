package cs455.scaling.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utilities {
	public static String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException{
		String hashString = "";

		MessageDigest digest = MessageDigest.getInstance("SHA1");
		byte[] hash = digest.digest(data);
		BigInteger hashInt = new BigInteger(1, hash);
		hashString = hashInt.toString(16);
		//if hash was truncated, add 'a' to the end until it reaches 
		//the necessary 40 characters
		if(hashString.length() != 40){
			hashString +='a';
		}
		return hashString;
	}
}
