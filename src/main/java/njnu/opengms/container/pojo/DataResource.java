package njnu.opengms.container.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @ClassName DataResource
 * @Description todo
 * @Author sun_liber
 * @Date 2019/2/13
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
/**
 * type应该是设计为有限的集合
 */
public class DataResource {
    @Id
    String id;
    String sourceStoreId;
    String author;
    String type;
    String dataItemId;
    String mdlId;
    String fileName;
    String suffix;
    List<String> tags;
    Date createDate;
}
