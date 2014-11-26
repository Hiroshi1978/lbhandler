/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.ApplySecurityGroupsToLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.ApplySecurityGroupsToLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.AttachLoadBalancerToSubnetsRequest;
import com.amazonaws.services.elasticloadbalancing.model.AttachLoadBalancerToSubnetsResult;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateAppCookieStickinessPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateAppCookieStickinessPolicyResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLBCookieStickinessPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLBCookieStickinessPolicyResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerPolicyResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerPolicyResult;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeInstanceHealthRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeInstanceHealthResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancerPoliciesRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancerPoliciesResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.DetachLoadBalancerFromSubnetsRequest;
import com.amazonaws.services.elasticloadbalancing.model.DetachLoadBalancerFromSubnetsResult;
import com.amazonaws.services.elasticloadbalancing.model.DisableAvailabilityZonesForLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DisableAvailabilityZonesForLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.EnableAvailabilityZonesForLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.EnableAvailabilityZonesForLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerListenerSSLCertificateRequest;
import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerPoliciesOfListenerRequest;
import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerPoliciesOfListenerResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import web.component.impl.CloudBlock;

/**
 *
 * @author Hiroshi
 */
public class AWSELB implements CloudBlock{
    
    private final AmazonElasticLoadBalancing awsHttpClient;
    
    private AWSELB(){
        
        AmazonElasticLoadBalancingClient initialClient = new AmazonElasticLoadBalancingClient(AWS.access().credentials());
        setUpHttpClient(initialClient);
        awsHttpClient = initialClient;
    }
    
    private void setUpHttpClient(AmazonElasticLoadBalancingClient awsELBClient){

        Properties conf = AWS.access().conf();
        String endpoint = conf.getProperty("elb.endpoint");
        String serviceName = conf.getProperty("elb.servicename");
        String regionName = conf.getProperty("region");
        
        awsELBClient.setEndpoint(endpoint, serviceName, regionName);
        awsELBClient.setServiceNameIntern(serviceName);
    }
    
    static AWSELB get(){
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

    public DescribeLoadBalancersResult describeLoadBalancers(Collection<String> loadBalancerNames){
        
        if(loadBalancerNames == null || loadBalancerNames.isEmpty())
            throw new IllegalArgumentException("Load Balancer Names not specified.");
            
        DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest();
        request.setLoadBalancerNames(loadBalancerNames);
        
        return awsHttpClient.describeLoadBalancers(request);
    }

    public DescribeLoadBalancersResult describeLoadBalancers(String loadBalancerName){
    
        return this.describeLoadBalancers(Collections.singleton(loadBalancerName));
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
        request.setListeners(Collections.singleton(getDefaultHttpListener())); 
       
        return awsHttpClient.createLoadBalancer(request); 
    } 
   
    public CreateLoadBalancerResult createLoadBalancer(String loadBalancerName, String certificateId){ 

        if(loadBalancerName == null || loadBalancerName.equals("")) 
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(certificateId == null || certificateId.equals("")) 
            throw new IllegalArgumentException("Certificate ID not specified."); 

        CreateLoadBalancerRequest request = new CreateLoadBalancerRequest(loadBalancerName); 
               request.setListeners(Collections.singleton(getDefaultHttpsListener(certificateId))); 
       
        return awsHttpClient.createLoadBalancer(request); 
    } 

    public void deleteLoadBalancer(String loadBalancerName){
        DeleteLoadBalancerRequest request = new DeleteLoadBalancerRequest(loadBalancerName);
        awsHttpClient.deleteLoadBalancer(request);
    }

    public int deleteAllLoadBalancers(){
        
        return (int) awsHttpClient.describeLoadBalancers()
                .getLoadBalancerDescriptions().stream()
                //i don't have confidence in the appropriateness of the codes below.
                .map(LoadBalancerDescription::getLoadBalancerName)
                .map(DeleteLoadBalancerRequest::new)
                .map(request -> {
                    boolean succeededToDelete = false;
                    try{
                        awsHttpClient.deleteLoadBalancer(request);
                        succeededToDelete = true;
                    }catch(AmazonClientException e){
                    }
                    return succeededToDelete;
                })
                .filter(succeededToDelete -> true)
                .count();
    }

    public RegisterInstancesWithLoadBalancerResult registerInstancesWithLoadBalancer(RegisterInstancesWithLoadBalancerRequest request){
    
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified.");
        if(request.getInstances() == null || request.getInstances().isEmpty())
            throw new IllegalArgumentException("Instances not specified.");
        
        return awsHttpClient.registerInstancesWithLoadBalancer(request);
    }
    
    public RegisterInstancesWithLoadBalancerResult registerInstancesWithLoadBalancer(String loadBalancerName, Collection<Instance> instances){
    
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified.");
        if(instances == null || instances.isEmpty())
            throw new IllegalArgumentException("Instances not specified.");

        RegisterInstancesWithLoadBalancerRequest request = new RegisterInstancesWithLoadBalancerRequest(loadBalancerName, new ArrayList<>(instances) );
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

    public DeregisterInstancesFromLoadBalancerResult deregisterInstancesFromLoadBalancer(String loadBalancerName, Collection<Instance> instances){

        if(loadBalancerName == null || loadBalancerName.isEmpty())
           throw new IllegalArgumentException("Load Balancer Name not specified.");
        if(instances == null || instances.isEmpty())
           throw new IllegalArgumentException("Instances not specified.");

        DeregisterInstancesFromLoadBalancerRequest request = new DeregisterInstancesFromLoadBalancerRequest(loadBalancerName,new ArrayList<>(instances));
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

    public AttachLoadBalancerToSubnetsResult attachLoadBalancerToSubnets(String loadBalancerName, Collection<String> subnetIds){

        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(subnetIds == null || subnetIds.isEmpty())
            throw new IllegalArgumentException("Subnets not specified.");

        AttachLoadBalancerToSubnetsRequest request = 
                new AttachLoadBalancerToSubnetsRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withSubnets(subnetIds);
        
        return awsHttpClient.attachLoadBalancerToSubnets(request);
    }
    
   public void deleteLoadBalancerListeners(DeleteLoadBalancerListenersRequest request){
       awsHttpClient.deleteLoadBalancerListeners(request);
   }
   public void deleteLoadBalancerListeners(String loadBalancerName, Collection<Listener> listeners){
       
        DeleteLoadBalancerListenersRequest request = 
                new DeleteLoadBalancerListenersRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withLoadBalancerPorts(listeners.stream()
                                            .map(Listener::getLoadBalancerPort)
                                            .collect(Collectors.toList()));

        deleteLoadBalancerListeners(request);
    }

   
    public CreateLoadBalancerResult createLoadBalancer(String loadBalancerName, Collection<Listener> listeners){

       if(loadBalancerName == null || loadBalancerName.equals(""))
           throw new IllegalArgumentException("Load Balancer Name not specified.");
       if(listeners == null || listeners.isEmpty())
           throw new IllegalArgumentException("Listeners not specified.");

       CreateLoadBalancerRequest request = 
                new CreateLoadBalancerRequest(loadBalancerName)
                    .withListeners(listeners);

       return awsHttpClient.createLoadBalancer(request);
    }

    public CreateLoadBalancerResult createLoadBalancerWithSubnets(String loadBalancerName, Collection<Listener> listeners, Collection<String> subnetIds){

       if(loadBalancerName == null || loadBalancerName.equals(""))
           throw new IllegalArgumentException("Load Balancer Name not specified.");
       if(listeners == null || listeners.isEmpty())
           throw new IllegalArgumentException("Listeners not specified.");
       if(subnetIds == null || subnetIds.isEmpty())
           throw new IllegalArgumentException("Subnets not specified.");

       CreateLoadBalancerRequest request = 
                new CreateLoadBalancerRequest(loadBalancerName)
                    .withListeners(listeners)
                    .withSubnets(subnetIds);

       return awsHttpClient.createLoadBalancer(request);
    }

    public CreateLoadBalancerResult createLoadBalancerWithAvailabilityZones(String loadBalancerName, Collection<Listener> listeners, Collection<AvailabilityZone> availabilityZones){

       if(loadBalancerName == null || loadBalancerName.equals(""))
           throw new IllegalArgumentException("Load Balancer Name not specified.");
       if(listeners == null || listeners.isEmpty())
           throw new IllegalArgumentException("Listeners not specified.");
       if(availabilityZones == null || availabilityZones.isEmpty())
           throw new IllegalArgumentException("Availability zones not specified.");

       CreateLoadBalancerRequest request = 
                new CreateLoadBalancerRequest(loadBalancerName)
                    .withListeners(listeners)
                    .withAvailabilityZones(availabilityZones.stream()
                                            .map(AvailabilityZone::getZoneName)
                                            .collect(Collectors.toList()));

       return awsHttpClient.createLoadBalancer(request);
    }
    
    private Listener getDefaultHttpListener(){

       return new Listener()
                .withInstancePort(80)
                .withLoadBalancerPort(80)
                .withInstanceProtocol("HTTP")
                .withProtocol("HTTP");
    }

    private Listener getDefaultHttpsListener(String certificateId){

        return new Listener()
                .withInstancePort(443)
                .withLoadBalancerPort(443)
                .withInstanceProtocol("HTTPS")
                .withProtocol("HTTPS")
                .withSSLCertificateId(certificateId);
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

    public DetachLoadBalancerFromSubnetsResult detachLoadBalancerFromSubnets(String loadBalancerName, Collection<String> subnetIds){

        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(subnetIds == null || subnetIds.isEmpty())
            throw new IllegalArgumentException("Subnets not specified.");

        DetachLoadBalancerFromSubnetsRequest request = 
                new DetachLoadBalancerFromSubnetsRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withSubnets(subnetIds);
       
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

    public EnableAvailabilityZonesForLoadBalancerResult enableAvailabilityZonesForLoadBalancer(String loadBalancerName, Collection<String> availabilityZones){
       
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(availabilityZones == null || availabilityZones.isEmpty())
            throw new IllegalArgumentException("Availability Zones not specified.");
       
        EnableAvailabilityZonesForLoadBalancerRequest request = new EnableAvailabilityZonesForLoadBalancerRequest(loadBalancerName, new ArrayList<>(availabilityZones));
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

    public DisableAvailabilityZonesForLoadBalancerResult disableAvailabilityZonesForLoadBalancer(String loadBalancerName, Collection<String> availabilityZones){
       
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(availabilityZones == null || availabilityZones.isEmpty())
            throw new IllegalArgumentException("Availability Zones not specified.");
       
        DisableAvailabilityZonesForLoadBalancerRequest request = new DisableAvailabilityZonesForLoadBalancerRequest(loadBalancerName, new ArrayList<>(availabilityZones));
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

    public void createLoadBalancerListeners(String loadBalancerName, Collection<Listener> listeners){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(listeners == null || listeners.isEmpty())
            throw new IllegalArgumentException("Listeners not specified.");

        CreateLoadBalancerListenersRequest request  = new CreateLoadBalancerListenersRequest(loadBalancerName, new ArrayList<>(listeners));
        awsHttpClient.createLoadBalancerListeners(request);
    }

    public void createHttpListenerOfLoadBalancerWithPort(String loadBalancerName, int instancePort, int servicePort){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(instancePort < 0 )
            throw new IllegalArgumentException("Invalid instance port specified.");
        if(servicePort < 0 )
            throw new IllegalArgumentException("Invalid service port specified.");

        CreateLoadBalancerListenersRequest request  = 
                new CreateLoadBalancerListenersRequest(
                        loadBalancerName, 
                        Collections.singletonList(getDefaultHttpListener()
                                                    .withInstancePort(instancePort)
                                                    .withLoadBalancerPort(servicePort)));
        
        awsHttpClient.createLoadBalancerListeners(request);
    }

    public LoadBalancerDescription getLoadBalancerDescription(String loadBalancerName){

        List<String> loadBalancerNames = new ArrayList<>();
        loadBalancerNames.add(loadBalancerName);
        List<LoadBalancerDescription> descriptions = this.getLoadBalancerDescriptions(loadBalancerNames);
        return descriptions.isEmpty() ? new LoadBalancerDescription() : descriptions.get(0);
        
    }

    public List<LoadBalancerDescription> getLoadBalancerDescriptions(Collection<String> loadBalancerNames){

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

    public DescribeInstanceHealthResult describeInstanceHealth(String loadBalancerName, Collection<Instance> instances){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        
        DescribeInstanceHealthRequest request = 
                new DescribeInstanceHealthRequest(loadBalancerName)
                    .withInstances(instances);
        
        return awsHttpClient.describeInstanceHealth(request);
    }

    public DescribeInstanceHealthResult describeInstanceHealth(String loadBalancerName, Instance instance){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        
        DescribeInstanceHealthRequest request = 
                new DescribeInstanceHealthRequest(loadBalancerName)
                    .withInstances(Collections.singleton(instance));
        
        return awsHttpClient.describeInstanceHealth(request);
    }

    public ConfigureHealthCheckResult configureHealthCheck(ConfigureHealthCheckRequest request){

        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getHealthCheck() == null)
            throw new IllegalArgumentException("Health check parameters not specified.");

        return awsHttpClient.configureHealthCheck(request);
    }
    
    public ConfigureHealthCheckResult configureHealthCheck(String loadBalancerName, HealthCheck hc){
        
        ConfigureHealthCheckRequest request = 
                new ConfigureHealthCheckRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withHealthCheck(hc);
        
        return configureHealthCheck(request);
    }

    public DescribeLoadBalancerPoliciesResult describeLoadBalancerPolicies(){

        return awsHttpClient.describeLoadBalancerPolicies();
    }
    public DescribeLoadBalancerPoliciesResult describeLoadBalancerPolicies(DescribeLoadBalancerPoliciesRequest request){

        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        
        return awsHttpClient.describeLoadBalancerPolicies(request);
    }
    public DescribeLoadBalancerPoliciesResult describeLoadBalancerPolicies(String loadBalancerName){

        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        
        DescribeLoadBalancerPoliciesRequest request = 
                new DescribeLoadBalancerPoliciesRequest()
                    .withLoadBalancerName(loadBalancerName);
        
        return describeLoadBalancerPolicies(request);
    }
    
    public CreateLoadBalancerPolicyResult createLoadBalancerPolicy(CreateLoadBalancerPolicyRequest request){
        
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");

        return awsHttpClient.createLoadBalancerPolicy(request);
    }

    public DeleteLoadBalancerPolicyResult deleteLoadBalancerPolicy(DeleteLoadBalancerPolicyRequest request){
        
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getPolicyName() == null || request.getPolicyName().isEmpty())
            throw new IllegalArgumentException("Policy name not specified.");

        return awsHttpClient.deleteLoadBalancerPolicy(request);
    }
    public DeleteLoadBalancerPolicyResult deleteLoadBalancerPolicy(String loadBalancerName, String policyName){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(policyName == null || policyName.isEmpty())
            throw new IllegalArgumentException("Policy name not specified.");
        
        DeleteLoadBalancerPolicyRequest request = 
                new DeleteLoadBalancerPolicyRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withPolicyName(policyName);
        
        return deleteLoadBalancerPolicy(request);
    }

    public CreateAppCookieStickinessPolicyResult createAppCookieStickinessPolicy(CreateAppCookieStickinessPolicyRequest request){
    
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getPolicyName() == null || request.getPolicyName().isEmpty())
            throw new IllegalArgumentException("Policy name not specified.");
        if(request.getCookieName() == null || request.getCookieName().isEmpty())
            throw new IllegalArgumentException("Cookie name not specified.");

        return awsHttpClient.createAppCookieStickinessPolicy(request);
    }
    public CreateAppCookieStickinessPolicyResult createAppCookieStickinessPolicy(String loadBalancerName, String policyName, String cookieName){
        
        CreateAppCookieStickinessPolicyRequest request = 
                new CreateAppCookieStickinessPolicyRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withPolicyName(policyName)
                    .withCookieName(cookieName);
        
        return createAppCookieStickinessPolicy(request);
    }
    
    public CreateLBCookieStickinessPolicyResult createLBCookieStickinessPolicy(CreateLBCookieStickinessPolicyRequest request){
    
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getPolicyName() == null || request.getPolicyName().isEmpty())
            throw new IllegalArgumentException("Policy name not specified.");

        return awsHttpClient.createLBCookieStickinessPolicy(request);
    }
    public CreateLBCookieStickinessPolicyResult createLBCookieStickinessPolicy(String loadBalancerName, String policyName){
        
        CreateLBCookieStickinessPolicyRequest request = 
                new CreateLBCookieStickinessPolicyRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withPolicyName(policyName);

        return createLBCookieStickinessPolicy(request);
    }
    
    public void setLoadBalancerListenerSSLCertificate(SetLoadBalancerListenerSSLCertificateRequest request){
        
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getLoadBalancerPort() == null)
            throw new IllegalArgumentException("Load balancer port not specified.");
        if(request.getSSLCertificateId() == null || request.getSSLCertificateId().isEmpty())
            throw new IllegalArgumentException("SSL certificate id not specified.");

        awsHttpClient.setLoadBalancerListenerSSLCertificate(request);
    }

    public void setLoadBalancerListenerSSLCertificate(String loadBalancerName, int port, String certificateId){
        
        SetLoadBalancerListenerSSLCertificateRequest request = 
                new SetLoadBalancerListenerSSLCertificateRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withLoadBalancerPort(port)
                    .withSSLCertificateId(certificateId);
        
        setLoadBalancerListenerSSLCertificate(request);
    }
    
    public SetLoadBalancerPoliciesOfListenerResult setLoadBalancerPoliciesOfListener(SetLoadBalancerPoliciesOfListenerRequest request){
        
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getLoadBalancerPort() == null)
            throw new IllegalArgumentException("Load balancer port not specified.");
        if(request.getPolicyNames() == null || request.getPolicyNames().isEmpty())
            throw new IllegalArgumentException("Policy name not specified.");

        return awsHttpClient.setLoadBalancerPoliciesOfListener(request);
    }
    public SetLoadBalancerPoliciesOfListenerResult setLoadBalancerPoliciesOfListener(String loadBalancerName, int port, Collection<String> policyNames){
        
        SetLoadBalancerPoliciesOfListenerRequest request = 
                new SetLoadBalancerPoliciesOfListenerRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withLoadBalancerPort(port)
                    .withPolicyNames(policyNames);
        
        return setLoadBalancerPoliciesOfListener(request);
        
    }
    public SetLoadBalancerPoliciesOfListenerResult setLoadBalancerPoliciesOfListener(String loadBalancerName, int port, String policyName){
                
        return setLoadBalancerPoliciesOfListener(loadBalancerName, port, Collections.singleton(policyName));
        
    }

    public ApplySecurityGroupsToLoadBalancerResult applySecurityGroupsToLoadBalancer(ApplySecurityGroupsToLoadBalancerRequest request){
       
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getSecurityGroups() == null || request.getSecurityGroups().isEmpty())
            throw new IllegalArgumentException("Security groups not specified.");
        
        return awsHttpClient.applySecurityGroupsToLoadBalancer(request);
    }
    public ApplySecurityGroupsToLoadBalancerResult applySecurityGroupsToLoadBalancer(String loadBalancerName, Collection<String> securityGroups){
       
        ApplySecurityGroupsToLoadBalancerRequest request = 
                new ApplySecurityGroupsToLoadBalancerRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withSecurityGroups(securityGroups);
                    
        return applySecurityGroupsToLoadBalancer(request);
    }
    public ApplySecurityGroupsToLoadBalancerResult applySecurityGroupsToLoadBalancer(String loadBalancerName, String securityGroup){

        return applySecurityGroupsToLoadBalancer(loadBalancerName, Collections.singleton(securityGroup));
    }
}
