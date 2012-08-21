package org.nsidc.metrics.metrics_client;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.nsidc.metrics.model.Metric;
import org.nsidc.metrics.model.ObjectFactory;
import org.nsidc.metrics.model.Sample;
import org.nsidc.metrics.model.Service;

/**
 * Unit test for simple App.
 */
public class MetricsClientTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MetricsClientTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MetricsClientTest.class );
    }

    /**
     * Marshall metrics model into XML
     * @throws JAXBException 
     */
    public void testModel() throws JAXBException
    {
    	Sample sample = createSample();
		
		// Marshall the answers
		JAXBContext jc = JAXBContext.newInstance(Sample.class);
		Marshaller marshaller = jc.createMarshaller();
		java.io.StringWriter sw = new StringWriter();
        
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(sample, sw);        
        String xmlString =  sw.toString();
        		
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		
		ByteArrayInputStream input = new ByteArrayInputStream (xmlString.getBytes());
		Sample newSample = (Sample)unmarshaller.unmarshal(input);
		
		sw = new StringWriter();
		marshaller.marshal(newSample, sw);
		String newXmlString =  sw.toString();
		
        assertTrue( newXmlString.equalsIgnoreCase(xmlString) );
    }
    
    private Sample createSample() 
    {
    	ObjectFactory objectFactory = new ObjectFactory();
		Sample sample = objectFactory.createSample();

		Metric metric = objectFactory.createMetric();
		metric.setName("someMetricName");
		metric.setValue("someValue");

		Metric metric2 = objectFactory.createMetric();
		metric2.setName("anotherMetricName");
		metric2.setValue("anotherValue");

		List<Metric> metrics = new ArrayList<Metric>();
		metrics.add(metric);
		metrics.add(metric2);

		Service service = objectFactory.createService();
		service.setId(1);
		service.setInstance("MyInstance");
		service.setServiceName("aService");
		service.setSponsor("ThatSponsor");

		Calendar calendar = Calendar.getInstance();
		sample.setEntryTs(calendar.getTime());
		sample.setIpAddress("1.2.3.256");
		sample.setSessionId("MySession");
		sample.setUserAgent("AgentMan");
		sample.setMetrics(metrics);
		sample.setService(service);

		return sample;
    }
    
    /**
     * Test the metrics client on development
     * @throws Exception 
     */
    public void testClient() throws Exception
    {
    	MetricsClient client = new MetricsClient();
    	client.setSample(createSample());
    	client.run();
    }
}
