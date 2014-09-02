/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.awselb.model;

import web.component.api.model.Subnet;

/**
 *
 * @author Hiroshi
 */
public class SubnetImpl implements Subnet{

    private final String subnetId;
    
    private SubnetImpl(String subnetId){
        this.subnetId = subnetId;
    }
    
    @Override
    public String getSubnetId() {
        return this.subnetId;
    }
    
    public static Subnet create(String id){
        return new SubnetImpl(id);
    }
}
