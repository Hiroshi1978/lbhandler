lbhandler
=========

We aim to offer libraries that makes it easier to handle resources of some web services including AWS Elastic Load Balancing (ELB), Cloudn Load Balancing Advanced (LBA), and construct your application.

The sample code below uses an implementation that communicates with ELB API or the corresponding one of LBA.

### How to create new load balancer ?

Maybe you can write like this.

```java

    LoadBalancer lb = new YourImplClass.Builder("MyFirstLoadBalancer")
                    .defaultHttpListener()
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

