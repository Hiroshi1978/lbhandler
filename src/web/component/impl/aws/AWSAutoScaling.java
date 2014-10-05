/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AttachInstancesRequest;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import web.component.impl.CloudBlock;

/**
 *
 * @author Hiroshi
 */
public class AWSAutoScaling implements CloudBlock{
    
    private final AmazonAutoScaling awsHttpClient;
    
    private AWSAutoScaling(){
        
        AmazonAutoScalingClient initialClient = new AmazonAutoScalingClient(AWS.access().credentials());
        setUpHttpClient(initialClient);
        awsHttpClient = initialClient;
    }
    
    private void setUpHttpClient(AmazonAutoScalingClient awsAutoScalingClient){

        Properties conf = AWS.access().conf();
        String endpoint = conf.getProperty("autoscaling.endpoint");
        String serviceName = conf.getProperty("autoscaling.servicename");
        String regionName = conf.getProperty("region");
        
        awsAutoScalingClient.setEndpoint(endpoint, serviceName, regionName);
        awsAutoScalingClient.setServiceNameIntern(serviceName);
    }
    
    static AWSAutoScaling get(){
        return new AWSAutoScaling();
    }
    
    public void attachInstances(AttachInstancesRequest request){
        
        if(request.getAutoScalingGroupName() == null || request.getAutoScalingGroupName().isEmpty())
            throw new IllegalArgumentException("Auto scaling group name not specified.");
        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ids not specified.");
        
        awsHttpClient.attachInstances(request);
    }
    
    public void attachInstances(String autoScalingGroupName, List<String> instanceIds){
        
        AttachInstancesRequest request = new AttachInstancesRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setInstanceIds(instanceIds);
        
        attachInstances(request);
    }

    public void attachInstance(String autoScalingGroupName, String instanceId){
        
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);
        
        attachInstances(autoScalingGroupName, instanceIds);
    }
    
    public void createAutoScalingGroup(CreateAutoScalingGroupRequest request){
     
        if(request.getAutoScalingGroupName() == null || request.getAutoScalingGroupName().isEmpty())
            throw new IllegalArgumentException("Auto scaling group name not specified.");
        if(request.getMaxSize() == null)
            throw new IllegalArgumentException("Max size not specified.");
        if(request.getMinSize() == null)
            throw new IllegalArgumentException("Min size not specified.");

        awsHttpClient.createAutoScalingGroup(request);
    }

    public void createAutoScalingGroup(String autoScalingGroupName, int maxSize, int minSize){
     
        CreateAutoScalingGroupRequest request = new CreateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setMaxSize(maxSize);
        request.setMinSize(minSize);
        
        createAutoScalingGroup(request);
    }

    public void deleteAutoScalingGroup(DeleteAutoScalingGroupRequest request){
     
        if(request.getAutoScalingGroupName() == null || request.getAutoScalingGroupName().isEmpty())
            throw new IllegalArgumentException("Auto scaling group name not specified.");
        
        awsHttpClient.deleteAutoScalingGroup(request);
    }

    public void deleteAutoScalingGroup(String autoScalingGroupName){
     
        DeleteAutoScalingGroupRequest request = new DeleteAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        
        deleteAutoScalingGroup(request);
    }

    public DescribeAutoScalingGroupsResult describeAutoScalingGroups(){
        
        return awsHttpClient.describeAutoScalingGroups();
    }

    public DescribeAutoScalingGroupsResult describeAutoScalingGroups(DescribeAutoScalingGroupsRequest request){
        
        if(request.getAutoScalingGroupNames() == null || request.getAutoScalingGroupNames().isEmpty())
            throw new IllegalArgumentException("Auto scaling group names not specified.");
            
        return awsHttpClient.describeAutoScalingGroups(request);
    }

    public DescribeAutoScalingGroupsResult describeAutoScalingGroups(List<String> autoScalingGroupNames){
        
        DescribeAutoScalingGroupsRequest request = new DescribeAutoScalingGroupsRequest();
        request.setAutoScalingGroupNames(autoScalingGroupNames);
            
        return describeAutoScalingGroups(request);
    }

    public DescribeAutoScalingGroupsResult describeAutoScalingGroup(String autoScalingGroupName){
        
        List<String> autoScalingGroupNames = new ArrayList<>();
        autoScalingGroupNames.add(autoScalingGroupName);
            
        return describeAutoScalingGroups(autoScalingGroupNames);
    }

    public AutoScalingGroup getExistAutoScalingGroupByName(String autoScalingGroupName){
        return describeAutoScalingGroup(autoScalingGroupName).getAutoScalingGroups().get(0);
    }
    
    public void createLaunchConfiguration(CreateLaunchConfigurationRequest request){
     
        if(request.getLaunchConfigurationName() == null || request.getLaunchConfigurationName().isEmpty())
            throw new IllegalArgumentException("Launch configuration name not specified.");

        awsHttpClient.createLaunchConfiguration(request);
    }

    public void createLaunchConfiguration(String launchConfigurationName){
     
        CreateLaunchConfigurationRequest request = new CreateLaunchConfigurationRequest();
        request.setLaunchConfigurationName(launchConfigurationName);
        
        createLaunchConfiguration(request);
    }

    public void deleteLaunchConfiguration(DeleteLaunchConfigurationRequest request){
     
        if(request.getLaunchConfigurationName() == null || request.getLaunchConfigurationName().isEmpty())
            throw new IllegalArgumentException("Launch configuration name not specified.");

        awsHttpClient.deleteLaunchConfiguration(request);
    }

    public void deleteLaunchConfiguration(String launchConfigurationName){
     
        DeleteLaunchConfigurationRequest request = new DeleteLaunchConfigurationRequest();
        request.setLaunchConfigurationName(launchConfigurationName);

        deleteLaunchConfiguration(request);
    }

}
