/**
 * 
 *  Persistor implementation for Elasticsearch. This class extends {@link org.finra.buildcollector.AbstractBuildPersistor}.
 * 
 * 
 * @author Otto Scheel
 * Company: Finra
 */
package org.finra.buildcollector.elasticsearch;


import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.finra.buildcollector.AbstractBuildPersistor;
import org.finra.buildcollector.BuildRecord;
import org.jfree.util.Log;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.Secret;

public class ElasticsearchPersistor extends AbstractBuildPersistor{

	private static final Logger LOG = Logger.getLogger(ElasticsearchPersistor.class.getName());
	private final String url;
	private final String index;
	private final String type;
	private final String username;
	private final Secret password;
	private final int connectionTimeout;
	private final int readTimeout;

	@DataBoundConstructor
	public ElasticsearchPersistor(String pattern, String url, String index, String username, Secret password,
			String type, int connectionTimeout, int readTimeout) {
		super("Elasticsearch", pattern);
		this.url = url;
		this.index = index;
		this.type = type;
		this.username = username;
		this.password = password;
		this.connectionTimeout = connectionTimeout;
		this.readTimeout = readTimeout;
	}
	
	public String getUsername() {
		return username;
	}

	public Secret getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public String getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}
	
	public int getConnectionTimeout() {
		return connectionTimeout;
	}


	public int getReadTimeout() {
		return readTimeout;
	}
	
	public boolean isEmpty(){
		if (url == null || url.isEmpty())
			return true;
		if (index == null || index.isEmpty())
			return true;
		if (type == null || type.isEmpty())
			return true;
		return false;
	}
	

	
	public void persistResults(BuildRecord buildRecord) {
		try {
			
			if(!isEmpty()){
				Index index = new Index.Builder(buildRecord).index(this.index)
						                                    .type(this.type)
						                                    .build();
				
				JestClientFactory factory = new JestClientFactory();

				HttpClientConfig clientConfig = new HttpClientConfig.Builder(this.url).connTimeout(this.connectionTimeout)
																                        .multiThreaded(false)
																                        .readTimeout(this.readTimeout)
																                        .build();

				factory.setHttpClientConfig(clientConfig);
				
				JestClient client = factory.getObject();
				JestResult result = client.execute(index);

				if (result.isSucceeded()) {
					LOG.info("Sent build to Elasticsearch for: "+ buildRecord.getJobName());
				} else {
					LOG.warning("Failed to index build: " + result.getErrorMessage());
					

				}
			}else{
				LOG.info("Client is not configured, skipping this task.");
			}
		} catch (Exception e) {
			LOG.severe("Error when sending build data: " + " " + e.getMessage());
		}
	}
	
	/**
	 * 
	 * Descriptor for this class.
	 * 
	 * @author Otto Scheel
	 * Company : Finra
	 */
	@Extension
	public static class DescriptorImpl extends AbstractBuildPersistor.DescriptorImpl {

		public DescriptorImpl() {}

		@Override
		public String getId() {
			return "Elasticsearch";
		}


		@Override
		public String getDisplayName() {
			return "Elasticsearch";
		}

		/**
		 * 
		 * Methods below are used by Jelly to validate fields.
		 */
		public FormValidation doCheckIndex(@QueryParameter String value) {
			return FormValidation.validateRequired(value);
		}

		public FormValidation doCheckType(@QueryParameter String value) {
			return FormValidation.validateRequired(value);
		}

		public FormValidation doCheckUrl(@QueryParameter String value) {
			return FormValidation.validateRequired(value);
		}
		

	}

}
