/**
 * 
 * Class that extends Plugin for Jenkins. This class is responsible for loading and managing the different persistors which have been configured in the Jenkins main page.
 * 
 * @author Otto Scheel
 * Company: Finra
 */
package org.finra.buildcollector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.finra.buildcollector.elasticsearch.ElasticsearchPersistor;
import org.finra.buildcollector.elasticsearch.ElasticsearchPersistor.DescriptorImpl;
import org.jfree.util.Log;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.collect.ImmutableList;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;

public class BuildCollectorPlugin extends Plugin {
	
	/**
	 * 
	 * List which holds the persistors which have been configured for the build collector plugin.
	 * 
	 */
	private  List<AbstractBuildPersistor> persistors;
	private static final Logger LOG = Logger.getLogger(BuildCollectorPlugin.class.getName());
	
	public BuildCollectorPlugin() {}

	public List<? extends AbstractBuildPersistor> getPersistors(){
		return Collections.unmodifiableList(persistors);
	}
	
	
	/**
	 * Method overidden to load persistors from memory.
	 * 
	 * (non-Javadoc)
	 * @see hudson.Plugin#start()
	 */
	@Override
	public void start() throws Exception {
		load();
		this.persistors = persistors == null ? persistors = new ArrayList<AbstractBuildPersistor>() : new ArrayList<AbstractBuildPersistor>(persistors); 
		LOG.fine("Loaded plugin.");
	}
	
	/**
	 * 
	 * Method which is called by jenkins when the main configuration page is saved/configured.
	 *  
	 */
	public void configure(StaplerRequest req, JSONObject json)
			throws hudson.model.Descriptor.FormException, IOException {
		
		Object collectors = json.get("buildcollector");
		persistors.clear();
		if(collectors != null){
			persistors = req.bindJSONToList(AbstractBuildPersistor.class, collectors);
		}
		
		save();
	}
	
}
