/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import java.util.HashMap;
import java.util.Map;
import web.component.api.model.Zone;

/**
 *
 * @author Hiroshi
 */
public class ZoneImpl extends AWSModelBase implements Zone{

    private static final Map<String,Zone> existZones = new HashMap<>();
    
    private final AvailabilityZone ec2Zone = new AvailabilityZone();
    
    private ZoneImpl(Builder builder){
        
        ec2Zone.setZoneName(builder.name);
    }
    
    public AvailabilityZone asEc2Zone(){
        return ec2Zone;
    }
    
    @Override
    public String getName(){
        return ec2Zone.getZoneName();
    }
    
    @Override
    public String getRegionName(){
        
        String regionName = ec2Zone.getRegionName();
        
        if(regionName == null || regionName.isEmpty()){

            regionName = "";

            try{
                regionName = ec2().getEc2AvailabilitiZone(getName()).getRegionName();
                ec2Zone.setRegionName(regionName);
            }catch(RuntimeException e){
                //do nothing.
            }
        }
        
        return regionName;    
    }
    
    @Override
    public String getState(){
        
        String state = "Unknown state.";
        
        try{
            state = ec2().getEc2AvailabilitiZone(getName()).getState();
        }catch(){
            //do nothing.
        }
        
        return state;    
    }
    
    @Override
    public boolean equals(Object toBeCompared){
        if(toBeCompared instanceof ZoneImpl)
            ZoneImpl asZoneImpl = (ZoneImpl)toBeCompared;
            return getName().equals(asZoneImpl.getName()) && getRegionName().equals(asZoneImpl.getRegionName());
        return false;
    }
    
    @Override
    public int hashCode(){
        //this is wrong, but don't know how to implement this method properly.
        return 31 * ( getName().hashCode() + getRegionName().hashCode() );
    }

    @Override
    public String toString(){
        return "{ZoneName: " + getName() + ", RegionName: " + getRegionName() + "}";
    }
    
    public static class Builder {
        
        private String name;
        
        public Builder name(String name){
            this.name = name;
            return this;
        }
        
        public Zone build(){
            if(existZones.get(name) == null)
                existZones.put(name, new ZoneImpl(this));
            return existZones.get(name);
        }
    }
}
