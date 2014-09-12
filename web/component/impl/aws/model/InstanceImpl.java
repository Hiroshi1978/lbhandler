/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import java.util.ArrayList;
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
public class InstanceImpl extends AWSModelBase implements Instance{

    private static final Map<String,Instance> existInstances = new HashMap<>();
    private final com.amazonaws.services.elasticloadbalancing.model.Instance elbInstance = new com.amazonaws.services.elasticloadbalancing.model.Instance();
    private final com.amazonaws.services.ec2.model.Instance ec2Instance = new com.amazonaws.services.ec2.model.Instance();

    private InstanceImpl(String id){
        elbInstance.setInstanceId(id);
    }
    
   /*
    * Build new instance of this class form Instance of EC2 class instance.
    * This costructor should be called when new instance is launched in cloud.
    */
    private InstanceImpl(com.amazonaws.services.ec2.model.Instance newEc2Instance){
        
        //initialize EC2 instance.
        ec2Instance.setInstanceId(newEc2Instance.getInstanceId());
        ec2Instance.setInstanceType(newEc2Instance.getInstanceType());
        ec2Instance.setInstanceLifecycle(newEc2Instance.getInstanceLifecycle());
        ec2Instance.setImageId(newEc2Instance.getImageId());
        ec2Instance.setLaunchTime(newEc2Instance.getLaunchTime());
        ec2Instance.setPlacement(newEc2Instance.getPlacement());
        
        //initialize ELB instance.
        elbInstance.setInstanceId(newEc2Instance.getInstanceId());
    }

   /*
    * return new instance of this class through specified Instance of EC2 class instance.
    * This static method should be called when new instance is launched in cloud.
    */
    private static Instance create(Builder builder){
        
        AWSEC2 ec2 = (AWSEC2)AWS.get(AWS.BlockName.EC2);
        RunInstancesRequest request = new RunInstancesRequest();
        request.setImageId(builder.imageId);
        request.setInstanceType(builder.type);
        request.setMinCount(1);
        request.setMaxCount(1);
        RunInstancesResult result = ec2.runInstances(request);
        com.amazonaws.services.ec2.model.Instance newEc2Instance = result.getReservation().getInstances().get(0);
        InstanceImpl newInstance = new InstanceImpl(newEc2Instance);
        existInstances.put(newInstance.getId(), newInstance);
        
        return existInstances.get(newInstance.getId());
    }
    
    private static InstanceImpl get(Builder builder){
        return null;
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

    @Override
    public void start() {
        StartInstancesRequest request = new StartInstancesRequest();
        List<String> ids = new ArrayList<>();
        ids.add(getId());
        request.setInstanceIds(ids);
        ec2().startInstances(request);
    }

    @Override
    public void stop() {
        StopInstancesRequest request = new StopInstancesRequest();
        List<String> ids = new ArrayList<>();
        ids.add(getId());
        request.setInstanceIds(ids);
        ec2().stopInstances(request);
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
        private String imageId;
        private String type;
        private String placement;
        
        public Builder id(String id){
            this.id = id;
            return this;
        }
        
       /*
        * Return instance that exists in cloud.
        */
        public Instance get(){
            
            if(id == null)
                throw new IllegalArgumentException("Instance ID not specified.");
            
            return InstanceImpl.get(this);
        }
        
        public Builder imageId(String imageId){
            this.imageId = imageId;
            return this;
        }
        public Builder type(String type){
            this.type = type;
            return this;
        }
        public Builder placement(String placement){
            this.placement = placement;
            return this;
        }
        
       /*
        * Create new instance in cloud and return it.
        */
        public Instance create(){
            
            if(this.imageId == null || this.type == null)
                throw new IllegalArgumentException("Image ID and Instance Type must be specified.");
            
            return InstanceImpl.create(this);
        }
    }
}
