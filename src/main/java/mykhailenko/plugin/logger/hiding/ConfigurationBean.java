package mykhailenko.plugin.logger.hiding;

import mykhailenko.plugin.logger.hiding.DeleteLoggsMojo.Level;

public class ConfigurationBean {
	
	private String [] loggerClasses = {"org.apache.log4j.Logger"};
	
	private DeleteLoggsMojo.Level currentLoggingLevel = Level.ALL;

	public ConfigurationBean(Level level) {
		this.currentLoggingLevel = level;
	}

	public String[] getLoggerClasses() {
		return loggerClasses;
	}

	public void setLoggerClasses(String[] loggerClasses) {
		this.loggerClasses = loggerClasses;
	}

	public DeleteLoggsMojo.Level getCurrentLoggingLevel() {
		return currentLoggingLevel;
	}

	public void setCurrentLoggingLevel(DeleteLoggsMojo.Level currentLoggingLevel) {
		this.currentLoggingLevel = currentLoggingLevel;
	}

	
	
}
