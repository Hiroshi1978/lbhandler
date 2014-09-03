/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.awselb.model;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import web.component.api.model.Zone;

/**
 *
 * @author Hiroshi
 */
public class ZoneImpl extends AvailabilityZone implements Zone{
    
    private final String name;
    
    private ZoneImpl(String name){
        this.name = name;
    }
    
    public static Zone create(String name){
        return new ZoneImpl(name);
    }
    
    @Override
    public String getName(){
        return name;
    }
}
