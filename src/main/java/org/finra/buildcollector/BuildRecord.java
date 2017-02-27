/**
 * 
 * POJO that holds the information for the build.  
 * 
 * @author Otto Scheel
 * Company: Finra
 */
package org.finra.buildcollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BuildRecord {
	
	    private int number;
	    private String jobName;
	    private String result;
	    private long startTime;
	    private long duration;
	    private Map<String, String> environment;
	    private List<AbstractPluginRecord> pluginRecords;
	    
	    public BuildRecord(){
	    	pluginRecords = new ArrayList<AbstractPluginRecord>();
	    }
	    
		public int getNumber() {
			return number;
		}
		
		public void setNumber(int number) {
			this.number = number;
		}
		
		public String getJobName() {
			return jobName;
		}
		
		public void setJobName(String jobName) {
			this.jobName = jobName;
		}
		
		public String getResult() {
			return result;
		}
		
		public void setResult(String result) {
			this.result = result;
		}
		
		public long getStartTime() {
			return startTime;
		}
		
		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
		
		public long getDuration() {
			return duration;
		}
		
		public void setDuration(long duration) {
			this.duration = duration;
		}
		
		public Map<String, String> getEnvironment() {
			return environment;
		}
		
		public void setEnvironment(Map<String, String> environment) {
			this.environment = environment;
		}

		public List<AbstractPluginRecord> getRecords() {
			return pluginRecords;
		}

		@Override
		public String toString() {
			return "BuildRecord [number=" + number + ", jobName=" + jobName + ", result=" + result + ", startTime="
					+ startTime + ", duration=" + duration + ", environment=" + environment + ", pluginRecords="
					+ pluginRecords + "]";
		}		
		
}
