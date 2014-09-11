/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import web.component.api.model.Instance;
import web.component.api.model.InstanceState;
import web.component.api.model.LoadBalancer;
import web.component.impl.aws.AWS;
import web.component.impl.aws.ec2.AWSEC2;

/**
 *
 * @author Hiroshi
 */
public class InstanceImpl implements Instance{

    private static final Map<String,Instance> existInstances = new HashMap<>();
    private final AWSEC2 ec2 = (AWSEC2)AWS.get(AWS.ComponentName.EC2);
    private final com.amazonaws.services.elasticloadbalancing.model.Instance elbInstance = new com.amazonaws.services.elasticloadbalancing.model.Instance();
    private final com.amazonaws.services.ec2.model.Instance ec2Instance = new com.amazonaws.services.ec2.model.Instance();

    private InstanceImpl(String id){
        elbInstance.setInstanceId(id);
    }
    
    private InstanceImpl(Builder builder){
        elbInstance.setInstanceId(builder.id);
    }

    public com.amazonaws.services.elasticloadbalancing.model.Instance asElbInstance(){
        return elbInstance;
    }
    
    public com.amazonaws.services.ec2.model.Instance asEc2Instance(){
        return ec2Instance;
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
        if(toCompare instanceof InstanceImpl)
            return this.getId().equals(((InstanceImpl)toCompare).getId());
        return false;
    }

    @Override
    public int hashCode() {
        //this is wrong, but I don't know how to implement this method.
        return 31 * getId().hashCode();
    }

    @Override
    public InstanceState getState(){
        throw new UnsupportedOperationException("Not yet supported.");
    }

    @Override
    public InstanceState getStateFromLB(LoadBalancer lb){
        return lb.getInstanceState(this);
    }

    @Override
    public String toString(){
        return "{BackendInstanceID: " + getId() + "}";
    }

    public static class State implements InstanceState{

        private final com.amazonaws.services.elasticloadbalancing.model.InstanceState elbInstanceState;

        private State(com.amazonaws.services.elasticloadbalancing.model.InstanceState elbInstanceState){
         this.elbInstanceState = elbInstanceState;
        }
        
        public static State create(com.amazonaws.services.elasticloadbalancing.model.InstanceState elbInstanceState){
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
        
        public Instance build(){
            
            if(existInstances.get(id) == null)
                existInstances.put(id, new InstanceImpl(this));
            return existInstances.get(id);
        }
    }
}
