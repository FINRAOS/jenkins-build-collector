package org.finra.buildcollector;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.kohsuke.stapler.QueryParameter;

import hudson.DescriptorExtensionList;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

import jenkins.model.Jenkins;

public abstract class AbstractBuildPersistor extends AbstractDescribableImpl<AbstractBuildPersistor>{
	
	public static final String GLOBAL_JOB_PATTERN = ".*";
	private static final Logger LOG = Logger.getLogger(AbstractBuildPersistor.class.getName());

	private final String name;
	private final String pattern;
	private Pattern toMatch;
	
	public AbstractBuildPersistor(String name, String pattern) {
		this.name = name;
		this.pattern = pattern;
		
		try{
			toMatch = Pattern.compile(pattern);
		}catch(PatternSyntaxException e){
			toMatch = Pattern.compile(GLOBAL_JOB_PATTERN);
			LOG.warning("Could not compile pattern for Elasticsearch persistor. Using default global pattern");
		}
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public String getName() {
		return name;
	}
	/**
	 * 
	 *  This method will be called by the build listener upon completion of a build for every class
	 *  that extends {@link AbstractBuildPersistor}. 
	 * 
	 * @param buildRecord
	 */
	public abstract void persistResults(BuildRecord buildRecord);
	
	/**
	 * 
	 *  Method will be called to determine if a build should be persisted by the class that extends
	 *  {@link AbstractBuildPersistor}. If this method returns true, the persistResult method called 
	 *  for this class will be true.
	 * 
	 * @param jobName
	 * @return True if the job name matches the regex expr, false otherwise.
	 */
	public boolean matches(String jobName) {
		
		if(jobName != null && !jobName.isEmpty()){
		   Matcher matcher = toMatch.matcher(jobName);
		   return matcher.matches();
		}
		return false;
	}
	
	public Descriptor<AbstractBuildPersistor> getDescriptor() {
	     return Jenkins.getInstance().getDescriptor(this.getClass());
	}
	
	/**
	 * 
	 * Returns a list of all descriptors that extends this class. This is used by Jelly to render the persistor dropdown.
	 * 
	 * @return
	 */
    public static DescriptorExtensionList<AbstractBuildPersistor,Descriptor<AbstractBuildPersistor>> all() {
        return Jenkins.getInstance().<AbstractBuildPersistor,Descriptor<AbstractBuildPersistor>>getDescriptorList(AbstractBuildPersistor.class);
    }
    
    public static class DescriptorImpl extends Descriptor<AbstractBuildPersistor> {
		
    	public String getDisplayName() {
			return "Abstract Build Persistor";
		}
		
		public FormValidation doCheckPattern(@QueryParameter String value) {
			
			if(value == null || value.isEmpty()){
				return FormValidation.validateRequired(value);
			}
			try{
				Pattern.compile(value);
				return FormValidation.ok();
			}catch(PatternSyntaxException e){
				return FormValidation.error("Invalid pattern, please enter a valid pattern.");
			}
			
		}
    }
    
}
