/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.awselb.model;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import java.util.HashMap;
import java.util.Map;
import web.component.api.model.Zone;

/**
 *
 * @author Hiroshi
 */
public class ZoneImpl extends AvailabilityZone implements Zone{

    private static final Map<String,Zone> existZones = new HashMap<>();
    
    public static Zone create(String name){
        if(existZones.get(name) == null)
            existZones.put(name, new ZoneImpl(name));
        return existZones.get(name);
    }
    
    private ZoneImpl(String name){
        super.setZoneName(name);
    }
    
    @Override
    public String getName(){
        return super.getZoneName();
    }
    
    @Override
    public boolean equals(Object toBeCompared){
        if(toBeCompared instanceof ZoneImpl)
            return this.getName().equals(((ZoneImpl)toBeCompared).getName());
        return false;
    }
    
    @Override
    public void setZoneName(String zoneName){
        throw new UnsupportedOperationException("Zone name can not be modified.");
    }
    
    @Override
    public int hashCode(){
        //this is wrong, but don't know how to implement this method properly.
        return 31 * this.getName().hashCode();
    }

    @Override
    public String toString(){
        return "{ZoneName: " + getName() + "}";
    }
}
