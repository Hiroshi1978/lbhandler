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
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.Subnet;
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
        
        if(request.getImageId() == null || request.getImageId().isEmpty())
            throw new IllegalArgumentException("Image ID not specified.");
        if(request.getInstanceType() == null || request.getInstanceType().isEmpty())
            throw new IllegalArgumentException("Instance type not specified.");
        if(request.getMinCount() == null)
            request.setMinCount(1);
        if(request.getMaxCount() == null)
            request.setMaxCount(1);
            
        return awsHttpClient.runInstances(request);
    }
    public Instance createInstance(String imageId, String instanceType, String zoneName){
        
        RunInstancesRequest request = new RunInstancesRequest();
        request.setImageId(imageId);
        request.setInstanceType(instanceType);
        if(zoneName != null && !zoneName.isEmpty())
            request.setPlacement(new Placement(zoneName));
        RunInstancesResult  result = runInstances(request);
        Instance newInstance = result.getReservation().getInstances().get(0);
        
        return newInstance;
    }
    public Instance createInstance(String imageId, String instanceType){
        return createInstance(imageId, instanceType, null);
    }
    
    public StartInstancesResult startInstances(StartInstancesRequest request){

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
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

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
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

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
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
    public Instance getExistEc2Instance(String instanceId){
        
        Instance existInstance = null;
        List<Reservation> reservs = describeInstance(instanceId).getReservations();
        if(reservs != null && !reservs.isEmpty()){
            existInstance = reservs.get(0).getInstances().get(0);
        }
        
        return existInstance;
    }
    public InstanceState getInstanceState(String instanceId){
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? null : existInstance.getState();
    }
    public String getInstanceStateName(String instanceId){
        
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? "Unknown state" : existInstance.getState().getName();
    }
    public Integer getInstanceStateCode(String instanceId){
        
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? -1 : existInstance.getState().getCode();
    }
    public String getInstanceStateTransitionReason(String instanceId){
        
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? "Unknown reason" : existInstance.getStateTransitionReason();
    }
    
    public TerminateInstancesResult terminateInstances(TerminateInstancesRequest request){

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
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

        if(request.getZoneNames()== null || request.getZoneNames().isEmpty())
            throw new IllegalArgumentException("Availability zones not specified.");
               
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
    public AvailabilityZone getExistEc2AvailabilityZone(String zoneName){
        return describeAvailabilityZone(zoneName).getAvailabilityZones().get(0);
    }
    
    public DescribeSubnetsResult describeSubnets(DescribeSubnetsRequest request){
        if(request.getSubnetIds() == null || request.getSubnetIds().isEmpty())
            throw new IllegalArgumentException("Subnet IDs not specified.");
        return awsHttpClient.describeSubnets(request);
    }    
    public DescribeSubnetsResult describeSubnets(List<String> subnetIds){
        DescribeSubnetsRequest request = new DescribeSubnetsRequest();
        request.setSubnetIds(subnetIds);
        return describeSubnets(request);
    }
    public DescribeSubnetsResult describeSubnet(String subnetId){
        List<String> subnetIds = new ArrayList<>();
        subnetIds.add(subnetId);
        return describeSubnets(subnetIds);
    }
    public Subnet getExistEc2Subnet(String subnetId){
        return describeSubnet(subnetId).getSubnets().get(0);
    }
}
