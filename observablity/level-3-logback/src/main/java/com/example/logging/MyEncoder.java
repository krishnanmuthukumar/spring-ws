package com.example.logging;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;

public class MyEncoder extends EncoderBase<ILoggingEvent> {

	@Override
	public byte[] encode(ILoggingEvent event) {
		String output = "MY-ENCODER | " + event.getLevel() + " | " + event.getLoggerName() + " | "
				+ event.getFormattedMessage() + " | " + event.getMessage() + " | " + Arrays.toString(event.getArgumentArray())
				+ System.lineSeparator();
		return output.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public byte[] headerBytes() {
		return "===== LOG START =====\n".getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public byte[] footerBytes() {
		return "===== LOG END =====\n".getBytes(StandardCharsets.UTF_8);
	}

}
