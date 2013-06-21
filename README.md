Maven plugin dedicated for remove unnesscary logs.

Example of using:

 
	<build>
		<plugins>
			<plugin>
				<groupId>mykhailenko.plugin</groupId>
				<artifactId>logger.hiding</artifactId>
				<version>0.0.1-SNAPSHOT</version>
				<executions>
					<execution>
						<phase>process-classes</phase>
						<goals>
							<goal>loggerhiding</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<level>info</level> // << HERE YOU CAN DEFINE YOUR LOGGING LEVEL. SO ALL LOGGING BELOW THAT LEVEL WILL BE REMOVED FROM CLASS FILES
				</configuration>
			</plugin>
			....
		</plugins>
	</build>




Created by Hlib Mykhailenko.
