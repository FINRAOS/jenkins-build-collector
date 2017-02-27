package org.finra.buildcollector;

/**
 * 
 *  Base class which needs to be extended in the object in which the plugin information will be stored.
 * 
 * @author Otto Scheel
 */
public abstract class AbstractPluginRecord {
	
	private final String pluginName;
	
	public AbstractPluginRecord(String pluginName){
		this.pluginName = pluginName;
	}

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
		AbstractPluginRecord other = (AbstractPluginRecord) obj;
		if (pluginName == null) {
			if (other.pluginName != null)
				return false;
		} else if (!pluginName.equals(other.pluginName))
			return false;
		return true;
	}
	
}
