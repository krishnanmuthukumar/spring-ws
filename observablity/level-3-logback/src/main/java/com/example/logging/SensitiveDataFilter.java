package com.example.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class SensitiveDataFilter extends Filter<ILoggingEvent> {

	@Override
	public FilterReply decide(ILoggingEvent event) {
		String message = event.getFormattedMessage();

		if (message == null) {
			return FilterReply.NEUTRAL;
		}
		
		String lowerCaseMessage = message.toLowerCase();

		if (lowerCaseMessage.contains("password") || lowerCaseMessage.contains("token")
				|| lowerCaseMessage.contains("secret")) {

			return FilterReply.DENY;
		}
		
		return FilterReply.NEUTRAL;
	}

}
