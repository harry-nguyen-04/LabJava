package vhuwng.lab.D2.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vhuwng.lab.D2.repository.IOrderRepository;
import vhuwng.lab.D2.repository.InMemoryOrderRepository;
import vhuwng.lab.D2.repository.JsonOrderRepository;
import vhuwng.lab.D2.service.OrderUtil;

import java.util.List;

@Configuration
public class OrderRepositoryConfig {

    @Bean
    public IOrderRepository orderRepository(OrderUtil orderUtil, ApplicationArguments args) {
        List<String> values = args.getOptionValues("source");
        // default is json if no source is specified
        String source = (values == null || values.isEmpty()) ? "json" : values.getFirst();
        return switch (source) {
            case "json" -> new JsonOrderRepository(orderUtil);
            case "in-memory" -> new InMemoryOrderRepository(orderUtil);
            default -> throw new IllegalArgumentException(
                    "--source phải là json hoặc in-memory, nhận được: " + source
            );
        };
    }
}
