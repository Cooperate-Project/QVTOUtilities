package de.cooperateproject.qvtoutils.blackbox.tests;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.m2m.internal.qvt.oml.library.Context;
import org.eclipse.m2m.qvt.oml.util.IContext;
import org.eclipse.m2m.qvt.oml.util.Log;

@SuppressWarnings("restriction")
public class TestUtils {

	private static final Logger LOGGER = Logger.getLogger(TestUtils.class);

	private static class Log4JLogger implements Log {

		private Level defaultLogLevel;
		private Logger logger;

		public Log4JLogger(Logger logger, Level defaultLogLevel) {
			this.logger = logger;
			this.defaultLogLevel = defaultLogLevel;
		}

		@Override
		public void log(int level, String message, Object param) {
			String logMessage = message;
			if (param != null) {
				logMessage = String.format("%s %s", message, param);
			}
			logger.log(Level.toLevel(level), logMessage);
		}

		@Override
		public void log(int level, String message) {
			log(level, message, null);
		}

		@Override
		public void log(String message, Object param) {
			log(defaultLogLevel.toInt(), message, param);
		}

		@Override
		public void log(String message) {
			log(message, null);
		}

	}

	public static IContext createLoggingContext() {
		Context context = new Context();
		context.setLog(new Log4JLogger(LOGGER, Level.INFO));
		return context;
	}

}
