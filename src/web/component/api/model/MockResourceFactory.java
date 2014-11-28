/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author ksc
 */
public class MockResourceFactory extends ResourceFactory{

    static int instanceIdSeed = 0;
    static final List<Zone> zones = new ArrayList<Zone>();
    static final List<Instance> instances = new ArrayList<Instance>();
    static final List<LoadBalancer> lbs = new ArrayList<LoadBalancer>();
    static final List<VPC> vpcs = new ArrayList<VPC>();
    static final List<Subnet> subnets = new ArrayList<Subnet>();
    
    static final ExecutorService pool = Executors.newFixedThreadPool(5);
    
    static String generateInstanceId(){
        String id = "i-f9e2";
        if (++instanceIdSeed < 10)
            id = id + "00" + instanceIdSeed; 
        else if (instanceIdSeed < 100)
            id = id + "0" + instanceIdSeed; 
        else
            id = id + instanceIdSeed;
        return id;
    }
    
    MockResourceFactory(){
        
        //create zone
        Zone za = new ZoneMock("ap-northeast-1a");
        Zone zb = new ZoneMock("ap-northeast-1b");
        //Zone zc = new ZoneMock("ap-northeast-1c");
        //Zone zd = new ZoneMock("ap-northeast-1d");
        zones.add(za);
        zones.add(zb);
        //zones.add(zc);
        //zones.add(zd);
        
        //vpc
        VPC v = new MockVpc();
        vpcs.add(v);
        
        //subnets
        Subnet s = new MockSubnet();
        subnets.add(s);
        
        //create instance
        for(int i=0; i< (new Random().nextInt(3) + 9);i++){
            String id = generateInstanceId();
            Instance ins = new InstanceMock(id);
            instances.add(ins);
        }
        
        //create load balancer
        LoadBalancer lb = new MockLoadBalancer("testlb-0001");
        List<Instance> toRegister = new ArrayList<Instance>();
        Random rnd = new Random(2);
        for(int i=0;i<instances.size(); i++){
            Instance ins = instances.get(i);
            if(( rnd.nextInt() % 2 ) == 0)
                toRegister.add(ins);
        }
        lb.registerInstances(toRegister);
        lb.enableZone(za);
        lb.enableZone(zb);
        //lb.enableZone(zc);
        lbs.add(lb);
        Runnable r = new Runnable(){
            public void run(){
                while(true){
                    try{
                         Thread.sleep(1000);
                    }catch(Exception e){
                
                    }
                    for(int i=instances.size()-1;i>=0;i--){
                        Instance ins = instances.get(i);
                        cycleInstances(ins);
                    }
                }
            }  
        };
        pool.submit(r);
    }
    
    static void cycleInstances(Instance old){
        if(old.getStateName().equals("terminated")){
            InstanceMock newOne = new InstanceMock(generateInstanceId());
            instances.remove(old);
            System.out.println("cloud removed one old vm. " + old.getId());
            instances.add(newOne);
            System.out.println("cloud published one new vm. " + newOne.getId());
            System.out.println("instances count : " + instances.size());
        }
    }
    
    @Override
    public List<Instance> getInstances() {
        
        Collections.sort(instances);
        return instances;
    }

    @Override
    public Instance getInstance(String id) {
        for(Instance i : instances)
            if(i.getId().equals(id))
                return i;
        return null;
    }

    @Override
    public List<Zone> getZones() {
        return zones;
    }

    @Override
    public Zone getZone(String name) {
        for(Zone z : zones){
            if(z.getName().equals(name))
                return z;
        }
        return null;
    }

    @Override
    public List<VPC> getVPCs() {
        return vpcs;
    }

    @Override
    public VPC getVPC(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Subnet> getSubnets() {
        return subnets;
    }

    @Override
    public Subnet getSubnet(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<LoadBalancer> getLoadBalancers() {
        return lbs;
    }

    @Override
    public LoadBalancer getLoadBalancer(String name) {
        for(LoadBalancer lb : lbs)
            if(lb.getName().equals(name))
                return lb;
        return null;
    }

    @Override
    public List<AutoScalingGroup> getAutoScalingGroups() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AutoScalingGroup getAutoScalingGroup(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<LaunchConfiguration> getLaunchConfigurations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LaunchConfiguration getLaunchConfiguration(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    static class ZoneMock implements Zone{
        
        String name;
        String region;
        String state;
        
        ZoneMock(String name){
            this.name = name;
            this.region = "ap-northeast-1";
            this.state = "available";
        }
        
        @Override
        public List<String> getMessages() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getRegionName() {
            return region;
        }

        @Override
        public String getState() {
            return state;
        }

        @Override
        public int compareTo(Zone o) {
            return this.getName().compareTo(o.getName());
        }
        
    }
    
    static class InstanceMock implements Instance{
        
        static int ipcounter = 1;
        static Random lifeCycleSeed = new Random(new Date().getTime());
        String id;
        int num ;
        String state = "pending";
        String zone;
        String publicip;
        Date lastStateDate = new Date();
        int lifeCycleUnit = lifeCycleSeed.nextInt(10)+ 1;
        
        InstanceMock(String id){
            this.id = id;
            this.num = ipcounter++;
            this.publicip = "54.64.192." + num;
            this.zone = "ap-northeast-1" + ( (ipcounter % 2) == 0 ? "a" : "b" );
        }
        
        @Override
        public LoadBalancer getLoadBalancer() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<LoadBalancer> getLoadBalancers() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getPlacement() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void registerWith(LoadBalancer lb) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void deregisterFrom(LoadBalancer lb) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public InstanceState getInstanceState() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public BackendState getBackendStateOf(LoadBalancer lb) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Integer getStateCode() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getStateName() {
            
            long age = new Date().getTime() - lastStateDate.getTime();
            long transitionUnit = lifeCycleUnit * 1000;
            
            if(state.equals("pending")){
                if(age > transitionUnit){
                    state = "running";
                    lastStateDate = new Date();
                }
            }else if(state.equals("running")){
                if(age > transitionUnit * 5){
                    state = "stopping";
                    lastStateDate = new Date();
                }
            }else if(state.equals("stopping")){
                if(age > transitionUnit * 3){
                    state = "stopped";
                    lastStateDate = new Date();
                }
            }else if(state.equals("stopped")){
                if(age > transitionUnit * 3){
                    state = "shutting-down";
                    lastStateDate = new Date();
                    if(age % 2 == 0){
                        state = "running";
                        lastStateDate = new Date();
                    }
                }
            }else if(state.equals("shutting-down")){
                if(age > transitionUnit * 3){
                    state = "terminated";
                    lastStateDate = new Date();
                }
            }
            return state;
        }

        @Override
        public String getStateTransitionReason() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getPublicIpAddress() {
            return publicip;
        }

        @Override
        public String getSubnetId() {
            return "xxxx-xxxx"; //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getVpcId() {
            return "xxxx-xxxx"; //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getImageId() {
            return "ami-xxxxx"; //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getInstanceType() {
            return "xxxx-xxxx"; //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getLifeCycle() {
            return "xxxx-xxxx"; //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getZoneName() {
            return zone;
        }

        @Override
        public void start() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void stop() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void terminate() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isStarted() {
            return state.equals("running");
        }

        @Override
        public boolean isStopped() {
            return state.equals("stopped");
        }

        @Override
        public boolean isTerminated() {
            return state.equals("terminated");
        }

        @Override
        public int compareTo(Instance o) {
            return this.getId().compareTo(o.getId());
        }
        
    }
    
    
    static class MockLoadBalancer implements LoadBalancer{

        List<Instance> backendInstes = new ArrayList<Instance>();
        List<Zone> zones = new ArrayList<Zone>();
        String name;
        
        @Override
        public boolean isStarted() {
            return true;
        }
        public MockLoadBalancer(String name){
            this.name = name;
        }
        
        @Override
        public void createListeners(List<LoadBalancerListener> listeners) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void deleteListeners(List<LoadBalancerListener> listeners) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void createListener(LoadBalancerListener listener) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void deleteListener(LoadBalancerListener listener) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void registerInstances(List<Instance> instances) {
            this.backendInstes.addAll(instances);
        }

        @Override
        public void deregisterInstances(List<Instance> instances) {
            this.backendInstes.removeAll(instances);
        }

        @Override
        public void registerInstance(Instance instance) {
            this.backendInstes.add(instance);
        }

        @Override
        public void deregisterInstance(Instance instance) {
            this.backendInstes.remove(instance);
        }

        @Override
        public void enableZones(List<Zone> zones) {
            this.zones.addAll(zones);
        }

        @Override
        public void enableZone(Zone zone) {
            this.zones.add(zone);
        }

        @Override
        public void disableZones(List<Zone> zones) {
            this.zones.removeAll(zones);
        }

        @Override
        public void disableZone(Zone zone) {
            this.zones.remove(zone);
        }

        @Override
        public HealthCheck configureHealthCheck(HealthCheck healthCheck) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void delete() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<BackendState> getInstanceStates() {
            List<BackendState> states = new ArrayList<BackendState>();
            for(Instance ins : backendInstes)
                states.add(new MockBackendState(ins));
            return states;
        }

        @Override
        public List<BackendState> getInstanceStates(List<Instance> backendInstances) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<BackendState> getInstanceStatesByInstanceId(List<String> backendInstanceIds) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public BackendState getInstanceState(Instance backendInstance) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public BackendState getInstanceState(String backendInstanceId) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isDestroyed() {
            return false;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<LoadBalancerListener> getListeners() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<Zone> getZones() {
            return this.zones;
        }

        @Override
        public List<Subnet> getSubnets() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<Instance> getBackendInstances() {
            return this.backendInstes;
        }

        @Override
        public int compareTo(LoadBalancer o) {
            return this.name.compareTo(o.getName());
        }
        
    }
    
    static class MockVpc implements VPC{

        @Override
        public String getCidrBlock() {
            return "10.1.0.0/16";
        }

        @Override
        public String getDhcpOptionsId() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getInstanceTenancy() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Boolean getIsDefault() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getState() {
            return "available";
        }

        @Override
        public String getId() {
            return "xxx-xxx";
        }

        @Override
        public void delete() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int compareTo(VPC o) {
            return this.getId().compareTo(o.getId());
        }
        
    }
    
    static class MockSubnet implements Subnet{

        @Override
        public String getId() {
            return "yyy-yyy";
        }

        @Override
        public String getZone() {
            return "ap-northeast-1a";
        }

        @Override
        public Integer getAvailableIpAddressCount() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getCidrBlock() {
            return "10.1.1.0/24";
        }

        @Override
        public boolean getDefaultForAz() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean getMapPublicIpOnLaunch() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getState() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getVpcId() {
            return vpcs.get(0).getId();
        }

        @Override
        public void delete() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int compareTo(Subnet o) {
            return this.getId().compareTo(o.getId());
        }
    }
    
    static class MockBackendState implements BackendState{
        
        Instance b;
        MockBackendState(Instance ins){
            this.b = ins;
        }
        
        @Override
        public String getInstanceId() {
            return b.getId();
        }

        @Override
        public String getDescription() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getReasonCode() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getState() {
            String state = "OutOfService";
            if("running".equals(b.getStateName()))
                state = "InService";
            return state;
        }

        @Override
        public int compareTo(BackendState o) {
            return this.getInstanceId().compareTo(o.getInstanceId());
        }
        
    }
    

    
}
