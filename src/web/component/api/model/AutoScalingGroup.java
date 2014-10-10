/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.api.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Hiroshi
 */
public interface AutoScalingGroup extends Comparable<AutoScalingGroup>{

    public String getAutoScalingGroupARN();
    public String getName();
    public List<String> getZones();
    public Date getCreatedTime();
    public int getDefaultCoolDown();
    public int getDesiredCapacity();
    public int getHealthCheckGracePeriod();
    public String getHealthCheckType();
    public List<Instance> getInstances();
    public String getLaunchConfigurationName();
    public List<String> getLoadBalancerNames();
    public int getMaxSize();
    public int getMinSize();
    public String getPlacementGroup();
    public String getStatus();
    public List<String> getTerminationPolicies();
    public String getVPCZoneIdentifier();
    
    public void attachInstances(List<Instance> instances);
    public void detachInstances(List<Instance> instances);
    
    public void setDesiredCapacity(int desiredCapacity);
    public void setZones(List<Zone> zones);
    public void setDefaultCoolDown(int defaultCoolDown);
    public void setHealthCheckGracePeriod(int healthCheckGracePeriod);
    public void setHealthCheckType(String healthCheckType);
    public void setLaunchConfigurationName(String launchConfigurationName);
    public void setMaxSize(int maxSize);
    public void setMinSize(int minSize);
    public void setPlacementGroup(String placementGroup);
    public void setVPCZoneIdentifier(String vpcZoneIdentifier);
    public void setTerminationPolicies(List<String> terminationPolicies);
    
    public void delete();
    public boolean exists();
}
