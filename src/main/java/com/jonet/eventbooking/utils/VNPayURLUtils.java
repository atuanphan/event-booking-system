package com.jonet.eventbooking.utils;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;

public class VNPayURLUtils {
	public static Map<String, String> getURL(VNPayReturnRequest request) {
		Map<String, String> params = new TreeMap<String, String>();
		Field[] fields = request.getClass().getDeclaredFields();
		for (Field it : fields) {
			it.setAccessible(true);
			String name = it.getName();
			try {
				Object value = it.get(request);
				if (name.startsWith("vnp") && !name.equals("vnp_SecureHash") && !name.equals("vnp_SecureHashType")
						&& value != null && !value.toString().isEmpty()) {
					params.put(name, value.toString());
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return params;
	}
	
	public static boolean verifyCallbackSignature(String vnpaySecureHash, String myCalculatedHash) {
		if(vnpaySecureHash == null || myCalculatedHash == null) {
			return false;
		}
		byte[] myHash = myCalculatedHash.getBytes();
		byte[] vnpayHash = vnpaySecureHash.getBytes();
		return MessageDigest.isEqual(myHash, vnpayHash);
	}
}
