package cs455.scaling.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utilities {
	public static String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException{
		String hashString = "";
		//while(hashString.length()!=40){
			MessageDigest digest = MessageDigest.getInstance("SHA1");
			byte[] hash = digest.digest(data);
			BigInteger hashInt = new BigInteger(1, hash);
			hashString = hashInt.toString(16);
			if(hashString.length() != 40){
				System.out.println("ERROR: hash not 40 chars!");
			}
		//}
		//	return hashInt;
		return hashString;
	}
}
