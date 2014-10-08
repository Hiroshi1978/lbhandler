/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import web.component.api.model.AutoScalingGroup;
import web.component.api.model.Instance;
import web.component.api.model.Zone;
import web.component.impl.aws.AWS;
import web.component.impl.aws.AWSAutoScaling;

/**
 *
 * @author Hiroshi
 */
public class AutoScalingGroupImpl extends AWSModelBase implements AutoScalingGroup{

    private final String name;
    
    private AutoScalingGroupImpl(String name){
        this.name = name;
    }
    
    private static AutoScalingGroup create(Builder builder){
        
        AWSAutoScaling as = AWS.access().as();
        
        as.createAutoScalingGroup(
                builder.name, 
                builder.maxSize, 
                builder.minSize, 
                builder.instanceId, 
                builder.launchConfigurationName
            );
        return new AutoScalingGroupImpl(builder.name);
    }
    
    private static AutoScalingGroup get(Builder builder){
        
        AWSAutoScaling as = AWS.access().as();

        if(as.getExistAutoScalingGroupByName(builder.name) == null)
            throw new RuntimeException("auto scaling group with the specified name [" + builder.name + "] not found.");
        
        return new AutoScalingGroupImpl(builder.name);
    }
    
    private com.amazonaws.services.autoscaling.model.AutoScalingGroup copyAwsASGroup(com.amazonaws.services.autoscaling.model.AutoScalingGroup source){
        
        com.amazonaws.services.autoscaling.model.AutoScalingGroup copy = new com.amazonaws.services.autoscaling.model.AutoScalingGroup();
        copy.setAutoScalingGroupARN(source.getAutoScalingGroupARN());
        copy.setAutoScalingGroupName(source.getAutoScalingGroupName());
        copy.setAvailabilityZones(source.getAvailabilityZones());
        copy.setDefaultCooldown(source.getDefaultCooldown());
        copy.setDesiredCapacity(source.getDesiredCapacity());
        copy.setEnabledMetrics(source.getEnabledMetrics());
        copy.setHealthCheckGracePeriod(source.getHealthCheckGracePeriod());
        copy.setHealthCheckType(source.getHealthCheckType());
        copy.setInstances(source.getInstances());
        copy.setLaunchConfigurationName(source.getLaunchConfigurationName());
        copy.setLoadBalancerNames(source.getLoadBalancerNames());
        copy.setMaxSize(source.getMaxSize());
        copy.setMinSize(source.getMinSize());
        copy.setPlacementGroup(source.getPlacementGroup());
        copy.setSuspendedProcesses(source.getSuspendedProcesses());
        copy.setTags(source.getTags());
        copy.setTerminationPolicies(source.getTerminationPolicies());
        copy.setVPCZoneIdentifier(source.getVPCZoneIdentifier());
        
        return copy;
    }
    
    @Override
    public String getAutoScalingGroupARN() {
        return as().getExistAutoScalingGroupByName(name).getAutoScalingGroupARN();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getZones() {
        return as().getExistAutoScalingGroupByName(name).getAvailabilityZones();
    }

    @Override
    public Date getCreatedTime() {
        return as().getExistAutoScalingGroupByName(name).getCreatedTime();
    }

    @Override
    public int getDefaultCoolDown() {
        return as().getExistAutoScalingGroupByName(name).getDefaultCooldown();
    }

    @Override
    public int getDesiredCapacity() {
        return as().getExistAutoScalingGroupByName(name).getDesiredCapacity();
    }

    @Override
    public int getHealthCheckGracePeriod() {
        return as().getExistAutoScalingGroupByName(name).getHealthCheckGracePeriod();
    }

    @Override
    public String getHealthCheckType() {
        return as().getExistAutoScalingGroupByName(name).getHealthCheckType();
    }

    @Override
    public List<Instance> getInstances() {

        List<Instance> instances = new ArrayList<>();
        for(String id : as().getAttachedInstanceIds(name))
            instances.add(new InstanceImpl.Builder().id(id).get());
        
        return instances;
    }

    @Override
    public String getLaunchConfigurationName() {
        return as().getExistAutoScalingGroupByName(name).getLaunchConfigurationName();
    }

    @Override
    public List<String> getLoadBalancerNames() {
        return as().getExistAutoScalingGroupByName(name).getLoadBalancerNames();
    }

    @Override
    public int getMaxSize() {
        return as().getExistAutoScalingGroupByName(name).getMaxSize();
    }

    @Override
    public int getMinSize() {
        return as().getExistAutoScalingGroupByName(name).getMinSize();
    }

    @Override
    public String getPlacementGroup() {
        return as().getExistAutoScalingGroupByName(name).getPlacementGroup();
    }

    @Override
    public String getStatus() {
        return as().getExistAutoScalingGroupByName(name).getStatus();
    }

    @Override
    public List<String> getTerminationPolicies() {
        return as().getExistAutoScalingGroupByName(name).getTerminationPolicies();
    }

    @Override
    public String getVPCZoneIdentifier() {
        return as().getExistAutoScalingGroupByName(name).getVPCZoneIdentifier();
    }

    @Override
    public void setDesiredCapacity(int desiredCapacity) {
        as().setDesiredCapacity(getName(), desiredCapacity);
    }
    
    @Override
    public int compareTo(AutoScalingGroup o) {
        
        if(o == null)
            throw new NullPointerException();
        
        return this.getName().compareTo(o.getName());
    }

    @Override
    public void attachInstances(List<Instance> instances) {
        
        if(instances == null || instances.isEmpty())
            throw new IllegalArgumentException("Instances not specified.");
        
        List<String> instanceIds = new ArrayList<>();
        for(Instance instance : instances)
            instanceIds.add(instance.getId());
        
        as().attachInstances(name, instanceIds);
    }
    public void attachInstance(Instance instance) {
        as().attachInstance(name, instance.getId());
    }
    
    @Override
    public void detachInstances(List<Instance> instances) {
        
        if(instances == null || instances.isEmpty())
            throw new IllegalArgumentException("Instances not specified.");
        
        List<String> instanceIds = new ArrayList<>();
        for(Instance instance : instances)
            instanceIds.add(instance.getId());
        
        as().detachInstances(name, instanceIds);
    }
    public void detachInstance(Instance instance) {
        as().detachInstance(name, instance.getId());
    }
    
    @Override
    public void setZones(List<Zone> zones){
        
        List<String> zoneNames = new ArrayList<>();
        for(Zone z : zones)
            zoneNames.add(z.getName());
        
        as().updateAutoScalingGroupAvailabilityZones(name, zoneNames);
    }

    @Override
    public void setDefaultCoolDown(int defaultCoolDown) {
        as().updateAutoScalingGroupDefaultCoolDown(name, defaultCoolDown);
    }

    @Override
    public void setHealthCheckGracePeriod(int healthCheckGracePeriod) {
        as().updateAutoScalingGroupHealthCheckGracePeriod(name, healthCheckGracePeriod);
    }

    @Override
    public void setHealthCheckType(String healthCheckType) {
        as().updateAutoScalingGroupHealthCheckType(name, healthCheckType);
    }

    @Override
    public void setLaunchConfigurationName(String launchConfigurationName) {
        as().updateAutoScalingGroupLaunchConfigurationName(name, launchConfigurationName);
    }

    @Override
    public void setMaxSize(int maxSize) {
        as().updateAutoScalingGroupMaxSize(name, maxSize);
    }

    @Override
    public void setMinSize(int minSize) {
        as().updateAutoScalingGroupMinSize(name, minSize);
    }

    @Override
    public void setPlacementGroup(String placementGroup) {
        as().updateAutoScalingGroupPlacementGroup(name, placementGroup);
    }

    @Override
    public void setVPCZoneIdentifier(String vpcZoneIdentifier) {
        as().updateAutoScalingGroupVPCZoneIdentifier(name, vpcZoneIdentifier);
    }

    @Override
    public void setTerminationPolicies(List<String> terminationPolicies) {
        as().updateAutoScalingGroupTerminationPolicies(name, terminationPolicies);
    }
    
    @Override
    public void delete(){
        
        if(as().getExistAutoScalingGroupByName(name) != null)
            as().deleteAutoScalingGroup(name);
    }
    
    static class Builder {

        private String name;
        private int maxSize;
        private int minSize;
        private String instanceId;
        private String launchConfigurationName;
        
        public Builder name(String name){
            this.name = name;
            return this;
        }
        public Builder max(int max){
            this.maxSize = max;
            return this;
        }
        public Builder min(int min){
            this.minSize = min;
            return this;
        }
        public Builder instanceId(String instanceId){
            this.instanceId = instanceId;
            return this;
        }
        public Builder launchConfiguration(String lc){
            this.launchConfigurationName = lc;
            return this;
        }
        
        public AutoScalingGroup create(){
            
            if(name == null || maxSize == 0 || minSize == 0 )
                throw new IllegalArgumentException("Auto scaling group name and max size and min size are required.");
            if(maxSize < minSize)
                throw new IllegalArgumentException("Max size must be equal to or larger than min size.");
            if((instanceId == null || instanceId.isEmpty()) &&
                    (launchConfigurationName == null || launchConfigurationName.isEmpty()))
                throw new IllegalArgumentException("Either instancd ID or launch configuration name must be specified.");
            
            return AutoScalingGroupImpl.create(this);
        }
        
        public AutoScalingGroup get(){
            
            if(name == null)
                throw new IllegalArgumentException("Auto scaling group name is required.");
            
            return AutoScalingGroupImpl.get(this);
        }
    }
    
}
