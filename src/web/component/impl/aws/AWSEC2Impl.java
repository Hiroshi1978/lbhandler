/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateSubnetRequest;
import com.amazonaws.services.ec2.model.CreateSubnetResult;
import com.amazonaws.services.ec2.model.CreateVpcRequest;
import com.amazonaws.services.ec2.model.CreateVpcResult;
import com.amazonaws.services.ec2.model.DeleteSubnetRequest;
import com.amazonaws.services.ec2.model.DeleteVpcRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import com.amazonaws.services.ec2.model.Vpc;
import java.util.List;
import java.util.Properties;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author Hiroshi
 */
public class AWSEC2Impl implements AWSEC2{
    
    private final AmazonEC2 awsHttpClient;
    
    private AWSEC2Impl(){
        
        AmazonEC2Client initialClient = new AmazonEC2Client(((AWSImpl)AWS.access()).credentials());
        setUpHttpClient(initialClient);
        awsHttpClient = initialClient;
    }
    
    private void setUpHttpClient(AmazonEC2Client awsEC2Client){

        Properties conf = ((AWSImpl)AWS.access()).conf();
        String endpoint = conf.getProperty("ec2.endpoint");
        String serviceName = conf.getProperty("ec2.servicename");
        String regionName = conf.getProperty("region");
        
        awsEC2Client.setEndpoint(endpoint, serviceName, regionName);
        awsEC2Client.setServiceNameIntern(serviceName);
    }
    
    static AWSEC2Impl get(){
        return new AWSEC2Impl();
    }
    
    @Override
    public RunInstancesResult runInstances(RunInstancesRequest request){
        
        if(request.getImageId() == null || request.getImageId().isEmpty())
            throw new IllegalArgumentException("Image ID not specified.");
        if(request.getInstanceType() == null || request.getInstanceType().isEmpty())
            throw new IllegalArgumentException("Instance type not specified.");
        if(request.getMinCount() == null)
            request.setMinCount(1);
        if(request.getMaxCount() == null)
            request.setMaxCount(1);
            
        return awsHttpClient.runInstances(request);
    }
    @Override
    public Instance createInstance(String imageId, String instanceType, String zoneName){
        
        return runInstances(new RunInstancesRequest()
                                .withImageId(imageId)
                                .withInstanceType(instanceType)
                                .withPlacement((new Placement(zoneName))))
                    .getReservation().getInstances().stream()
                    .findFirst().get();
    }
    @Override
    public Instance createInstance(String imageId, String instanceType){
        return createInstance(imageId, instanceType, null);
    }
    
    @Override
    public StartInstancesResult startInstances(StartInstancesRequest request){

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
        return awsHttpClient.startInstances(request);
    }
    @Override
    public StartInstancesResult startInstances(List<String> instanceIds){
        return startInstances(new StartInstancesRequest(instanceIds));
    }
    @Override
    public StartInstancesResult startInstance(String instanceId){
        return startInstances(singletonList(instanceId));
    }    
    
    @Override
    public StopInstancesResult stopInstances(StopInstancesRequest request){

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
        return awsHttpClient.stopInstances(request);
    }
    @Override
    public StopInstancesResult stopInstances(List<String> instanceIds){
        return stopInstances(new StopInstancesRequest(instanceIds));
    }
    @Override
    public StopInstancesResult stopInstance(String instanceId){
        return stopInstances(singletonList(instanceId));
    }
    
    @Override
    public DescribeInstancesResult describeInstances(){
        return awsHttpClient.describeInstances();
    }
    @Override
    public DescribeInstancesResult describeInstances(DescribeInstancesRequest request){

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
        return awsHttpClient.describeInstances(request);
    }
    @Override
    public DescribeInstancesResult describeInstances(List<String> instanceIds){
        return describeInstances(new DescribeInstancesRequest()
                                        .withInstanceIds(instanceIds));
    }
    @Override
    public DescribeInstancesResult describeInstance(String instanceId){
        return describeInstances(singletonList(instanceId));
    }
    @Override
    public List<Instance> getExistEc2Instances(){
        return describeInstances().getReservations().stream()
                .flatMap(reserv -> reserv.getInstances().stream())
                .collect(toList());
    }
    @Override
    public Instance getExistEc2Instance(String instanceId){
        //return the instance of the specified instanceId, or null if it does not exist.
        return describeInstance(instanceId).getReservations().stream()
                .flatMap(reserv -> reserv.getInstances().stream())
                .findFirst().orElse(null);
    }
    @Override
    public InstanceState getInstanceState(String instanceId){
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? null : existInstance.getState();
    }
    @Override
    public String getInstanceStateName(String instanceId){
        
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? "Unknown state" : existInstance.getState().getName();
    }
    @Override
    public Integer getInstanceStateCode(String instanceId){
        
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? -1 : existInstance.getState().getCode();
    }
    @Override
    public String getInstanceStateTransitionReason(String instanceId){
        
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? "Unknown reason" : existInstance.getStateTransitionReason();
    }
    
    @Override
    public TerminateInstancesResult terminateInstances(TerminateInstancesRequest request){

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
        return awsHttpClient.terminateInstances(request);
    }
    @Override
    public TerminateInstancesResult terminateInstances(List<String> instanceIds){
        return terminateInstances(new TerminateInstancesRequest(instanceIds));
    }
    @Override
    public TerminateInstancesResult terminateInstance(String instanceId){
        return terminateInstances(singletonList(instanceId));
    }
    
    @Override
    public DescribeAvailabilityZonesResult describeAvailabilityZones(){
        return awsHttpClient.describeAvailabilityZones();
    }
    @Override
    public DescribeAvailabilityZonesResult describeAvailabilityZones(DescribeAvailabilityZonesRequest request){

        if(request.getZoneNames()== null || request.getZoneNames().isEmpty())
            throw new IllegalArgumentException("Availability zones not specified.");
               
        return awsHttpClient.describeAvailabilityZones(request);
    }
    @Override
    public DescribeAvailabilityZonesResult describeAvailabilityZones(List<String> zoneNames){
        return describeAvailabilityZones(new DescribeAvailabilityZonesRequest()
                                                .withZoneNames(zoneNames));
    }
    @Override
    public DescribeAvailabilityZonesResult describeAvailabilityZone(String zoneName){
        return describeAvailabilityZones(singletonList(zoneName));
    }
    @Override
    public List<AvailabilityZone> getExistEc2AvailabilityZones(){
        return describeAvailabilityZones().getAvailabilityZones();
    }
    @Override
    public AvailabilityZone getExistEc2AvailabilityZone(String zoneName){
        return describeAvailabilityZone(zoneName).getAvailabilityZones().stream()
                .findFirst().get();
    }
    
    @Override
    public DescribeSubnetsResult describeSubnets(){
        return awsHttpClient.describeSubnets();
    }    
    @Override
    public DescribeSubnetsResult describeSubnets(DescribeSubnetsRequest request){
        if(request.getSubnetIds() == null || request.getSubnetIds().isEmpty())
            throw new IllegalArgumentException("Subnet IDs not specified.");
        return awsHttpClient.describeSubnets(request);
    }    
    @Override
    public DescribeSubnetsResult describeSubnets(List<String> subnetIds){
        return describeSubnets(new DescribeSubnetsRequest()
                                    .withSubnetIds(subnetIds));
    }
    @Override
    public DescribeSubnetsResult describeSubnet(String subnetId){
        return describeSubnets(singletonList(subnetId));
    }
    @Override
    public List<Subnet> getExistEc2Subnets(){
        return describeSubnets().getSubnets();
    }
    @Override
    public Subnet getExistEc2Subnet(String subnetId){
        return describeSubnet(subnetId).getSubnets().stream()
                .findFirst().orElse(null);
    }
    
    @Override
    public CreateSubnetResult createSubnet(CreateSubnetRequest request){

        if(request.getVpcId() == null || request.getVpcId().isEmpty())
            throw new IllegalArgumentException("VPC ID not specified.");
        if(request.getCidrBlock()== null || request.getCidrBlock().isEmpty())
            throw new IllegalArgumentException("CIDR block not specified.");
        
        return awsHttpClient.createSubnet(request);
    }
    @Override
    public CreateSubnetResult createSubnet(String vpcId, String cidrBlock, String availabilityZone){

        return createSubnet(new CreateSubnetRequest()
                            .withVpcId(vpcId)
                            .withCidrBlock(cidrBlock)
                            .withAvailabilityZone(availabilityZone));
    }
    @Override
    public CreateSubnetResult createSubnet(String vpcId, String cidrBlock){
        return createSubnet(vpcId, cidrBlock, null);
    }
    @Override
    public Subnet getNewSubnet(String vpcId, String cidrBlock, String availabilityZone){
        return createSubnet(vpcId, cidrBlock, availabilityZone).getSubnet();
    }
    
    @Override
    public void deleteSubnet(DeleteSubnetRequest request){
        
        if(request.getSubnetId()== null || request.getSubnetId().isEmpty())
            throw new IllegalArgumentException("Subnet ID not specified.");
        
        awsHttpClient.deleteSubnet(request);
    }
    @Override
    public void deleteSubnet(String subnetId){
        deleteSubnet(new DeleteSubnetRequest(subnetId));
    }

    @Override
    public DescribeVpcsResult describeVpcs(){
        return awsHttpClient.describeVpcs();
    }    
    @Override
    public DescribeVpcsResult describeVpcs(DescribeVpcsRequest request){
        if(request.getVpcIds()== null || request.getVpcIds().isEmpty())
            throw new IllegalArgumentException("VPC IDs not specified.");
        return awsHttpClient.describeVpcs(request);
    }    
    @Override
    public DescribeVpcsResult describeVpcs(List<String> vpcIds){
        return describeVpcs(new DescribeVpcsRequest().withVpcIds(vpcIds));
    }
    @Override
    public DescribeVpcsResult describeVpc(String vpcId){
        return describeVpcs(singletonList(vpcId));
    }
    @Override
    public List<Vpc> getExistEc2Vpcs(){
        return describeVpcs().getVpcs();
    }
    @Override
    public Vpc getExistEc2Vpc(String vpcId){
        
        return describeVpc(vpcId).getVpcs().stream()
                .findFirst().orElse(null);
    }
    
    @Override
    public CreateVpcResult createVpc(CreateVpcRequest request){

        if(request.getCidrBlock()== null || request.getCidrBlock().isEmpty())
            throw new IllegalArgumentException("CIDR block not specified.");
        if(request.getInstanceTenancy()== null || request.getInstanceTenancy().isEmpty())
            throw new IllegalArgumentException("Instance tenancy not specified.");
        
        return awsHttpClient.createVpc(request);
    }
    @Override
    public CreateVpcResult createVpc(String cidrBlock, String tenancy){

        return createVpc(new CreateVpcRequest()
                                .withCidrBlock(cidrBlock)
                                .withInstanceTenancy(tenancy));
    }
    @Override
    public Vpc getNewVpc(String cidrBlock, String tenancy){
        return createVpc(cidrBlock, tenancy).getVpc();
    }
    
    @Override
    public void deleteVpc(DeleteVpcRequest request){
        
        if(request.getVpcId() == null || request.getVpcId().isEmpty())
            throw new IllegalArgumentException("VPC ID not specified.");
        
        awsHttpClient.deleteVpc(request);
    }
    @Override
    public void deleteVpc(String vpcId){
        deleteVpc(new DeleteVpcRequest(vpcId));
    }
    
    @Override
    public Instance createDefaultInstance(){
        
        Properties conf = ((AWSImpl)AWS.access()).conf();
        String defaultImageId = conf.getProperty("ec2.default.imageid");
        String defaultInstanceType = conf.getProperty("ec2.default.instancetype");
        String defaultZoneName = conf.getProperty("ec2.default.zonename");
        
        return createInstance(defaultImageId, defaultInstanceType, defaultZoneName);
    }
}
