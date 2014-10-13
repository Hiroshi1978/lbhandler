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
import com.amazonaws.services.autoscaling.model.DescribeLaunchConfigurationsRequest;
import com.amazonaws.services.autoscaling.model.DescribeLaunchConfigurationsResult;
import com.amazonaws.services.autoscaling.model.DetachInstancesRequest;
import com.amazonaws.services.autoscaling.model.Instance;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.autoscaling.model.SetDesiredCapacityRequest;
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupResult;
import com.amazonaws.services.autoscaling.model.UpdateAutoScalingGroupRequest;
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
    
    public void detachInstances(DetachInstancesRequest request){
        
        if(request.getAutoScalingGroupName() == null || request.getAutoScalingGroupName().isEmpty())
            throw new IllegalArgumentException("Auto scaling group name not specified.");
        if(request.getShouldDecrementDesiredCapacity() == null)
            throw new IllegalArgumentException("Should decrement desired capacity  or not is not specified.");

        awsHttpClient.detachInstances(request);
    }
    public void detachInstances(String autoScalingGroupName, List<String> instanceIds, boolean shouldDecrementDesiredCapacity){
        
        DetachInstancesRequest request = new DetachInstancesRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setInstanceIds(instanceIds);
        request.setShouldDecrementDesiredCapacity(shouldDecrementDesiredCapacity);
        
        detachInstances(request);
    }
    public void detachInstances(String autoScalingGroupName, List<String> instanceIds){
        
        DetachInstancesRequest request = new DetachInstancesRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setInstanceIds(instanceIds);
        request.setShouldDecrementDesiredCapacity(false);
        
        detachInstances(request);
    }
    public void detachInstancesDecreasingDesiredCapacity(String autoScalingGroupName, List<String> instanceIds){
        
        DetachInstancesRequest request = new DetachInstancesRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setInstanceIds(instanceIds);
        request.setShouldDecrementDesiredCapacity(true);
        
        detachInstances(request);
    }
    public void detachInstance(String autoScalingGroupName, String instanceId, boolean shouldDecrementDesiredCapacity){
        
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);
        
        detachInstances(autoScalingGroupName,instanceIds,shouldDecrementDesiredCapacity);
    }
    public void detachInstance(String autoScalingGroupName, String instanceId){
        
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);
        
        detachInstances(autoScalingGroupName,instanceIds);
    }
    public void detachInstanceDecreasingDesiredCapacity(String autoScalingGroupName, String instanceId){
        
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);
        
        detachInstancesDecreasingDesiredCapacity(autoScalingGroupName,instanceIds);
    }
    
    
    public void createAutoScalingGroup(CreateAutoScalingGroupRequest request){
     
        if(request.getAutoScalingGroupName() == null || request.getAutoScalingGroupName().isEmpty())
            throw new IllegalArgumentException("Auto scaling group name not specified.");
        if(request.getMaxSize() == null)
            throw new IllegalArgumentException("Max size not specified.");
        if(request.getMinSize() == null)
            throw new IllegalArgumentException("Min size not specified.");
        if((request.getLaunchConfigurationName() == null || request.getLaunchConfigurationName().isEmpty()) &&
                (request.getInstanceId() == null || request.getInstanceId().isEmpty()))
            throw new IllegalArgumentException("Either launch configuration name or instance ID must be specified.");
        if((request.getLaunchConfigurationName() != null && !request.getLaunchConfigurationName().isEmpty()) &&
                (request.getInstanceId() != null && !request.getInstanceId().isEmpty()))
            throw new IllegalArgumentException("Either launch configuration name or instance ID must be specified.");
        if((request.getAvailabilityZones() == null || request.getAvailabilityZones().isEmpty()) &&
                request.getVPCZoneIdentifier() == null)
            throw new IllegalArgumentException("At least one availability zone or VPC subnet must be specified.");
        
        awsHttpClient.createAutoScalingGroup(request);
    }

    public void createAutoScalingGroup(
                String autoScalingGroupName, 
                int maxSize, 
                int minSize, 
                String instanceId, 
                String launchConfigurationName,
                List<String> zoneNames,
                String vpcZoneIdentifier,
                int desiredCapacity){
     
        CreateAutoScalingGroupRequest request = new CreateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setMaxSize(maxSize);
        request.setMinSize(minSize);
        request.setDesiredCapacity(desiredCapacity);
        if(instanceId != null && !instanceId.isEmpty())
            request.setInstanceId(instanceId);
        if(launchConfigurationName != null && !launchConfigurationName.isEmpty())
            request.setLaunchConfigurationName(launchConfigurationName);
        if(zoneNames != null && !zoneNames.isEmpty())
            request.setAvailabilityZones(zoneNames);
        if(vpcZoneIdentifier != null && !vpcZoneIdentifier.isEmpty())
            request.setVPCZoneIdentifier(vpcZoneIdentifier);
        
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

    public List<AutoScalingGroup> getExistAutoScalingGroups(){
        return describeAutoScalingGroups().getAutoScalingGroups();
    }
    public AutoScalingGroup getExistAutoScalingGroupByName(String autoScalingGroupName){
        
        List<AutoScalingGroup> existASGroups = describeAutoScalingGroup(autoScalingGroupName).getAutoScalingGroups();
        AutoScalingGroup existOne = null;
        if(existASGroups != null && !existASGroups.isEmpty())
            existOne = existASGroups.get(0);
        
        return existOne;
    }
    
    public DescribeLaunchConfigurationsResult describeLaunchConfigurations(){
        
        return awsHttpClient.describeLaunchConfigurations();
    }
    public DescribeLaunchConfigurationsResult describeLaunchConfigurations(DescribeLaunchConfigurationsRequest request){
        
        if(request.getLaunchConfigurationNames() == null || request.getLaunchConfigurationNames().isEmpty())
            throw new IllegalArgumentException("Launch configuration names not specified.");
        
        return awsHttpClient.describeLaunchConfigurations(request);
    }
    public DescribeLaunchConfigurationsResult describeLaunchConfigurations(List<String> launchConfigurationNames){
        
        DescribeLaunchConfigurationsRequest request = new DescribeLaunchConfigurationsRequest();
        request.setLaunchConfigurationNames(launchConfigurationNames);
        
        return describeLaunchConfigurations(request);
    }
    public DescribeLaunchConfigurationsResult describeLaunchConfiguration(String launchConfigurationName){
        
        List<String> launchConfigurationNames = new ArrayList<>();
        launchConfigurationNames.add(launchConfigurationName);
        
        return describeLaunchConfigurations(launchConfigurationNames);
    }
    public LaunchConfiguration getExistLaunchConfiguration(String launchConfigurationName){
        
        List<LaunchConfiguration> lcs = describeLaunchConfiguration(launchConfigurationName).getLaunchConfigurations();
        LaunchConfiguration existOne = null;
        if(lcs != null && !lcs.isEmpty())
            existOne = lcs.get(0);
        
        return existOne;
    }
    
    public List<String> getAttachedInstanceIds(String autoScalingGroupName){
        List<Instance> instances = getExistAutoScalingGroupByName(autoScalingGroupName).getInstances();
        List<String> instanceIds = new ArrayList<>();
        for(Instance instance : instances)
            instanceIds.add(instance.getInstanceId());
        return instanceIds;
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
    
    public void setDesiredCapacity(SetDesiredCapacityRequest request){
        
        if(request.getAutoScalingGroupName() == null || request.getAutoScalingGroupName().isEmpty())
            throw new IllegalArgumentException("Auto scaling group name not specified.");
        if(request.getDesiredCapacity() == null)
            throw new IllegalArgumentException("Desired capacity not specified.");
        
        awsHttpClient.setDesiredCapacity(request);
    }
    public void setDesiredCapacity(String autoScalingGroupName, int desiredCapacity){
        
        SetDesiredCapacityRequest request = new SetDesiredCapacityRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setDesiredCapacity(desiredCapacity);
        
        awsHttpClient.setDesiredCapacity(request);
    }
    
    public void updateAutoScalingGroup(UpdateAutoScalingGroupRequest request){
        
        if(request.getAutoScalingGroupName() == null || request.getAutoScalingGroupName().isEmpty())
            throw new IllegalArgumentException("Auto scaling group name not specified.");
        
        awsHttpClient.updateAutoScalingGroup(request);
    }
    public void updateAutoScalingGroupAvailabilityZones(String autoScalingGroupName, List<String> zoneNames){
        
        UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setAvailabilityZones(zoneNames);
        updateAutoScalingGroup(request);
    }
    public void updateAutoScalingGroupDefaultCoolDown(String autoScalingGroupName, int defaultCoolDown){
        
        UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setDefaultCooldown(defaultCoolDown);
        updateAutoScalingGroup(request);
    }
    public void updateAutoScalingGroupHealthCheckGracePeriod(String autoScalingGroupName, int healthCheckGracePeriod){
        
        UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setHealthCheckGracePeriod(healthCheckGracePeriod);
        updateAutoScalingGroup(request);
    }
    public void updateAutoScalingGroupHealthCheckType(String autoScalingGroupName, String healthCheckType){
        
        UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setHealthCheckType(healthCheckType);
        updateAutoScalingGroup(request);
    }
    public void updateAutoScalingGroupLaunchConfigurationName(String autoScalingGroupName, String launchConfigurationName){
        
        UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setLaunchConfigurationName(launchConfigurationName);
        updateAutoScalingGroup(request);
    }
    public void updateAutoScalingGroupMaxSize(String autoScalingGroupName, int maxSize){
        
        UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setMaxSize(maxSize);
        updateAutoScalingGroup(request);
    }
    public void updateAutoScalingGroupMinSize(String autoScalingGroupName, int minSize){
        
        UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setMinSize(minSize);
        updateAutoScalingGroup(request);
    }
    public void updateAutoScalingGroupPlacementGroup(String autoScalingGroupName, String placementGroup){
        
        UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setPlacementGroup(placementGroup);
        updateAutoScalingGroup(request);
    }
    public void updateAutoScalingGroupVPCZoneIdentifier(String autoScalingGroupName, String vpcZoneIdentifier){
        
        UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setVPCZoneIdentifier(vpcZoneIdentifier);
        updateAutoScalingGroup(request);
    }
    public void updateAutoScalingGroupTerminationPolicies(String autoScalingGroupName, List<String> terminationPolicies){
        
        UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setTerminationPolicies(terminationPolicies);
        updateAutoScalingGroup(request);
    }
    
    public TerminateInstanceInAutoScalingGroupResult terminateInstanceInAutoScalingGroup(TerminateInstanceInAutoScalingGroupRequest request){
        
        if(request.getInstanceId() == null || request.getInstanceId().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        if(request.getShouldDecrementDesiredCapacity() == null)
            throw new IllegalArgumentException("Whether decrease desired capacity or not is not specified.");

        return awsHttpClient.terminateInstanceInAutoScalingGroup(request);
    }
    public TerminateInstanceInAutoScalingGroupResult terminateInstanceInAutoScalingGroup(String instanceId){
        
        TerminateInstanceInAutoScalingGroupRequest request = new TerminateInstanceInAutoScalingGroupRequest();
        request.setInstanceId(instanceId);
        request.setShouldDecrementDesiredCapacity(false);

        return terminateInstanceInAutoScalingGroup(request);
    }
    public TerminateInstanceInAutoScalingGroupResult terminateInstanceInAutoScalingGroupDecreasingDesiredCapacity(String instanceId){
        
        TerminateInstanceInAutoScalingGroupRequest request = new TerminateInstanceInAutoScalingGroupRequest();
        request.setInstanceId(instanceId);
        request.setShouldDecrementDesiredCapacity(true);

        return terminateInstanceInAutoScalingGroup(request);
    }
}
