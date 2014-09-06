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
    
    @Override
    public boolean equals(Object toBeCompared){
        if(toBeCompared instanceof ZoneImpl)
            return this.getName().equals(((ZoneImpl)toBeCompared).getName());
        return false;
    }
    
    @Override
    public int hashCode(){
        //this is wrong, but don't know how to implement this method properly.
        return 31 * this.getName().hashCode();
    }

    @Override
    public String toString(){
        return "{ZoneName: " + name + "}";
    }
}
