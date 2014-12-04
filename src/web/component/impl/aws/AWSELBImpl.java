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
import com.amazonaws.services.elasticloadbalancing.model.AccessLog;
import com.amazonaws.services.elasticloadbalancing.model.ApplySecurityGroupsToLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.ApplySecurityGroupsToLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.AttachLoadBalancerToSubnetsRequest;
import com.amazonaws.services.elasticloadbalancing.model.AttachLoadBalancerToSubnetsResult;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckResult;
import com.amazonaws.services.elasticloadbalancing.model.ConnectionDraining;
import com.amazonaws.services.elasticloadbalancing.model.ConnectionSettings;
import com.amazonaws.services.elasticloadbalancing.model.CreateAppCookieStickinessPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateAppCookieStickinessPolicyResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLBCookieStickinessPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLBCookieStickinessPolicyResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerPolicyResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.CrossZoneLoadBalancing;
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
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancerPolicyTypesRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancerPolicyTypesResult;
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
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerAttributes;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.ModifyLoadBalancerAttributesRequest;
import com.amazonaws.services.elasticloadbalancing.model.ModifyLoadBalancerAttributesResult;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerListenerSSLCertificateRequest;
import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerPoliciesOfListenerRequest;
import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerPoliciesOfListenerResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author Hiroshi
 */
public class AWSELBImpl implements AWSELB{
    
    private final AmazonElasticLoadBalancing awsHttpClient;
    
    private AWSELBImpl(){
        
        AmazonElasticLoadBalancingClient initialClient = new AmazonElasticLoadBalancingClient(((AWSImpl)AWS.access()).credentials());
        setUpHttpClient(initialClient);
        awsHttpClient = initialClient;
    }
    
    private void setUpHttpClient(AmazonElasticLoadBalancingClient awsELBClient){

        Properties conf = ((AWSImpl)AWS.access()).conf();
        String endpoint = conf.getProperty("elb.endpoint");
        String serviceName = conf.getProperty("elb.servicename");
        String regionName = conf.getProperty("region");
        
        awsELBClient.setEndpoint(endpoint, serviceName, regionName);
        awsELBClient.setServiceNameIntern(serviceName);
    }
    
    static AWSELBImpl get(){
        return new AWSELBImpl();
    }

    @Override
    public DescribeLoadBalancersResult describeLoadBalancers(){
        return awsHttpClient.describeLoadBalancers();
    }
    
    @Override
    public DescribeLoadBalancersResult describeLoadBalancers(DescribeLoadBalancersRequest request){
        
        if(request == null)
            throw new IllegalArgumentException("Invalid Request.");
        
        return awsHttpClient.describeLoadBalancers(request);
    }

    @Override
    public DescribeLoadBalancersResult describeLoadBalancers(Collection<String> loadBalancerNames){
        
        if(loadBalancerNames == null || loadBalancerNames.isEmpty())
            throw new IllegalArgumentException("Load Balancer Names not specified.");
            
        return awsHttpClient.describeLoadBalancers(
                new DescribeLoadBalancersRequest()
                    .withLoadBalancerNames(loadBalancerNames));
    }

    @Override
    public DescribeLoadBalancersResult describeLoadBalancers(String loadBalancerName){
    
        return this.describeLoadBalancers(singleton(loadBalancerName));
    }

    @Override
    public CreateLoadBalancerResult createLoadBalancer(CreateLoadBalancerRequest request){ 

        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().equals("")) 
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getListeners() == null || request.getListeners().isEmpty()) 
            throw new IllegalArgumentException("Listeners not specified."); 
        if(request.getAvailabilityZones() == null || request.getAvailabilityZones().isEmpty()) 
            throw new IllegalArgumentException("AvailavilityZones not specified."); 
           
        return awsHttpClient.createLoadBalancer(request); 
    } 
   
    @Override
    public CreateLoadBalancerResult createLoadBalancer(String loadBalancerName){ 

        if(loadBalancerName == null || loadBalancerName.equals("")) 
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
       
        return awsHttpClient.createLoadBalancer(
                new CreateLoadBalancerRequest(loadBalancerName)
                    .withListeners(singleton(getDefaultHttpListener()))); 
    } 
   
    @Override
    public CreateLoadBalancerResult createLoadBalancer(String loadBalancerName, String certificateId){ 

        if(loadBalancerName == null || loadBalancerName.equals("")) 
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(certificateId == null || certificateId.equals("")) 
            throw new IllegalArgumentException("Certificate ID not specified."); 

        return awsHttpClient.createLoadBalancer(
                    new CreateLoadBalancerRequest(loadBalancerName)
                        .withListeners(singleton(getDefaultHttpsListener(certificateId)))); 
    } 

    @Override
    public void deleteLoadBalancer(String loadBalancerName){
        awsHttpClient.deleteLoadBalancer(new DeleteLoadBalancerRequest(loadBalancerName));
    }

    @Override
    public int deleteAllLoadBalancers(){
        
        return (int) awsHttpClient.describeLoadBalancers()
                .getLoadBalancerDescriptions().stream()
                //i don't have confidence in the appropriateness of the codes below.
                .map(LoadBalancerDescription::getLoadBalancerName)
                .map(DeleteLoadBalancerRequest::new)
                .filter(request -> {
                    boolean succeededToDelete = false;
                    try{
                        awsHttpClient.deleteLoadBalancer(request);
                        succeededToDelete = true;
                    }catch(AmazonClientException e){
                    }
                    return succeededToDelete;
                })
                .count();
    }

    @Override
    public RegisterInstancesWithLoadBalancerResult registerInstancesWithLoadBalancer(RegisterInstancesWithLoadBalancerRequest request){
    
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified.");
        if(request.getInstances() == null || request.getInstances().isEmpty())
            throw new IllegalArgumentException("Instances not specified.");
        
        return awsHttpClient.registerInstancesWithLoadBalancer(request);
    }
    
    @Override
    public RegisterInstancesWithLoadBalancerResult registerInstancesWithLoadBalancer(String loadBalancerName, Collection<Instance> instances){
    
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified.");
        if(instances == null || instances.isEmpty())
            throw new IllegalArgumentException("Instances not specified.");

        return awsHttpClient.registerInstancesWithLoadBalancer(new RegisterInstancesWithLoadBalancerRequest(loadBalancerName, new ArrayList<>(instances) ));
    }

    @Override
    public DeregisterInstancesFromLoadBalancerResult deregisterInstancesFromLoadBalancer(DeregisterInstancesFromLoadBalancerRequest request){
 
        if(request == null)
            throw new IllegalArgumentException("Invalid Request.");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified.");
        if(request.getInstances() == null || request.getInstances().isEmpty())
            throw new IllegalArgumentException("Instances not specified.");
 
        return awsHttpClient.deregisterInstancesFromLoadBalancer(request);
    }

    @Override
    public DeregisterInstancesFromLoadBalancerResult deregisterInstancesFromLoadBalancer(String loadBalancerName, Collection<Instance> instances){

        if(loadBalancerName == null || loadBalancerName.isEmpty())
           throw new IllegalArgumentException("Load Balancer Name not specified.");
        if(instances == null || instances.isEmpty())
           throw new IllegalArgumentException("Instances not specified.");

        return awsHttpClient.deregisterInstancesFromLoadBalancer(
                    new DeregisterInstancesFromLoadBalancerRequest(
                            loadBalancerName,new ArrayList<>(instances)));
    }

    @Override
    public AttachLoadBalancerToSubnetsResult attachLoadBalancerToSubnets(AttachLoadBalancerToSubnetsRequest request){

        if(request == null)
            throw new IllegalArgumentException("Invalid Request.");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getSubnets() == null || request.getSubnets().isEmpty())
            throw new IllegalArgumentException("Subnets not specified.");
 
        return awsHttpClient.attachLoadBalancerToSubnets(request);
    }

    @Override
    public AttachLoadBalancerToSubnetsResult attachLoadBalancerToSubnets(String loadBalancerName, Collection<String> subnetIds){

        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(subnetIds == null || subnetIds.isEmpty())
            throw new IllegalArgumentException("Subnets not specified.");

        return awsHttpClient.attachLoadBalancerToSubnets(
                    new AttachLoadBalancerToSubnetsRequest()
                        .withLoadBalancerName(loadBalancerName)
                        .withSubnets(subnetIds));
    }
    
    @Override
    public void deleteLoadBalancerListeners(DeleteLoadBalancerListenersRequest request){
        awsHttpClient.deleteLoadBalancerListeners(request);
    }
    @Override
    public void deleteLoadBalancerListeners(String loadBalancerName, Collection<Listener> listeners){
       
        deleteLoadBalancerListeners(
                new DeleteLoadBalancerListenersRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withLoadBalancerPorts(listeners.stream()
                                            .map(Listener::getLoadBalancerPort)
                                            .collect(toList())));
    }

   
    @Override
    public CreateLoadBalancerResult createLoadBalancer(String loadBalancerName, Collection<Listener> listeners){

       if(loadBalancerName == null || loadBalancerName.equals(""))
           throw new IllegalArgumentException("Load Balancer Name not specified.");
       if(listeners == null || listeners.isEmpty())
           throw new IllegalArgumentException("Listeners not specified.");

       return awsHttpClient.createLoadBalancer(
               new CreateLoadBalancerRequest(loadBalancerName)
                    .withListeners(listeners));
    }

    @Override
    public CreateLoadBalancerResult createLoadBalancerWithSubnets(String loadBalancerName, Collection<Listener> listeners, Collection<String> subnetIds){

       if(loadBalancerName == null || loadBalancerName.equals(""))
           throw new IllegalArgumentException("Load Balancer Name not specified.");
       if(listeners == null || listeners.isEmpty())
           throw new IllegalArgumentException("Listeners not specified.");
       if(subnetIds == null || subnetIds.isEmpty())
           throw new IllegalArgumentException("Subnets not specified.");

       return awsHttpClient.createLoadBalancer(
               new CreateLoadBalancerRequest(loadBalancerName)
                    .withListeners(listeners)
                    .withSubnets(subnetIds));
    }

    @Override
    public CreateLoadBalancerResult createLoadBalancerWithAvailabilityZones(String loadBalancerName, Collection<Listener> listeners, Collection<AvailabilityZone> availabilityZones){

       if(loadBalancerName == null || loadBalancerName.equals(""))
           throw new IllegalArgumentException("Load Balancer Name not specified.");
       if(listeners == null || listeners.isEmpty())
           throw new IllegalArgumentException("Listeners not specified.");
       if(availabilityZones == null || availabilityZones.isEmpty())
           throw new IllegalArgumentException("Availability zones not specified.");

       return awsHttpClient.createLoadBalancer(
               new CreateLoadBalancerRequest(loadBalancerName)
                    .withListeners(listeners)
                    .withAvailabilityZones(availabilityZones.stream()
                                            .map(AvailabilityZone::getZoneName)
                                            .collect(toList())));
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
  
    @Override
    public DetachLoadBalancerFromSubnetsResult detachLoadBalancerFromSubnets(DetachLoadBalancerFromSubnetsRequest request){
        
        if(request == null)
            throw new IllegalArgumentException("Invalid Request");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getSubnets() == null || request.getSubnets().isEmpty())
            throw new IllegalArgumentException("Subnets not specified.");

        return awsHttpClient.detachLoadBalancerFromSubnets(request);
    }

    @Override
    public DetachLoadBalancerFromSubnetsResult detachLoadBalancerFromSubnets(String loadBalancerName, Collection<String> subnetIds){

        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(subnetIds == null || subnetIds.isEmpty())
            throw new IllegalArgumentException("Subnets not specified.");

        return awsHttpClient.detachLoadBalancerFromSubnets(
                new DetachLoadBalancerFromSubnetsRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withSubnets(subnetIds));
    }
   
    @Override
    public EnableAvailabilityZonesForLoadBalancerResult enableAvailabilityZonesForLoadBalancer(EnableAvailabilityZonesForLoadBalancerRequest request){
       
        if(request == null)
            throw new IllegalArgumentException("Invalid Request");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getAvailabilityZones()== null || request.getAvailabilityZones().isEmpty())
            throw new IllegalArgumentException("Availability Zones not specified.");
       
        return awsHttpClient.enableAvailabilityZonesForLoadBalancer(request);
    }

    @Override
    public EnableAvailabilityZonesForLoadBalancerResult enableAvailabilityZonesForLoadBalancer(String loadBalancerName, Collection<String> availabilityZones){
       
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(availabilityZones == null || availabilityZones.isEmpty())
            throw new IllegalArgumentException("Availability Zones not specified.");
       
        return awsHttpClient.enableAvailabilityZonesForLoadBalancer(new EnableAvailabilityZonesForLoadBalancerRequest(loadBalancerName, new ArrayList<>(availabilityZones)));
    }

    @Override
    public DisableAvailabilityZonesForLoadBalancerResult disableAvailabilityZonesForLoadBalancer(DisableAvailabilityZonesForLoadBalancerRequest request){
        
        if(request == null)
            throw new IllegalArgumentException("Invalid Request");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getAvailabilityZones()== null || request.getAvailabilityZones().isEmpty())
            throw new IllegalArgumentException("Availability Zones not specified.");
       
        return awsHttpClient.disableAvailabilityZonesForLoadBalancer(request);
    }

    @Override
    public DisableAvailabilityZonesForLoadBalancerResult disableAvailabilityZonesForLoadBalancer(String loadBalancerName, Collection<String> availabilityZones){
       
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(availabilityZones == null || availabilityZones.isEmpty())
            throw new IllegalArgumentException("Availability Zones not specified.");
       
        return awsHttpClient.disableAvailabilityZonesForLoadBalancer(new DisableAvailabilityZonesForLoadBalancerRequest(loadBalancerName, new ArrayList<>(availabilityZones)));
    }

    @Override
    public void createLoadBalancerListeners(CreateLoadBalancerListenersRequest request){
        
        if(request == null)
            throw new IllegalArgumentException("Invalid Request");
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(request.getListeners()== null || request.getListeners().isEmpty())
            throw new IllegalArgumentException("Listeners not specified.");

        awsHttpClient.createLoadBalancerListeners(request);
    }

    @Override
    public void createLoadBalancerListeners(String loadBalancerName, Collection<Listener> listeners){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(listeners == null || listeners.isEmpty())
            throw new IllegalArgumentException("Listeners not specified.");

        awsHttpClient.createLoadBalancerListeners(new CreateLoadBalancerListenersRequest(loadBalancerName, new ArrayList<>(listeners)));
    }

    @Override
    public void createHttpListenerOfLoadBalancerWithPort(String loadBalancerName, int instancePort, int servicePort){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        if(instancePort < 0 )
            throw new IllegalArgumentException("Invalid instance port specified.");
        if(servicePort < 0 )
            throw new IllegalArgumentException("Invalid service port specified.");

        awsHttpClient.createLoadBalancerListeners(
                 new CreateLoadBalancerListenersRequest(
                        loadBalancerName, 
                        singletonList(getDefaultHttpListener()
                                        .withInstancePort(instancePort)
                                        .withLoadBalancerPort(servicePort))));
    }

    @Override
    public LoadBalancerDescription getLoadBalancerDescription(String loadBalancerName){

        return getLoadBalancerDescriptions(singletonList(loadBalancerName))
                .stream().findFirst()
                .orElse(new LoadBalancerDescription());
        
    }

    @Override
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
    
    @Override
    public DescribeInstanceHealthResult describeInstanceHealth(DescribeInstanceHealthRequest request){

        if(request == null)
            throw new IllegalArgumentException("Invalid request."); 
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 

        return awsHttpClient.describeInstanceHealth(request);
    }

    @Override
    public DescribeInstanceHealthResult describeInstanceHealth(String loadBalancerName){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 

        return awsHttpClient.describeInstanceHealth(new DescribeInstanceHealthRequest(loadBalancerName));
    }

    @Override
    public DescribeInstanceHealthResult describeInstanceHealth(String loadBalancerName, Collection<Instance> instances){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        
        return awsHttpClient.describeInstanceHealth(
                new DescribeInstanceHealthRequest(loadBalancerName)
                    .withInstances(instances));
    }

    @Override
    public DescribeInstanceHealthResult describeInstanceHealth(String loadBalancerName, Instance instance){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load Balancer Name not specified."); 
        
        return awsHttpClient.describeInstanceHealth(
                new DescribeInstanceHealthRequest(loadBalancerName)
                    .withInstances(singleton(instance)));
        
    }

    @Override
    public ConfigureHealthCheckResult configureHealthCheck(ConfigureHealthCheckRequest request){

        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getHealthCheck() == null)
            throw new IllegalArgumentException("Health check parameters not specified.");

        return awsHttpClient.configureHealthCheck(request);
    }
    
    @Override
    public ConfigureHealthCheckResult configureHealthCheck(String loadBalancerName, HealthCheck hc){
        
        return configureHealthCheck(
                new ConfigureHealthCheckRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withHealthCheck(hc));
        
    }

    @Override
    public DescribeLoadBalancerPoliciesResult describeLoadBalancerPolicies(){

        return awsHttpClient.describeLoadBalancerPolicies();
    }
    @Override
    public DescribeLoadBalancerPoliciesResult describeLoadBalancerPolicies(DescribeLoadBalancerPoliciesRequest request){

        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        
        return awsHttpClient.describeLoadBalancerPolicies(request);
    }
    @Override
    public DescribeLoadBalancerPoliciesResult describeLoadBalancerPolicies(String loadBalancerName){

        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        
        return describeLoadBalancerPolicies(
                new DescribeLoadBalancerPoliciesRequest()
                    .withLoadBalancerName(loadBalancerName));
    }
    
    @Override
    public CreateLoadBalancerPolicyResult createLoadBalancerPolicy(CreateLoadBalancerPolicyRequest request){
        
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");

        return awsHttpClient.createLoadBalancerPolicy(request);
    }

    @Override
    public DeleteLoadBalancerPolicyResult deleteLoadBalancerPolicy(DeleteLoadBalancerPolicyRequest request){
        
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getPolicyName() == null || request.getPolicyName().isEmpty())
            throw new IllegalArgumentException("Policy name not specified.");

        return awsHttpClient.deleteLoadBalancerPolicy(request);
    }
    @Override
    public DeleteLoadBalancerPolicyResult deleteLoadBalancerPolicy(String loadBalancerName, String policyName){
        
        if(loadBalancerName == null || loadBalancerName.isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(policyName == null || policyName.isEmpty())
            throw new IllegalArgumentException("Policy name not specified.");
        
        return deleteLoadBalancerPolicy(
                new DeleteLoadBalancerPolicyRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withPolicyName(policyName));
        
    }

    @Override
    public CreateAppCookieStickinessPolicyResult createAppCookieStickinessPolicy(CreateAppCookieStickinessPolicyRequest request){
    
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getPolicyName() == null || request.getPolicyName().isEmpty())
            throw new IllegalArgumentException("Policy name not specified.");
        if(request.getCookieName() == null || request.getCookieName().isEmpty())
            throw new IllegalArgumentException("Cookie name not specified.");

        return awsHttpClient.createAppCookieStickinessPolicy(request);
    }
    @Override
    public CreateAppCookieStickinessPolicyResult createAppCookieStickinessPolicy(String loadBalancerName, String policyName, String cookieName){
        
        return createAppCookieStickinessPolicy(
                new CreateAppCookieStickinessPolicyRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withPolicyName(policyName)
                    .withCookieName(cookieName));
    }
    
    @Override
    public CreateLBCookieStickinessPolicyResult createLBCookieStickinessPolicy(CreateLBCookieStickinessPolicyRequest request){
    
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getPolicyName() == null || request.getPolicyName().isEmpty())
            throw new IllegalArgumentException("Policy name not specified.");

        return awsHttpClient.createLBCookieStickinessPolicy(request);
    }
    @Override
    public CreateLBCookieStickinessPolicyResult createLBCookieStickinessPolicy(String loadBalancerName, String policyName){
        
        return createLBCookieStickinessPolicy(
                new CreateLBCookieStickinessPolicyRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withPolicyName(policyName));
    }
    
    @Override
    public void setLoadBalancerListenerSSLCertificate(SetLoadBalancerListenerSSLCertificateRequest request){
        
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getLoadBalancerPort() == null)
            throw new IllegalArgumentException("Load balancer port not specified.");
        if(request.getSSLCertificateId() == null || request.getSSLCertificateId().isEmpty())
            throw new IllegalArgumentException("SSL certificate id not specified.");

        awsHttpClient.setLoadBalancerListenerSSLCertificate(request);
    }

    @Override
    public void setLoadBalancerListenerSSLCertificate(String loadBalancerName, int port, String certificateId){
        
        setLoadBalancerListenerSSLCertificate(
                new SetLoadBalancerListenerSSLCertificateRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withLoadBalancerPort(port)
                    .withSSLCertificateId(certificateId));
    }
    
    @Override
    public SetLoadBalancerPoliciesOfListenerResult setLoadBalancerPoliciesOfListener(SetLoadBalancerPoliciesOfListenerRequest request){
        
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getLoadBalancerPort() == null)
            throw new IllegalArgumentException("Load balancer port not specified.");
        if(request.getPolicyNames() == null || request.getPolicyNames().isEmpty())
            throw new IllegalArgumentException("Policy name not specified.");

        return awsHttpClient.setLoadBalancerPoliciesOfListener(request);
    }
    @Override
    public SetLoadBalancerPoliciesOfListenerResult setLoadBalancerPoliciesOfListener(String loadBalancerName, int port, Collection<String> policyNames){
        
        return setLoadBalancerPoliciesOfListener(
                new SetLoadBalancerPoliciesOfListenerRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withLoadBalancerPort(port)
                    .withPolicyNames(policyNames));
        
    }
    @Override
    public SetLoadBalancerPoliciesOfListenerResult setLoadBalancerPoliciesOfListener(String loadBalancerName, int port, String policyName){
                
        return setLoadBalancerPoliciesOfListener(loadBalancerName, port, singleton(policyName));
        
    }

    @Override
    public ApplySecurityGroupsToLoadBalancerResult applySecurityGroupsToLoadBalancer(ApplySecurityGroupsToLoadBalancerRequest request){
       
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getSecurityGroups() == null || request.getSecurityGroups().isEmpty())
            throw new IllegalArgumentException("Security groups not specified.");
        
        return awsHttpClient.applySecurityGroupsToLoadBalancer(request);
    }
    @Override
    public ApplySecurityGroupsToLoadBalancerResult applySecurityGroupsToLoadBalancer(String loadBalancerName, Collection<String> securityGroups){
       
        return applySecurityGroupsToLoadBalancer(
                new ApplySecurityGroupsToLoadBalancerRequest()
                    .withLoadBalancerName(loadBalancerName)
                    .withSecurityGroups(securityGroups));
    }
    @Override
    public ApplySecurityGroupsToLoadBalancerResult applySecurityGroupsToLoadBalancer(String loadBalancerName, String securityGroup){

        return applySecurityGroupsToLoadBalancer(loadBalancerName, singleton(securityGroup));
    }
    
    @Override
    public DescribeLoadBalancerPolicyTypesResult describeLoadBalancerPolicyTypes(){
        return awsHttpClient.describeLoadBalancerPolicyTypes();
    }
    @Override
    public DescribeLoadBalancerPolicyTypesResult describeLoadBalancerPolicyTypes(DescribeLoadBalancerPolicyTypesRequest request){
        if(request == null)
            throw new IllegalArgumentException("Invalid request.");
        return awsHttpClient.describeLoadBalancerPolicyTypes(request);
    }
    @Override
    public DescribeLoadBalancerPolicyTypesResult describeLoadBalancerPolicyTypes(List<String> policyTypeNames){
        return describeLoadBalancerPolicyTypes(new DescribeLoadBalancerPolicyTypesRequest()
                                                    .withPolicyTypeNames(policyTypeNames));
    }
    @Override
    public DescribeLoadBalancerPolicyTypesResult describeLoadBalancerPolicyTypes(String policyTypeName){
        if(policyTypeName == null || policyTypeName.isEmpty())
            throw new IllegalArgumentException("Policy type name not specified.");
        return describeLoadBalancerPolicyTypes(singletonList(policyTypeName));
    }
    
    @Override
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(ModifyLoadBalancerAttributesRequest request){
        if(request.getLoadBalancerName() == null || request.getLoadBalancerName().isEmpty())
            throw new IllegalArgumentException("Load balancer name not specified.");
        if(request.getLoadBalancerAttributes() == null)
            throw new IllegalArgumentException("Load balancer attributes not specified.");
        return awsHttpClient.modifyLoadBalancerAttributes(request);
    }
    @Override
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(String loadBalancerName, LoadBalancerAttributes attributes){
        return modifyLoadBalancerAttributes(new ModifyLoadBalancerAttributesRequest()
                                                .withLoadBalancerName(loadBalancerName)
                                                .withLoadBalancerAttributes(attributes));
    }
    @Override
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(String loadBalancerName, AccessLog accessLog){
        
        if(accessLog == null)
            throw new IllegalArgumentException("Access log not specified.");
        
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new LoadBalancerAttributes()
                                                .withAccessLog(accessLog));
    }
    @Override
    public ModifyLoadBalancerAttributesResult enableAccessLog(String loadBalancerName){
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new AccessLog().withEnabled(Boolean.TRUE));
    }
    @Override
    public ModifyLoadBalancerAttributesResult enableAccessLog(String loadBalancerName, Integer emitInterval){
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new AccessLog().withEnabled(Boolean.TRUE)
                                                .withEmitInterval(emitInterval));
    }
    @Override
    public ModifyLoadBalancerAttributesResult enableAccessLog(String loadBalancerName, String s3BucketName, String s3BucketPrefix){
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new AccessLog().withEnabled(Boolean.TRUE)
                                                .withS3BucketName(s3BucketName)
                                                .withS3BucketPrefix(s3BucketPrefix));
    }
    @Override
    public ModifyLoadBalancerAttributesResult disableAccessLog(String loadBalancerName){
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new AccessLog().withEnabled(Boolean.FALSE));
    }
    @Override
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(String loadBalancerName, ConnectionDraining draining){
        
        if(draining == null)
            throw new IllegalArgumentException("ConnectionDraining not specified.");
        
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new LoadBalancerAttributes()
                                                .withConnectionDraining(draining));
    }
    @Override
    public ModifyLoadBalancerAttributesResult enableConnectionDraining(String loadBalancerName){
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new ConnectionDraining().withEnabled(Boolean.TRUE));
    }
    @Override
    public ModifyLoadBalancerAttributesResult enableConnectionDraining(String loadBalancerName, Integer timeout){

        if(timeout == null)
            throw new IllegalArgumentException("ConnectionDraining timeout not specified.");
        
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new ConnectionDraining().withEnabled(Boolean.TRUE)
                                                .withTimeout(timeout));
    }
    @Override
    public ModifyLoadBalancerAttributesResult disableConnectionDraining(String loadBalancerName){
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new ConnectionDraining().withEnabled(Boolean.FALSE));
    }

    @Override
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(String loadBalancerName, ConnectionSettings settings){
        
        if(settings == null)
            throw new IllegalArgumentException("ConnectionSettings not specified.");
        
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new LoadBalancerAttributes()
                                                .withConnectionSettings(settings));
    }
    @Override
    public ModifyLoadBalancerAttributesResult configureIdleTimeout(String loadBalancerName, Integer idleTimeout){
        
        if(idleTimeout == null)
            throw new IllegalArgumentException("Idle timeout not specified.");
        
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new ConnectionSettings()
                                                .withIdleTimeout(idleTimeout));
    }
    @Override
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(String loadBalancerName, CrossZoneLoadBalancing crossZoneBalancing){
        
        if(crossZoneBalancing == null)
            throw new IllegalArgumentException("CrossZoneLoadBalancing not specified.");
        
        return modifyLoadBalancerAttributes(loadBalancerName,
                                            new LoadBalancerAttributes()
                                                .withCrossZoneLoadBalancing(crossZoneBalancing));
    }
}
