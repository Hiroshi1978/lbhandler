    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.ListenerDescription;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import java.util.ArrayList;
import java.util.List;
import web.component.api.model.Instance;
import web.component.api.model.InstanceState;
import web.component.api.model.LoadBalancer;
import web.component.api.model.LoadBalancerListener;
import web.component.api.model.Subnet;
import web.component.api.model.Zone;
import web.component.impl.aws.AWS;
import web.component.impl.aws.elb.AWSELB;

/**
 *
 * @author Hiroshi
 */
public class LoadBalancerImpl implements LoadBalancer{

    private final AWSELB elb = (AWSELB)AWS.get(AWS.ComponentName.ELB);
    private final String name;
    private final List<LoadBalancerListener> listeners = new ArrayList<>();
    private final List<Zone> zones = new ArrayList<>();
    private final List<Subnet> subnets = new ArrayList<>();
    private final List<Instance> instances = new ArrayList<>();
    
    private volatile boolean started;
    private volatile boolean destroyed;
    
    private LoadBalancerImpl(Builder builder){
        
        this.name = builder.name;

        List<Listener> elbListeners = new ArrayList<>();
        for(LoadBalancerListener listener : builder.listeners){
            if(listener instanceof LoadBalancerListenerImpl){
                elbListeners.add(((LoadBalancerListenerImpl)listener).asElbListener());
            }else{
                throw new IllegalArgumentException("Invalid listeners specified.");
            }
        }
        this.listeners.addAll(builder.listeners);
        
        List<AvailabilityZone> availabilityZones = new ArrayList<>();
        if(builder.zones != null){
            for(Zone zone : builder.zones){
                if(zone instanceof ZoneImpl){
                    availabilityZones.add(((ZoneImpl)zone).asElbZone());
                }else{
                    throw new IllegalArgumentException("Invalid zones specified.");
                }
            }
        }
        if(builder.zones != null)
            this.zones.addAll(builder.zones);
        
        List<String> subnetIds = new ArrayList<>();
        if(builder.subnets != null){
            for(Subnet subnet : builder.subnets){
                if(subnet instanceof SubnetImpl){
                    subnetIds.add(subnet.getId());
                }else{
                    throw new IllegalArgumentException("Invalid subnets specified.");
                }
            }
        }
        if(builder.subnets != null)
            this.subnets.addAll(builder.subnets);
        
        if(this.zones != null && !this.zones.isEmpty()){
            elb.createLoadBalancerWithAvailabilityZones(name, elbListeners, availabilityZones);
        }else if(this.subnets != null && !this.subnets.isEmpty()){
            elb.createLoadBalancerWithSubnets(name, elbListeners, subnetIds);
        }else{
            throw new IllegalArgumentException("Either zones or subnets must be specified.");
        }
    }

    private LoadBalancerImpl(String name){
        this.name = name;
    }
    
   /*
    * This constructor should be called only inside the getExistLoadBalancerByName method.
    */
    private LoadBalancerImpl(
            String name, 
            List<LoadBalancerListener> listeners,
            List<Zone> zones,
            List<Subnet> subnets,
            List<Instance> backendInstances
    ) {
        
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(listeners == null || listeners.isEmpty())
            throw new IllegalArgumentException("Load balancer listeners not specified.");
        if(zones == null || zones.isEmpty())
            throw new IllegalArgumentException("Zones not specified.");
        
        //set load balancer name.
        this.name = name;

        //set listeners.
        this.listeners.addAll(listeners);
           

        //set zones.
        this.zones.addAll(zones);

        //set subnets.
        if(subnets != null && !subnets.isEmpty())
            this.subnets.addAll(subnets);

        //set backendinstances.
        if(backendInstances != null && !backendInstances.isEmpty())
            this.instances.addAll(backendInstances);
    }
    
    /**
     * Check the state of the load balancer by calling DescribeLoadBalancers API.
     * The result is true if the name of this load balancer could be found in the response.
     * 
     * @return true if this load balancer is started.
     */
    @Override
    public boolean isStarted() {
        
        if(!started){
            LoadBalancerDescription description = elb.getLoadBalancerDescription(name);
            boolean exists = name.equals(description.getLoadBalancerName());
            if(!started && exists)
                //To be set only once.
                started = true;
        }
        return started;
    }

    /**
     * Add new listeners to this load balancer by calling CreateLoadBalancerListeners API.
     * 
     * @param newListeners List of the listeners to be added to this load balancer.
     */
    @Override
    public void createListeners(List<LoadBalancerListener> newListeners) {

        if(isDestroyed())
            return;

        List<LoadBalancerListener> listenersToAdd  = new ArrayList();
        List<Listener> elbListeners = new ArrayList<>();
        for(LoadBalancerListener newListener : newListeners){
            if(newListener instanceof LoadBalancerListenerImpl){
                if(!listeners.contains(newListener)){
                    listenersToAdd.add(newListener);
                    elbListeners.add(((LoadBalancerListenerImpl)newListener).asElbListener());
                }
                            }else{
                throw new IllegalArgumentException("Invalid listeners specified.");
            }
        }
        if(!elbListeners.isEmpty())
            elb.createLoadBalancerListeners(name,elbListeners);
        if(!listenersToAdd.isEmpty())
            this.listeners.addAll(listenersToAdd);
    }

    @Override
    public void deleteListeners(List<LoadBalancerListener> listenersToDelete) {

        if(isDestroyed())
            return;

        List<LoadBalancerListener> listenersToRemove  = new ArrayList();
        List<Listener> elbListeners = new ArrayList<>();
        for(LoadBalancerListener listenerToDelete : listenersToDelete){
            if(listenerToDelete instanceof LoadBalancerListenerImpl){
                if(listeners.contains(listenerToDelete)){
                    listenersToRemove.add(listenerToDelete);
                    elbListeners.add(((LoadBalancerListenerImpl)listenerToDelete).asElbListener());
                }
            }else{
                throw new IllegalArgumentException("Invalid listeners specified.");
            }
        }
        if(!elbListeners.isEmpty())
            elb.deleteLoadBalancerListeners(name,elbListeners);
        if(!listenersToRemove.isEmpty())
            this.listeners.removeAll(listenersToRemove);
    }

    @Override
    public void createListener(LoadBalancerListener newListener) {

        if(isDestroyed())
            return;

        List<LoadBalancerListener> newListeners = new ArrayList<>();
        newListeners.add(newListener);
        this.createListeners(newListeners);
    }

    @Override
    public void deleteListener(LoadBalancerListener listenerToDelete) {

        if(isDestroyed())
            return;

        List<LoadBalancerListener> listenersToDelete = new ArrayList<>();
        listenersToDelete.add(listenerToDelete);
        this.deleteListeners(listenersToDelete);
    }

    @Override
    public void registerInstances(List<Instance> newInstances) {

        if(isDestroyed())
            return;

        List<Instance> backendInstancesToAdd = new ArrayList<>();
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = new ArrayList<>();
        for(Instance newInstance : newInstances){
            if(newInstance instanceof InstanceImpl){
                if(!instances.contains(newInstance)){
                    backendInstancesToAdd.add(newInstance);
                    elbInstances.add(((InstanceImpl)newInstance).asElbInstance());
                }
            }else{
                throw new IllegalArgumentException("Invalid instances specified.");
            }
        }
        if(!elbInstances.isEmpty())
            elb.registerInstancesWithLoadBalancer(name, elbInstances);
        if(!backendInstancesToAdd.isEmpty())
            this.instances.addAll(backendInstancesToAdd);
    }

    @Override
    public void deregisterInstances(List<Instance> instancesToDelete) {

        if(isDestroyed())
            return;
        
        List<Instance> backendInstancesToRemove = new ArrayList<>();
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = new ArrayList<>();
        for(Instance instanceToDelete : instancesToDelete){
            if(instanceToDelete instanceof InstanceImpl){
                if(instances.contains(instanceToDelete)){
                    backendInstancesToRemove.add(instanceToDelete);
                    elbInstances.add(((InstanceImpl)instanceToDelete).asElbInstance());
                }
            }else{
                throw new IllegalArgumentException("Invalid instances specified.");
            }
        }
        if(!elbInstances.isEmpty())
            elb.deregisterInstancesFromLoadBalancer(name, elbInstances);
        if(!backendInstancesToRemove.isEmpty())
            this.instances.removeAll(backendInstancesToRemove);
    }

    @Override
    public void registerInstance(Instance newBackendInstance) {
        
        if(isDestroyed())
            return;

        List<Instance> newBackendInstances = new ArrayList<>();
        newBackendInstances.add(newBackendInstance);
        this.registerInstances(newBackendInstances);
    }

    @Override
    public void deregisterInstance(Instance instanceToDelete) {

        if(isDestroyed())
            return;
        
        List<Instance> instancesToDelete = new ArrayList<>();
        instancesToDelete.add(instanceToDelete);
        this.deregisterInstances(instancesToDelete);
    }

    @Override
    public void enableZones(List<Zone> zones) {
        
        if(isDestroyed())
            return;
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void disableZones(List<Zone> zones) {
        
        if(isDestroyed())
            return;
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enableZone(Zone zone) {
        
        if(isDestroyed())
            return;
        
        List<Zone> zones = new ArrayList<>();
        zones.add(zone);
        this.enableZones(zones);
    }

    @Override
    public void disableZone(Zone zone) {
        
        if(isDestroyed())
            return;
        
        List<Zone> zones = new ArrayList<>();
        zones.add(zone);
        this.disableZones(zones);
    }

    @Override
    public void delete() {
        
        if(isDestroyed())
            return;
        
        elb.deleteLoadBalancer(name);
        listeners.clear();
        instances.clear();
        zones.clear();
        subnets.clear();
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        
        if(!destroyed){
            LoadBalancerDescription description = elb.getLoadBalancerDescription(name);
            boolean exists = name.equals(description.getLoadBalancerName());
            if(started && !exists)
                //To be set only once.
                destroyed = true;
        }
        return destroyed;
    }
 
    /**
     * Returns an instance of the load balancer with the name specified as parameter.
     * If there is no load balancer by the name, this method returns null.
     * @param name unique name of the load balancer.
     * @return instance of the load balancer.
     */
    public static LoadBalancer getExistLoadBalancerByName(String name){
        
        LoadBalancer loadBalancer = null;
        AWSELB elb = (AWSELB)AWS.get(AWS.ComponentName.ELB);

        try{
            LoadBalancerDescription description = elb.getLoadBalancerDescription(name);
            if(name.equals(description.getLoadBalancerName())){
            
                List<LoadBalancerListener> listeners = new ArrayList<>();
                List<ListenerDescription> listenerDescriptions = description.getListenerDescriptions();
                for(ListenerDescription listenerDescription : listenerDescriptions)
                    listeners.add(new LoadBalancerListenerImpl.Builder().description(listenerDescription).build());
                List<Zone> zones = new ArrayList<>();
                List<String> zoneNames = description.getAvailabilityZones();
                for(String zoneName : zoneNames)
                    zones.add(new ZoneImpl.Builder().name(zoneName).build());
                
                List<Subnet> subnets = new ArrayList<>();
                List<String> subnetIds = description.getSubnets();
                for(String subnetId : subnetIds)
                    subnets.add(new SubnetImpl.Builder().id(subnetId).build());
                
                List<Instance> registeredInstances = new ArrayList<>();
                List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = description.getInstances();
                for(com.amazonaws.services.elasticloadbalancing.model.Instance elbInstance : elbInstances)
                    registeredInstances.add(new InstanceImpl.Builder().id(elbInstance.getInstanceId()).build());

                loadBalancer = new LoadBalancerImpl(name,listeners,zones,subnets,registeredInstances);
            }
        }catch(AmazonServiceException ase){
            System.out.println(ase.getMessage());
            System.out.println("Load balancer with the name [" + name + "] does not exist.");
        }
        
        return loadBalancer;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<LoadBalancerListener> getListeners() {
        return this.listeners;
    }

    @Override
    public List<Zone> getZones() {
        return this.zones;
    }

    @Override
    public List<Subnet> getSubnets() {
        return this.subnets;
    }

    @Override
    public List<Instance> getBackendInstances() {
        
        if(isDestroyed())
            return null;

        return instances.isEmpty() ? this.getBackendInstances(true) : instances;
    }

    public List<Instance> getBackendInstances(boolean reload) {
        
        if(isDestroyed())
            return null;
        
        if(reload){
            List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = elb.getLoadBalancerDescription(name).getInstances();
            List<Instance> latestInstances = new ArrayList<>();
            for(com.amazonaws.services.elasticloadbalancing.model.Instance elbInstance : elbInstances)
                latestInstances.add(new InstanceImpl.Builder().id(elbInstance.getInstanceId()).build());
            instances.clear();
            instances.addAll(latestInstances);
        }
        return instances;
        
    }

    public static class Builder{
        
        private final String name;
        private final List<LoadBalancerListener> listeners = new ArrayList<>();
        private final List<Zone> zones = new ArrayList<>();
        private final List<Subnet> subnets = new ArrayList<>();
        
        public Builder(String name){
            this.name = name;
        }
        
        public Builder listeners(List<LoadBalancerListener> newListeners){
            listeners.addAll(newListeners);
            return this;
        }
        
        public Builder listener(LoadBalancerListener newListener){
            listeners.add(newListener);
            return this;
        }
        
        public Builder defaultHttpListener(){
            
            LoadBalancerListener listener = new LoadBalancerListenerImpl.Builder().build();
            listeners.add(listener);
            return this;
        }
        
        public Builder defaultHttpsListener(String certificateId){
            
            LoadBalancerListener listener = new LoadBalancerListenerImpl.Builder()
                .instancePort(443).instanceProtocol("HTTPS")
                .servicePort(443).serviceProtocol("HTTPS")
                .certificateId(certificateId)
                .build();

            listeners.add(listener);
            return this;
        }
        
        public Builder zones(List<Zone> newZones){
            zones.addAll(newZones);
            return this;
        }
        
        public Builder zone(Zone newZone){
            zones.add(newZone);
            return this;
        }
        
        public Builder zone(String zoneName){
            zones.add(new ZoneImpl.Builder().name(zoneName).build());
            return this;
        }
        
        public Builder subnets(List<Subnet> newSubnets){
            subnets.addAll(newSubnets);
            return this;
        }
        
        public Builder subnet(Subnet newSubnet){
            subnets.add(newSubnet);
            return this;
        }
        
        public Builder subnet(String subnetId){
            subnets.add(new SubnetImpl.Builder().id(subnetId).build());
            return this;
        }
        
        public LoadBalancer build(){
            // We create new load balancer only if there does not exist one with the name.
            LoadBalancer lb = LoadBalancerImpl.getExistLoadBalancerByName(name);
            if(lb == null)
                lb = new LoadBalancerImpl(this);
            return lb;
        }
    }
    
    public List<InstanceState> getInstanceStates(){
        
        if(isDestroyed())
            return null;
        
        List<com.amazonaws.services.elasticloadbalancing.model.InstanceState> elbInstanceStates = elb.describeInstanceHealth(name).getInstanceStates();
        List<InstanceState> states  = new ArrayList<>();
        for(com.amazonaws.services.elasticloadbalancing.model.InstanceState elbInstanceState : elbInstanceStates)
            states.add( InstanceImpl.State.create(elbInstanceState));
        return states;
    }
    
    public List<InstanceState> getInstanceStates(List<Instance> instancesToCheck){

        if(isDestroyed())
            return null;
        
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = new ArrayList<>();
        for(Instance instanceToCheck : instancesToCheck){
            if(instanceToCheck instanceof InstanceImpl){
                elbInstances.add(((InstanceImpl)instanceToCheck).asElbInstance());
            }else{
                com.amazonaws.services.elasticloadbalancing.model.Instance elbInstance = new com.amazonaws.services.elasticloadbalancing.model.Instance();
                elbInstance.setInstanceId(instanceToCheck.getId());
                elbInstances.add(elbInstance);
            }
        }
        
        List<com.amazonaws.services.elasticloadbalancing.model.InstanceState> elbInstanceStates = elb.describeInstanceHealth(name, elbInstances).getInstanceStates();
        List<InstanceState> states  = new ArrayList<>();
        for(com.amazonaws.services.elasticloadbalancing.model.InstanceState elbInstanceState : elbInstanceStates)
            states.add( InstanceImpl.State.create(elbInstanceState));
        
        return states;
    }
    
    public List<InstanceState> getInstanceStatesByInstanceId(List<String> instanceIds){

        if(isDestroyed())
            return null;
        
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = new ArrayList<>();
        for(String id : instanceIds)
            elbInstances.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(id));
        
        List<com.amazonaws.services.elasticloadbalancing.model.InstanceState> elbInstanceStates = elb.describeInstanceHealth(name, elbInstances).getInstanceStates();
        List<InstanceState> states  = new ArrayList<>();
        for(com.amazonaws.services.elasticloadbalancing.model.InstanceState elbInstanceState : elbInstanceStates)
            states.add( InstanceImpl.State.create(elbInstanceState));
        
        return states;
    }

    public InstanceState getInstanceState(String backendInstanceId){

        if(isDestroyed())
            return null;
        
        List<String> backendInstanceIds = new ArrayList<>();
        backendInstanceIds.add(backendInstanceId);
        return this.getInstanceStatesByInstanceId(backendInstanceIds).get(0);
    }
    
    public InstanceState getInstanceState(Instance backendInstance){

        if(isDestroyed())
            return null;
        
        List<Instance> backendInstances = new ArrayList<>();
        backendInstances.add(backendInstance);
        return this.getInstanceStates(backendInstances).get(0);
    }

    public static DescribeLoadBalancersResult getAllLoadBalancerDescriptions(){

        AWSELB elb = (AWSELB)AWS.get(AWS.ComponentName.ELB);
        DescribeLoadBalancersResult result = elb.describeLoadBalancers();
        return result;
    }
    
    @Override
    public boolean equals(Object toBeCompared){
        if(toBeCompared instanceof LoadBalancerImpl)
            return this.getName().equals(((LoadBalancerImpl)toBeCompared).getName());
        return false;
    }

    @Override
    public int hashCode() {
        //this is wrong, but don't know how to implement this method properly.
        return 31 * this.getName().hashCode();
    }
    
    @Override
    public String toString(){
        return elb.getLoadBalancerDescription(name).toString();
    }
}
