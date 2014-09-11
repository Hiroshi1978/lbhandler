lbhandler
=========

We aim to offer libraries that makes it possible to handle resources of some web services including AWS Elastic Load Balancing (ELB), Cloudn Load Balancing Advanced (LBA), as the ordinary instances of Java classes on running JVM. And so help build your application that utilizes ELB or LBA.


## Usage

 * [Create new load balancer](#how-to-create-new-load-balancer-)
 * [Get information about load balancer](#how-to-get-information-about-the-created-load-balancer-)
 * [Register and deregister web servers](#lets-register-your-web-servers-with-the-load-balancer)

## Some details

 * [Dependency](#dependency)
 * [Set up HTTP Client](#setting-up-http-client)
 * [Further learning](#want-to-learn-elb-or-lba-)

### How to create new load balancer ?

Maybe you can write like this.

```java
    LoadBalancer lb = new LoadBalancerImpl.Builder("MyLB")
                        .defaultHttpListener().zones("zone-name").build();
```

Or, if you use VPC,

```java
    LoadBalancer lb = new LoadBalancerImpl.Builder("MyLB")
                        .defaultHttpListener().subnet("subnet-id").build();
```

If you need secure system with port 443, then this will go.

```java
    LoadBalancer lb = new LoadBalancerImpl.Builder("MyLB")
                        .defaultHttpsListener("ssl-certificate-id").build();
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
    BackendInstance webServerNo1  = BackendInstanceImpl.create("web-server-1");
    BackendInstance webServerNo2  = BackendInstanceImpl.create("web-server-2");
    . . . . . .
    BackendInstance webServerNo10 = BackendInstanceImpl.create("web-server-10");
    
    List<BackendInstance> webServers = new ArrayList<>();

    myWebServers.add(webServerNo1);
    myWebServers.add(webServerNo2);
    . . . . . .
    myWebServers.add(webServerNo10);

    lb.registerInstances(webServers);
```

Or, you can do the same thing in this way also.

```java
    for(BackendInstance webServer : webServers)
        webServer.registerWith(lb);
```

For checking the state of the server,

```java
    BackendInstanceState state = webServerNo1.getBackendInstanceState();
    
    System.out.println("ID            : " + state.getId());
    System.out.println("Service state : " + state.getState());
    System.out.println("Reason        : " + state.getReasonCode());
    System.out.println("Description   : " + state.getDescription());
```


Then, if you want to deregister them,

```java
    lb.deregisterInstances(webServers);
```

And again, this is as good.

```java
    for(BackendInstance webServer : webServers)
        webServer.deregisterFrom(lb);
```

### Dependency

Our library has some implementation that works with AWS. Please check the classes in [web.component.impl.aws.elb packages](https://github.com/Hiroshi1978/lbhandler/tree/master/web/component/impl/aws.elb) for such codes. Those classes needs AWS SDK for Java. It is necessary for paths to the JAR files that contain SDK to be included in your class path.
You can download AWS SDK for Java from [here](https://aws.amazon.com/jp/sdkforjava/).
To inspect the source codes, visit [GitHub AWS SDK for Java](https://github.com/aws/aws-sdk-java).

### Setting up HTTP Client

To use AWS ELB implementation, you have to create two files and prepare some configuration parameters for http client which communicates with AWS ELB following these steps.

###### Step 1. 
Create text file with name 'credentials.txt' in the parent directory of the directory where AWSElasticLoadBalancing.class exists (web/component/impl/aws).

###### Step 2. 
Edit it following the sample below, and save it.

```
    aws.key=YOURAWSACCESSKEY
    aws.secret=YOURAWSSECRETKEYCORRESPONDINGTOTHEACCESSKEY
```

###### Step 3. 
Create text file with name 'httpclient_config.txt' in the same directory.

###### Step 4. 
Edit it following the sample below, and save it. In most cases the value to the key 'signer.Type' is 'AWS4SignerType'.

```
    endpoint=protocol://endpoint.of.service/path
    servicename=properServiceName
    region=properRegionName
    signer.type=AWS4SignerType
```


### Want to learn ELB or LBA ?

 * [Amazon Elastic Load Balancing](http://aws.amazon.com/jp/elasticloadbalancing/)
 * [Cloudn Load Balancing Advanced (LBA)](http://www.ntt.com/cloudn/data/lba.html)
