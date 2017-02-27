/**
 * 
 * 
 *  This is the class in the plugin which listens for build changes. Once the build has reached the "On Completed" step, this class will be called and build information will be gathered and persisted. 
 *  This class will also discover any classes that extend {@link org.finra.buildcollector.AbstractBuildRecorder} and call their getPluginRecord method when the build reaches the on completed step.
 * 
 * 
 * @author Otto Scheel
 * Company: Finra
 */
package org.finra.buildcollector;

import java.io.PrintStream;
import java.util.logging.Logger;

import hudson.model.Cause;
import hudson.slaves.OfflineCause;
import jenkins.model.Jenkins;

import org.jfree.util.Log;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

@SuppressWarnings("rawtypes")
@Extension
public class BuildCollectorListener extends RunListener<Run>{

	private static Logger LOG = Logger.getLogger(BuildCollectorListener.class.getName());

	public BuildCollectorListener() {
		super();
		LOG.info("Starting plugin");
	}

	/**
	 * 
	 * 
	 *  This method builds the build record for a build. It will also discover build recorders and call their onCompleted method.
	 * 
	 * 
	 * @param r
	 * @param listener
	 * @return
	 */
	public BuildRecord populateRecord(Run r, TaskListener listener) {

		LOG.info("Populating record for build collector task.");

		final Job job = r.getParent();
		BuildRecord build = new BuildRecord();
		Cause.UserIdCause userCause = (Cause.UserIdCause)r.getCause(Cause.UserIdCause.class);
		EnvVars envVars = null;

		try {
			r.getLogText();
			envVars = r.getEnvironment(listener);
			if(userCause != null){
				//Adding user to environment variables
				envVars.put("JENKINS_USER_ID_CAUSE",userCause.getUserId());
				envVars.put("JENKINS_USERNAME_CAUSE",userCause.getUserName());
			}

		} catch (Exception e) {
			LOG.warning("An exception occured while fetching enviroment variables" + e);
		}

		build.setDuration(r.getDuration());
		build.setJobName(job.getFullName());
		build.setResult(r.getResult().toString());
		build.setStartTime(r.getStartTimeInMillis());
		build.setNumber(r.getNumber());
		build.setEnvironment(envVars);
		
	
		for (AbstractPluginRecorder recorder : AbstractPluginRecorder.getAllRecorders()) {
			
			LOG.info("Calling get record for " + recorder.getPluginName());
			try {
				AbstractPluginRecord record = recorder.getPluginRecord(r, listener);
				if(record != null ){
					build.getRecords().add(record);
				}
			} catch (Exception e) {
				LOG.warning("Failed to retrieve the plugin record " + e);
			}
		}

		return build;
	}

	@Override
	public void onCompleted(Run r, TaskListener listener) {
		LOG.info("Started con completed method.");
		BuildCollectorPlugin plugin = Jenkins.getInstance().getPlugin(BuildCollectorPlugin.class);
		
		
		if(plugin.getPersistors() != null &&
				plugin.getPersistors().size() > 0){

			final BuildRecord buildRecord = populateRecord(r, listener);
			
			for(AbstractBuildPersistor buildPersistor : plugin.getPersistors()){
				LOG.info("Calling persistor: "+buildPersistor.getName());
				try {
					
					if(buildPersistor.matches(buildRecord.getJobName())){
						
						LOG.info("Matched pattern for persistor.");
						buildPersistor.persistResults(buildRecord);
					}else{
						LOG.info("Did not match pattern for persistor.");
					}
					
				} catch (Exception e) {
					LOG.severe("Error calling persistor: " + buildPersistor.getName() + buildRecord.getJobName() + " " + e.getMessage());
				}
			}
			
		}else{
			
			LOG.warning("No persistor found.");
		}

	}

}
