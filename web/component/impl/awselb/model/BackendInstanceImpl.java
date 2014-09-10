/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.awselb.model;

import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.InstanceState;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import web.component.api.model.BackendInstance;
import web.component.api.model.BackendInstanceState;
import web.component.api.model.LoadBalancer;

/**
 *
 * @author Hiroshi
 */
public class BackendInstanceImpl implements BackendInstance{

    private static final Map<String,BackendInstance> existBackendInstances = new HashMap<>();

    private final Instance elbInstance = new Instance();
    
    private BackendInstanceImpl(String id){
        elbInstance.setInstanceId(id);
    }
    
    private BackendInstanceImpl(Builder builder){
        elbInstance.setInstanceId(builder.id);
    }
    
    @Override
    public LoadBalancer getLoadBalancer() {
        throw new UnsupportedOperationException("Not yet supported.");
    }

    @Override
    public List<LoadBalancer> getLoadBalancers(){
        throw new UnsupportedOperationException("Not yet supported.");
    }
    
    @Override
    public String getId() {
        return elbInstance.getInstanceId();
    }

    @Override
    public void registerWith(LoadBalancer newLb) {
        
        if(newLb == null || !(newLb instanceof LoadBalancerImpl))
            throw new IllegalArgumentException("Invalid load balancer specified.");
            
        newLb.registerInstance(this);
    }

    @Override
    public void deregisterFrom(LoadBalancer lb) {
        
        if(lb == null || !(lb instanceof LoadBalancerImpl))
            throw new IllegalArgumentException("Invalid load balancer specified.");
        
        lb.deregisterInstance(this);
    }

    @Override
    public boolean equals(Object toCompare){
        if(toCompare instanceof BackendInstanceImpl)
            return this.getId().equals(((BackendInstanceImpl)toCompare).getId());
        return false;
    }

    @Override
    public int hashCode() {
        //this is wrong, but I don't know how to implement this method.
        return 31 * getId().hashCode();
    }

    @Override
    public BackendInstanceState getState(){
        throw new UnsupportedOperationException("Not yet supported.");
    }

    @Override
    public BackendInstanceState getStateFromLB(LoadBalancer lb){
        return lb.getInstanceState(this);
    }

    @Override
    public String toString(){
        return "{BackendInstanceID: " + getId() + "}";
    }

    public static class State implements BackendInstanceState{

        private final InstanceState elbInstanceState;
        
        private State(InstanceState elbInstanceState){
         this.elbInstanceState = elbInstanceState;
        }
        
        public static State create(InstanceState elbInstanceState){
            return new State(elbInstanceState);
        }
        
        @Override
        public String getDescription() {
            return elbInstanceState.getDescription();
        }

        @Override
        public String getId() {
            return elbInstanceState.getInstanceId();
        }

        @Override
        public String getReasonCode() {
            return elbInstanceState.getReasonCode();
        }

        @Override
        public String getState() {
            return elbInstanceState.getState();
        }
        
        @Override
        public String toString(){
            return getDescription();
        }
    }
    
    public static class Builder {
        
        private String id;
        
        public Builder id(String id){
            this.id = id;
            return this;
        }
        
        public BackendInstance build(){
            
            if(existBackendInstances.get(id) == null)
                existBackendInstances.put(id, new BackendInstanceImpl(this));
            return existBackendInstances.get(id);
        }
    }
}
