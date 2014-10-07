/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import java.util.List;
import web.component.api.model.LaunchConfiguration;
import web.component.impl.aws.AWS;
import web.component.impl.aws.AWSAutoScaling;

/**
 *
 * @author Hiroshi
 */
public class LaunchConfigurationImpl extends AWSModelBase implements LaunchConfiguration{

    private final com.amazonaws.services.autoscaling.model.LaunchConfiguration awsLaunchConfiguration;
    
    private LaunchConfigurationImpl(com.amazonaws.services.autoscaling.model.LaunchConfiguration source){
        
        awsLaunchConfiguration = copyAwsLaunchConfiguration(source);
    }
    
    private static LaunchConfiguration create(Builder builder){
        
        AWSAutoScaling as = (AWSAutoScaling)AWS.access().get(AWS.BlockName.AutoScaling);
        CreateLaunchConfigurationRequest request = new CreateLaunchConfigurationRequest();
        
        request.setAssociatePublicIpAddress(builder.associatePublicIpAddress);
        request.setEbsOptimized(builder.ebsOptimized);
        request.setIamInstanceProfile(builder.iamInstanceProfile);
        request.setImageId(builder.imageId);
        request.setInstanceId(builder.instanceId);
        request.setInstanceType(builder.instanceType);
        request.setKernelId(builder.kernelId);
        request.setKeyName(builder.keyName);
        request.setLaunchConfigurationName(builder.name);
        request.setPlacementTenancy(builder.placementTenancy);
        request.setRamdiskId(builder.ramdiskId);
        request.setSecurityGroups(builder.securityGroups);
        request.setSpotPrice(builder.spotPrice);
        request.setUserData(builder.userData);
        
        as.createLaunchConfiguration(builder.name);
        
        return new LaunchConfigurationImpl(as.getExistLaunchConfiguration(builder.name));
    }
    
    private static LaunchConfiguration get(Builder builder){
        
        AWSAutoScaling as = (AWSAutoScaling)AWS.access().get(AWS.BlockName.AutoScaling);
        return new LaunchConfigurationImpl(as.getExistLaunchConfiguration(builder.name));
    }
    
    private com.amazonaws.services.autoscaling.model.LaunchConfiguration copyAwsLaunchConfiguration(
        com.amazonaws.services.autoscaling.model.LaunchConfiguration source){
        
        com.amazonaws.services.autoscaling.model.LaunchConfiguration copy = new com.amazonaws.services.autoscaling.model.LaunchConfiguration();
        
        copy.setAssociatePublicIpAddress(source.getAssociatePublicIpAddress());
        copy.setBlockDeviceMappings(source.getBlockDeviceMappings());
        copy.setCreatedTime(source.getCreatedTime());
        copy.setEbsOptimized(source.getEbsOptimized());
        copy.setIamInstanceProfile(source.getIamInstanceProfile());
        copy.setImageId(source.getImageId());
        copy.setInstanceMonitoring(source.getInstanceMonitoring());
        copy.setInstanceType(source.getInstanceType());
        copy.setKernelId(source.getKernelId());
        copy.setKeyName(source.getKeyName());
        copy.setLaunchConfigurationARN(source.getLaunchConfigurationARN());
        copy.setLaunchConfigurationName(source.getLaunchConfigurationName());
        copy.setPlacementTenancy(source.getPlacementTenancy());
        copy.setRamdiskId(source.getRamdiskId());
        copy.setSecurityGroups(source.getSecurityGroups());
        copy.setSpotPrice(source.getSpotPrice());
        copy.setUserData(source.getUserData());
        
        return copy;
    }
    
    com.amazonaws.services.autoscaling.model.LaunchConfiguration asAwsLaunchConfiguration(){
        return copyAwsLaunchConfiguration(awsLaunchConfiguration);
    }
    
    @Override
    public String getName() {
        return awsLaunchConfiguration.getLaunchConfigurationName();
    }

    @Override
    public int compareTo(LaunchConfiguration o) {
        
        if(o == null)
            throw new NullPointerException();
        
        return this.getName().compareTo(o.getName());
    }

    @Override
    public boolean getAssociatePublicIpAddress() {
        return awsLaunchConfiguration.getAssociatePublicIpAddress();
    }

    @Override
    public boolean getEbsOptimized() {
        return awsLaunchConfiguration.getEbsOptimized();
    }

    @Override
    public String getIamInstanceProfile() {
        return awsLaunchConfiguration.getIamInstanceProfile();
    }

    @Override
    public String getImageId() {
        return awsLaunchConfiguration.getImageId();
    }

    @Override
    public String getInstanceType() {
        return awsLaunchConfiguration.getInstanceType();
    }

    @Override
    public String getKernelId() {
        return awsLaunchConfiguration.getKernelId();
    }

    @Override
    public String getKeyName() {
        return awsLaunchConfiguration.getKeyName();
    }

    @Override
    public String getPlacementTenancy() {
        return awsLaunchConfiguration.getPlacementTenancy();
    }

    @Override
    public String getRamdiskId() {
        return awsLaunchConfiguration.getRamdiskId();
    }

    @Override
    public List<String> getSecurityGroups() {
        return awsLaunchConfiguration.getSecurityGroups();
    }

    @Override
    public String getSpotPrice() {
        return awsLaunchConfiguration.getSpotPrice();
    }

    @Override
    public String getUserData() {
        return awsLaunchConfiguration.getUserData();
    }
    
    @Override
    public void delete(){
        
        if(as().getExistLaunchConfiguration(getName()) != null)
            as().deleteLaunchConfiguration(getName());
    }
    
    @Override
    public String toString(){
        return awsLaunchConfiguration.toString();
    }
    
    @Override
    public boolean equals(Object o){
        
        if(o instanceof LaunchConfigurationImpl){
            LaunchConfigurationImpl asImpl = (LaunchConfigurationImpl)o;
            return awsLaunchConfiguration.equals(asImpl.awsLaunchConfiguration);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * awsLaunchConfiguration.hashCode();
    }
    
    
    static class Builder{
        
        private String name;
        private boolean associatePublicIpAddress;
        private boolean ebsOptimized;
        private String iamInstanceProfile;
        private String imageId;
        private String instanceId;
        private String instanceType;
        private String kernelId;
        private String keyName;
        private String placementTenancy;
        private String ramdiskId;
        private List<String> securityGroups;
        private String spotPrice;
        private String userData;
        
        public Builder name(String name){
            this.name = name;
            return this;
        }
        public Builder associatePublicIpAddress(boolean associatePublicIpAddress){
            this.associatePublicIpAddress = associatePublicIpAddress;
            return this;
        }
        public Builder ebsOptimized(boolean ebsOptimized){
            this.ebsOptimized = ebsOptimized;
            return this;
        }
        public Builder iamInstanceProfile(String iamInstanceProfile){
            this.iamInstanceProfile = iamInstanceProfile;
            return this;
        }
        public Builder imageId(String imageId){
            this.imageId = imageId;
            return this;
        }
        public Builder instanceId(String instanceId){
            this.instanceId = instanceId;
            return this;
        }
        public Builder instanceType(String instanceType){
            this.instanceType = instanceType;
            return this;
        }
        public Builder kernelId(String kernelId){
            this.kernelId = kernelId;
            return this;
        }
        public Builder keyName(String keyName){
            this.keyName = keyName;
            return this;
        }
        public Builder placementTenancy(String placementTenancy){
            this.placementTenancy = placementTenancy;
            return this;
        }
        public Builder ramdiskId(String ramdiskId){
            this.ramdiskId = ramdiskId;
            return this;
        }
        public Builder securityGroups(List<String> securityGroups){
            this.securityGroups = securityGroups;
            return this;
        }
        public Builder spotPrice(String spotPrice){
            this.spotPrice = spotPrice;
            return this;
        }
        public Builder userData(String userData){
            this.userData = userData;
            return this;
        }
        
        public LaunchConfiguration create(){
            
            if(name == null || name.isEmpty())
                throw new IllegalArgumentException("Launch configuration name not specified.");

            return LaunchConfigurationImpl.create(this);
        }
        
        public LaunchConfiguration get(){
            
            if(name == null || name.isEmpty())
                throw new IllegalArgumentException("Launch configuration name not specified.");
            
            return LaunchConfigurationImpl.get(this);
        }
    }
}
