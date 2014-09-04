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

    private LoadBalancer lb;
    private final String id;
    
    private BackendInstanceImpl(String id){
        this.id = id;
    }
    
    private BackendInstanceImpl(LoadBalancer lb, String id){
        this.lb = lb;
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
        return lb;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void register(LoadBalancer newLb) {
        if(lb == null || !lb.equals(newLb)){
            newLb.registerInstance(this);
            lb = newLb;
        }
    }

    @Override
    public void deregister() {
        if(lb != null)
            lb.deregisterInstance(this);
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
