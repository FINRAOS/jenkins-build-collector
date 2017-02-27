/*
 *Author: Fawaz Joseph
 *Company: FINRA 
 */
package org.finra.buildcollector;

import hudson.model.FreeStyleBuild;
import hudson.model.TaskListener;
import hudson.model.FreeStyleProject;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class BuildCollectorListenerTest {
	
	@Rule
	public JenkinsRule j = new JenkinsRule();
	public FreeStyleProject project;
	public BuildCollectorListener bcl;
	public TaskListener taskListener;

	@Before
	public void setUp() {
		try {
			// Creating a Free Style project
			project = j.createFreeStyleProject("K554343223422");
			// Creating an instance of the entry point class
			bcl = new BuildCollectorListener();
			taskListener = j.createTaskListener();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testCase() {

		// Setting a random number for the build number
		Random random = new Random();
		int buildNumber = random.nextInt();

		// Creating the expected buildrecord
		BuildRecord buildRecord = new BuildRecord();
		buildRecord.setJobName("K554343223422");
		buildRecord.setNumber(buildNumber);
		try {

			FreeStyleBuild build = project.scheduleBuild2(0).get();
			// Setting a random build for the test build
			build.number = buildNumber;
			
			// Getting the back the testRecord back from the BuildRecordListener
			BuildRecord testbR = bcl.populateRecord(build, taskListener);

			// Checking the two classes based on the two properties jobName and
			// number
			j.assertEqualBeans(testbR, buildRecord, "jobName,number");

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}