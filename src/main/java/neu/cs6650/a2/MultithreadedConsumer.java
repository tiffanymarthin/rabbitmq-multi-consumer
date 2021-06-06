/* TODO:
  - How to print the concurrent hash map after it's completed (race condition)
  - Implement the pooling
* */

package neu.cs6650.a2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultithreadedConsumer {
  private static final Logger logger = LogManager.getLogger(MultithreadedConsumer.class.getName());
  private final static String QUEUE_NAME = "wordCountQueue";
  private final static Integer MAX_MESSAGE_PER_RECEIVER = 1;
  private final static Integer MAX_THREADS = 1;

  private static ConcurrentHashMap<String, Integer> wordMap = new ConcurrentHashMap<>();

  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    final Connection connection = factory.newConnection();

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          final Channel channel = connection.createChannel();
          channel.queueDeclare(QUEUE_NAME, true, false, false, null);
          // max one message per receiver
          channel.basicQos(MAX_MESSAGE_PER_RECEIVER);
          System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

          final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//            System.out.println(message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            System.out.println(WordCount.updateMap(message, wordMap));
            for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
              System.out.println(entry.getKey() + " val: " + entry.getValue());
            }
            logger.info("Callback thread ID = " + Thread.currentThread().getId() + " Received: " + message);
          };
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
          logger.info(e.getMessage());
        }
      }
    };

//    String[] threadId = new String[MAX_THREADS];
//
//    for (int i = 0; i < MAX_THREADS; i++) {
//      String name = "consumer" + i;
//      threadId[i] = name;
//    }
    for (int i = 0; i < MAX_THREADS; i++) {
      Thread thread = new Thread(runnable);
      thread.start();
    }

//    System.out.println(wordMap.size());
//    for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
//      System.out.println(entry.getKey() + " val: " + entry.getValue());
//    }
  }

}
