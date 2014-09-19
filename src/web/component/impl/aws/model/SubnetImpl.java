/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.HashMap;
import java.util.Map;
import web.component.api.model.Subnet;

/**
 *
 * @author Hiroshi
 */
public class SubnetImpl extends AWSModelBase implements Subnet{

    private static final Map<String,Subnet> existSubnets = new HashMap<>();
    
    private final com.amazonaws.services.ec2.model.Subnet ec2Subnet = new com.amazonaws.services.ec2.model.Subnet();
    
    private SubnetImpl(String id){
        com.amazonaws.services.ec2.model.Subnet source = ec2().getExistEc2Subnet(id);
        ec2Subnet.setSubnetId(source.getSubnetId());
        ec2Subnet.setAvailabilityZone(source.getAvailabilityZone());
        ec2Subnet.setAvailableIpAddressCount(source.getAvailableIpAddressCount());
        ec2Subnet.setCidrBlock(source.getCidrBlock());
        ec2Subnet.setDefaultForAz(source.getDefaultForAz());
        ec2Subnet.setMapPublicIpOnLaunch(source.getMapPublicIpOnLaunch());
        ec2Subnet.setTags(source.getTags());
        ec2Subnet.setVpcId(source.getVpcId());
    }
    
    private SubnetImpl(String vpcId, String cidrBlock, String zone){
        com.amazonaws.services.ec2.model.Subnet source = ec2().getNewSubnet(vpcId, cidrBlock, zone);
        ec2Subnet.setSubnetId(source.getSubnetId());
        ec2Subnet.setAvailabilityZone(source.getAvailabilityZone());
        ec2Subnet.setAvailableIpAddressCount(source.getAvailableIpAddressCount());
        ec2Subnet.setCidrBlock(source.getCidrBlock());
        ec2Subnet.setDefaultForAz(source.getDefaultForAz());
        ec2Subnet.setMapPublicIpOnLaunch(source.getMapPublicIpOnLaunch());
        ec2Subnet.setTags(source.getTags());
        ec2Subnet.setVpcId(source.getVpcId());        
    }
    
    private static SubnetImpl get(Builder builder){
        return new SubnetImpl(builder.id);
    }
    
    private static SubnetImpl create(Builder builder){
        return new SubnetImpl(builder.vpcId, builder.cidrBlock, builder.zone);
    }
    
    public com.amazonaws.services.ec2.model.Subnet asElbSubnet(){
        return ec2Subnet;
    }
    
    @Override
    public String getId() {
        return ec2Subnet.getSubnetId();
    }
    
    @Override
    public boolean equals(Object toBeCompared){
        if(toBeCompared instanceof SubnetImpl)
            return this.getId().equals(((SubnetImpl)toBeCompared).getId());
        return false;
    }
    
    @Override
    public int hashCode(){
        //this is wrong, but don't know how to implement this method properly.
        return 31 * this.getId().hashCode();
    }
    
    @Override
    public String toString(){
        return "{SubnetID: " + getId() + "}";
    }

    @Override
    public String getZone() {
        return ec2Subnet.getAvailabilityZone();
    }

    @Override
    public Integer getAvailableIpAddressCount() {
        return ec2Subnet.getAvailableIpAddressCount();
    }

    @Override
    public String getCidrBlock() {
        return ec2Subnet.getCidrBlock();
    }

    @Override
    public boolean getDefaultForAz() {
        return ec2Subnet.getDefaultForAz();
    }

    @Override
    public boolean getMapPublicIpOnLaunch() {
        return ec2Subnet.getMapPublicIpOnLaunch();
    }

    @Override
    public String getState() {
        
        String state = "Unknown state";
        
        try{
            state = ec2().getExistEc2Subnet(getId()).getState();
        }catch(RuntimeException e){
            //do nothing.
        }
        
        return state;
    }

    @Override
    public String getVpcId() {
        return ec2Subnet.getVpcId();
    }

    @Override
    public void delete(){
        ec2().deleteSubnet(getId());
    }
    
    public static class Builder {
    
        private String id;
        private String zone;
        private String cidrBlock;
        private String vpcId;
        
        public Builder id(String id){
            this.id = id;
            return this;
        }
        
        public Subnet get(){
            
            if(id == null || id.isEmpty())
                throw new IllegalArgumentException("Subnet ID not specified.");
                        
            if(existSubnets.get(id) == null)
                existSubnets.put(id, SubnetImpl.get(this));
            return existSubnets.get(id);
        }
        
        public Builder zone(String zone){
            this.zone = zone;
            return this;
        }
        public Builder cidrBlock(String cidrBlock){
            this.cidrBlock  = cidrBlock;
            return this;
        }
        public Builder vpcId(String vpcId){
            this.vpcId  = vpcId;
            return this;
        }
        
        public Subnet create(){
            
            if(vpcId == null || vpcId.isEmpty())
                throw new IllegalArgumentException("VPC ID not specified.");
            if(cidrBlock == null || cidrBlock.isEmpty())
                throw new IllegalArgumentException("CIDR block not specified.");
                        
            Subnet newSubnet = SubnetImpl.create(this);
            existSubnets.put(newSubnet.getId(), newSubnet);
            return newSubnet;
        }
    }
}
