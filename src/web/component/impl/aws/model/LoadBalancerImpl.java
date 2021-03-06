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
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import java.util.ArrayList;
import java.util.List;
import web.component.api.model.BackendState;
import web.component.api.model.HealthCheck;
import web.component.api.model.Instance;
import web.component.api.model.LoadBalancer;
import web.component.api.model.LoadBalancerListener;
import web.component.api.model.Subnet;
import web.component.api.model.Zone;
import web.component.impl.aws.AWSImpl;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author Hiroshi
 */
public class LoadBalancerImpl extends AWSModelBase implements LoadBalancer{

    private final String name;
    
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
        
        List<AvailabilityZone> availabilityZones = new ArrayList<>();
        if(builder.zones != null){
            for(Zone zone : builder.zones){
                if(zone instanceof ZoneImpl){
                    availabilityZones.add(((ZoneImpl)zone).asEc2Zone());
                }else{
                    throw new IllegalArgumentException("Invalid zones specified.");
                }
            }
        }
        
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
        
        if(!availabilityZones.isEmpty()){
            elb().createLoadBalancerWithAvailabilityZones(name, elbListeners, availabilityZones);
        }else if(!subnetIds.isEmpty()){
            elb().createLoadBalancerWithSubnets(name, elbListeners, subnetIds);
        }else{
            throw new IllegalArgumentException("Either zones or subnets must be specified.");
        }
    }

    private LoadBalancerImpl(String name){
        this.name = name;
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
            LoadBalancerDescription description = elb().getLoadBalancerDescription(name);
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

        List<Listener> elbListeners = new ArrayList<>();
        for(LoadBalancerListener newListener : newListeners){
            if(newListener instanceof LoadBalancerListenerImpl)
                elbListeners.add(((LoadBalancerListenerImpl)newListener).asElbListener());
            else
                throw new IllegalArgumentException("Invalid listeners specified.");
        }
        if(!elbListeners.isEmpty())
            elb().createLoadBalancerListeners(name,elbListeners);
    }

    @Override
    public void deleteListeners(List<LoadBalancerListener> listenersToDelete) {

        if(isDestroyed())
            return;

        List<LoadBalancerListener> listenersToRemove  = new ArrayList();
        List<Listener> elbListeners = new ArrayList<>();
        for(LoadBalancerListener listenerToDelete : listenersToDelete){
            if(listenerToDelete instanceof LoadBalancerListenerImpl)
                elbListeners.add(((LoadBalancerListenerImpl)listenerToDelete).asElbListener());
            else
                throw new IllegalArgumentException("Invalid listeners specified.");
        }
        if(!elbListeners.isEmpty())
            elb().deleteLoadBalancerListeners(name,elbListeners);
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

        List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = new ArrayList<>();
        for(Instance newInstance : newInstances){
            if(newInstance instanceof InstanceImpl)
                elbInstances.add(((InstanceImpl)newInstance).asElbInstance());
            else
                throw new IllegalArgumentException("Invalid instances specified.");
        }
        if(!elbInstances.isEmpty())
            elb().registerInstancesWithLoadBalancer(name, elbInstances);
    }

    @Override
    public void deregisterInstances(List<Instance> instancesToDelete) {

        if(isDestroyed())
            return;
        
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = new ArrayList<>();
        for(Instance instanceToDelete : instancesToDelete){
            if(instanceToDelete instanceof InstanceImpl)
                elbInstances.add(((InstanceImpl)instanceToDelete).asElbInstance());
            else
                throw new IllegalArgumentException("Invalid instances specified.");
        }
        if(!elbInstances.isEmpty())
            elb().deregisterInstancesFromLoadBalancer(name, elbInstances);
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
        
        List<String> zoneNames = new ArrayList<>();
        for(Zone zone : zones)
            if(zone instanceof ZoneImpl)
                zoneNames.add(zone.getName());
            else
                throw new IllegalArgumentException("Invalid Zone specified.");
        
        if(!zoneNames.isEmpty())
            elb().enableAvailabilityZonesForLoadBalancer(name, zoneNames);
    }

    @Override
    public void disableZones(List<Zone> zones) {
        
        if(isDestroyed())
            return;
        
        List<String> zoneNames = new ArrayList<>();
        for(Zone zone : zones)
            if(zone instanceof ZoneImpl)
                zoneNames.add(zone.getName());
            else
                throw new IllegalArgumentException("Invalid Zone specified.");
        
        if(!zoneNames.isEmpty())
            elb().disableAvailabilityZonesForLoadBalancer(name, zoneNames);
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
        
        elb().deleteLoadBalancer(name);
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        
        if(!destroyed){
            LoadBalancerDescription description = elb().getLoadBalancerDescription(name);
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

            LoadBalancerDescription description = AWSImpl.access().elb().getLoadBalancerDescription(name);
            if(name.equals(description.getLoadBalancerName()))
                loadBalancer = new LoadBalancerImpl(name);

        }catch(AmazonServiceException ase){
            System.out.println(ase.getMessage());
            System.out.println("Load balancer with the name [" + name + "] does not exist.");
        }
        
        return loadBalancer;
    }

    public static List<LoadBalancer> getExistLoadBalancers(){

        List<LoadBalancerDescription> descs 
                = getAllLoadBalancerDescriptions().getLoadBalancerDescriptions();

        return descs.stream()
                    .map(LoadBalancerDescription::getLoadBalancerName)
                    .map(LoadBalancerImpl::getExistLoadBalancerByName)
                    .collect(toList());
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<LoadBalancerListener> getListeners() {
        
        if(isDestroyed())
            return null;
        
        return elb().getLoadBalancerDescription(name).getListenerDescriptions().stream()
                .map(desc -> new LoadBalancerListenerImpl.Builder().description(desc).build())
                .collect(toList());
    }

    @Override
    public List<Zone> getZones() {
        
        if(isDestroyed())
            return null;
        
        return elb().getLoadBalancerDescription(name).getAvailabilityZones().stream()
                .map(AWSResourceFactory::getZone)
                .collect(toList());
    }

    @Override
    public List<Subnet> getSubnets() {
        
        if(isDestroyed())
            return null;
        
        return elb().getLoadBalancerDescription(name).getSubnets().stream()
                .map(AWSResourceFactory::getSubnet)
                .collect(toList());
    }

    @Override
    public List<Instance> getBackendInstances() {
        
        if(isDestroyed())
            return null;
        
        return elb().getLoadBalancerDescription(name).getInstances().stream()
                .map(com.amazonaws.services.elasticloadbalancing.model.Instance::getInstanceId)
                .map(AWSResourceFactory::getInstance)
                .collect(toList());
    }

   /*
    * 
    */
    @Override
    public int compareTo(LoadBalancer o) {
        
        if(o == null)
            throw new NullPointerException();
        
        return this.getName().compareTo(o.getName());
    }

    static class Builder{
        
        private final String name;
        private final List<LoadBalancerListener> listeners = new ArrayList<>();
        private final List<Zone> zones = new ArrayList<>();
        private final List<Subnet> subnets = new ArrayList<>();
        
        public Builder(String name){
            this.name = name;
        }
        
        public Builder listeners(List<LoadBalancerListener> newListeners){
            this.listeners.addAll(newListeners);
            return this;
        }
        
        public Builder listener(LoadBalancerListener newListener){
            this.listeners.add(newListener);
            return this;
        }
        
        public Builder defaultHttpListener(){
            
            LoadBalancerListener listener = new LoadBalancerListenerImpl.Builder().build();
            this.listeners.add(listener);
            return this;
        }
        
        public Builder defaultHttpsListener(String certificateId){
            
            LoadBalancerListener listener = new LoadBalancerListenerImpl.Builder()
                .instancePort(443).instanceProtocol("HTTPS")
                .servicePort(443).serviceProtocol("HTTPS")
                .certificateId(certificateId)
                .build();

            this.listeners.add(listener);
            return this;
        }
        
        public Builder zones(List<Zone> newZones){
            this.zones.addAll(newZones);
            return this;
        }
        
        public Builder zone(Zone newZone){
            this.zones.add(newZone);
            return this;
        }
        
        public Builder zone(String zoneName){
            this.zones.add(new ZoneImpl.Builder().name(zoneName).build());
            return this;
        }
        
        public Builder subnets(List<Subnet> newSubnets){
            this.subnets.addAll(newSubnets);
            return this;
        }
        
        public Builder subnet(Subnet newSubnet){
            this.subnets.add(newSubnet);
            return this;
        }
        
        public Builder subnet(String subnetId){
            this.subnets.add(new SubnetImpl.Builder().id(subnetId).get());
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
    
    @Override
    public List<BackendState> getInstanceStates(){
        
        if(isDestroyed())
            return null;
        
        return elb().describeInstanceHealth(name).getInstanceStates().stream()
                .map(InstanceImpl.BackendStateImpl::create)
                .collect(toList());
    }
    
    @Override
    public List<BackendState> getInstanceStates(List<Instance> instancesToCheck){

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
        
        return elb().describeInstanceHealth(name, elbInstances)
                        .getInstanceStates().stream()
                        .map(InstanceImpl.BackendStateImpl::create)
                        .collect(toList());
    }
    
    @Override
    public List<BackendState> getInstanceStatesByInstanceId(List<String> instanceIds){

        if(isDestroyed())
            return null;
        
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = 
                instanceIds.stream()
                .map(com.amazonaws.services.elasticloadbalancing.model.Instance::new)
                .collect(toList());
        
        List<com.amazonaws.services.elasticloadbalancing.model.InstanceState> elbInstanceStates = elb().describeInstanceHealth(name, elbInstances).getInstanceStates();
        
        return elbInstanceStates.stream()
                .map(InstanceImpl.BackendStateImpl::create)
                .collect(toList());
    }

    @Override
    public BackendState getInstanceState(String backendInstanceId){

        if(isDestroyed())
            return null;
        
        return getInstanceStatesByInstanceId(singletonList(backendInstanceId)).get(0);
    }
    
    @Override
    public BackendState getInstanceState(Instance backendInstance){

        if(isDestroyed())
            return null;
        
        List<Instance> backendInstances = new ArrayList<>();
        backendInstances.add(backendInstance);
        return this.getInstanceStates(backendInstances).get(0);
    }

    private static DescribeLoadBalancersResult getAllLoadBalancerDescriptions(){
        return AWSImpl.access().elb().describeLoadBalancers();
    }

    @Override
    public HealthCheck configureHealthCheck(HealthCheck healthCheck){
        
        if(!(healthCheck instanceof HealthCheckImpl))
            throw new IllegalArgumentException("invalid health check specified.");
        
        com.amazonaws.services.elasticloadbalancing.model.HealthCheck elbHealthCheck = 
            elb().configureHealthCheck(name, ((HealthCheckImpl)healthCheck).asElbHealthCheck()).getHealthCheck();
        
        return new HealthCheckImpl.Builder().build(elbHealthCheck);
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
        return elb().getLoadBalancerDescription(name).toString();
    }
}
