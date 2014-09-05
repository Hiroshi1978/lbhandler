    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.awselb.model;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.InstanceState;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.ListenerDescription;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import java.util.ArrayList;
import java.util.List;
import web.component.api.model.BackendInstance;
import web.component.api.model.BackendInstanceState;
import web.component.api.model.LoadBalancer;
import web.component.api.model.LoadBalancerListener;
import web.component.api.model.Subnet;
import web.component.api.model.Zone;
import web.component.impl.awselb.AWSElasticLoadBalancing;

/**
 *
 * @author Hiroshi
 */
public class LoadBalancerImpl implements LoadBalancer{

    private static final AWSElasticLoadBalancing elb = AWSElasticLoadBalancing.create();
    private final String name;
    private final List<LoadBalancerListener> listeners = new ArrayList<>();
    private final List<Zone> zones = new ArrayList<>();
    private final List<Subnet> subnets = new ArrayList<>();
    private final List<BackendInstance> backendInstances = new ArrayList<>();
    
    private volatile boolean started;
    private volatile boolean destroyed;
    
    private LoadBalancerImpl(Builder builder){
        
        this.name = builder.name;

        List<Listener> elbListeners = new ArrayList<>();
        for(LoadBalancerListener listener : builder.listeners){
            if(listener instanceof LoadBalancerListenerImpl){
                elbListeners.add((Listener) listener);
            }else{
                throw new IllegalArgumentException("Invalid listeners specified.");
            }
        }
        this.listeners.addAll(builder.listeners);
        
        List<AvailabilityZone> availabilityZones = new ArrayList<>();
        if(builder.zones != null){
            for(Zone zone : builder.zones){
                if(zone instanceof ZoneImpl){
                    availabilityZones.add((AvailabilityZone)zone);
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

    private LoadBalancerImpl(String name, List<LoadBalancerListener> listeners) {
        
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(listeners == null || listeners.isEmpty())
            throw new IllegalArgumentException("Load balancer listeners not specified.");
        
        this.name = name;
        this.listeners.addAll(listeners);
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
     * @param listeners List of the listeners to be added to this load balancer.
     */
    @Override
    public void createListeners(List<LoadBalancerListener> listeners) {
        List<Listener> elbListeners = new ArrayList<>();
        for(LoadBalancerListener listener : listeners){
            if(listener instanceof LoadBalancerListenerImpl){
                elbListeners.add((Listener)listener);
            }else{
                throw new IllegalArgumentException("Invalid listeners specified.");
            }
        }
        elb.createLoadBalancerListeners(name,elbListeners);
    }

    @Override
    public void deleteListeners(List<LoadBalancerListener> listeners) {
        List<Listener> elbListeners = new ArrayList<>();
        for(LoadBalancerListener listener : listeners){
            if(listener instanceof LoadBalancerListenerImpl){
                elbListeners.add((Listener)listener);
            }else{
                throw new IllegalArgumentException("Invalid listeners specified.");
            }
        }
        elb.deleteLoadBalancerListeners(name,elbListeners);
    }

    @Override
    public void createListener(LoadBalancerListener listener) {
        List<LoadBalancerListener> listeners = new ArrayList<>();
        listeners.add(listener);
        this.createListeners(listeners);
    }

    @Override
    public void deleteListener(LoadBalancerListener listener) {
        List<LoadBalancerListener> listeners = new ArrayList<>();
        listeners.add(listener);
        this.deleteListeners(listeners);
    }

    @Override
    public void registerInstances(List<BackendInstance> newBackendInstances) {
        List<Instance> elbInstances = new ArrayList<>();
        for(BackendInstance newBackendInstance : newBackendInstances){
            if(newBackendInstance instanceof BackendInstanceImpl){
                if(!backendInstances.contains(newBackendInstance)){
                    backendInstances.add(newBackendInstance);
                    elbInstances.add((Instance)newBackendInstance);
                }
            }else{
                throw new IllegalArgumentException("Invalid instances specified.");
            }
        }
        elb.registerInstancesWithLoadBalancer(name, elbInstances);
    }

    @Override
    public void deregisterInstances(List<BackendInstance> instancesToDelete) {
        List<Instance> elbInstances = new ArrayList<>();
        for(BackendInstance instanceToDelete : instancesToDelete){
            if(instanceToDelete instanceof BackendInstanceImpl){
                elbInstances.add((Instance)instanceToDelete);
            }else{
                throw new IllegalArgumentException("Invalid instances specified.");
            }
        }
        elb.deregisterInstancesFromLoadBalancer(name, elbInstances);
        backendInstances.removeAll(instancesToDelete);
    }

    @Override
    public void registerInstance(BackendInstance newBackendInstance) {
        List<BackendInstance> newBackendInstances = new ArrayList<>();
        newBackendInstances.add(newBackendInstance);
        this.registerInstances(newBackendInstances);
    }

    @Override
    public void deregisterInstance(BackendInstance instanceToDelete) {
        List<BackendInstance> instancesToDelete = new ArrayList<>();
        instancesToDelete.add(instanceToDelete);
        this.deregisterInstances(instancesToDelete);
    }

    @Override
    public void enableZones(List<Zone> zones) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void disableZones(List<Zone> zones) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enableZone(Zone zone) {
        List<Zone> zones = new ArrayList<>();
        zones.add(zone);
        this.enableZones(zones);
    }

    @Override
    public void disableZone(Zone zone) {
        List<Zone> zones = new ArrayList<>();
        zones.add(zone);
        this.disableZones(zones);
    }

    @Override
    public void delete() {
        elb.deleteLoadBalancer(name);
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
        
        try{
            LoadBalancerDescription description = elb.getLoadBalancerDescription(name);
            if(name.equals(description.getLoadBalancerName())){
            
                List<LoadBalancerListener> listeners = new ArrayList<>();
                List<ListenerDescription> listenerDescriptions = description.getListenerDescriptions();
                for(ListenerDescription listenerDescription : listenerDescriptions){
                    listeners.add(new LoadBalancerListenerImpl(listenerDescription));
                }
                loadBalancer = new LoadBalancerImpl(name,listeners);
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
    public List<BackendInstance> getBackendInstances() {
        return backendInstances.isEmpty() ? this.getBackendInstances(true) : backendInstances;
    }

    public List<BackendInstance> getBackendInstances(boolean reload) {
        if(reload){
            List<Instance> elbInstances = elb.getLoadBalancerDescription(name).getInstances();
            List<BackendInstance> latestInstances = new ArrayList<>();
            for(Instance elbInstance : elbInstances)
                latestInstances.add(BackendInstanceImpl.create(this, elbInstance.getInstanceId()));
            backendInstances.clear();
            backendInstances.addAll(latestInstances);
        }
        return backendInstances;
    }

    public static class Builder{
        
        private final String name;
        private List<LoadBalancerListener> listeners;
        private List<Zone> zones;
        private List<Subnet> subnets;
        
        public Builder(String name){
            this.name = name;
        }
        
        public Builder listeners(List<LoadBalancerListener> listeners){
            this.listeners = listeners;
            return this;
        }
        
        public Builder listener(LoadBalancerListener listener){
            List<LoadBalancerListener> listeners = new ArrayList<>();
            listeners.add(listener);
            return this.listeners(listeners);
        }
        
        public Builder defaultHttpListener(){
            LoadBalancerListenerImpl listener = new LoadBalancerListenerImpl();
            listener.setInstancePort(80);
            listener.setInstanceProtocol("HTTP");
            listener.setServicePort(80);
            listener.setServiceProtocol("HTTP");
            return this.listener(listener);
        }
        
        public Builder defaultHttpsListener(String serverCertificateId){
            LoadBalancerListenerImpl listener = new LoadBalancerListenerImpl();
            listener.setInstancePort(443);
            listener.setInstanceProtocol("HTTPS");
            listener.setServicePort(443);
            listener.setServiceProtocol("HTTPS");
            listener.setServerCertificate(serverCertificateId);
            return this.listener(listener);
        }
        
        public Builder zones(List<Zone> zones){
            this.zones = zones;
            return this;
        }
        
        public Builder zone(Zone zone){
            List<Zone> zones = new ArrayList<>();
            zones.add(zone);
            return this.zones(zones);
        }
        
        public Builder zone(String zoneName){
            Zone zone = ZoneImpl.create(zoneName);
            return this.zone(zone);
        }
        
        public Builder subnets(List<Subnet> subnets){
            this.subnets = subnets;
            return this;
        }
        
        public Builder subnet(Subnet subnet){
            List<Subnet> subnets = new ArrayList<>();
            subnets.add(subnet);
            return this.subnets(subnets);
        }
        
        public Builder subnet(String subnetId){
            Subnet subnet = SubnetImpl.create(subnetId);
            return this.subnet(subnet);
        }
        
        public LoadBalancer build(){
            // We create new load balancer only if there does not exist one with the name.
            LoadBalancer lb = LoadBalancerImpl.getExistLoadBalancerByName(name);
            if(lb == null)
                lb = new LoadBalancerImpl(this);
            return lb;
        }
    }
    
    public List<BackendInstanceState> getInstanceStates(){
        List<InstanceState> elbInstanceStates = elb.describeInstanceHealth(name).getInstanceStates();
        List<BackendInstanceState> states  = new ArrayList<>();
        for(InstanceState elbInstanceState : elbInstanceStates)
            states.add( BackendInstanceImpl.State.create(elbInstanceState));
        return states;
    }
    
    public List<BackendInstanceState> getInstanceStates(List<BackendInstance> backendInstances){

        List<Instance> elbInstances = new ArrayList<>();
        for(BackendInstance backendInstance : backendInstances){
            if(backendInstance instanceof BackendInstanceImpl){
                elbInstances.add((Instance)backendInstance);
            }else{
                Instance elbInstance = new Instance();
                elbInstance.setInstanceId(backendInstance.getId());
                elbInstances.add(elbInstance);
            }
        }
        
        List<InstanceState> elbInstanceStates = elb.describeInstanceHealth(name, elbInstances).getInstanceStates();
        List<BackendInstanceState> states  = new ArrayList<>();
        for(InstanceState elbInstanceState : elbInstanceStates)
            states.add( BackendInstanceImpl.State.create(elbInstanceState));
        
        return states;
    }
    
    public List<BackendInstanceState> getInstanceStatesByInstanceId(List<String> backendInstanceIds){

        List<Instance> elbInstances = new ArrayList<>();
        for(String id : backendInstanceIds)
            elbInstances.add(new Instance(id));
        
        List<InstanceState> elbInstanceStates = elb.describeInstanceHealth(name, elbInstances).getInstanceStates();
        List<BackendInstanceState> states  = new ArrayList<>();
        for(InstanceState elbInstanceState : elbInstanceStates)
            states.add( BackendInstanceImpl.State.create(elbInstanceState));
        
        return states;
    }

    public BackendInstanceState getInstanceState(String backendInstanceId){
        List<String> backendInstanceIds = new ArrayList<>();
        backendInstanceIds.add(backendInstanceId);
        return this.getInstanceStatesByInstanceId(backendInstanceIds).get(0);
    }
    
    public BackendInstanceState getInstanceState(BackendInstance backendInstance){
        List<BackendInstance> backendInstances = new ArrayList<>();
        backendInstances.add(backendInstance);
        return this.getInstanceStates(backendInstances).get(0);
    }

    public static DescribeLoadBalancersResult getAllLoadBalancerDescriptions(){
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
    
}
