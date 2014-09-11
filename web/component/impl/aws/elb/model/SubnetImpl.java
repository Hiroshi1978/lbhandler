/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.elb.model;

import java.util.HashMap;
import java.util.Map;
import web.component.api.model.Subnet;

/**
 *
 * @author Hiroshi
 */
public class SubnetImpl implements Subnet{

    private static final Map<String,Subnet> existSubnets = new HashMap<>();
    
    private final com.amazonaws.services.ec2.model.Subnet elbSubnet = new com.amazonaws.services.ec2.model.Subnet();
    
    private SubnetImpl(Builder builder){
        elbSubnet.setSubnetId(builder.id);
    }
    
    public com.amazonaws.services.ec2.model.Subnet asElbSubnet(){
        return elbSubnet;
    }
    
    @Override
    public String getId() {
        return elbSubnet.getSubnetId();
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

    public static class Builder {
    
        private String id;
        
        public Builder id(String id){
            this.id = id;
            return this;
        }
        
        public Subnet build(){
            if(existSubnets.get(id) == null)
                existSubnets.put(id, new SubnetImpl(this));
            return existSubnets.get(id);
        }
    }
}
