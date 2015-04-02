lbhandler
=========

We aim to offer library that makes it possible to handle resources of some web services including Amazon EC2 and Amazon Elastic Load Balancing (ELB) seemlessly as the ordinary instances of Java classes on running JVM. And so help build your application that utilizes those web services.


## Usage

 * [Configure VPC and subnets ...](#configure-vpc-and-subnets)
 * [Launch your web server ...](#launch-web-servers)
 * [Create new load balancer ...](#how-to-create-new-load-balancer-)
 * [Get information about load balancer ...](#how-to-get-information-about-the-created-load-balancer-)
 * [Register web servers ...](#lets-register-your-web-servers-with-the-load-balancer)
 * [Inspect the state of web servers ...](#inspect-the-state-of-the-server)
 * [Deregister web servers ...](#deregister-the-server)

## Some details

 * [Dependency](#dependency)
 * [Set up HTTP Client](#setting-up-http-client)
 * [For further learning](#want-to-learn-elb-or-lba-)

### Configure VPC and subnets.

```java
    VPC v = getNewVPC( builder -> builder.cidr("1.1.0.0/16").tenancy("default") );
    
    Subnet s1 = getNewSubnet( builder -> builder.cidr("1.1.1.0/24").vpc(v.getId()).zone("z1"));
    Subnet s2 = getNewSubnet( builder -> builder.cidr("1.1.2.0/24").vpc(v.getId()).zone("z2"));
    Stream<Subnet> subnets = Stream.of(s1,s2);
```

To check The result of the sample code,
```java
    System.out.println("------------------------------");
    subnets.forEach(subnet -> {
        System.out.println("available ip : " + subnet.getAvailableIpAddressCount());
        System.out.println("cidr block   : " + subnet.getCidrBlock());
        System.out.println("id           : " + subnet.getId());
        System.out.println("state        : " + subnet.getState());
        System.out.println("vpc id       : " + subnet.getVpcId());
        System.out.println("zone         : " + subnet.getZone());
        System.out.println("------------------------------");
    });
```

The output is :

    ------------------------------
    available ip : 251
    cidr block   : 10.1.1.0/24
    id           : subnet-xxxxxxxx
    state        : available
    vpc id       : vpc-xxxxxxxx
    zone         : z1
    ------------------------------
    available ip : 251
    cidr block   : 10.1.2.0/24
    id           : subnet-yyyyyyyy
    state        : available
    vpc id       : vpc-yyyyyyyy
    zone         : z2
    ------------------------------


### Launch web servers.

Make sure you have your own AMI that contains your web server application. Next code will launch the new instance.

```java
    Instance server = 
        getNewInstance( builder -> builder.imageId("id-of-your-ami").type("instance.type") );
```

When stop it,
```java
    server.stop();
```

And then for restarting,
```java
    server.start();
```

### How to create new load balancer ?

You can write like this.

```java
    LoadBalancer lb = getNewLoadBalancer("MyLB", builder -> 
                        builder.defaultHttpListener().zones("zone-name") );
```

If you use VPC,

```java
    LoadBalancer lb = getNewLoadBalancer("MyLB", builder -> 
                        builder.defaultHttpListener().subnet("subnet-id") );
```

If you need secure system with port 443, then this will go.

```java
    LoadBalancer lb = getNewLoadBalancer("MyLB", builder -> 
                        builder.defaultHttpsListener("ssl-certificate-id").build();
```

### How to get information about the created load balancer ?

For example, you may extract some information about your load balancer in this way.

```java
        try{
            while(!lb.isStarted()){
                System.out.println("Wait for the lb to be started...");
                Thread.sleep(10000);
            }
        }catch(InterruptedException e){
            System.exit(1);
        }
        System.out.println("");
        System.out.println(" ------------------------ ");
        System.out.println(lb.getName());
        System.out.println(lb.getListeners());
        System.out.println(lb.getOtherUsefulInformation());
        System.out.println(" ------------------------ ");
```

And the result may be like this.

    Wait for the lb to be started...
    Wait for the lb to be started...
    
     ------------------------ 
    MyLB
    [{Protocol: HTTP,LoadBalancerPort: 80,InstanceProtocol: HTTP,InstancePort: 80,}]
    Self Introduction : 'I'm very dilligent !'
     ------------------------ 


### Let's register your web servers with the load balancer.

It's very simple.

```java
    Stream<Instance> servers = 
        Stream.of(serverNo1,serverNo2, ... ,serverNo20);
        
    servers.forEach(lb::registerInstance);
```

Or, you can do the same thing in this way also.

```java
    servers.forEach( server -> server.registerWith(lb));
```

### Inspect the state of the server.

You can inspect the state of the server as the backend of the load balancer, through BackendState interface with the sample code below.

```java
    BackendState state = serverNo1.getBackendState();
    
    System.out.println("ID            : " + state.getId());
    System.out.println("Service state : " + state.getState());
    System.out.println("Reason        : " + state.getReasonCode());
    System.out.println("Description   : " + state.getDescription());
```

On the other hand, You can get the running state of the same server itself directly through InstanceState interface.

```java
    InstanceState state = serverNo1.getInstanceState();
    
    System.out.println("Code : " + state.getCode());
    System.out.println("Name : " + state.getName());
```

### Deregister the server.

Then, if you want to deregister them,

```java
    lb.deregisterInstances(servers);
```

And again, this is as good.

```java
    servers.forEach(server -> server.deregisterFrom(lb));
```

### Dependency

Our library has some implementation that works with AWS. Please check the classes in [web.component.impl.aws packages](https://github.com/Hiroshi1978/lbhandler/tree/master/src/web/component/impl/aws) for such codes. Those classes needs AWS SDK for Java. It is necessary for paths to the JAR files that contain SDK to be included in your class path.
You can download AWS SDK for Java from [here](https://aws.amazon.com/jp/sdkforjava/).
To inspect the source codes, visit [GitHub AWS SDK for Java](https://github.com/aws/aws-sdk-java).

### Setting up HTTP Client

To use AWS ELB implementation, you have to create two files and prepare some configuration parameters for http client which communicates with AWS ELB following these steps.

###### Step 1. 
Create text file with name 'aws_config.txt' in the parent directory of the directory where AWS.class exists (web/component/impl/aws).

###### Step 2. 
Edit it following the sample below, and save it.In most cases the value to the key 'signer.Type' is 'AWS4SignerType'. The value to the key 'endpoint' and 'servicename' must be set to each service's specific value. 

```
    #credentials
    aws.key=YOURAWSACCESSKEY
    aws.secret=YOURAWSSECRETKEYCORRESPONDINGTOTHEACCESSKEY

    #endpoint
    autoscaling.endpoint=protocol://endpoint.of.each.service/path
    ec2.endpoint=protocol://endpoint.of.each.service/path
    elb.endpoint=protocol://endpoint.of.each.service/path

    #servicename
    autoscaling.servicename=properServiceName
    ec2.servicename=properServiceName
    elb.servicename=properServiceName

    #other parameters
    region=properRegionName
    signer.type=AWS4SignerType
```

### Want to learn more?

 * [Amazon EC2](http://aws.amazon.com/jp/ec2/)
 * [Amazon Elastic Load Balancing](http://aws.amazon.com/jp/elasticloadbalancing/)
 * [Cloudn Load Balancing Advanced (LBA)](http://www.ntt.com/cloudn/data/lba.html)
