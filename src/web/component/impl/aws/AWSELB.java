/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

import com.amazonaws.services.ec2.model.AvailabilityZone;
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
import java.util.Collection;
import java.util.List;
import web.component.impl.CloudBlock;

/**
 *
 * @author Hiroshi
 */
public interface AWSELB extends CloudBlock{


    public DescribeLoadBalancersResult describeLoadBalancers();
    public DescribeLoadBalancersResult describeLoadBalancers(DescribeLoadBalancersRequest request);
    public DescribeLoadBalancersResult describeLoadBalancers(Collection<String> loadBalancerNames);
    public DescribeLoadBalancersResult describeLoadBalancers(String loadBalancerName);
    public CreateLoadBalancerResult createLoadBalancer(CreateLoadBalancerRequest request);
    public CreateLoadBalancerResult createLoadBalancer(String loadBalancerName);
    public CreateLoadBalancerResult createLoadBalancer(String loadBalancerName, String certificateId);
    public void deleteLoadBalancer(String loadBalancerName);
    public int deleteAllLoadBalancers();
    public RegisterInstancesWithLoadBalancerResult registerInstancesWithLoadBalancer(RegisterInstancesWithLoadBalancerRequest request);
    public RegisterInstancesWithLoadBalancerResult registerInstancesWithLoadBalancer(String loadBalancerName, Collection<Instance> instances);
    public DeregisterInstancesFromLoadBalancerResult deregisterInstancesFromLoadBalancer(DeregisterInstancesFromLoadBalancerRequest request);
    public DeregisterInstancesFromLoadBalancerResult deregisterInstancesFromLoadBalancer(String loadBalancerName, Collection<Instance> instances);
    public AttachLoadBalancerToSubnetsResult attachLoadBalancerToSubnets(AttachLoadBalancerToSubnetsRequest request);
    public AttachLoadBalancerToSubnetsResult attachLoadBalancerToSubnets(String loadBalancerName, Collection<String> subnetIds);
    public void deleteLoadBalancerListeners(DeleteLoadBalancerListenersRequest request);
    public void deleteLoadBalancerListeners(String loadBalancerName, Collection<Listener> listeners);
    public CreateLoadBalancerResult createLoadBalancer(String loadBalancerName, Collection<Listener> listeners);
    public CreateLoadBalancerResult createLoadBalancerWithSubnets(String loadBalancerName, Collection<Listener> listeners, Collection<String> subnetIds);
    public CreateLoadBalancerResult createLoadBalancerWithAvailabilityZones(String loadBalancerName, Collection<Listener> listeners, Collection<AvailabilityZone> availabilityZones);
    public DetachLoadBalancerFromSubnetsResult detachLoadBalancerFromSubnets(DetachLoadBalancerFromSubnetsRequest request);
    public DetachLoadBalancerFromSubnetsResult detachLoadBalancerFromSubnets(String loadBalancerName, Collection<String> subnetIds);
    public EnableAvailabilityZonesForLoadBalancerResult enableAvailabilityZonesForLoadBalancer(EnableAvailabilityZonesForLoadBalancerRequest request);
    public EnableAvailabilityZonesForLoadBalancerResult enableAvailabilityZonesForLoadBalancer(String loadBalancerName, Collection<String> availabilityZones);
    public DisableAvailabilityZonesForLoadBalancerResult disableAvailabilityZonesForLoadBalancer(DisableAvailabilityZonesForLoadBalancerRequest request);
    public DisableAvailabilityZonesForLoadBalancerResult disableAvailabilityZonesForLoadBalancer(String loadBalancerName, Collection<String> availabilityZones);
    public void createLoadBalancerListeners(CreateLoadBalancerListenersRequest request);
    public void createLoadBalancerListeners(String loadBalancerName, Collection<Listener> listeners);
    public void createHttpListenerOfLoadBalancerWithPort(String loadBalancerName, int instancePort, int servicePort);
    public LoadBalancerDescription getLoadBalancerDescription(String loadBalancerName);
    public List<LoadBalancerDescription> getLoadBalancerDescriptions(Collection<String> loadBalancerNames);
    public DescribeInstanceHealthResult describeInstanceHealth(DescribeInstanceHealthRequest request);
    public DescribeInstanceHealthResult describeInstanceHealth(String loadBalancerName);
    public DescribeInstanceHealthResult describeInstanceHealth(String loadBalancerName, Collection<Instance> instances);
    public DescribeInstanceHealthResult describeInstanceHealth(String loadBalancerName, Instance instance);
    public ConfigureHealthCheckResult configureHealthCheck(ConfigureHealthCheckRequest request);
    public ConfigureHealthCheckResult configureHealthCheck(String loadBalancerName, HealthCheck hc);
    public DescribeLoadBalancerPoliciesResult describeLoadBalancerPolicies();
    public DescribeLoadBalancerPoliciesResult describeLoadBalancerPolicies(DescribeLoadBalancerPoliciesRequest request);
    public DescribeLoadBalancerPoliciesResult describeLoadBalancerPolicies(String loadBalancerName);
    public CreateLoadBalancerPolicyResult createLoadBalancerPolicy(CreateLoadBalancerPolicyRequest request);
    public DeleteLoadBalancerPolicyResult deleteLoadBalancerPolicy(DeleteLoadBalancerPolicyRequest request);
    public DeleteLoadBalancerPolicyResult deleteLoadBalancerPolicy(String loadBalancerName, String policyName);
    public CreateAppCookieStickinessPolicyResult createAppCookieStickinessPolicy(CreateAppCookieStickinessPolicyRequest request);
    public CreateAppCookieStickinessPolicyResult createAppCookieStickinessPolicy(String loadBalancerName, String policyName, String cookieName);
    public CreateLBCookieStickinessPolicyResult createLBCookieStickinessPolicy(CreateLBCookieStickinessPolicyRequest request);
    public CreateLBCookieStickinessPolicyResult createLBCookieStickinessPolicy(String loadBalancerName, String policyName);
    public void setLoadBalancerListenerSSLCertificate(SetLoadBalancerListenerSSLCertificateRequest request);
    public void setLoadBalancerListenerSSLCertificate(String loadBalancerName, int port, String certificateId);
    public SetLoadBalancerPoliciesOfListenerResult setLoadBalancerPoliciesOfListener(SetLoadBalancerPoliciesOfListenerRequest request);
    public SetLoadBalancerPoliciesOfListenerResult setLoadBalancerPoliciesOfListener(String loadBalancerName, int port, Collection<String> policyNames);
    public SetLoadBalancerPoliciesOfListenerResult setLoadBalancerPoliciesOfListener(String loadBalancerName, int port, String policyName);
    public ApplySecurityGroupsToLoadBalancerResult applySecurityGroupsToLoadBalancer(ApplySecurityGroupsToLoadBalancerRequest request);
    public ApplySecurityGroupsToLoadBalancerResult applySecurityGroupsToLoadBalancer(String loadBalancerName, Collection<String> securityGroups);
    public ApplySecurityGroupsToLoadBalancerResult applySecurityGroupsToLoadBalancer(String loadBalancerName, String securityGroup);
    public DescribeLoadBalancerPolicyTypesResult describeLoadBalancerPolicyTypes();
    public DescribeLoadBalancerPolicyTypesResult describeLoadBalancerPolicyTypes(DescribeLoadBalancerPolicyTypesRequest request);
    public DescribeLoadBalancerPolicyTypesResult describeLoadBalancerPolicyTypes(List<String> policyTypeNames);
    public DescribeLoadBalancerPolicyTypesResult describeLoadBalancerPolicyTypes(String policyTypeName);
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(ModifyLoadBalancerAttributesRequest request);
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(String loadBalancerName, LoadBalancerAttributes attributes);
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(String loadBalancerName, AccessLog accessLog);
    public ModifyLoadBalancerAttributesResult enableAccessLog(String loadBalancerName);
    public ModifyLoadBalancerAttributesResult enableAccessLog(String loadBalancerName, Integer emitInterval);
    public ModifyLoadBalancerAttributesResult enableAccessLog(String loadBalancerName, String s3BucketName, String s3BucketPrefix);
    public ModifyLoadBalancerAttributesResult disableAccessLog(String loadBalancerName);
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(String loadBalancerName, ConnectionDraining draining);
    public ModifyLoadBalancerAttributesResult enableConnectionDraining(String loadBalancerName);
    public ModifyLoadBalancerAttributesResult enableConnectionDraining(String loadBalancerName, Integer timeout);
    public ModifyLoadBalancerAttributesResult disableConnectionDraining(String loadBalancerName);
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(String loadBalancerName, ConnectionSettings settings);
    public ModifyLoadBalancerAttributesResult configureIdleTimeout(String loadBalancerName, Integer idleTimeout);
    public ModifyLoadBalancerAttributesResult modifyLoadBalancerAttributes(String loadBalancerName, CrossZoneLoadBalancing crossZoneBalancing);
}
