/**
 * 
 * 
 *  Extension point for the elastic search plugin. This class can be extended to publish build information about 
 *  other plugins. See the svn extension of this plugin for more details on how to extend it.
 * 
 * @author Otto Scheel
 * Company : FINRA
 */
package org.finra.buildcollector;


import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;

public abstract class AbstractPluginRecorder implements ExtensionPoint {

	private final String pluginName;

	public AbstractPluginRecorder(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getPluginName() {
		return pluginName;
	}

	/**
	 *  This will return a list of all the classes which extend this plugin. It used by the ElasticsearchListener to
	 *  run the getPluginRecord for all extension points of this plugin.
	 *  
	 * @return ExtensionList<AbstractPluginRecorder>
	 */
	public static ExtensionList<AbstractPluginRecorder> getAllRecorders() {
		return Jenkins.getInstance().getExtensionList(AbstractPluginRecorder.class);
	}

	/**
	 * 
	 * 
	 * This is the method that needs to be extended by Extension points of this plugin. It will be called when the 
	 * build has completed. An example of how to implement this method can be found with the SVN extension for this 
	 * plugin.
	 * 
	 * 
	 * @param run
	 * @param listener
	 * @return A class that extends the AbstractPluginRecord. See Build Collector SVN extension for an example of how
	 *         to extend this class. 
	 */
	public abstract AbstractPluginRecord getPluginRecord(Run run, TaskListener listener);

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pluginName == null) ? 0 : pluginName.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractPluginRecorder other = (AbstractPluginRecorder) obj;
		if (pluginName == null) {
			if (other.pluginName != null)
				return false;
		} else if (!pluginName.equals(other.pluginName))
			return false;
		return true;
	}

}
