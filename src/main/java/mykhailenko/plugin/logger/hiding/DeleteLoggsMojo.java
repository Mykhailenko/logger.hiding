package mykhailenko.plugin.logger.hiding;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which touches a timestamp file.
 * 
 */
@Mojo(name = "touch", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class DeleteLoggsMojo extends AbstractMojo {
	private static final String sufix = ".class";

	public enum Level {
		OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL;

	}

	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File root;

	@Parameter(required = true, defaultValue = "all")
	private String level;

	@Override
	public void execute() throws MojoExecutionException {
		getLog().info("Start deleting logging below level : " + level);
		Collection<File> files = getFiles();
		Level lvl = getLevel(level);
		doModifications(files, lvl);
		getLog().info("Deleting logging : successfully completed.");
	}

	private void doModifications(Collection<File> files, Level level)
			throws MojoExecutionException {
		ConfigurationBean bean = new ConfigurationBean(level);
		LogRemover worker = new LogRemover(bean);
		for (File classFile : files) {
			try {
				worker.removeUnnessaceryLogs(classFile);
			} catch (IOException e) {
				throw new MojoExecutionException("Could not open file: "
						+ classFile.getAbsolutePath());
			}
			getLog().info(classFile.getAbsolutePath() + "...");
		}
	}

	private Collection<File> getFiles() throws MojoExecutionException {
		try {
			return getFiles(root);
		} catch (IOException e) {
			throw new MojoExecutionException("Can't read the files");
		}
	}

	private Level getLevel(String name) throws MojoExecutionException {
		try {
			return Enum.valueOf(Level.class, name.toUpperCase());
		} catch (IllegalArgumentException exc) {
			throw new MojoExecutionException("Illegel log level name: " + name);
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<File> getFiles(File root) throws IOException {
		return FileUtils.listFiles(root, fileFilter, dirFilter);
	}

	IOFileFilter dirFilter = new IOFileFilter() {

		@Override
		public boolean accept(File arg0, String arg1) {
			return true;
		}

		@Override
		public boolean accept(File arg0) {
			return true;
		}
	};
	IOFileFilter fileFilter = new IOFileFilter() {

		@Override
		public boolean accept(File file, String arg1) {
			return file.getName().endsWith(sufix);
		}

		@Override
		public boolean accept(File file) {
			return file.getName().endsWith(sufix);
		}
	};
}
