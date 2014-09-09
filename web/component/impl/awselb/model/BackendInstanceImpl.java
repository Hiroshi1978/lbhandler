/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.awselb.model;

import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.InstanceState;
import java.util.ArrayList;
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
public class BackendInstanceImpl extends Instance implements BackendInstance{

    private static final Map<String,BackendInstance> existBackendInstances = new HashMap<>();

    /*
    * Instance list of the load balancers with which this backend instance is registered.
    *
    */
    private final List<LoadBalancer> lbs = new ArrayList<>();
    
    private BackendInstanceImpl(String id){
        super.setInstanceId(id);
    }
    
    private BackendInstanceImpl(LoadBalancer lb, String id){
        
        if(!(lb instanceof LoadBalancerImpl))
            throw new IllegalArgumentException("Invalid load balancer specified.");
        this.lbs.add(lb);
        super.setInstanceId(id);
    }
    
    public static BackendInstance create(String id){
        if(existBackendInstances.get(id) == null)
            existBackendInstances.put(id, new  BackendInstanceImpl(id));
        return existBackendInstances.get(id);
    }

   /*
    * this method should be called only from LoadBalancerImpl class so defined as package private, not public.
    */
    static BackendInstance create(LoadBalancer lb, String id){
        if(existBackendInstances.get(id) == null)
            existBackendInstances.put(id, new  BackendInstanceImpl(lb,id));
        return existBackendInstances.get(id);
    }

    @Override
    public LoadBalancer getLoadBalancer() {
        return lbs.isEmpty() ? null : lbs.get(0);
    }

   /*
    * This method should be called only from the classes in this package, for example, by LoadBalancerImplClass
    * when its instance is constructed and its backendInstances member is initialized.
    * Should not be called by outer codes, so this is defined as package private, not as public.
    */
    void setLoadBalancer(LoadBalancer lb) {
        lbs.add(lb);
    }

    @Override
    public List<LoadBalancer> getLoadBalancers(){
        return lbs;
    }
    
    @Override
    public String getId() {
        return super.getInstanceId();
    }

    @Override
    public void registerWith(LoadBalancer newLb) {
        
        if(newLb == null || !(newLb instanceof LoadBalancerImpl))
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
        return 31 * getId().hashCode();
    }

    @Override
    public BackendInstanceState getBackendInstanceState(){
        return lbs.isEmpty() ? BackendInstanceImpl.State.create(new InstanceState()) : lbs.get(0).getInstanceState(this);
    }

    @Override
    public BackendInstanceState getBackendInstanceStateFromLB(LoadBalancer lb){
        return lbs.contains(lb) ? lb.getInstanceState(this) : BackendInstanceImpl.State.create(new InstanceState());
    }

    @Override
    public String toString(){
        return "{BackendInstanceID: " + getId() + "}";
    }

    @Override
    public void setInstanceId(String instanceId){
        throw new UnsupportedOperationException("Backend instance id can not be modified.");
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
}
