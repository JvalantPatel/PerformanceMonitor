package application;

import java.io.IOException;

public class Application {

	public static void main(String[] args) throws IOException {
		
		ConfigurationUtility.init();   //Initializing Configuration Utility
		StatisticsGenerator.init();    //Initializing Statistics Generator
		
		LogGenerator logGenerator = new LogGenerator();
		logGenerator.setVirtualMachine(ConfigurationUtility.getVM());
		logGenerator.setvHost(ConfigurationUtility.getvHost());
		logGenerator.start();

	}

}
