/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.AvailabilityZoneMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import web.component.api.model.Zone;

/**
 *
 * @author Hiroshi
 */
public class ZoneImpl extends AWSModelBase implements Zone{

    private static final Map<String,Zone> existZones = new HashMap<>();
    
    private final AvailabilityZone ec2Zone;
    
    private ZoneImpl(Builder builder){
        
        AvailabilityZone source = ec2().getExistEc2AvailabilityZone(builder.name);
        ec2Zone = copyEc2Zone(source);
    }
    
    AvailabilityZone asEc2Zone(){
        return copyEc2Zone(ec2Zone);
    }
    
    private AvailabilityZone copyEc2Zone(AvailabilityZone original){
        AvailabilityZone copy = new AvailabilityZone();
        copy.setMessages(original.getMessages());
        copy.setRegionName(original.getRegionName());
        copy.setZoneName(original.getZoneName());
        return copy;
    }
    
    @Override
    public List<String> getMessages(){
        List<AvailabilityZoneMessage> messages = ec2Zone.getMessages();
        List<String> messageStrings = new ArrayList<>();
        for(AvailabilityZoneMessage message : messages)
            messageStrings.add(message.getMessage());
        return messageStrings;
    }

    @Override
    public String getName(){
        return ec2Zone.getZoneName();
    }
    
    @Override
    public String getRegionName(){
        return ec2Zone.getRegionName();    
    }
    
    @Override
    public String getState(){
        
        String state = "Unknown state";
        
        try{
            state = ec2().getExistEc2AvailabilityZone(getName()).getState();
        }catch(RuntimeException e){
            //do nothing.
        }
        
        return state;    
    }
    
    @Override
    public boolean equals(Object toBeCompared){
        if(toBeCompared instanceof ZoneImpl){
            ZoneImpl asZoneImpl = (ZoneImpl)toBeCompared;
            return getName().equals(asZoneImpl.getName()) && getRegionName().equals(asZoneImpl.getRegionName());
        }
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

    @Override
    public int compareTo(Zone o) {
        
        if(o == null)
            throw new NullPointerException();
        
        return this.getName().compareTo(o.getName());
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
