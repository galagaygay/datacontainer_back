package njnu.opengms.container.controller.geoserver;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.component.GeoserverConfig;
import njnu.opengms.container.component.PathConfig;
import njnu.opengms.container.dto.dataresource.UpdateDataResourceDTO;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.service.DataResourceService;
import njnu.opengms.container.utils.ResultUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Collection;

/**
 * @ClassName CustomGeoserver
 * @Description Geoserver本身支持的数据存储分为三种，分别是矢量数据源、栅格数据源以及其他的WMS数据源
 * 代码对Geoserver提供的Rest风格接口，进行了自定义的封装
 * 完成了典型矢量数据-Shapefile和典型的栅格数据-geotiff的服务上传、调用功能
 * TODO 目前所有的业务逻辑代码是在Controller实现，可考虑生成静态工具类，以避免目前的HTTP重定向写法
 * @Author sun_liber
 * @Date 2019/2/21
 * @Version 1.0.0
 */
@RestController
@RequestMapping ("/custom_geoserver")
public class CustomGeoserver {

    @Autowired
    GeoserverConfig geoserverConfig;

    @Autowired
    PathConfig pathConfig;

    @Autowired
    DataResourceService dataResourceService;

    @Autowired
    RestTemplate restTemplate;

    @ApiOperation (value = "查看全部图层")
    @RequestMapping (value = "/layers", method = RequestMethod.GET)
    public JsonResult listLayers() throws Exception {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/layers.json";
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        return ResultUtils.success(responseEntity.getBody());
    }

    public HttpEntity setAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(geoserverConfig.getUsername(), geoserverConfig.getPassword());
        return new HttpEntity<>(null, headers);
    }

    @ApiOperation (value = "查看特定图层的详细信息", notes = "注意这里的layer要加前缀，及workspace:layer,默认workspace是datacontainer")
    @RequestMapping (value = "/layers/{workspaceAndLayer}", method = RequestMethod.GET)
    JsonResult listLayer(@PathVariable ("workspaceAndLauer") String workspaceAndLauer) {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/layers/" + workspaceAndLauer + ".json";
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        return ResultUtils.success(responseEntity.getBody());
    }

    @ApiOperation (value = "列出工作区的所有数据存储，分为DataStores和CoverageStores", notes = "注意我们这里将工作区给定死了为datacontainer")
    @RequestMapping (value = "/datacontainer", method = RequestMethod.GET)
    JsonResult list() {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/workspaces/datacontainer.json";
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        return ResultUtils.success(responseEntity.getBody());
    }

    @ApiOperation (value = "列出所有的数据存储，及datastores和coveragestores", notes = "注意我们这里将工作区给定死了为datacontainer,value为datastores时为矢量数据，coveragestores为栅格数据")
    @RequestMapping (value = "/datacontainer/{value}", method = RequestMethod.GET)
    JsonResult list(@PathVariable ("value") String value) {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/workspaces/datacontainer/";
        if ("datastores".equals(value)) {
            url += "datastores.json";
        } else if ("coveragestores".equals(value)) {
            url += "coveragestores.json";
        }
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        return ResultUtils.success(responseEntity.getBody());
    }

    @ApiOperation (value = "获取datastores和coveragestores中的featuretypes和coverages", notes = "注意我们这里将工作区给定死了为datacontainer")
    @RequestMapping (value = "/datacontainer/{value}/{storeName}", method = RequestMethod.GET)
    JsonResult list(@PathVariable ("value") String value,
                    @PathVariable ("storeName") String storeName) throws Exception {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/workspaces/datacontainer/";
        if ("datastores".equals(value)) {
            url += "datastores/" + storeName + ".json";
        } else if ("coveragestores".equals(value)) {
            url += "coveragestores/" + storeName + ".json";
        }
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        return ResultUtils.success(responseEntity.getBody());
    }

    @ApiOperation (value = "获取featuretypes和coverages中的featuretype和coverage", notes = "注意我们这里将工作区给定死了为datacontainer,同时当value为datastores时，" +
            "对应的storeName为特定的shapefileList")
    @RequestMapping (value = "/datacontainer/{value}/{storeName}/{featureName}", method = RequestMethod.GET)
    JsonResult list(@PathVariable ("value") String value,
                    @PathVariable ("storeName") String storeName,
                    @PathVariable ("layerName") String layerName) throws Exception {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/workspaces/datacontainer/";
        if ("datastores".equals(value)) {
            url += "datastores/" + storeName + "/featuretypes/" + layerName + ".json";
        } else if ("coveragestores".equals(value)) {
            url += "coveragestores/" + storeName + "/coverages/" + layerName + ".json";
        }
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        return ResultUtils.success(responseEntity.getBody());
    }

    @ApiOperation (value = "Creates or modifies a single data store,如果不存在会默认创建，这里我们默认创建了名为shapefileList的datastore", notes = "Geoserver原生支持三种上传方式，分别是file、url和external，我们这里将" +
            "使用external作为默认方式，因此我们创建一个dataStore的分别为两个步骤，第一步将文件复制到external路径之下" +
            "第二步，调用该方法以实现更新" +
            "同时我们这里采取了update=overwrite会对同名文件进行覆盖")
    @RequestMapping (value = "/datacontainer/datastores/shapefileList", method = RequestMethod.GET)
    JsonResult createDataStores(@RequestParam ("id") String id) throws Exception {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/workspaces/datacontainer/datastores/shapefileList/external.shp?update=overwrite";
        //根据id 前缀找到 指定的shp文件
        File dir = new File(pathConfig.getShapefiles());
        Collection<File> fileCollection = FileUtils.listFiles(dir, FileFilterUtils.and(new SuffixFileFilter(".shp"), new PrefixFileFilter(id)), null);
        File real_file = fileCollection.iterator().next();

        //注意这里是PUT请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, setAuthHeaderAndTextData(geoserverConfig.getShapefiles() + File.separator
                + real_file.getName()), String.class);
        if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
            //注意这里geoserver返回的HttpStatus是201
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        UpdateDataResourceDTO updateDataResourceDTO = new UpdateDataResourceDTO();
        updateDataResourceDTO.setToGeoserver(true);
        updateDataResourceDTO.setLayerName(real_file.getName());
        dataResourceService.save(id, updateDataResourceDTO);
        return ResultUtils.success("fileName:" + real_file.getName() + "发布成功");
    }

    public HttpEntity setAuthHeaderAndTextData(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(geoserverConfig.getUsername(), geoserverConfig.getPassword());
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new HttpEntity<>(text, headers);
    }

    @ApiOperation (value = "Creates or modifies a single coverage store", notes = "目前仅提供geotiff格式的栅格数据上传")
    @RequestMapping (value = "/datacontainer/coverageStores/{storeName}", method = RequestMethod.GET)
    JsonResult createCoverageStores(@RequestParam ("fileName") String fileName,
                                    @RequestParam ("id") String id,
                                    @PathVariable ("storeName") String storeName
    ) {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/workspaces/datacontainer/coveragestores/" + storeName + "/external.geotiff";
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, setAuthHeaderAndTextData(geoserverConfig.getGeotiffes() + File.separator + fileName), JSONObject.class);
        if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        UpdateDataResourceDTO updateDataResourceDTO = new UpdateDataResourceDTO();
        updateDataResourceDTO.setToGeoserver(true);
        updateDataResourceDTO.setLayerName(fileName);
        dataResourceService.save(id, updateDataResourceDTO);
        return ResultUtils.success("fileName:" + fileName + "发布成功");
    }
}
