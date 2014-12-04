/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

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
import web.component.impl.CloudBlock;

/**
 *
 * @author Hiroshi
 */
public interface AWSEC2 extends CloudBlock{
    
    public RunInstancesResult runInstances(RunInstancesRequest request);
    public Instance createInstance(String imageId, String instanceType, String zoneName);
    public Instance createInstance(String imageId, String instanceType);
    public StartInstancesResult startInstances(StartInstancesRequest request);
    public StartInstancesResult startInstances(List<String> instanceIds);
    public StartInstancesResult startInstance(String instanceId);
    public StopInstancesResult stopInstances(StopInstancesRequest request);
    public StopInstancesResult stopInstances(List<String> instanceIds);
    public StopInstancesResult stopInstance(String instanceId);
    public DescribeInstancesResult describeInstances();
    public DescribeInstancesResult describeInstances(DescribeInstancesRequest request);
    public DescribeInstancesResult describeInstances(List<String> instanceIds);
    public DescribeInstancesResult describeInstance(String instanceId);
    public List<Instance> getExistEc2Instances();
    public Instance getExistEc2Instance(String instanceId);
    public InstanceState getInstanceState(String instanceId);
    public String getInstanceStateName(String instanceId);
    public Integer getInstanceStateCode(String instanceId);
    public String getInstanceStateTransitionReason(String instanceId);
    public TerminateInstancesResult terminateInstances(TerminateInstancesRequest request);
    public TerminateInstancesResult terminateInstances(List<String> instanceIds);
    public TerminateInstancesResult terminateInstance(String instanceId);
    public DescribeAvailabilityZonesResult describeAvailabilityZones();
    public DescribeAvailabilityZonesResult describeAvailabilityZones(DescribeAvailabilityZonesRequest request);
    public DescribeAvailabilityZonesResult describeAvailabilityZones(List<String> zoneNames);
    public DescribeAvailabilityZonesResult describeAvailabilityZone(String zoneName);
    public List<AvailabilityZone> getExistEc2AvailabilityZones();
    public AvailabilityZone getExistEc2AvailabilityZone(String zoneName);
    public DescribeSubnetsResult describeSubnets();
    public DescribeSubnetsResult describeSubnets(DescribeSubnetsRequest request);
    public DescribeSubnetsResult describeSubnets(List<String> subnetIds);
    public DescribeSubnetsResult describeSubnet(String subnetId);
    public List<Subnet> getExistEc2Subnets();
    public Subnet getExistEc2Subnet(String subnetId);
    public CreateSubnetResult createSubnet(CreateSubnetRequest request);
    public CreateSubnetResult createSubnet(String vpcId, String cidrBlock, String availabilityZone);
    public CreateSubnetResult createSubnet(String vpcId, String cidrBlock);
    public Subnet getNewSubnet(String vpcId, String cidrBlock, String availabilityZone);
    public void deleteSubnet(DeleteSubnetRequest request);
    public void deleteSubnet(String subnetId);
    public DescribeVpcsResult describeVpcs();
    public DescribeVpcsResult describeVpcs(DescribeVpcsRequest request);
    public DescribeVpcsResult describeVpcs(List<String> vpcIds);
    public DescribeVpcsResult describeVpc(String vpcId);
    public List<Vpc> getExistEc2Vpcs();
    public Vpc getExistEc2Vpc(String vpcId);
    public CreateVpcResult createVpc(CreateVpcRequest request);
    public CreateVpcResult createVpc(String cidrBlock, String tenancy);
    public Vpc getNewVpc(String cidrBlock, String tenancy);
    public void deleteVpc(DeleteVpcRequest request);
    public void deleteVpc(String vpcId);
    public Instance createDefaultInstance(); 
}
