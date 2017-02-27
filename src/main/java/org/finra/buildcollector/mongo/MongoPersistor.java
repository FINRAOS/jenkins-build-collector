/**
 * 
 *  Persistor implementation for MongoDB. 
 * 
 * @author Otto Scheel
 * Company: Finra
 */
package org.finra.buildcollector.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.finra.buildcollector.AbstractBuildPersistor;
import org.finra.buildcollector.BuildRecord;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoSocketException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.Secret;

public class MongoPersistor extends AbstractBuildPersistor{
	
	private static Logger LOG = Logger.getLogger(MongoPersistor.class.getName());
	
	private final String hostname;
	private final int port;
	private final String dbName;
	private final String collectionName;
	private final int timeout;
	private final String username;
	private final Secret password;
	
	@DataBoundConstructor
	public MongoPersistor(String pattern, String hostname, int port,
			String dbName, String collectionName, String username, Secret password, int timeout) {
		super("Mongo", pattern);
		this.hostname = hostname;
		this.port = port;
		this.dbName = dbName;
		this.collectionName = collectionName;
		this.username = username;
		this.password = password;
		this.timeout = timeout;
	}
	
	public String getUsername() {
		return username;
	}


	public Secret getPassword() {
		return password;
	}


	public String getHostname() {
		return hostname;
	}


	public int getPort() {
		return port;
	}


	public String getDbName() {
		return dbName;
	}


	public String getCollectionName() {
		return collectionName;
	}


	public int getTimeout() {
		return timeout;
	}
	
	
	public boolean hasRequiredFields(){
		
		if(hostname == null || 
				hostname.isEmpty())
			return false;
		
		if(dbName == null || 
				dbName.isEmpty())
			return false;
		
		if(collectionName == null || 
				collectionName.isEmpty())
			return false;
		
		if(port == 0)
			return false;
		
		return true;
	}
	
	private boolean hasUsernameAndPassword(){
		if(username != null && !username.isEmpty()){
			if(password != null && !password.getPlainText().isEmpty()){
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 
	 *  Method called by the Build Collector framework to persist JSON results.
	 * 
	 * @param BuildRecord 
	 */
	@Override
	public void persistResults(BuildRecord buildRecord) {
		
		MongoClient client = null;
		
		try {
			if(hasRequiredFields()){
				
			    List<ServerAddress> seeds = new ArrayList<ServerAddress>();
			    List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
			    seeds.add(new ServerAddress(this.hostname, this.port));
				
				if(hasUsernameAndPassword()){
					credentialsList.add(MongoCredential.createCredential(this.username, 
												                         this.dbName, 
												                         this.password.getPlainText().toCharArray()));
				}
				
				
				MongoClientOptions clientOptions = MongoClientOptions.builder()
						                                             .connectTimeout(this.timeout)
						                                             .build();
				client = new MongoClient(seeds, credentialsList, clientOptions);
							
				MongoDatabase db = client.getDatabase(dbName);
				Gson gson = new Gson();
				
				//Converto to JSON and replace special characters
				String jsonBuild = gson.toJson(buildRecord)
						               .replaceAll("\\.", "_");
				
				Document doc = Document.parse(jsonBuild);
				MongoCollection<Document> collection = db.getCollection(collectionName);
				collection.insertOne(doc);
				
				
				LOG.info("Build information has been saved successfully: " + buildRecord.getJobName());
			}else{
				LOG.info("Mongo client has is not configured, skipping this task.");
			}
			
		} catch (MongoSocketException e) {
			LOG.severe("Error when sending build data: " + " " + e.getMessage());
		}catch (Exception e) {
			LOG.severe("Error when sending build data: " + " " + e.getMessage());
		}finally{
			if(client != null){
				client.close();
			}
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
		public String getDisplayName() {
			return "Mongo";
		}

		/**
		 * 
		 * Methods below are used by Jelly to validate fields.
		 */
		public FormValidation doCheckHostname(@QueryParameter String value) {
			return FormValidation.validateRequired(value);
		}

		public FormValidation doCheckDbName(@QueryParameter String value) {
			return FormValidation.validateRequired(value);
		}

		public FormValidation doCheckPort(@QueryParameter String value) {
			return FormValidation.validateRequired(value);
		}
		
		public FormValidation doCheckCollectionName(@QueryParameter String value) {
			return FormValidation.validateRequired(value);
		}

		public FormValidation doCheckTimeout(@QueryParameter String val) {
			int toTest = 0;
			try {
				toTest = Integer.parseInt(val);
				if (toTest < 0 || toTest > 60) {
					return FormValidation.error("Must be an integer between 1 and 60");
				}
			} catch (NumberFormatException e) {
				return FormValidation.error("Please verify that the string is an integer.");
			}

			return FormValidation.ok();
		}
		
		public FormValidation doTestConnection(@QueryParameter("hostname") final String hostname,
		                                       @QueryParameter("port") final String port,
		                                       @QueryParameter("dbName") final String dbName,
		                                       @QueryParameter("username")final String username,
		                                       @QueryParameter("password")final String secret){
			MongoClient mongoClient = null;
			try{
				
				int portNum = 0;
			    List<ServerAddress> seeds = new ArrayList<ServerAddress>();
			    List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
				
				if(hostname == null || hostname.isEmpty()){
					return FormValidation.error("Please enter a valid hostname");
				}
				
				if(port == null || port.isEmpty()){
					return FormValidation.error("Please enter a valid port");
				}else{
					try{
						portNum = Integer.parseInt(port);
					}catch(Exception e){
						return FormValidation.error("Please enter a number for the port number");
					}
				}
				
				if(dbName == null || dbName.isEmpty()){
					return FormValidation.error("Please enter a valid database name");
				}
				
				if(username != null && !username.isEmpty()){
					
					if(secret == null || secret.isEmpty()){
						return FormValidation.error("Password is required if a username is provided.");
					}else{
						credentialsList.add(MongoCredential.createCredential(username, 
																			dbName, 
																			secret.toCharArray()));
					}
				}
				
				
				seeds.add(new ServerAddress(hostname, portNum));
				mongoClient = new MongoClient(seeds, 
						                      credentialsList,
						                      MongoClientOptions.builder().connectTimeout(1000).build());
				MongoDatabase db = mongoClient.getDatabase(dbName);
				Bson command = new BasicDBObject("ping","1");
				db.runCommand(command);
				
			} catch (MongoSocketException e) {
				return FormValidation.error(e, "A problem occured while testing the connection.");
			}catch(Exception e){
				return FormValidation.error(e, "A problem occured while testing the connection.");
			}finally{
				if(mongoClient != null){
					mongoClient.close();
				}
			}
			
			return FormValidation.ok("Mongo server found.");
		}


	}


}
