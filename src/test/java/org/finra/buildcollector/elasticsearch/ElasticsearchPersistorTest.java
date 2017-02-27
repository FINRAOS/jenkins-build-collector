/*
 *Author: Fawaz Joseph, Otto Scheel
 *Company: FINRA 
 */
package org.finra.buildcollector.elasticsearch;

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

public class ElasticsearchPersistorTest {

	@Rule
	public JenkinsRule j = new JenkinsRule();

	public FreeStyleProject project;
	public HtmlPage settings;
	public JenkinsRule.WebClient webClient;
	public ElasticsearchPersistor esp;
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
		HtmlAnchor es = settings.getAnchorByText("Elasticsearch");
		es.click();
		
		settings.getElementsByName("_.url").get(1).setAttribute("value", "sampleUrl");
        settings.getElementByName("_.index").setAttribute("value","jenkinstest");
        settings.getElementByName("_.type").setAttribute("value", "build");
        settings.getElementByName("_.username").setAttribute("value", "user");
        settings.getElementByName("_.password").setAttribute("value", "password");

		BuildCollectorPlugin plugin = j.getInstance().getPlugin(BuildCollectorPlugin.class);


		try {
			j.submit(form);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		ElasticsearchPersistor elasticsearchPersistor = new ElasticsearchPersistor(".*", 
				                                                                   "sampleUrl", 
				                                                                   "jenkinstest", 
				                                                                   "user", 
				                                                                   Secret.fromString("password"), 
				                                                                   "build", 
				                                                                   1000, 
				                                                                   1000);

		try {
			j.assertEqualBeans(plugin.getPersistors().get(0), elasticsearchPersistor, "url,index,type,username,password,connectionTimeout,readTimeout,pattern");
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
		HtmlAnchor es = settings.getAnchorByText("Elasticsearch");
		es.click();
		
		Assert.assertEquals("Expect to find one instance of index", settings
				.getElementsByName("_.index").size(), 1);
		Assert.assertEquals("Expect to find one instance of type", settings
				.getElementsByName("_.type").size(), 1);
		Assert.assertEquals("Expect to find two instance of url", settings
				.getElementsByName("_.url").size(), 2);
		Assert.assertEquals("Expect to find two instance of pattern", settings
				.getElementsByName("_.pattern").size(), 1);
		Assert.assertEquals("Expect to find one instance of username", settings
				.getElementsByName("_.username").size(), 1);
		Assert.assertEquals("Expect to find one instance of password", settings
				.getElementsByName("_.username").size(), 1);
		Assert.assertEquals("Expect to find one instance of Connection Timeout", settings
				.getElementsByName("_.connectionTimeout").size(), 1);
		Assert.assertEquals("Expect to find one instance of Read Timeout", settings
				.getElementsByName("_.readTimeout").size(), 1);

	}

	// Test that project succeeds even with bad settings.
	@Test
	public void testEmptySettings() throws Exception {

		HtmlForm form = settings.getFormByName("config");
		HtmlButton b = form.getButtonByCaption("Add a new Persistor");
		b.click();
		HtmlAnchor es = settings.getAnchorByText("Elasticsearch");
		es.click();
		
		settings.getElementsByName("_.url").get(1).setAttribute("value", "");
        settings.getElementByName("_.index").setAttribute("value","");
        settings.getElementByName("_.type").setAttribute("value", "");
        settings.getElementByName("_.username").setAttribute("value", "");
        settings.getElementByName("_.password").setAttribute("value", "");
		// Creating a simple project
		project = j.createFreeStyleProject();
		// executing the project and blocking until it finishes
		FreeStyleBuild build = project.scheduleBuild2(0).get();

		j.assertBuildStatusSuccess(build);
	}
}