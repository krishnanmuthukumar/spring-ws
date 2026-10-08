package com.example.logging;

import java.nio.charset.StandardCharsets;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class MyAppenderWithEncoder extends AppenderBase<ILoggingEvent>{


	private MyEncoder encoder;

	public void setEncoder(MyEncoder encoder) {
		this.encoder = encoder;
	}

	@Override
	public void start() {
		if (encoder == null) {
			addError("No encoder configured for MyAppender");
			return;
		}

		if (!encoder.isStarted()) {
			encoder.start();
		}
		super.start();
		write(encoder.headerBytes());
	}

	@Override
	public void stop() {
		if (isStarted() && encoder != null) {
			write(encoder.footerBytes());
			encoder.stop();
		}
		super.stop();
	}

	@Override
	protected void append(ILoggingEvent event) {
		write(encoder.encode(event));
	}

	private void write(byte[] data) {
		if (data != null && data.length > 0) {
			System.out.print(new String(data, StandardCharsets.UTF_8));
		}
	}

}
