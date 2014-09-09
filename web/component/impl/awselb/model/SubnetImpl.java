/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.awselb.model;

import java.util.HashMap;
import java.util.Map;
import web.component.api.model.Subnet;

/**
 *
 * @author Hiroshi
 */
public class SubnetImpl extends com.amazonaws.services.ec2.model.Subnet implements Subnet{

    private static final Map<String,Subnet> existSubnets = new HashMap<>();
    
    private SubnetImpl(String id){
        super.setSubnetId(id);
    }
    
    @Override
    public String getId() {
        return super.getSubnetId();
    }
    
    public static Subnet create(String id){
        if(existSubnets.get(id) == null)
            existSubnets.put(id, new SubnetImpl(id));
        return existSubnets.get(id);
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
    public void setSubnetId(String subnetId){
        throw new UnsupportedOperationException("Subnet id can not be modified.");
    }
}
