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
import java.util.List;
import java.util.Properties;
import web.component.impl.CloudBlock;
import static java.util.stream.Collectors.toList;
import static java.util.Collections.singletonList;

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
        
        attachInstances(new AttachInstancesRequest()
                        .withAutoScalingGroupName(autoScalingGroupName)
                        .withInstanceIds(instanceIds));
    }

    public void attachInstance(String autoScalingGroupName, String instanceId){
        attachInstances(autoScalingGroupName, singletonList(instanceId));
    }
    
    public void detachInstances(DetachInstancesRequest request){
        
        if(request.getAutoScalingGroupName() == null || request.getAutoScalingGroupName().isEmpty())
            throw new IllegalArgumentException("Auto scaling group name not specified.");
        if(request.getShouldDecrementDesiredCapacity() == null)
            throw new IllegalArgumentException("Should decrement desired capacity  or not is not specified.");

        awsHttpClient.detachInstances(request);
    }
    public void detachInstances(String autoScalingGroupName, List<String> instanceIds, boolean shouldDecrementDesiredCapacity){
        
        detachInstances(new DetachInstancesRequest()
                        .withAutoScalingGroupName(autoScalingGroupName)
                        .withInstanceIds(instanceIds)
                        .withShouldDecrementDesiredCapacity(shouldDecrementDesiredCapacity));
    }
    public void detachInstances(String autoScalingGroupName, List<String> instanceIds){
        
        detachInstances(new DetachInstancesRequest()
                        .withAutoScalingGroupName(autoScalingGroupName)
                        .withInstanceIds(instanceIds)
                        .withShouldDecrementDesiredCapacity(false));
    }
    public void detachInstancesDecreasingDesiredCapacity(String autoScalingGroupName, List<String> instanceIds){
        
        detachInstances(new DetachInstancesRequest()
                        .withAutoScalingGroupName(autoScalingGroupName)
                        .withInstanceIds(instanceIds)
                        .withShouldDecrementDesiredCapacity(true));
    }
    public void detachInstance(String autoScalingGroupName, String instanceId, boolean shouldDecrementDesiredCapacity){
        detachInstances(autoScalingGroupName,singletonList(instanceId),shouldDecrementDesiredCapacity);
    }
    public void detachInstance(String autoScalingGroupName, String instanceId){
        detachInstances(autoScalingGroupName,singletonList(instanceId));
    }
    public void detachInstanceDecreasingDesiredCapacity(String autoScalingGroupName, String instanceId){
        detachInstancesDecreasingDesiredCapacity(autoScalingGroupName,singletonList(instanceId));
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
     
        CreateAutoScalingGroupRequest request = 
                new CreateAutoScalingGroupRequest()
                    .withAutoScalingGroupName(autoScalingGroupName)
                    .withMaxSize(maxSize)
                    .withMinSize(minSize);
        
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
     
        deleteAutoScalingGroup(new DeleteAutoScalingGroupRequest()
                                 .withAutoScalingGroupName(autoScalingGroupName));        
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
        
        return describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest()
                                                .withAutoScalingGroupNames(autoScalingGroupNames));           
    }

    public DescribeAutoScalingGroupsResult describeAutoScalingGroup(String autoScalingGroupName){
        
        return describeAutoScalingGroups(singletonList(autoScalingGroupName));
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
        
        return describeLaunchConfigurations(new DescribeLaunchConfigurationsRequest()
                                                .withLaunchConfigurationNames(launchConfigurationNames));
    }
    public DescribeLaunchConfigurationsResult describeLaunchConfiguration(String launchConfigurationName){
        
        return describeLaunchConfigurations(singletonList(launchConfigurationName));
    }
    public LaunchConfiguration getExistLaunchConfiguration(String launchConfigurationName){
        
        List<LaunchConfiguration> lcs = describeLaunchConfiguration(launchConfigurationName).getLaunchConfigurations();
        LaunchConfiguration existOne = null;
        if(lcs != null && !lcs.isEmpty())
            existOne = lcs.get(0);
        
        return existOne;
    }
    
    public List<String> getAttachedInstanceIds(String autoScalingGroupName){
        
        return getExistAutoScalingGroupByName(autoScalingGroupName)
                .getInstances().stream()
                .map(Instance::getInstanceId)
                .collect(toList());
    }
    
    public void createLaunchConfiguration(CreateLaunchConfigurationRequest request){
     
        if(request.getLaunchConfigurationName() == null || request.getLaunchConfigurationName().isEmpty())
            throw new IllegalArgumentException("Launch configuration name not specified.");

        awsHttpClient.createLaunchConfiguration(request);
    }

    public void createLaunchConfiguration(String launchConfigurationName){
     
        createLaunchConfiguration(new CreateLaunchConfigurationRequest()
                                      .withLaunchConfigurationName(launchConfigurationName));        
    }

    public void deleteLaunchConfiguration(DeleteLaunchConfigurationRequest request){
     
        if(request.getLaunchConfigurationName() == null || request.getLaunchConfigurationName().isEmpty())
            throw new IllegalArgumentException("Launch configuration name not specified.");

        awsHttpClient.deleteLaunchConfiguration(request);
    }

    public void deleteLaunchConfiguration(String launchConfigurationName){
     
        deleteLaunchConfiguration(new DeleteLaunchConfigurationRequest()
                                    .withLaunchConfigurationName(launchConfigurationName));
    }
    
    public void setDesiredCapacity(SetDesiredCapacityRequest request){
        
        if(request.getAutoScalingGroupName() == null || request.getAutoScalingGroupName().isEmpty())
            throw new IllegalArgumentException("Auto scaling group name not specified.");
        if(request.getDesiredCapacity() == null)
            throw new IllegalArgumentException("Desired capacity not specified.");
        
        awsHttpClient.setDesiredCapacity(request);
    }
    public void setDesiredCapacity(String autoScalingGroupName, int desiredCapacity){
        
        awsHttpClient.setDesiredCapacity(new SetDesiredCapacityRequest()
                                            .withAutoScalingGroupName(autoScalingGroupName)
                                            .withDesiredCapacity(desiredCapacity));
        
    }
    
    public void updateAutoScalingGroup(UpdateAutoScalingGroupRequest request){
        
        if(request.getAutoScalingGroupName() == null || request.getAutoScalingGroupName().isEmpty())
            throw new IllegalArgumentException("Auto scaling group name not specified.");
        
        awsHttpClient.updateAutoScalingGroup(request);
    }
    public void updateAutoScalingGroupAvailabilityZones(String autoScalingGroupName, List<String> zoneNames){
        
        updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
                                   .withAutoScalingGroupName(autoScalingGroupName)
                                   .withAvailabilityZones(zoneNames));        
    }
    public void updateAutoScalingGroupDefaultCoolDown(String autoScalingGroupName, int defaultCoolDown){
        
        updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
                                    .withAutoScalingGroupName(autoScalingGroupName)
                                    .withDefaultCooldown(defaultCoolDown)); 
   }
    public void updateAutoScalingGroupHealthCheckGracePeriod(String autoScalingGroupName, int healthCheckGracePeriod){
        
        updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
                                    .withAutoScalingGroupName(autoScalingGroupName)
                                    .withHealthCheckGracePeriod(healthCheckGracePeriod));       
    }
    public void updateAutoScalingGroupHealthCheckType(String autoScalingGroupName, String healthCheckType){
        
        updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
                                    .withAutoScalingGroupName(autoScalingGroupName)
                                    .withHealthCheckType(healthCheckType));
    }
    public void updateAutoScalingGroupLaunchConfigurationName(String autoScalingGroupName, String launchConfigurationName){
        
        updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
                                    .withAutoScalingGroupName(autoScalingGroupName)
                                    .withLaunchConfigurationName(launchConfigurationName));
    }
    public void updateAutoScalingGroupMaxSize(String autoScalingGroupName, int maxSize){
        
        updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
                                    .withAutoScalingGroupName(autoScalingGroupName)
                                    .withMaxSize(maxSize));
    }
    public void updateAutoScalingGroupMinSize(String autoScalingGroupName, int minSize){
        
        updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
                                    .withAutoScalingGroupName(autoScalingGroupName)
                                    .withMinSize(minSize));
    }
    public void updateAutoScalingGroupPlacementGroup(String autoScalingGroupName, String placementGroup){
        
        updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
                                    .withAutoScalingGroupName(autoScalingGroupName)
                                    .withPlacementGroup(placementGroup));
    }
    public void updateAutoScalingGroupVPCZoneIdentifier(String autoScalingGroupName, String vpcZoneIdentifier){
        
        updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
                                    .withAutoScalingGroupName(autoScalingGroupName)
                                    .withVPCZoneIdentifier(vpcZoneIdentifier));
    }
    public void updateAutoScalingGroupTerminationPolicies(String autoScalingGroupName, List<String> terminationPolicies){
        
        updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
                                    .withAutoScalingGroupName(autoScalingGroupName)
                                    .withTerminationPolicies(terminationPolicies));
    }
    
    public TerminateInstanceInAutoScalingGroupResult terminateInstanceInAutoScalingGroup(TerminateInstanceInAutoScalingGroupRequest request){
        
        if(request.getInstanceId() == null || request.getInstanceId().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        if(request.getShouldDecrementDesiredCapacity() == null)
            throw new IllegalArgumentException("Whether decrease desired capacity or not is not specified.");

        return awsHttpClient.terminateInstanceInAutoScalingGroup(request);
    }
    public TerminateInstanceInAutoScalingGroupResult terminateInstanceInAutoScalingGroup(String instanceId){
        
        return terminateInstanceInAutoScalingGroup(new TerminateInstanceInAutoScalingGroupRequest()
                                                        .withInstanceId(instanceId)
                                                        .withShouldDecrementDesiredCapacity(false));
    }
    public TerminateInstanceInAutoScalingGroupResult terminateInstanceInAutoScalingGroupDecreasingDesiredCapacity(String instanceId){
        
        return terminateInstanceInAutoScalingGroup(new TerminateInstanceInAutoScalingGroupRequest()
                                                    .withInstanceId(instanceId)
                                                    .withShouldDecrementDesiredCapacity(true));

    }
}
