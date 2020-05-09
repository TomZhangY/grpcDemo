import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 15:03 2020/5/9
 */
public class HelloWorldClient {

    private  ManagedChannel channel = null ;
    private  GreeterGrpc.GreeterBlockingStub blockingStub = null ;
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldClient.class.getName());
    ManagedChannelBuilder builder = null;
    public HelloWorldClient(String host,int port){
        builder =  ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext();
        channel = builder.build();
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }


    public void shutdown() throws InterruptedException {
//        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        channel.shutdown();
    }

    public  void greet(String name){

        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try{
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e)
        {
            logger.info("RPC failed: "+ e.getStatus());
            return;
        }
        logger.info("Greeting: "+response.getMessage());
//        channel.shutdown();

    }

    public static void main(String[] args) throws InterruptedException {
        HelloWorldClient client = new HelloWorldClient("10.78.115.104",50000);
        try{
            String user = "world";
            if (args.length > 0){
                user = args[0];
            }
            client.greet(user);
            client.greet(user);
            client.greet(user);
            client.greet(user);
            client.greet(user);

        }finally {
            client.shutdown();
        }

        Thread.sleep(60000);
    }
}
