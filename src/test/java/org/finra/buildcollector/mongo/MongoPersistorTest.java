/*
 *Author: Fawaz Joseph, Otto Scheel
 *Company: FINRA 
 */
package org.finra.buildcollector.mongo;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.util.Secret;

import java.io.IOException;
import org.finra.buildcollector.BuildCollectorPlugin;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class MongoPersistorTest {

	@Rule
	public JenkinsRule j = new JenkinsRule();

	public FreeStyleProject project;
	public HtmlPage settings;
	public JenkinsRule.WebClient webClient;
	public BuildCollectorPlugin plugin; 

	@Before
	public void setUp() {
		try {
			// We have to configure some setting that the elastic core plugin
			// will pull from
			webClient = j.createWebClient();
			settings = webClient.goTo("configure");
			plugin = j.getInstance().getPlugin(BuildCollectorPlugin.class);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Test the config object that is used to populate the Jest api
	@Test
	public void testConfig() throws IOException {

		// Navigate to form and populate elasticsearch settings.
		HtmlForm form = settings.getFormByName("config");
		HtmlButton b = form.getButtonByCaption("Add a new Persistor");
		b.click();
		HtmlAnchor es = settings.getAnchorByText("Mongo");
		es.click();
		
		settings.getElementByName("_.hostname").setAttribute("value", "hostname");
        settings.getElementByName("_.dbName").setAttribute("value", "dbName");
        settings.getElementByName("_.collectionName").setAttribute("value", "collectionName");
        settings.getElementByName("_.username").setAttribute("value", "username");
        settings.getElementByName("_.password").setAttribute("value", "password");

		BuildCollectorPlugin plugin = j.getInstance().getPlugin(BuildCollectorPlugin.class);


		try {
			j.submit(form);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MongoPersistor mongo = new MongoPersistor(".*", 
				                                  "hostname", 
				                                  27017, 
				                                  "dbName", 
				                                  "collectionName", 
				                                  "username", 
				                                  Secret.fromString("password"), 
				                                  1000);

		try {
			j.assertEqualBeans(plugin.getPersistors().get(0), mongo, "hostname,port,dbName,username,password,timeout,collectionName,pattern");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Testing the setting has the right fields
	@Test
	public void testSettings() throws IOException {

		
		HtmlForm form = settings.getFormByName("config");
		HtmlButton b = form.getButtonByCaption("Add a new Persistor");
		b.click();
		HtmlAnchor es = settings.getAnchorByText("Mongo");
		es.click();
		
		Assert.assertEquals("Expect to find one instance of hostname", settings
				.getElementsByName("_.hostname").size(), 1);
		Assert.assertEquals("Expect to find two instance of pattern", settings
				.getElementsByName("_.pattern").size(), 1);
		Assert.assertEquals("Expect to find one instance of dbName", settings
				.getElementsByName("_.dbName").size(), 1);
		Assert.assertEquals("Expect to find one instance of collectionName", settings
				.getElementsByName("_.collectionName").size(), 1);
		Assert.assertEquals("Expect to find one instance of username", settings
				.getElementsByName("_.username").size(), 1);
		Assert.assertEquals("Expect to find one instance of password", settings
				.getElementsByName("_.username").size(), 1);
		Assert.assertEquals("Expect to find one instance of Timeout", settings
				.getElementsByName("_.timeout").size(), 1);

	}

	// Test that project succeeds even with bad settings.
	@Test
	public void testEmptySettings() throws Exception {

		HtmlForm form = settings.getFormByName("config");
		HtmlButton b = form.getButtonByCaption("Add a new Persistor");
		b.click();
		HtmlAnchor es = settings.getAnchorByText("Mongo");
		es.click();
		
		settings.getElementByName("_.hostname").setAttribute("value", "");
        settings.getElementByName("_.dbName").setAttribute("value","");
        settings.getElementByName("_.port").setAttribute("value", "");

		// Creating a simple project
		project = j.createFreeStyleProject();
		// executing the project and blocking until it finishes
		FreeStyleBuild build = project.scheduleBuild2(0).get();

		j.assertBuildStatusSuccess(build);
	}
}