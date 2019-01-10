package njnu.opengms.container.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @ClassName PepoleCofig
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/10
 * @Version 1.0.0
 */
@Component
@PropertySource (value = "classpath:myProperties/people.properties")
@ConfigurationProperties (prefix = "my.class")
@Data
public class People {
    String name;
    String email;
}
