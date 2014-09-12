/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.ec2;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import java.io.IOException;
import java.util.Properties;
import web.component.impl.CloudBlock;

/**
 *
 * @author Hiroshi
 */
public class AWSEC2 implements CloudBlock{
    
    private final AmazonEC2 awsHttpClient;
    
    private AWSEC2(){
        
        Properties conf = new Properties();
        try {
            conf.load(this.getClass().getResourceAsStream("../credentials.txt"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        String awsKey = conf.getProperty("aws.key");
        String secretKey = conf.getProperty("aws.secret");
        AWSCredentials credentials = new BasicAWSCredentials(awsKey,secretKey);
    	
        AmazonEC2Client initialClient = new AmazonEC2Client(credentials);
        setUpHttpClient(initialClient);
        awsHttpClient = initialClient;
    }
    
    private void setUpHttpClient(AmazonEC2Client awsEC2Client){

        Properties conf = new Properties();
        try {
            conf.load(this.getClass().getResourceAsStream("./httpclient_config.txt"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        String endpoint = conf.getProperty("endpoint");
        String serviceName = conf.getProperty("servicename");
        String regionName = conf.getProperty("region");
        
        awsEC2Client.setEndpoint(endpoint, serviceName, regionName);
        awsEC2Client.setServiceNameIntern(serviceName);
    }
    
    public static AWSEC2 get(){
        return new AWSEC2();
    }
    
    public RunInstancesResult runInstances(RunInstancesRequest request){
        return awsHttpClient.runInstances(request);
    }
    public StartInstancesResult startInstances(StartInstancesRequest request){
        return awsHttpClient.startInstances(request);
    }
    public StopInstancesResult stopInstances(StopInstancesRequest request){
        return awsHttpClient.stopInstances(request);
    }
    
    public DescribeAvailabilityZonesResult describeAvailabilityZones(DescribeAvailabilityZonesRequest request){
        return awsHttpClient.describeAvailabilityZones(request);
    }
    public DescribeAvailabilityZonesResult describeAvailabilityZones(List<String> zoneNames){
        DescribeAvailabilityZonesRequest request = new DescribeAvailabilityZonesRequest();
        request.setZoneNames(zoneNames);
        return describeAvailabilityZones(request);
    }
    public DescribeAvailabilityZonesResult describeAvailabilityZones(String zoneName){
        List<String> zoneNames = new ArrayList<>();
        zoneNames.add(zoneName);
        return describeAvailabilityZones(zoneNames);
    }
}
