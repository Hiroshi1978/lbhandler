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

    private final String id;
    
    private SubnetImpl(String id){
        this.id = id;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    public static Subnet create(String id){
        return new SubnetImpl(id);
    }
}
