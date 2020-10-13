package be.upgrade.it.aws.sqs.with.dlq.consumer;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class ConsumerApplication {
    private static final Log logger = LogFactory.getLog(ConsumerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    public Function<SQSEvent, Boolean> processSQSEvents() {
        return event -> {
            for (SQSEvent.SQSMessage msg : event.getRecords()) {
                String body = msg.getBody();
                logger.info(String.format("Message received: Body: %s", body));
                if (body.contains("error")) {
                    logger.error("Message Body contains error: throwing RuntimeException");
                    throw new RuntimeException("Message Body contains error");
                } else {
                    logger.info("Message processed successful");
                }
            }
            return true;
        };
    }
}
