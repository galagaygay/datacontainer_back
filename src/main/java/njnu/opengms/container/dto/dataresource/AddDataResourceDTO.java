package njnu.opengms.container.dto.dataresource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName AddDataResourceDTO
 * @Description todo
 * @Author sun_liber
 * @Date 2019/2/13
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddDataResourceDTO {
    String sourceStoreId;
    String author;
    String type;
    String dataItemId;
    String mdlId;
    String fileName;
    String suffix;
    List<String> tags;
}
