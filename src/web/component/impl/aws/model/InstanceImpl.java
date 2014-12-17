/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import web.component.api.model.BackendState;
import web.component.api.model.Instance;
import web.component.api.model.InstanceState;
import web.component.api.model.LoadBalancer;
import web.component.impl.aws.AWS;
import web.component.impl.aws.AWSEC2;

/**
 *
 * @author Hiroshi
 */
public class InstanceImpl extends AWSModelBase implements Instance{

    private static final Map<String,Instance> existInstances = new HashMap<>();
    private final com.amazonaws.services.elasticloadbalancing.model.Instance elbInstance;
    private final com.amazonaws.services.ec2.model.Instance ec2Instance;
    
   /*
    * Build new instance of this class that communicates with the VM identified by the specified instance ID in cloud.
    */
    private InstanceImpl(String id){
        //initialize EC2 instance.
        ec2Instance = copyEc2Instance(ec2().getExistEc2Instance(id));
        //initialize ELB instance.
        elbInstance = new com.amazonaws.services.elasticloadbalancing.model.Instance(ec2Instance.getInstanceId());
    }
    
   /*
    * Build new instance of this class from the specified instance of EC2 Instance class.
    * This costructor should be called when the new VM is launched in cloud.
    */
    private InstanceImpl(com.amazonaws.services.ec2.model.Instance newEc2Instance){
        
        //initialize EC2 instance.
        ec2Instance = copyEc2Instance(newEc2Instance);
        //initialize ELB instance.
        elbInstance = new com.amazonaws.services.elasticloadbalancing.model.Instance(ec2Instance.getInstanceId());
    }

   /*
    * return new instance of this class through specified Instance of EC2 class instance.
    * This static method should be called when new instance is launched in cloud.
    */
    private static Instance create(Builder builder){
        
        AWSEC2 ec2 = AWS.access().ec2();
        com.amazonaws.services.ec2.model.Instance newEc2Instance = ec2.createInstance(builder.imageId, builder.type, builder.zoneName);
        Instance newInstance = new InstanceImpl(newEc2Instance);
        existInstances.put(newInstance.getId(), newInstance);
        
        return existInstances.get(newInstance.getId());
    }
    
    private static Instance get(Builder builder){
        if(existInstances.get(builder.id) == null)
            existInstances.put(builder.id, new InstanceImpl(builder.id));
        return existInstances.get(builder.id);
    }
    
   /*
    * Offer the view of this instance as ELB Instance.
    * This method returns the new instance of com.amazonaws.services.elasticloadbalancing.model.Instance class.
    */
    public com.amazonaws.services.elasticloadbalancing.model.Instance asElbInstance(){
        //return the copy of the instance the field 'elbInstance' of this object refers.
        return copyElbInstance(elbInstance);
    }
    
    com.amazonaws.services.ec2.model.Instance asEc2Instance(){
        //return the copy of the instance the field 'ec2Instance' of this object refers.
        return copyEc2Instance(ec2Instance);
    }
 
    private com.amazonaws.services.elasticloadbalancing.model.Instance copyElbInstance(com.amazonaws.services.elasticloadbalancing.model.Instance original){
        
        return new com.amazonaws.services.elasticloadbalancing.model.Instance()
                .withInstanceId(original.getInstanceId());
    }
    
    private com.amazonaws.services.ec2.model.Instance copyEc2Instance(com.amazonaws.services.ec2.model.Instance original){
        
        return new com.amazonaws.services.ec2.model.Instance()
                .withAmiLaunchIndex(original.getAmiLaunchIndex())
                .withArchitecture(original.getArchitecture())
                .withBlockDeviceMappings(original.getBlockDeviceMappings())
                .withClientToken(original.getClientToken())
                .withEbsOptimized(original.getEbsOptimized())
                .withHypervisor(original.getHypervisor())
                .withIamInstanceProfile(original.getIamInstanceProfile())
                .withImageId(original.getImageId())
                .withInstanceId(original.getInstanceId())
                .withInstanceLifecycle(original.getInstanceLifecycle())
                .withInstanceType(original.getInstanceType())
                .withKernelId(original.getKernelId())
                .withKeyName(original.getKeyName())
                .withLaunchTime(original.getLaunchTime())
                .withMonitoring(original.getMonitoring())
                .withNetworkInterfaces(original.getNetworkInterfaces())
                .withPlacement(original.getPlacement())
                .withPlatform(original.getPlatform())
                .withPrivateDnsName(original.getPrivateDnsName())
                .withPrivateIpAddress(original.getPrivateIpAddress())
                .withPublicDnsName(original.getPublicDnsName())
                .withProductCodes(original.getProductCodes())
                .withPublicIpAddress(original.getPublicIpAddress())
                .withRamdiskId(original.getRamdiskId())
                .withRootDeviceName(original.getRootDeviceName())
                .withRootDeviceType(original.getRootDeviceType())
                .withSecurityGroups(original.getSecurityGroups())
                .withSourceDestCheck(original.getSourceDestCheck())
                .withSpotInstanceRequestId(original.getSpotInstanceRequestId())
                .withSriovNetSupport(original.getSriovNetSupport())
                .withState(original.getState())
                .withStateReason(original.getStateReason())
                .withStateTransitionReason(original.getStateTransitionReason())
                .withSubnetId(original.getSubnetId())
                .withTags(original.getTags())
                .withVirtualizationType(original.getVirtualizationType())
                .withVpcId(original.getVpcId());
    }

    @Override
    public LoadBalancer getLoadBalancer() {
        return getLoadBalancers().stream()
                    .findFirst().orElse(null);
    }

    @Override
    public List<LoadBalancer> getLoadBalancers(){
        return LoadBalancerImpl.getExistLoadBalancers();
    }
    
    @Override
    public String getId() {
        return elbInstance.getInstanceId();
    }

    @Override
    public String getImageId(){
        return ec2Instance.getImageId();
    }

    @Override
    public String getInstanceType(){
        return ec2Instance.getInstanceType();
    }

    @Override
    public String getLifeCycle(){
        return ec2Instance.getInstanceLifecycle();
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

   /*
    * [AWS EC2 instance state transition]
    *
    *   pending =>     running     =>  stopping  =>  stopped  => shutting-down => terminated
    *                                <---- isStartable() ---->
    *             <-isStoppable()->
    *   <----------------------- isTerminatable() ---------------------------->
    */
    @Override
    public String getStateName(){
        return ec2().getInstanceStateName(getId());
    }

    @Override
    public Integer getStateCode(){
        return ec2().getInstanceStateCode(getId());
    }

    @Override
    public boolean isStarted(){
        return getStateName().equals("running");
    }
    
    @Override
    public boolean isStopped(){
        return getStateName().equals("stopped");
    }
    
    @Override
    public boolean isTerminated(){
        return getStateName().equals("terminated");
    }
    
    @Override
    public String getStateTransitionReason(){
        return ec2().getInstanceStateTransitionReason(getId());
    }

    @Override
    public String getPublicIpAddress(){
        return ec2Instance.getPublicIpAddress();
    }
    
    @Override
    public String getSubnetId(){
        return ec2Instance.getSubnetId();
    }

    @Override
    public String getVpcId(){
        return ec2Instance.getVpcId();
    }

    @Override
    public String getZoneName(){
        return ec2Instance.getPlacement().getAvailabilityZone();
    }
    
    @Override
    public String toString(){
        return "{BackendInstanceID: " + getId() + "}";
    }

    @Override
    public void start() {
        if(isStartable())
            ec2().startInstance(getId());
    }
    private boolean isStartable(){
        String stateName = getStateName();
        return stateName.equals("stopped") || stateName.equals("stopping");
    }
    
    @Override
    public void stop() {
        if(isStoppable())
            ec2().stopInstance(getId());
    }
    private boolean isStoppable(){
        String stateName = getStateName();
        return stateName.equals("running") ;
    }

    @Override
    public String getPlacement() {
        return ec2Instance.getPlacement().toString();
    }

    @Override
    public void terminate() {
        if(isTerminatable())
            ec2().terminateInstance(getId());
    }
    private boolean isTerminatable(){
        String stateName = getStateName();
        return !stateName.equals("terminated");
    }

    @Override
    public InstanceState getInstanceState(){
        return InstanceStateImpl.create(this);
    }

    @Override
    public BackendState getBackendStateOf(LoadBalancer lb) {
        if(lb == null)
            throw new IllegalArgumentException("Load balancer not specified.");
        return BackendStateImpl.create(this, lb);
    }

    @Override
    public int compareTo(Instance o) {
        
        if(o == null)
            throw new NullPointerException();
        
        return this.getId().compareTo(o.getId());
    }
    
   /*
    * The instance of this class expresses snapshot of the state of the specified Instance (VM).
    * So it is immutable and its equal method never returns true unless it is compared with itself.
    */
    public static class InstanceStateImpl implements InstanceState{

        private final com.amazonaws.services.ec2.model.InstanceState ec2InstanceState;
    
        private InstanceStateImpl(Instance instance){            
            ec2InstanceState = AWS.access().ec2().getInstanceState(instance.getId());
        }
        
        //only InstanceImpl class can create instance.
        private static InstanceState create(Instance instance){
            return new InstanceStateImpl(instance);
        }

        @Override
        public Integer getCode(){
            return ec2InstanceState.getCode();
        }
        
        @Override
        public String getName(){
            return ec2InstanceState.getName();
        }
        
        @Override
        public String toString(){
            return ec2InstanceState.toString();
        }
    }
    
    public static class BackendStateImpl implements BackendState {

        private final com.amazonaws.services.elasticloadbalancing.model.InstanceState elbInstanceState;

        private BackendStateImpl(Instance instance, LoadBalancer lb){
            
            if(instance == null || lb == null)
                throw new IllegalArgumentException("Both instance and load balancer must be specified.");
                
            elbInstanceState = 
                    AWS.access().elb()
                            .describeInstanceHealth(lb.getName(), ((InstanceImpl)instance).asElbInstance())
                            .getInstanceStates().stream()
                            .findFirst().orElse(null);
        }
        
        private BackendStateImpl(com.amazonaws.services.elasticloadbalancing.model.InstanceState source){
            elbInstanceState = 
                    new com.amazonaws.services.elasticloadbalancing.model.InstanceState()
                            .withDescription(source.getDescription())
                            .withInstanceId(source.getInstanceId())
                            .withReasonCode(source.getReasonCode())
                            .withState(source.getState());
        }
        
        //only InstanceImpl class can create instance.
        private static BackendState create(Instance instance, LoadBalancer lb){
            return new BackendStateImpl(instance, lb);
        }

        //only the classes in this package can create instance.
        static BackendState create(com.amazonaws.services.elasticloadbalancing.model.InstanceState source){
            return new BackendStateImpl(source);
        }
        
        @Override
        public String getInstanceId() {
            return elbInstanceState.getInstanceId();
        }

        @Override
        public String getDescription() {
            return elbInstanceState.getDescription();
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
            return elbInstanceState.toString();
        }

        @Override
        public int compareTo(BackendState o) {
            if(o == null)
                throw new NullPointerException();
            return this.getInstanceId().compareTo(o.getInstanceId());
        }
    }
    
    static class Builder {
        
        private String id;
        private String imageId;
        private String type;
        private String zoneName;
        private String subnetId;
        
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
        public Builder zoneName(String zoneName){
            this.zoneName = zoneName;
            return this;
        }
        public Builder subnetId(String subnetId){
            this.subnetId = subnetId;
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
