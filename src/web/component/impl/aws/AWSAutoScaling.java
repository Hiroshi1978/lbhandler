/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

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
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.autoscaling.model.SetDesiredCapacityRequest;
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupResult;
import com.amazonaws.services.autoscaling.model.UpdateAutoScalingGroupRequest;
import java.util.List;
import web.component.impl.CloudBlock;

/**
 *
 * @author Hiroshi
 */
public interface AWSAutoScaling extends CloudBlock{
    
    public void attachInstances(AttachInstancesRequest request);
    public void attachInstances(String autoScalingGroupName, List<String> instanceIds);
    public void attachInstance(String autoScalingGroupName, String instanceId);
    public void detachInstances(DetachInstancesRequest request);
    public void detachInstances(String autoScalingGroupName, List<String> instanceIds, boolean shouldDecrementDesiredCapacity);
    public void detachInstances(String autoScalingGroupName, List<String> instanceIds);
    public void detachInstancesDecreasingDesiredCapacity(String autoScalingGroupName, List<String> instanceIds);
    public void detachInstance(String autoScalingGroupName, String instanceId, boolean shouldDecrementDesiredCapacity);
    public void detachInstance(String autoScalingGroupName, String instanceId);
    public void detachInstanceDecreasingDesiredCapacity(String autoScalingGroupName, String instanceId);
    public void createAutoScalingGroup(CreateAutoScalingGroupRequest request);
    public void createAutoScalingGroup(
                    String autoScalingGroupName, 
                    int maxSize,
                    int minSize, 
                    String instanceId, 
                    String launchConfigurationName,
                    List<String> zoneNames,
                    String vpcZoneIdentifier,
                    int desiredCapacity
            );
    public void deleteAutoScalingGroup(DeleteAutoScalingGroupRequest request);
    public void deleteAutoScalingGroup(String autoScalingGroupName);
    public DescribeAutoScalingGroupsResult describeAutoScalingGroups();
    public DescribeAutoScalingGroupsResult describeAutoScalingGroups(DescribeAutoScalingGroupsRequest request);
    public DescribeAutoScalingGroupsResult describeAutoScalingGroups(List<String> autoScalingGroupNames);
    public DescribeAutoScalingGroupsResult describeAutoScalingGroup(String autoScalingGroupName);
    public List<AutoScalingGroup> getExistAutoScalingGroups();
    public AutoScalingGroup getExistAutoScalingGroupByName(String autoScalingGroupName);
    public DescribeLaunchConfigurationsResult describeLaunchConfigurations();
    public DescribeLaunchConfigurationsResult describeLaunchConfigurations(DescribeLaunchConfigurationsRequest request);
    public DescribeLaunchConfigurationsResult describeLaunchConfigurations(List<String> launchConfigurationNames);
    public DescribeLaunchConfigurationsResult describeLaunchConfiguration(String launchConfigurationName);
    public LaunchConfiguration getExistLaunchConfiguration(String launchConfigurationName);
    public List<String> getAttachedInstanceIds(String autoScalingGroupName);
    public void createLaunchConfiguration(CreateLaunchConfigurationRequest request);
    public void createLaunchConfiguration(String launchConfigurationName);
    public void deleteLaunchConfiguration(DeleteLaunchConfigurationRequest request);
    public void deleteLaunchConfiguration(String launchConfigurationName);
    public void setDesiredCapacity(SetDesiredCapacityRequest request);
    public void setDesiredCapacity(String autoScalingGroupName, int desiredCapacity);
    public void updateAutoScalingGroup(UpdateAutoScalingGroupRequest request);
    public void updateAutoScalingGroupAvailabilityZones(String autoScalingGroupName, List<String> zoneNames);
    public void updateAutoScalingGroupDefaultCoolDown(String autoScalingGroupName, int defaultCoolDown);
    public void updateAutoScalingGroupHealthCheckGracePeriod(String autoScalingGroupName, int healthCheckGracePeriod);
    public void updateAutoScalingGroupHealthCheckType(String autoScalingGroupName, String healthCheckType);
    public void updateAutoScalingGroupLaunchConfigurationName(String autoScalingGroupName, String launchConfigurationName);
    public void updateAutoScalingGroupMaxSize(String autoScalingGroupName, int maxSize);
    public void updateAutoScalingGroupMinSize(String autoScalingGroupName, int minSize);
    public void updateAutoScalingGroupPlacementGroup(String autoScalingGroupName, String placementGroup);
    public void updateAutoScalingGroupVPCZoneIdentifier(String autoScalingGroupName, String vpcZoneIdentifier);
    public void updateAutoScalingGroupTerminationPolicies(String autoScalingGroupName, List<String> terminationPolicies);
    public TerminateInstanceInAutoScalingGroupResult terminateInstanceInAutoScalingGroup(TerminateInstanceInAutoScalingGroupRequest request);
    public TerminateInstanceInAutoScalingGroupResult terminateInstanceInAutoScalingGroup(String instanceId);
    public TerminateInstanceInAutoScalingGroupResult terminateInstanceInAutoScalingGroupDecreasingDesiredCapacity(String instanceId);
}
