/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.awselb.model;

import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.InstanceState;
import web.component.api.model.BackendInstance;
import web.component.api.model.BackendInstanceState;
import web.component.api.model.LoadBalancer;

/**
 *
 * @author Hiroshi
 */
public class BackendInstanceImpl extends Instance implements BackendInstance{

    private final List<LoadBalancer> lbs = new ArrayList<>();
    private final String id;
    
    private BackendInstanceImpl(String id){
        this.id = id;
    }
    
    private BackendInstanceImpl(LoadBalancer lb, String id){
        
        if(!(lb instanceof LoadBalancerImpl))
            throw new IllegalArgumentException("Invalid load balancer specified.");
        this.lbs.add(lb);
        this.id = id;
    }
    
    public static BackendInstance create(String id){
        return new BackendInstanceImpl(id);
    }

    public static BackendInstance create(LoadBalancer lb, String id){
        return new BackendInstanceImpl(lb,id);
    }

    @Override
    public LoadBalancer getLoadBalancer() {
        return lbs.get(0);
    }

    @Override
    public List<LoadBalancer> getLoadBalancers(){
        return lbs;
    }
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void registerWith(LoadBalancer newLb) {
        
        if(!(newLb instanceof LoadBalancerImpl))
            throw new IllegalArgumentException("Invalid load balancer specified.");
            
        if(!lbs.contains(newLb)){
            newLb.registerInstance(this);
            lbs.add(newLb);
        }
    }

    @Override
    public void deregisterFrom(LoadBalancer lb) {
        
        if(lb == null || !(lb instanceof LoadBalancerImpl))
            throw new IllegalArgumentException("Invalid load balancer specified.");
        
        if(lbs.contains(lb)){
            lb.deregisterInstance(this);
            lbs.remove(lb);
        }
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
        return 31 * id.hashCode();
    }

    @Override
    public BackendInstanceState getBackendInstanceState(){
        return lb == null ? BackendInstanceImpl.State.create(new InstanceState()) : lb.getInstanceState(this);
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
        
    }
}
