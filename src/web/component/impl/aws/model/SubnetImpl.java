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
    
    private final com.amazonaws.services.ec2.model.Subnet ec2Subnet;
    
    private SubnetImpl(String id){
        com.amazonaws.services.ec2.model.Subnet source = ec2().getExistEc2Subnet(id);
        ec2Subnet = copyEc2Subnet(source);
    }
    
    private SubnetImpl(String vpcId, String cidrBlock, String zone){
        com.amazonaws.services.ec2.model.Subnet source = ec2().getNewSubnet(vpcId, cidrBlock, zone);
        ec2Subnet = copyEc2Subnet(source);
    }
    
    private static SubnetImpl get(Builder builder){
        return new SubnetImpl(builder.id);
    }
    
    private static SubnetImpl create(Builder builder){
        return new SubnetImpl(builder.vpcId, builder.cidrBlock, builder.zone);
    }
    
    com.amazonaws.services.ec2.model.Subnet asEc2Subnet(){
        return copyEc2Subnet(ec2Subnet);
    }
    
    private com.amazonaws.services.ec2.model.Subnet copyEc2Subnet(com.amazonaws.services.ec2.model.Subnet original){
        
        com.amazonaws.services.ec2.model.Subnet copy = new com.amazonaws.services.ec2.model.Subnet();
        copy.setSubnetId(original.getSubnetId());
        copy.setAvailabilityZone(original.getAvailabilityZone());
        copy.setAvailableIpAddressCount(original.getAvailableIpAddressCount());
        copy.setCidrBlock(original.getCidrBlock());
        copy.setDefaultForAz(original.getDefaultForAz());
        copy.setMapPublicIpOnLaunch(original.getMapPublicIpOnLaunch());
        copy.setTags(original.getTags());
        copy.setVpcId(original.getVpcId());
        
        return copy;
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
        if(ec2().getExistEc2Subnet(getId()) != null)
            ec2().deleteSubnet(getId());
    }

    @Override
    public int compareTo(Subnet o) {
        
        if(o == null)
            throw new NullPointerException();
        
        return this.getId().compareTo(o.getId());
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
        public Builder cidr(String cidrBlock){
            this.cidrBlock  = cidrBlock;
            return this;
        }
        public Builder vpc(String vpcId){
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
