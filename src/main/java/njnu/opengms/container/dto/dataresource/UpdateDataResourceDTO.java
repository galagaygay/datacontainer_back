package njnu.opengms.container.dto.dataresource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName UpdateDataResourceDTO
 * @Description todo
 * @Author sun_liber
 * @Date 2019/2/13
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDataResourceDTO {
    boolean toGeoserver;
    String layerName;

    String meta;
}
