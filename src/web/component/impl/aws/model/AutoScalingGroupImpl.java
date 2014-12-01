/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.Date;
import java.util.List;
import static java.util.stream.Collectors.toList;
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
                builder.launchConfigurationName,
                builder.zoneNames,
                builder.vpcZoneIdentifier,
                builder.desiredCapacity
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
        
        return new com.amazonaws.services.autoscaling.model.AutoScalingGroup()
                        .withAutoScalingGroupARN(source.getAutoScalingGroupARN())
                        .withAutoScalingGroupName(source.getAutoScalingGroupName())
                        .withAvailabilityZones(source.getAvailabilityZones())
                        .withDefaultCooldown(source.getDefaultCooldown())
                        .withDesiredCapacity(source.getDesiredCapacity())
                        .withEnabledMetrics(source.getEnabledMetrics())
                        .withHealthCheckGracePeriod(source.getHealthCheckGracePeriod())
                        .withHealthCheckType(source.getHealthCheckType())
                        .withInstances(source.getInstances())
                        .withLaunchConfigurationName(source.getLaunchConfigurationName())
                        .withLoadBalancerNames(source.getLoadBalancerNames())
                        .withMaxSize(source.getMaxSize())
                        .withMinSize(source.getMinSize())
                        .withPlacementGroup(source.getPlacementGroup())
                        .withSuspendedProcesses(source.getSuspendedProcesses())
                        .withTags(source.getTags())
                        .withTerminationPolicies(source.getTerminationPolicies())
                        .withVPCZoneIdentifier(source.getVPCZoneIdentifier());
    }
    
    com.amazonaws.services.autoscaling.model.AutoScalingGroup asAwsAutoScalingGroup(){
        return as().getExistAutoScalingGroupByName(name);
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
        return as().getAttachedInstanceIds(name).stream()
                .map(id -> new InstanceImpl.Builder().id(id).get())
                .collect(toList());
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
        
        as().attachInstances(name, instances.stream()
                                        .map(Instance::getId)
                                        .collect(toList()));
    }
    public void attachInstance(Instance instance) {
        as().attachInstance(name, instance.getId());
    }
    
    @Override
    public void detachInstances(List<Instance> instances) {
        
        if(instances == null || instances.isEmpty())
            throw new IllegalArgumentException("Instances not specified.");
        
        as().detachInstances(name, instances.stream()
                                        .map(Instance::getId)
                                        .collect(toList()));
    }
    public void detachInstance(Instance instance) {
        as().detachInstance(name, instance.getId());
    }
    
    @Override
    public void setZones(List<Zone> zones){
        
        if(zones == null || zones.isEmpty())
            throw new IllegalArgumentException("Zones not specified.");
        
        as().updateAutoScalingGroupAvailabilityZones(name, zones.stream()
                                                                .map(Zone::getName)
                                                                .collect(toList()));
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
    
    @Override
    public boolean exists(){
        return as().getExistAutoScalingGroupByName(name) != null;
    }
    
    @Override
    public String toString(){
        return as().getExistAutoScalingGroupByName(name).toString();
    }
    
    @Override
    public boolean equals(Object o){
        
        if(o instanceof AutoScalingGroupImpl){
            AutoScalingGroupImpl asImpl = (AutoScalingGroupImpl)o;
            com.amazonaws.services.autoscaling.model.AutoScalingGroup thisAwsASGroup
                    = as().getExistAutoScalingGroupByName(name);
            com.amazonaws.services.autoscaling.model.AutoScalingGroup awsASGroupToCompare
                    = asImpl.asAwsAutoScalingGroup();
            return thisAwsASGroup.equals(awsASGroupToCompare);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * as().getExistAutoScalingGroupByName(name).hashCode();
    }
    
    static class Builder {

        private String name;
        private int maxSize;
        private int minSize;
        private int desiredCapacity;
        private String instanceId;
        private String launchConfigurationName;
        private List<String> zoneNames;
        private String vpcZoneIdentifier;
        
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
        public Builder desiredCapacity(int desiredCapacity){
            this.desiredCapacity = desiredCapacity;
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
        public Builder zones(List<String> zones){
            this.zoneNames = zones;
            return this;
        }
        public Builder vpcZoneIdentifier(String vpcZoneIdentifier){
            this.vpcZoneIdentifier = vpcZoneIdentifier;
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
            if((instanceId != null && !instanceId.isEmpty()) &&
                    (launchConfigurationName != null && !launchConfigurationName.isEmpty()))
                throw new IllegalArgumentException("Either instancd ID or launch configuration name must be specified.");
            if((zoneNames == null || zoneNames.isEmpty()) &&
                    (vpcZoneIdentifier != null && vpcZoneIdentifier.isEmpty()))
                throw new IllegalArgumentException("Either availability zone or VPC zone identifier must be specified.");
           
            return AutoScalingGroupImpl.create(this);
        }
        
        public AutoScalingGroup get(){
            
            if(name == null)
                throw new IllegalArgumentException("Auto scaling group name is required.");
            
            return AutoScalingGroupImpl.get(this);
        }
    }
    
}
