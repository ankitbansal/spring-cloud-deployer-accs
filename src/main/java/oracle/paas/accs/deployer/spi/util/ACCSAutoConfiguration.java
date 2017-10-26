package oracle.paas.accs.deployer.spi.util;

import oracle.paas.accs.deployer.spi.app.ACCSAppDeployer;
import oracle.paas.accs.deployer.spi.app.ACCSTaskLauncher;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@EnableConfigurationProperties
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class ACCSAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TaskLauncher.class)
    public TaskLauncher taskLauncher() {
        return new ACCSTaskLauncher();
    }

    @Bean
    @ConditionalOnMissingBean(AppDeployer.class)
    public AppDeployer appDeployer() {
        return new ACCSAppDeployer();
    }
}
