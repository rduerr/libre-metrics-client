package org.nsidc.metrics.metrics_client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.log4j.Logger;
import org.nsidc.metrics.model.Sample;
import org.nsidc.metrics.model.Service;

/**
 * 
 *
 */
public class MetricsClient {
	private Sample sample;
	private String serviceURL;
	private Logger logger = Logger.getLogger(MetricsClient.class);
	
	public MetricsClient() {
		ConfigurationFactory factory = new ConfigurationFactory();
		URL configURL = getClass().getResource("/config.xml");
		factory.setConfigurationURL(configURL);
		Configuration config;
		try {
			config = factory.getConfiguration();
		
			// Get the environment setting	
			String env = config.getString("environment");
			logger.info("Metrics client found environment" + env);
			// Get the metrics service url from the system		
			serviceURL = config.getString(env + ".metrics.URL");
			logger.info("Metrics server is:" + serviceURL);
		} catch (ConfigurationException e) {
			logger.error("Metrics client could not initialize properties and will not report metrics.");
			
			e.printStackTrace();
		}
	}
	
	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public int sendMetrics() throws Exception {

		Integer response = -1;
		// Validate the sample object to try and prevent
		// exceptions due to the sample itself
		validate(sample);		
		
		if ( serviceURL == null || serviceURL.isEmpty() )
		{
			throw new Exception("Unable to find system property 'metricsURL'; check maven configuration."); //$NON-NLS-1$
		}
		Service service = sample.getService();
		String fullURL = serviceURL + "projects/" + service.getSponsor() 
				+ "/services/" + service.getServiceName() + "/instances/"
				+ service.getInstance();
		
		logger.info("Sending metric info to: " + fullURL);
		
		// Send the sample to the metrics service
		try {
			response = sendRequest(fullURL);
		} catch (Throwable t) {
			// Swallow exception when sample is actually sent
			// to avoid interfering with application
			System.err.println("Error: " + t);
		}
		return response;
	}

	private int sendRequest(String fullURL) throws IOException, JAXBException {
		URL url = new URL(fullURL);
		
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setRequestMethod("POST"); 
		connection.setRequestProperty("content-type", "application/xml"); 
		connection.setRequestProperty("accept", "application/xml");
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(true);
		connection.connect();
		
		logger.info("Trying to create new JAXBContext instance");
		JAXBContext jc = JAXBContext.newInstance(Sample.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.marshal(sample, connection.getOutputStream());
		
		connection.disconnect();
		
		int response = connection.getResponseCode();
		if (response >= 300 && response < 400  )
		{
			 String location = connection.getHeaderField("location");
			 // Prevent an infinite loop by making sure the urls are different
			 if ( location.compareTo(fullURL) != 0 ) {
				 response = sendRequest(location);
			 }			 
		}
		else if (response != HttpURLConnection.HTTP_OK) {				
			throw new RuntimeException(
					"Operation failed: " + connection.getResponseCode() + ": " + connection.getResponseMessage()); 
		}
		
		return connection.getResponseCode();
	}

	private void validate(Sample sample) throws Exception {
		// Make sure that the Service element is valid
		Service s = sample.getService();
		if (s == null 
				|| s.getInstance() == null 
				|| s.getInstance().isEmpty()
				|| s.getServiceName() == null 
				|| s.getServiceName().isEmpty()
				|| s.getSponsor() == null 
				|| s.getSponsor().isEmpty()) {
			throw new Exception("Sample is invalid and missing important Service information."); 
		}
	}

	public void run() {
		try {
			logger.info("Trying to send metrics...");
			sendMetrics();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
