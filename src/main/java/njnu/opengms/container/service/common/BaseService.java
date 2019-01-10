package njnu.opengms.container.service.common;

/**
 * @param <E>   实体
 * @param <AD>  AddDTO
 * @param <UD>  UpdateDTO
 * @param <FD>  FindDTO
 * @param <VO>  VisualizationObject
 * @param <UID> ID
 *
 * @InterfaceName BaseService
 * @Description todo
 * @Author sun_liber
 * @Date 2018/9/8
 * @Version 1.0.0
 */
public interface BaseService<E, AD, UD, FD, VO, UID> extends
        CreateService<AD>,
        QueryService<E, FD, VO, UID>,
        DeleteService<UID>,
        UpdateService<UID, UD> {
}
