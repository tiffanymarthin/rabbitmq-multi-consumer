package neu.cs6650.a2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents a simple multi-threaded consumer
 * It will consume messages from RabbitMQ and update the wordcount hashmap
 */
public class MultithreadedConsumer {

  private static final Logger logger = LogManager.getLogger(MultithreadedConsumer.class.getName());
  private final static String QUEUE_NAME = "wordCountQueue";
  private final static Integer MAX_MESSAGE_PER_RECEIVER = 1;
  private static Integer MAX_THREADS = 32;

  private static ConcurrentHashMap<String, Integer> wordMap = new ConcurrentHashMap<>();

  public static void main(String[] args) throws IOException, TimeoutException {
    // Command line arguments for easier tests
    //TODO validates the inputs
    if (args.length == 1) {
      MAX_THREADS = Integer.parseInt(args[0]);
    }

    Properties prop = new Properties();
    try (InputStream input = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("config.properties")) {
      prop.load(input);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername(prop.getProperty("rabbit.username"));
    factory.setPassword(prop.getProperty("rabbit.password"));
    factory.setHost(prop.getProperty("rabbit.ip"));
    final Connection connection = factory.newConnection();

    Runnable runnable = () -> {
      try {
        final Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // max one message per receiver
        channel.basicQos(MAX_MESSAGE_PER_RECEIVER);

        final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
          WordCount.updateMap(message, wordMap);
//          logger.info("Callback thread ID = " + Thread.currentThread().getId() + " Received: " + message);
        };

        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});
      } catch (IOException e) {
        logger.info(e.getMessage());
      }
    };

//    ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

    for (int i = 0; i < MAX_THREADS; i++) {
      Thread thread = new Thread(runnable);
      thread.start();
//      executorService.submit(runnable);
    }
  }
}
