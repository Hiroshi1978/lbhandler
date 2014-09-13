/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.elb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.AttachLoadBalancerToSubnetsRequest;
import com.amazonaws.services.elasticloadbalancing.model.AttachLoadBalancerToSubnetsResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeInstanceHealthRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeInstanceHealthResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.DetachLoadBalancerFromSubnetsRequest;
import com.amazonaws.services.elasticloadbalancing.model.DetachLoadBalancerFromSubnetsResult;
import com.amazonaws.services.elasticloadbalancing.model.DisableAvailabilityZonesForLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DisableAvailabilityZonesForLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.EnableAvailabilityZonesForLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.EnableAvailabilityZonesForLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import web.component.impl.CloudBlock;

/**
 *
 * @author Hiroshi
 */
public class AWSELB implements CloudBlock{
    
    private final AmazonElasticLoadBalancing awsHttpClient;
    
    private AWSELB(){
        
        Properties conf = new Properties();
        try {
            conf.load(this.getClass().getResourceAsStream("../credentials.txt"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        String awsKey = conf.getProperty("aws.key");
        String secretKey = conf.getProperty("aws.secret");
        AWSCredentials credentials = new BasicAWSCredentials(awsKey,secretKey);
    	
        AmazonElasticLoadBalancingClient initialClient = new AmazonElasticLoadBalancingClient(credentials);
        setUpHttpClient(initialClient);
        awsHttpClient = initialClient;
    }
    
    private void setUpHttpClient(AmazonElasticLoadBalancingClient awsELBClient){

        Properties conf = new Properties();
        try {
            conf.load(this.getClass().getResourceAsStream("./httpclient_config.txt"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        String endpoint = conf.getProperty("endpoint");
        String serviceName = conf.getProperty("servicename");
        String regionName = conf.getProperty("region");
        
        awsELBClient.setEndpoint(endpoint, serviceName, regionName);
        awsELBClient.setServiceNameIntern(serviceName);
    }
    
    public static AWSELB get(){
        return new AWSELB();
    }

    public DescribeLoadBalancersResult describeLoadBalancers(){
        return awsHttpClient.describeLoadBalancers();
    }
    
    public DescribeLoadBalancersResult describeLoadBalancers(DescribeLoadBalancersRequest request){
        
        if(request == null)
            throw new IllegalArgumentException("Invalid Request.");
        
        return awsHttpClient.describeLoadBalancers(request);
    }

    public DescribeLoadBalancersResult describeLoadBalancers(List<String> loadBalancerNames){
        
        if(loadBalancerNames == null || loadBalancerNames.isEmpty())
            throw new IllegalArgumentException("Load Balancer Names not specified.");
            
        DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest();
        request.setLoadBalancerNames(loadBalancerNames);
        
        return awsHttpClient.describeLoadBalancers(request);
    }

    public DescribeLoadBalancersResult describeLoadBalancers(String loadBalancerName){
    
        List<String> loadBalancerNames = new ArrayList<>();
        loadBalancerNames.add(loadBalancerName);
        return this.describeLoadBalancers(loadBalancerNames);
    }

    public CreateLoadBalancerResult createLoadBalancer(CreateLoadBalancerRequest request){ 

        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().equals("")) 
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getListeners() == null || request.getListeners().isEmpty()) 
            throw new IllegalArgumentException("Listeners not specified."); 
        if(request.getAvailabilityZones() == null || request.getAvailabilityZones().isEmpty()) 
            throw new IllegalArgumentException("AvailavilityZones not specified."); 
           
        return awsHttpClient.createLoadBalancer(request); 
    } 
   
    public CreateLoadBalancerResult createLoadBalancer(String loadBalancerName){ 

        if(loadBalancerName == null || loadBalancerName.equals("")) 
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
       
        CreateLoadBalancerRequest request = new CreateLoadBalancerRequest(loadBalancerName); 
       
        List<Listener> listeners = new ArrayList<>(); 
        listeners.add(getDefaultHttpListener()); 
        request.setListeners(listeners); 
       
        return awsHttpClient.createLoadBalancer(request); 
    } 
   
    public CreateLoadBalancerResult createLoadBalancer(String loadBalancerName, String certificateId){ 

        if(loadBalancerName == null || loadBalancerName.equals("")) 
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(certificateId == null || certificateId.equals("")) 
            throw new IllegalArgumentException("Certificate ID not specified."); 

        CreateLoadBalancerRequest request = new CreateLoadBalancerRequest(loadBalancerName); 
       
        List<Listener> listeners = new ArrayList<>(); 
        listeners.add(getDefaultHttpsListener(certificateId)); 
        request.setListeners(listeners); 
       
        return awsHttpClient.createLoadBalancer(request); 
    } 

    public void deleteLoadBalancer(String loadBalancerName){
        DeleteLoadBalancerRequest request = new DeleteLoadBalancerRequest(loadBalancerName);
        awsHttpClient.deleteLoadBalancer(request);
    }

    public int deleteAllLoadBalancers(){
        DescribeLoadBalancersResult result = awsHttpClient.describeLoadBalancers();
        List<LoadBalancerDescription> descriptions = result.getLoadBalancerDescriptions();
        
        int deleteCount = 0;
        
        for(LoadBalancerDescription description : descriptions){
            String loadBalancerName = description.getLoadBalancerName();
            DeleteLoadBalancerRequest request = new DeleteLoadBalancerRequest(loadBalancerName);
            try{
                awsHttpClient.deleteLoadBalancer(request);
            }catch(Exception e){
                System.out.println("Delete Load Balancer failed. [" + loadBalancerName + "]");
            }
            deleteCount++;
        }
        return deleteCount;
    }

        public RegisterInstancesWithLoadBalancerResult registerInstancesWithLoadBalancer(RegisterInstancesWithLoadBalancerRequest request){
    
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified.");
        if(request.getInstances() == null || request.getInstances().isEmpty())
            throw new IllegalArgumentException("Instances not specified.");
        
        return awsHttpClient.registerInstancesWithLoadBalancer(request);
    }
    
    public RegisterInstancesWithLoadBalancerResult registerInstancesWithLoadBalancer(String loadBalancerName, List<Instance> instances){
    
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified.");
        if(instances == null || instances.isEmpty())
            throw new IllegalArgumentException("Instances not specified.");

        RegisterInstancesWithLoadBalancerRequest request = new RegisterInstancesWithLoadBalancerRequest(loadBalancerName, instances);
        return awsHttpClient.registerInstancesWithLoadBalancer(request);
    }

    public DeregisterInstancesFromLoadBalancerResult deregisterInstancesFromLoadBalancer(DeregisterInstancesFromLoadBalancerRequest request){
 
        if(request == null)
            throw new IllegalArgumentException("Invalid Request.");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified.");
        if(request.getInstances() == null || request.getInstances().isEmpty())
            throw new IllegalArgumentException("Instances not specified.");
 
        return awsHttpClient.deregisterInstancesFromLoadBalancer(request);
    }

    public DeregisterInstancesFromLoadBalancerResult deregisterInstancesFromLoadBalancer(String loadBalancerName, List<Instance> instances){

        if(loadBalancerName == null || loadBalancerName.isEmpty())
           throw new IllegalArgumentException("Load Balancer Name not specified.");
        if(instances == null || instances.isEmpty())
           throw new IllegalArgumentException("Instances not specified.");

        DeregisterInstancesFromLoadBalancerRequest request = new DeregisterInstancesFromLoadBalancerRequest(loadBalancerName,instances);
        return awsHttpClient.deregisterInstancesFromLoadBalancer(request);
    }

    public AttachLoadBalancerToSubnetsResult attachLoadBalancerToSubnets(AttachLoadBalancerToSubnetsRequest request){

        if(request == null)
            throw new IllegalArgumentException("Invalid Request.");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getSubnets() == null || request.getSubnets().isEmpty())
            throw new IllegalArgumentException("Subnets not specified.");
 
        return awsHttpClient.attachLoadBalancerToSubnets(request);
    }

    public AttachLoadBalancerToSubnetsResult attachLoadBalancerToSubnets(String loadBalancerName, List<String> subnetIds){

        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(subnetIds == null || subnetIds.isEmpty())
            throw new IllegalArgumentException("Subnets not specified.");

        AttachLoadBalancerToSubnetsRequest request = new AttachLoadBalancerToSubnetsRequest();
        request.setLoadBalancerName(loadBalancerName);
        request.setSubnets(subnetIds);
        
        return awsHttpClient.attachLoadBalancerToSubnets(request);
    }
    
   public void deleteLoadBalancerListeners(DeleteLoadBalancerListenersRequest request){
       awsHttpClient.deleteLoadBalancerListeners(request);
   }
   public void deleteLoadBalancerListeners(String loadBalancerName, List<Listener> listeners){
       
       DeleteLoadBalancerListenersRequest request = new DeleteLoadBalancerListenersRequest();
       request.setLoadBalancerName(loadBalancerName);
       List<Integer> ports = new ArrayList<>();
       for(Listener listener : listeners)
           ports.add(listener.getLoadBalancerPort());
       request.setLoadBalancerPorts(ports);
       
       deleteLoadBalancerListeners(request);
   }

   
    public CreateLoadBalancerResult createLoadBalancer(String loadBalancerName, List<Listener> listeners){

       if(loadBalancerName == null || loadBalancerName.equals(""))
           throw new IllegalArgumentException("Load Balancer Name not specified.");
       if(listeners == null || listeners.isEmpty())
           throw new IllegalArgumentException("Listeners not specified.");

       CreateLoadBalancerRequest request = new CreateLoadBalancerRequest(loadBalancerName);
       request.setListeners(listeners);
       return awsHttpClient.createLoadBalancer(request);
    }

    public CreateLoadBalancerResult createLoadBalancerWithSubnets(String loadBalancerName, List<Listener> listeners, List<String> subnetIds){

       if(loadBalancerName == null || loadBalancerName.equals(""))
           throw new IllegalArgumentException("Load Balancer Name not specified.");
       if(listeners == null || listeners.isEmpty())
           throw new IllegalArgumentException("Listeners not specified.");
       if(subnetIds == null || subnetIds.isEmpty())
           throw new IllegalArgumentException("Subnets not specified.");

       CreateLoadBalancerRequest request = new CreateLoadBalancerRequest(loadBalancerName);
       request.setListeners(listeners);
       request.setSubnets(subnetIds);
       return awsHttpClient.createLoadBalancer(request);
    }

    public CreateLoadBalancerResult createLoadBalancerWithAvailabilityZones(String loadBalancerName, List<Listener> listeners, List<AvailabilityZone> availabilityZones){

       if(loadBalancerName == null || loadBalancerName.equals(""))
           throw new IllegalArgumentException("Load Balancer Name not specified.");
       if(listeners == null || listeners.isEmpty())
           throw new IllegalArgumentException("Listeners not specified.");
       if(availabilityZones == null || availabilityZones.isEmpty())
           throw new IllegalArgumentException("Availability zones not specified.");

       CreateLoadBalancerRequest request = new CreateLoadBalancerRequest(loadBalancerName);
       request.setListeners(listeners);
       List<String> availabilityZoneNames = new ArrayList<>();
       for(AvailabilityZone avz : availabilityZones)
           availabilityZoneNames.add(avz.getZoneName());
       request.setAvailabilityZones(availabilityZoneNames);
       
       return awsHttpClient.createLoadBalancer(request);
    }
    
    private Listener getDefaultHttpListener(){

       Listener listener = new Listener();
       
       listener.setInstancePort(80);
       listener.setLoadBalancerPort(80);
       listener.setProtocol("HTTP");
       
       return listener;
    }

    private Listener getDefaultHttpsListener(String certificateId){

        Listener listener = new Listener();
        
        listener.setInstancePort(443);
        listener.setLoadBalancerPort(443);
        listener.setProtocol("HTTPS");
        listener.setSSLCertificateId(certificateId);
       
        return listener;
    }
  
    public DetachLoadBalancerFromSubnetsResult detachLoadBalancerFromSubnets(DetachLoadBalancerFromSubnetsRequest request){
        
        if(request == null)
            throw new IllegalArgumentException("Invalid Request");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getSubnets() == null || request.getSubnets().isEmpty())
            throw new IllegalArgumentException("Subnets not specified.");

        return awsHttpClient.detachLoadBalancerFromSubnets(request);
    }

    public DetachLoadBalancerFromSubnetsResult detachLoadBalancerFromSubnets(String loadBalancerName, List<String> subnetIds){

        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(subnetIds == null || subnetIds.isEmpty())
            throw new IllegalArgumentException("Subnets not specified.");

        DetachLoadBalancerFromSubnetsRequest request = new DetachLoadBalancerFromSubnetsRequest();
        request.setLoadBalancerName(loadBalancerName);
        request.setSubnets(subnetIds);
       
        return awsHttpClient.detachLoadBalancerFromSubnets(request);
    }
   
    public EnableAvailabilityZonesForLoadBalancerResult enableAvailabilityZonesForLoadBalancer(EnableAvailabilityZonesForLoadBalancerRequest request){
       
        if(request == null)
            throw new IllegalArgumentException("Invalid Request");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getAvailabilityZones()== null || request.getAvailabilityZones().isEmpty())
            throw new IllegalArgumentException("Availability Zones not specified.");
       
        return awsHttpClient.enableAvailabilityZonesForLoadBalancer(request);
    }

    public EnableAvailabilityZonesForLoadBalancerResult enableAvailabilityZonesForLoadBalancer(String loadBalancerName, List<String> availabilityZones){
       
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(availabilityZones == null || availabilityZones.isEmpty())
            throw new IllegalArgumentException("Availability Zones not specified.");
       
        EnableAvailabilityZonesForLoadBalancerRequest request = new EnableAvailabilityZonesForLoadBalancerRequest(loadBalancerName, availabilityZones);
        return awsHttpClient.enableAvailabilityZonesForLoadBalancer(request);
    }

    public DisableAvailabilityZonesForLoadBalancerResult disableAvailabilityZonesForLoadBalancer(DisableAvailabilityZonesForLoadBalancerRequest request){
        
        if(request == null)
            throw new IllegalArgumentException("Invalid Request");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getAvailabilityZones()== null || request.getAvailabilityZones().isEmpty())
            throw new IllegalArgumentException("Availability Zones not specified.");
       
        return awsHttpClient.disableAvailabilityZonesForLoadBalancer(request);
    }

    public DisableAvailabilityZonesForLoadBalancerResult disableAvailabilityZonesForLoadBalancer(String loadBalancerName, List<String> availabilityZones){
       
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(availabilityZones == null || availabilityZones.isEmpty())
            throw new IllegalArgumentException("Availability Zones not specified.");
       
        DisableAvailabilityZonesForLoadBalancerRequest request = new DisableAvailabilityZonesForLoadBalancerRequest(loadBalancerName, availabilityZones);
        return awsHttpClient.disableAvailabilityZonesForLoadBalancer(request);
    }

    public void createLoadBalancerListeners(CreateLoadBalancerListenersRequest request){
        
        if(request == null)
            throw new IllegalArgumentException("Invalid Request");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getListeners()== null || request.getListeners().isEmpty())
            throw new IllegalArgumentException("Listeners not specified.");

        awsHttpClient.createLoadBalancerListeners(request);
    }

    public void createLoadBalancerListeners(String loadBalancerName, List<Listener> listeners){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(listeners == null || listeners.isEmpty())
            throw new IllegalArgumentException("Listeners not specified.");

        CreateLoadBalancerListenersRequest request  = new CreateLoadBalancerListenersRequest(loadBalancerName, listeners);
        awsHttpClient.createLoadBalancerListeners(request);
    }

    public void createHttpListenerOfLoadBalancerWithPort(String loadBalancerName, int instancePort, int servicePort){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(instancePort < 0 )
            throw new IllegalArgumentException("Invalid instance port specified.");
        if(servicePort < 0 )
            throw new IllegalArgumentException("Invalid service port specified.");

        Listener listener = getDefaultHttpListener();
        listener.setInstancePort(instancePort);
        listener.setLoadBalancerPort(servicePort);
        List<Listener> listeners = new ArrayList<>();
        listeners.add(listener);
        
        CreateLoadBalancerListenersRequest request  = new CreateLoadBalancerListenersRequest(loadBalancerName, listeners);
        awsHttpClient.createLoadBalancerListeners(request);
    }

    public LoadBalancerDescription getLoadBalancerDescription(String loadBalancerName){

        List<String> loadBalancerNames = new ArrayList<>();
        loadBalancerNames.add(loadBalancerName);
        List<LoadBalancerDescription> descriptions = this.getLoadBalancerDescriptions(loadBalancerNames);
        return descriptions.isEmpty() ? new LoadBalancerDescription() : descriptions.get(0);
        
    }

    public List<LoadBalancerDescription> getLoadBalancerDescriptions(List<String> loadBalancerNames){

        DescribeLoadBalancersResult result = null;
        
        try{
             result =  this.describeLoadBalancers(loadBalancerNames);
        }catch(AmazonServiceException ase){
            System.out.println(ase.getMessage());
        }
        
        if(result == null)
            return new ArrayList<>();
        
        return result.getLoadBalancerDescriptions();
    }
    
    public DescribeInstanceHealthResult describeInstanceHealth(DescribeInstanceHealthRequest request){

        if(request == null)
            throw new IllegalArgumentException("Invalid request."); 
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 

        return awsHttpClient.describeInstanceHealth(request);
    }

    public DescribeInstanceHealthResult describeInstanceHealth(String loadBalancerName){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 

        DescribeInstanceHealthRequest request = new DescribeInstanceHealthRequest(loadBalancerName);
        return awsHttpClient.describeInstanceHealth(request);
    }

    public DescribeInstanceHealthResult describeInstanceHealth(String loadBalancerName, List<Instance> instances){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        
        DescribeInstanceHealthRequest request = new DescribeInstanceHealthRequest(loadBalancerName);
        if(instances != null)
            request.setInstances(instances);
        
        return awsHttpClient.describeInstanceHealth(request);
    }

    public DescribeInstanceHealthResult describeInstanceHealth(String loadBalancerName, Instance instance){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        
        DescribeInstanceHealthRequest request = new DescribeInstanceHealthRequest(loadBalancerName);
        if(instance != null){
            List<Instance> instances = new ArrayList<>();
            instances.add(instance);
            request.setInstances(instances);
        }
        
        return awsHttpClient.describeInstanceHealth(request);
    }

}
