/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import web.component.api.model.VPC;

/**
 *
 * @author Hiroshi
 */
public class VPCImpl extends AWSModelBase implements VPC{

    private final com.amazonaws.services.ec2.model.Vpc ec2Vpc = new com.amazonaws.services.ec2.model.Vpc();

    private VPCImpl(String id){
        com.amazonaws.services.ec2.model.Vpc source = ec2().getExistEc2Vpc(id);
        ec2Vpc.setCidrBlock(source.getCidrBlock());
        ec2Vpc.setDhcpOptionsId(source.getDhcpOptionsId());
        ec2Vpc.setInstanceTenancy(source.getInstanceTenancy());
        ec2Vpc.setIsDefault(source.getIsDefault());
        ec2Vpc.setTags(source.getTags());
        ec2Vpc.setVpcId(source.getVpcId());
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
        
        String state = "Unknow state";
        
        try{
            state = ec2().getExistEc2Vpc(getVpcId()).getState();
        }catch(RuntimeException e){
            //do nothing.
        }
        
        return state;
    }

    @Override
    public String getVpcId() {
        return ec2Vpc.getVpcId();
    }
    
    @Override
    public boolean equals(Object toBeCompared){
        if(toBeCompared instanceof VPCImpl)
            if(getVpcId().equals(((VPC)toBeCompared).getVpcId()))
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
}
