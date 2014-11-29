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
import com.amazonaws.services.ec2.model.Reservation;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import web.component.impl.CloudBlock;
import static java.util.Collections.singletonList;

/**
 *
 * @author Hiroshi
 */
public class AWSEC2 implements CloudBlock{
    
    private final AmazonEC2 awsHttpClient;
    
    private AWSEC2(){
        
        AmazonEC2Client initialClient = new AmazonEC2Client(AWS.access().credentials());
        setUpHttpClient(initialClient);
        awsHttpClient = initialClient;
    }
    
    private void setUpHttpClient(AmazonEC2Client awsEC2Client){

        Properties conf = AWS.access().conf();
        String endpoint = conf.getProperty("ec2.endpoint");
        String serviceName = conf.getProperty("ec2.servicename");
        String regionName = conf.getProperty("region");
        
        awsEC2Client.setEndpoint(endpoint, serviceName, regionName);
        awsEC2Client.setServiceNameIntern(serviceName);
    }
    
    static AWSEC2 get(){
        return new AWSEC2();
    }
    
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
    public Instance createInstance(String imageId, String instanceType, String zoneName){
        
        return runInstances(new RunInstancesRequest()
                                .withImageId(imageId)
                                .withInstanceType(instanceType)
                                .withPlacement((new Placement(zoneName))))
                    .getReservation()
                    .getInstances()
                    .get(0);
    }
    public Instance createInstance(String imageId, String instanceType){
        return createInstance(imageId, instanceType, null);
    }
    
    public StartInstancesResult startInstances(StartInstancesRequest request){

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
        return awsHttpClient.startInstances(request);
    }
    public StartInstancesResult startInstances(List<String> instanceIds){
        return startInstances(new StartInstancesRequest(instanceIds));
    }
    public StartInstancesResult startInstance(String instanceId){
        return startInstances(singletonList(instanceId));
    }    
    
    public StopInstancesResult stopInstances(StopInstancesRequest request){

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
        return awsHttpClient.stopInstances(request);
    }
    public StopInstancesResult stopInstances(List<String> instanceIds){
        return stopInstances(new StopInstancesRequest(instanceIds));
    }
    public StopInstancesResult stopInstance(String instanceId){
        return stopInstances(singletonList(instanceId));
    }
    
    public DescribeInstancesResult describeInstances(){
        return awsHttpClient.describeInstances();
    }
    public DescribeInstancesResult describeInstances(DescribeInstancesRequest request){

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
        return awsHttpClient.describeInstances(request);
    }
    public DescribeInstancesResult describeInstances(List<String> instanceIds){
        return describeInstances(new DescribeInstancesRequest()
                                        .withInstanceIds(instanceIds));
    }
    public DescribeInstancesResult describeInstance(String instanceId){
        return describeInstances(singletonList(instanceId));
    }
    public List<Instance> getExistEc2Instances(){
        DescribeInstancesResult result = describeInstances();
        List<Instance> instances = new ArrayList<>();
        for(Reservation reserv : result.getReservations())
            for(Instance instance : reserv.getInstances())
                instances.add(instance);
        return instances;
    }
    public Instance getExistEc2Instance(String instanceId){
        
        Instance existInstance = null;
        List<Reservation> reservs = describeInstance(instanceId).getReservations();
        if(reservs != null && !reservs.isEmpty()){
            existInstance = reservs.get(0).getInstances().get(0);
        }
        
        return existInstance;
    }
    public InstanceState getInstanceState(String instanceId){
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? null : existInstance.getState();
    }
    public String getInstanceStateName(String instanceId){
        
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? "Unknown state" : existInstance.getState().getName();
    }
    public Integer getInstanceStateCode(String instanceId){
        
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? -1 : existInstance.getState().getCode();
    }
    public String getInstanceStateTransitionReason(String instanceId){
        
        Instance existInstance = getExistEc2Instance(instanceId);
        return existInstance == null ? "Unknown reason" : existInstance.getStateTransitionReason();
    }
    
    public TerminateInstancesResult terminateInstances(TerminateInstancesRequest request){

        if(request.getInstanceIds() == null || request.getInstanceIds().isEmpty())
            throw new IllegalArgumentException("Instance ID not specified.");
        
        return awsHttpClient.terminateInstances(request);
    }
    public TerminateInstancesResult terminateInstances(List<String> instanceIds){
        return terminateInstances(new TerminateInstancesRequest(instanceIds));
    }
    public TerminateInstancesResult terminateInstance(String instanceId){
        return terminateInstances(singletonList(instanceId));
    }
    
    public DescribeAvailabilityZonesResult describeAvailabilityZones(){
        return awsHttpClient.describeAvailabilityZones();
    }
    public DescribeAvailabilityZonesResult describeAvailabilityZones(DescribeAvailabilityZonesRequest request){

        if(request.getZoneNames()== null || request.getZoneNames().isEmpty())
            throw new IllegalArgumentException("Availability zones not specified.");
               
        return awsHttpClient.describeAvailabilityZones(request);
    }
    public DescribeAvailabilityZonesResult describeAvailabilityZones(List<String> zoneNames){
        return describeAvailabilityZones(new DescribeAvailabilityZonesRequest()
                                                .withZoneNames(zoneNames));
    }
    public DescribeAvailabilityZonesResult describeAvailabilityZone(String zoneName){
        return describeAvailabilityZones(singletonList(zoneName));
    }
    public List<AvailabilityZone> getExistEc2AvailabilityZones(){
        return describeAvailabilityZones().getAvailabilityZones();
    }
    public AvailabilityZone getExistEc2AvailabilityZone(String zoneName){
        return describeAvailabilityZone(zoneName).getAvailabilityZones().get(0);
    }
    
    public DescribeSubnetsResult describeSubnets(){
        return awsHttpClient.describeSubnets();
    }    
    public DescribeSubnetsResult describeSubnets(DescribeSubnetsRequest request){
        if(request.getSubnetIds() == null || request.getSubnetIds().isEmpty())
            throw new IllegalArgumentException("Subnet IDs not specified.");
        return awsHttpClient.describeSubnets(request);
    }    
    public DescribeSubnetsResult describeSubnets(List<String> subnetIds){
        return describeSubnets(new DescribeSubnetsRequest()
                                    .withSubnetIds(subnetIds));
    }
    public DescribeSubnetsResult describeSubnet(String subnetId){
        return describeSubnets(singletonList(subnetId));
    }
    public List<Subnet> getExistEc2Subnets(){
        return describeSubnets().getSubnets();
    }
    public Subnet getExistEc2Subnet(String subnetId){
        Subnet subnet = null;
        
        try{
            subnet = describeSubnet(subnetId).getSubnets().get(0);
        }catch(RuntimeException e){
            System.out.println("Subnet with ID[" + subnetId + "] not exist in cloud.");
        }
            
        return subnet;
    }
    
    public CreateSubnetResult createSubnet(CreateSubnetRequest request){

        if(request.getVpcId() == null || request.getVpcId().isEmpty())
            throw new IllegalArgumentException("VPC ID not specified.");
        if(request.getCidrBlock()== null || request.getCidrBlock().isEmpty())
            throw new IllegalArgumentException("CIDR block not specified.");
        
        return awsHttpClient.createSubnet(request);
    }
    public CreateSubnetResult createSubnet(String vpcId, String cidrBlock, String availabilityZone){

        return createSubnet(new CreateSubnetRequest()
                            .withVpcId(vpcId)
                            .withCidrBlock(cidrBlock)
                            .withAvailabilityZone(availabilityZone));
    }
    public CreateSubnetResult createSubnet(String vpcId, String cidrBlock){
        return createSubnet(vpcId, cidrBlock, null);
    }
    public Subnet getNewSubnet(String vpcId, String cidrBlock, String availabilityZone){
        return createSubnet(vpcId, cidrBlock, availabilityZone).getSubnet();
    }
    
    public void deleteSubnet(DeleteSubnetRequest request){
        
        if(request.getSubnetId()== null || request.getSubnetId().isEmpty())
            throw new IllegalArgumentException("Subnet ID not specified.");
        
        awsHttpClient.deleteSubnet(request);
    }
    public void deleteSubnet(String subnetId){
        deleteSubnet(new DeleteSubnetRequest(subnetId));
    }

    public DescribeVpcsResult describeVpcs(){
        return awsHttpClient.describeVpcs();
    }    
    public DescribeVpcsResult describeVpcs(DescribeVpcsRequest request){
        if(request.getVpcIds()== null || request.getVpcIds().isEmpty())
            throw new IllegalArgumentException("VPC IDs not specified.");
        return awsHttpClient.describeVpcs(request);
    }    
    public DescribeVpcsResult describeVpcs(List<String> vpcIds){
        return describeVpcs(new DescribeVpcsRequest().withVpcIds(vpcIds));
    }
    public DescribeVpcsResult describeVpc(String vpcId){
        return describeVpcs(singletonList(vpcId));
    }
    public List<Vpc> getExistEc2Vpcs(){
        return describeVpcs().getVpcs();
    }
    public Vpc getExistEc2Vpc(String vpcId){
        
        Vpc vpc = null;
        
        try{
            vpc = describeVpc(vpcId).getVpcs().get(0);
        }catch(RuntimeException e){
            System.out.println("VPC with ID[" + vpcId + "] not exist in cloud.");
        }
            
        return vpc;
    }
    
    public CreateVpcResult createVpc(CreateVpcRequest request){

        if(request.getCidrBlock()== null || request.getCidrBlock().isEmpty())
            throw new IllegalArgumentException("CIDR block not specified.");
        if(request.getInstanceTenancy()== null || request.getInstanceTenancy().isEmpty())
            throw new IllegalArgumentException("Instance tenancy not specified.");
        
        return awsHttpClient.createVpc(request);
    }
    public CreateVpcResult createVpc(String cidrBlock, String tenancy){

        return createVpc(new CreateVpcRequest()
                                .withCidrBlock(cidrBlock)
                                .withInstanceTenancy(tenancy));
    }
    public Vpc getNewVpc(String cidrBlock, String tenancy){
        return createVpc(cidrBlock, tenancy).getVpc();
    }
    
    public void deleteVpc(DeleteVpcRequest request){
        
        if(request.getVpcId() == null || request.getVpcId().isEmpty())
            throw new IllegalArgumentException("VPC ID not specified.");
        
        awsHttpClient.deleteVpc(request);
    }
    public void deleteVpc(String vpcId){
        deleteVpc(new DeleteVpcRequest(vpcId));
    }
    
    public Instance createDefaultInstance(){
        
        Properties conf = AWS.access().conf();
        String defaultImageId = conf.getProperty("ec2.default.imageid");
        String defaultInstanceType = conf.getProperty("ec2.default.instancetype");
        String defaultZoneName = conf.getProperty("ec2.default.zonename");
        
        return createInstance(defaultImageId, defaultInstanceType, defaultZoneName);
    }
}
