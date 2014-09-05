lbhandler
=========

We aim to offer libraries that makes it possible to handle resources of some web services including AWS Elastic Load Balancing (ELB), Cloudn Load Balancing Advanced (LBA), as the ordinary instances of Java classes on running JVM. And so help build your application that utilizes ELB or LBA.


### How to create new load balancer ?

Maybe you can write like this.

```java

    LoadBalancer lb = new LoadBalancerImpl.Builder("MyFirstLoadBalancer")
                    .defaultHttpListener()
                    .zones("availability-zone-name")
                    .build();

```

Or, if you use VPC,

```java

    LoadBalancer lb = new LoadBalancerImpl.Builder("MyFirstLoadBalancer")
                    .defaultHttpListener()
                    .subnet("your-subnet-id")
                    .build();

```

If you need secure system with port 443, then this will go.

```java

    LoadBalancer lb = new LoadBalancerImpl.Builder("MyFirstLoadBalancer")
                    .defaultHttpsListener("your-registered-certificate-id")
                    .build();

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
        System.out.println(" Describe my load balancer. ");
        System.out.println(" ------------------------ ");
        System.out.println(lb.getName());
        System.out.println(lb.getListeners());
        System.out.println(lb.getOtherUsefulInformation());
        System.out.println(" ------------------------ ");

```

And the result may be like this.

    Wait for the lb to be started...
    Wait for the lb to be started...
    
     Describe my load balancer. 
     ------------------------ 
    MyFirstLoadBalancer
    [{Protocol: HTTP,LoadBalancerPort: 80,InstanceProtocol: HTTP,InstancePort: 80,}]
    Self Introduction : 'I'm very dilligent !'
     ------------------------ 


### Let's register your web servers with the load balancer.

It's very simple.

```java

    BackendInstance myWebServerNo1 = BackendInstanceImpl.create("id-of-you-web-server-no-1");
    BackendInstance myWebServerNo2 = BackendInstanceImpl.create("id-of-you-web-server-no-2");
    
    List<BackendInstance> myWebServers = new ArrayList<>();
    myWebServers.add(myWebServerNo1);
    myWebServers.add(myWebServerNo2);
    lb.registerInstances(myWebServers);

```

Or, you can do the same thing in this way also.

```java

    myWebServerNo1.registerWith(lb);
    myWebServerNo2.registerWith(lb);

```

For checking the state of the server,

```java

    BackendInstanceState state = myWebServerNo1.getBackendInstanceState();
    
    System.out.println("ID            : " + state.getId());
    System.out.println("Service state : " + state.getState());
    System.out.println("Reason        : " + state.getReasonCode());
    System.out.println("Description   : " + state.getDescription());

```


Then, if you want to deregister them,

```java

    lb.deregisterInstances(myWebServers);
    
```

And again, this is as good.

```java

    myWebServerNo1.deregisterFrom(lb);
    myWebServerNo2.deregisterFrom(lb);
    
```

### Dependency

Our library has some implementation that works with AWS. Please check the classes in [web.component.impl.awselb packages](https://github.com/Hiroshi1978/lbhandler/tree/master/web/component/impl/awselb) for such codes. Those classes needs AWS SDK for Java. It is necessary for paths to the JAR files that contain SDK to be included in your class path.
You can download AWS SDK for Java from [here](https://aws.amazon.com/jp/sdkforjava/).
To inspect the source codes, visit [GitHub AWS SDK for Java](https://github.com/aws/aws-sdk-java).

### Want to learn ELB or LBA ?

 * [Amazon Elastic Load Balancing](http://aws.amazon.com/jp/elasticloadbalancing/)
 * [Cloudn Load Balancing Advanced (LBA)](http://www.ntt.com/cloudn/data/lba.html)
