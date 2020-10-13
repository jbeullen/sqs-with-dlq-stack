package be.upgrade.it.aws.sqs.with.dlq.producer;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;
import java.util.function.Function;

@SpringBootApplication
public class ProducerApplication {
    private static final Log logger = LogFactory.getLog(ProducerApplication.class);

    public ProducerApplication(@Value("${aws.sqs.queue.name}") String queueName) {
        this.queueName = queueName;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    private final String queueName;

    @Bean
    public Function<ScheduledEvent, Boolean> processScheduledEvent() {
        return event -> {
            Random generator = new Random(System.currentTimeMillis());
            int random = generator.nextInt();

            String template = "This is a random messageBody: Id: %d Result: %s";
            String messageBody = null;
            if(random % 2 == 0){
                messageBody = String.format(template, random, "error");
            } else {
                messageBody = String.format(template, random, "success");
            }

            logger.info(String.format("Sending message: Queue: name: %s Message: body: %s", queueName, messageBody));
            AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();
            String queueUrl = sqsClient.getQueueUrl(queueName).getQueueUrl();

            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(messageBody)
                    .withDelaySeconds(5);
            sqsClient.sendMessage(send_msg_request);

            return true;
        };
    }
}
