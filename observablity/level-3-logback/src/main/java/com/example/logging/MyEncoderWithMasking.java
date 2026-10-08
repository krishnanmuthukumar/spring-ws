package com.example.logging;

import java.nio.charset.StandardCharsets;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;

public class MyEncoderWithMasking extends EncoderBase<ILoggingEvent> {

	@Override
	public byte[] encode(ILoggingEvent event) {

		String message = event.getFormattedMessage();

		message = maskSensitiveData(message);

		String output = "MY-ENCODER | " + event.getLevel() + " | " + event.getLoggerName() + " | " + message
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

	private String maskSensitiveData(String message) {

		return message.replaceAll("(?i)(password\\s*[=:]\\s*)[^\\s,]+", "$1****")
				.replaceAll("(?i)(token\\s*[=:]\\s*)[^\\s,]+", "$1****");
	}

}
