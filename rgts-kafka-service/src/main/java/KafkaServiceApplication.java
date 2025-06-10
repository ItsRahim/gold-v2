import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@EnableKafka
@Configuration
@ComponentScan(basePackages = {"com.rahim.kafkaservice"})
public class KafkaServiceApplication {}
