/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.Date;
import java.util.List;
import web.component.api.model.AutoScalingGroup;
import web.component.api.model.Instance;
import web.component.impl.aws.AWS;
import web.component.impl.aws.AWSAutoScaling;

/**
 *
 * @author Hiroshi
 */
public class AutoScalingGroupImpl extends AWSModelBase implements AutoScalingGroup{

    private final com.amazonaws.services.autoscaling.model.AutoScalingGroup awsASGroup;
    
    private AutoScalingGroupImpl(com.amazonaws.services.autoscaling.model.AutoScalingGroup source){
        awsASGroup = copyAwsASGroup(source);
    }
    
    private static AutoScalingGroup create(Builder builder){
        
        AWSAutoScaling as = (AWSAutoScaling)AWS.access().get(AWS.BlockName.AutoScaling);
        as.createAutoScalingGroup(builder.name, builder.maxSize, builder.minSize);
        return new AutoScalingGroupImpl(as.getExistAutoScalingGroupByName(builder.name));
    }
    
    private static AutoScalingGroup get(Builder builder){
        
        AWSAutoScaling as = (AWSAutoScaling)AWS.access().get(AWS.BlockName.AutoScaling);
        return new AutoScalingGroupImpl(as.getExistAutoScalingGroupByName(builder.name));
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
        return awsASGroup.getAutoScalingGroupARN();
    }

    @Override
    public String getName() {
        return awsASGroup.getAutoScalingGroupName();
    }

    @Override
    public List<String> getZones() {
        return awsASGroup.getAvailabilityZones();
    }

    @Override
    public Date getCreatedTime() {
        return awsASGroup.getCreatedTime();
    }

    @Override
    public int getDefaultCoolDown() {
        return awsASGroup.getDefaultCooldown();
    }

    @Override
    public int getDesiredCapacity() {
        return awsASGroup.getDesiredCapacity();
    }

    @Override
    public int getHealthCheckGracePeriod() {
        return awsASGroup.getHealthCheckGracePeriod();
    }

    @Override
    public String getHealthCheckType() {
        return awsASGroup.getHealthCheckType();
    }

    @Override
    public List<Instance> getInstances() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLaunchConfigurationName() {
        return awsASGroup.getLaunchConfigurationName();
    }

    @Override
    public List<String> getLoadBalancerNames() {
        return awsASGroup.getLoadBalancerNames();
    }

    @Override
    public int getMaxSize() {
        return awsASGroup.getMaxSize();
    }

    @Override
    public int getMinSize() {
        return awsASGroup.getMinSize();
    }

    @Override
    public String getPlacementGroup() {
        return awsASGroup.getPlacementGroup();
    }

    @Override
    public String getStatus() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getTerminationPolicies() {
        return awsASGroup.getTerminationPolicies();
    }

    @Override
    public String getVPCZoneIdentifier() {
        return awsASGroup.getVPCZoneIdentifier();
    }

    @Override
    public int compareTo(AutoScalingGroup o) {
        
        if(o == null)
            throw new NullPointerException();
        
        return this.getName().compareTo(o.getName());
    }
    
    static class Builder {

        private String name;
        private int maxSize;
        private int minSize;
        
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
        
        public AutoScalingGroup create(){
            
            if(name == null || maxSize == 0 || minSize == 0 )
                throw new IllegalArgumentException("Auto scaling group name and max size and min size are required.");
            if(maxSize < minSize)
                throw new IllegalArgumentException("Max size must be equal to or larger than min size.");
            
            return AutoScalingGroupImpl.create(this);
        }
        
        public AutoScalingGroup get(){
            
            if(name == null)
                throw new IllegalArgumentException("Auto scaling group name is required.");
            
            return AutoScalingGroupImpl.get(this);
        }
    }
    
}
