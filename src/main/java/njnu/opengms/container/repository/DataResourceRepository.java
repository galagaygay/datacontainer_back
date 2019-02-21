package njnu.opengms.container.repository;

import njnu.opengms.container.pojo.DataResource;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @InterfaceName DataResourceRepository
 * @Description todo
 * @Author sun_liber
 * @Date 2019/2/13
 * @Version 1.0.0
 */
public interface DataResourceRepository extends MongoRepository<DataResource, String> {
    List<DataResource> findByAuthor(String author);

    List<DataResource> findByDataItemId(String dataItemId);

    List<DataResource> findByMdlId(String mdlId);

    List<DataResource> findByFileNameContains(String fileName);
}
