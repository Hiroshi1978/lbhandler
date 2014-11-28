/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.HashMap;
import java.util.Map;
import web.component.api.model.VPC;

/**
 *
 * @author Hiroshi
 */
public class VPCImpl extends AWSModelBase implements VPC{

    private static Map<String, VPC> existVPCs = new HashMap<>();
    private final com.amazonaws.services.ec2.model.Vpc ec2Vpc;

    private VPCImpl(String id){
        com.amazonaws.services.ec2.model.Vpc source = ec2().getExistEc2Vpc(id);
        ec2Vpc = copyEc2Vpc(source);
    }
    private VPCImpl(String cidrBlock, String tenancy){
        com.amazonaws.services.ec2.model.Vpc source = ec2().getNewVpc(cidrBlock, tenancy);
        ec2Vpc = copyEc2Vpc(source);
    }
    
    
    private static VPC get(Builder builder){
        return new VPCImpl(builder.id);
    }
    private static VPC create(Builder builder){
        return new VPCImpl(builder.cidrBlock, builder.tenancy);
    }
    
    com.amazonaws.services.ec2.model.Vpc asEc2Vpc(){
        return copyEc2Vpc(ec2Vpc);
    }
    
    private com.amazonaws.services.ec2.model.Vpc copyEc2Vpc(com.amazonaws.services.ec2.model.Vpc original){
        
        return new com.amazonaws.services.ec2.model.Vpc()
                    .withCidrBlock(original.getCidrBlock())
                    .withDhcpOptionsId(original.getDhcpOptionsId())
                    .withInstanceTenancy(original.getInstanceTenancy())
                    .withIsDefault(original.getIsDefault())
                    .withTags(original.getTags())
                    .withVpcId(original.getVpcId());
    }
    
    @Override
    public String getCidrBlock() {
        return ec2Vpc.getCidrBlock();
    }

    @Override
    public String getDhcpOptionsId() {
        return ec2Vpc.getDhcpOptionsId();
    }

    @Override
    public String getInstanceTenancy() {
        return ec2Vpc.getInstanceTenancy();
    }

    @Override
    public Boolean getIsDefault() {
        return ec2Vpc.getIsDefault();
    }

    @Override
    public String getState() {
        
        String state = "Unknown state";
        
        try{
            state = ec2().getExistEc2Vpc(getId()).getState();
        }catch(RuntimeException e){
            //do nothing.
        }
        
        return state;
    }

    @Override
    public String getId() {
        return ec2Vpc.getVpcId();
    }
    
    @Override
    public void delete(){
        if(ec2().getExistEc2Vpc(getId()) != null)
            ec2().deleteVpc(getId());
    }
    
    @Override
    public boolean equals(Object toBeCompared){
        if(toBeCompared instanceof VPCImpl)
            if(getId().equals(((VPC)toBeCompared).getId()))
                return true;
        return false;
    }
    
    @Override
    public int hashCode(){
        return 31 * ec2Vpc.hashCode();
    }
    
    @Override
    public String toString(){
        return ec2Vpc.toString();
    }

    @Override
    public int compareTo(VPC o) {
        
        if(o == null)
            throw new NullPointerException();
        
        return this.getId().compareTo(o.getId());
    }
    
    public static class Builder {
        
        private String id;
        private String cidrBlock;
        private String tenancy;
        
        public Builder id(String id){
            this.id = id;
            return this;
        }
        public Builder cidr(String cidr){
            this.cidrBlock = cidr;
            return this;
        }
        public Builder tenancy(String tenancy){
            this.tenancy = tenancy;
            return this;
        }

        public VPC get(){
            
            if(id == null || id.isEmpty())
                throw new IllegalArgumentException("VPC ID not specified.");
            
            if(existVPCs.get(id) == null)
                existVPCs.put(id, VPCImpl.get(this));
            return existVPCs.get(id);
        }
        
        public VPC create(){
            
            if(cidrBlock == null || cidrBlock.isEmpty())
                throw new IllegalArgumentException("CIDR block not specified.");
            if(tenancy == null || tenancy.isEmpty())
                throw new IllegalArgumentException("Instnce tenancy not specified.");
            if(!tenancy.equals("default") && !tenancy.equals("dedicated"))
                throw new IllegalArgumentException("Instnce tenancy has to be either 'default' or 'dedicated'.");
                        
            VPC newVPC = VPCImpl.create(this);
            existVPCs.put(newVPC.getId(), newVPC);
            return newVPC;
        }
    }
}
