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
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    public Instance createInstance(String imageId, String instanceType){
        
        RunInstancesRequest request = new RunInstancesRequest();
        request.setImageId(imageId);
        request.setInstanceType(instanceType);
        request.setMinCount(1);
        request.setMaxCount(1);
        RunInstancesResult  result = runInstances(request);
        Instance newInstance = result.getReservation().getInstances().get(0);
        
        return newInstance;
    }
    
    public StartInstancesResult startInstances(StartInstancesRequest request){
        return awsHttpClient.startInstances(request);
    }
    public StartInstancesResult startInstances(List<String> instanceIds){
        StartInstancesRequest request = new StartInstancesRequest(instanceIds);
        return startInstances(request);
    }
    public StartInstancesResult startInstance(String instanceId){
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);
        return startInstances(instanceIds);
    }    
    
    public StopInstancesResult stopInstances(StopInstancesRequest request){
        return awsHttpClient.stopInstances(request);
    }
    public StopInstancesResult stopInstances(List<String> instanceIds){
        StopInstancesRequest request = new StopInstancesRequest(instanceIds);
        return stopInstances(request);
    }
    public StopInstancesResult stopInstance(String instanceId){
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);
        return stopInstances(instanceIds);
    }
    
    public DescribeInstancesResult describeInstances(DescribeInstancesRequest request){
        return awsHttpClient.describeInstances(request);
    }
    public DescribeInstancesResult describeInstances(List<String> instanceIds){
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setInstanceIds(instanceIds);
        return describeInstances(request);
    }
    public DescribeInstancesResult describeInstance(String instanceId){
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);
        return describeInstances(instanceIds);
    }
    public Instance getExistInstance(String instanceId){
        return describeInstance(instanceId).getReservations().get(0).getInstances().get(0);
    }
    
    public TerminateInstancesResult terminateInstances(TerminateInstancesRequest request){
        return awsHttpClient.terminateInstances(request);
    }
    public TerminateInstancesResult terminateInstances(List<String> instanceIds){
        TerminateInstancesRequest request = new TerminateInstancesRequest(instanceIds);
        return terminateInstances(request);
    }
    public TerminateInstancesResult terminateInstance(String instanceId){
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);
        return terminateInstances(instanceIds);
    }
    
    public DescribeAvailabilityZonesResult describeAvailabilityZones(DescribeAvailabilityZonesRequest request){
        return awsHttpClient.describeAvailabilityZones(request);
    }
    public DescribeAvailabilityZonesResult describeAvailabilityZones(List<String> zoneNames){
        DescribeAvailabilityZonesRequest request = new DescribeAvailabilityZonesRequest();
        request.setZoneNames(zoneNames);
        return describeAvailabilityZones(request);
    }
    public DescribeAvailabilityZonesResult describeAvailabilityZone(String zoneName){
        List<String> zoneNames = new ArrayList<>();
        zoneNames.add(zoneName);
        return describeAvailabilityZones(zoneNames);
    }
    public AvailabilityZone getEc2AvailabilityZone(String zoneName){
        return describeAvailabilityZone(zoneName).getAvailabilityZones().get(0);
    }
}
