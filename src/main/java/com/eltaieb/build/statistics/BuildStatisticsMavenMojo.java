package com.eltaieb.build.statistics;

import org.apache.maven.execution.MavenSession;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.eltaieb.build.statistics.config.Constant;
import com.eltaieb.build.statistics.model.Build;
import com.eltaieb.build.statistics.model.Builds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Goal which touches a timestamp file.
 *
 * 
 */
@Mojo(name = "calculateStatistics", defaultPhase = LifecyclePhase.INSTALL)
public class BuildStatisticsMavenMojo extends AbstractMojo {

	/**
	 * Location of the file.
	 */
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	@Parameter(property = "statisticsLocationDir", required = true)
	private String statisticsLocationDir;

	private static String STAT_WEBAPP_DIRECTORY_PATH;

	private static String RECORDS_FILE_PATH;

	@Parameter(defaultValue = "${mojoExecution}", readonly = true)
	private MojoExecution mojo;

	@Parameter(defaultValue = "${session}", readonly = true)
	private MavenSession session;

	public void execute() throws MojoExecutionException {
		
		init();

		validateStatisticsLocationDirectory();

		copyWebAppToStatFolder();

		addCurrentBuildRecord();
		
		displayDashboardLink();

	}

	private void displayDashboardLink() {

		getLog().info("to view buil duration statistics kindly visit the following link");
		getLog().info(new String(STAT_WEBAPP_DIRECTORY_PATH+File.separator+"index.html").replace("\\\\", "\\"));

	}

	private void init() {
		STAT_WEBAPP_DIRECTORY_PATH = statisticsLocationDir+File.separator+"stat" ;
		RECORDS_FILE_PATH = STAT_WEBAPP_DIRECTORY_PATH + File.separator + "assets"+File.separator +Constant.RECORDS_JSONP_FILE_NAME;
		getLog().info("RECORDS_FILE_PATH is" + RECORDS_FILE_PATH);
	}

	private void addCurrentBuildRecord() throws MojoExecutionException {
		validateRecordsFileExistance();
		Builds records = loadRecords();
		attachCurrentBuildRecord(records);

	}

	private void attachCurrentBuildRecord(Builds records) throws MojoExecutionException {
		Build record = new Build(session.getCurrentProject().getArtifactId(),
				session.getCurrentProject().getParent() == null || session.getCurrentProject().getPackaging()
						.toUpperCase().equals(Constant.PARENT_PROJECT_PACKAGE_TYPE));

		records.addOrUpdateBuild(session.getStartTime().getTime(), record);
		saveRecordsIntoRecordsFile(records);

	}

	private void copyWebAppToStatFolder() throws MojoExecutionException {

		
 		try {
			extract("stat", STAT_WEBAPP_DIRECTORY_PATH);

		} catch (IOException e) {
			e.printStackTrace();
			throw new MojoExecutionException("unable to copy stat webapp to " + STAT_WEBAPP_DIRECTORY_PATH);
		}
	}

	private void saveRecordsIntoRecordsFile(Builds records) throws MojoExecutionException {
		
		
		
		try (PrintWriter out = new PrintWriter(RECORDS_FILE_PATH)) {
			Gson gson = new GsonBuilder().create();
			String jsonContent = gson.toJson(records);
			jsonContent = "records="+jsonContent;
		    out.println(jsonContent);
		} catch (Exception e) {
			 throw new MojoExecutionException("Unable to save records into "+ RECORDS_FILE_PATH);
		}

	}

	private Builds loadRecords() throws MojoExecutionException {
		try {
			
			 byte[] encoded = Files.readAllBytes(Paths.get(RECORDS_FILE_PATH));
			  String content =  new String(encoded);
 
			  // remove jsonP function
			  content = content.replace(" ", "");
			 content=  content.replace("records=", "");
 
 				getLog().info("records are replaced second" + content);

 			Builds result = new Gson().fromJson(content,Builds.class);
			getLog().info("records are " + result);

			return result != null ? result : new Builds();
		} catch (Exception  e) {
			throw new MojoExecutionException("unable to parse content of file " + RECORDS_FILE_PATH);
		}  
	}

	private void validateRecordsFileExistance() throws MojoExecutionException {
		File f = new File(RECORDS_FILE_PATH);
		;
		if (f.exists()) {
			getLog().info("well file " + f.getPath() + " exist");
			if (!f.canWrite()) {
				throw new MojoExecutionException("well file " + f.getPath() + " exist but can not write into it");
			}
		} else {
			getLog().info("try to create file " + f.getPath());
			try {
				if (f.createNewFile()) {
					getLog().info("  file " + f.getPath() + "has been created successfully");
					// as long the file just created so it's implicitly writable
				} else {
					throw new MojoExecutionException("unable to create file " + f.getPath());
				}
			} catch (IOException e) {
				throw new MojoExecutionException("unable to create file " + f.getPath(), e);
			}
		}
	}

	private void validateStatisticsLocationDirectory() throws MojoExecutionException {
		getLog().info("statisticsLocationDir is " + statisticsLocationDir);
		File f = new File(statisticsLocationDir);

		getLog().info("checking whether object " + statisticsLocationDir + " is exist or not");
		if (f.exists()) {
			getLog().info("well object  " + statisticsLocationDir + " exists");
			if (!f.isDirectory()) {
				throw new MojoExecutionException("object  " + statisticsLocationDir + " is a file not a dir");
			}
		} else {
			getLog().info("well object  " + statisticsLocationDir + " doesnt exist");
			getLog().info("try to create directory " + statisticsLocationDir);
			if (f.mkdir()) {
				getLog().info("well directory  " + statisticsLocationDir + " has been created successfully ");

			} else {
				throw new MojoExecutionException("not able to create directory  " + statisticsLocationDir
						+ " check wether current user has access");
			}
		}

		getLog().info("createing  " + STAT_WEBAPP_DIRECTORY_PATH);
		File statWebAppDir = new File(STAT_WEBAPP_DIRECTORY_PATH);
		if (statWebAppDir.exists()) {
			getLog().info("well object  " + STAT_WEBAPP_DIRECTORY_PATH + " exists");
			if (!statWebAppDir.isDirectory()) {
				throw new MojoExecutionException("object  " + STAT_WEBAPP_DIRECTORY_PATH + " is a file not a dir");
			}
			 
		} else {
			getLog().info("creating object  " + STAT_WEBAPP_DIRECTORY_PATH);
			if (statWebAppDir.mkdirs()) {
				getLog().info("directory  " + STAT_WEBAPP_DIRECTORY_PATH + " has been created successfully");

			} else {
				throw new MojoExecutionException("unable to create  " + STAT_WEBAPP_DIRECTORY_PATH);

			}
		}
	}
	
	
	  private void extract( String sourceDirectory, String writeDirectory ) throws IOException {
	        final URL dirURL = getClass().getClassLoader().getResource( sourceDirectory );
	        final String path = sourceDirectory;
	 
	        if( ( dirURL != null ) && dirURL.getProtocol().equals( "jar" ) ) {
	            final JarURLConnection jarConnection = (JarURLConnection) dirURL.openConnection();
	            System.out.println( "jarConnection is " + jarConnection );
	 
	            final ZipFile jar = jarConnection.getJarFile();
	 
	            final Enumeration< ? extends ZipEntry > entries = jar.entries(); // gives ALL entries in jar
	 
	            while( entries.hasMoreElements() ) {
	                final ZipEntry entry = entries.nextElement();
	                final String name = entry.getName();
	                // System.out.println( name );
	                if( !name.startsWith( path ) || name.contains("records.js") ) {
	                    // entry in wrong subdir -- don't copy
	                    continue;
	                }
	                final String entryTail = name.substring( path.length() );
	 
	                final File f = new File( writeDirectory + File.separator + entryTail );
	                if( entry.isDirectory() ) {
	                    // if its a directory, create it
	                    final boolean bMade = f.mkdir();
	                    System.out.println( (bMade ? "  creating " : "  unable to create ") + name );
	                }
	                else {
	                    System.out.println( "  writing  " + name );
	                    final InputStream is = jar.getInputStream( entry );
	                    final OutputStream os = new BufferedOutputStream( new FileOutputStream( f ) );
	                    final byte buffer[] = new byte[4096];
	                    int readCount;
	                    // write contents of 'is' to 'os'
	                    while( (readCount = is.read(buffer)) > 0 ) {
	                        os.write(buffer, 0, readCount);
	                    }
	                    os.close();
	                    is.close();
	                }
	            }
	 
	        }
	        else if( dirURL == null ) {
	            throw new IllegalStateException( "can't find " + sourceDirectory + " on the classpath" );
	        }
	        else {
	            // not a "jar" protocol URL
	            throw new IllegalStateException( "don't know how to handle extracting from " + dirURL );
	        }
	    }
	 
	}
 
