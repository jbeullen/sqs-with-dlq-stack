package be.upgrade.it.aws.sqs.with.dlq.error;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class ErrorApplication {
    private static final Log logger = LogFactory.getLog(ErrorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ErrorApplication.class, args);
    }

    @Bean
    public Function<SQSEvent, Boolean> processSQSEvents() {
        return event -> {
            for (SQSEvent.SQSMessage msg : event.getRecords()) {
                String body = msg.getBody();
                logger.info(String.format("Error Message received: Body: %s", body));
                logger.info("Error Message processed successful");
            }
            return true;
        };
    }
}
