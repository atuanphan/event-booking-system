package com.jonet.eventbooking.converter;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class UUIDConvert {
	public byte[] uuidToBytes(UUID uuid) {
	    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
	    bb.putLong(uuid.getMostSignificantBits());
	    bb.putLong(uuid.getLeastSignificantBits());
	    return bb.array();
	}
}
