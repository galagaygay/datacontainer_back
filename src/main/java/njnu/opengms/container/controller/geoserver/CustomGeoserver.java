//package njnu.opengms.container.controller.geoserver;
//
//import com.alibaba.fastjson.JSONObject;
//import io.swagger.annotations.ApiOperation;
//import njnu.opengms.container.bean.JsonResult;
//import njnu.opengms.container.component.GeoserverConfig;
//import njnu.opengms.container.enums.ResultEnum;
//import njnu.opengms.container.exception.MyException;
//import njnu.opengms.container.utils.ResultUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.HashMap;
//
///**
// * @ClassName CustomGeoserver
// * @Description todo
// * @Author sun_liber
// * @Date 2019/2/21
// * @Version 1.0.0
// */
//
//
//@RestController
//@RequestMapping ("/custom_geoserver")
//public class CustomGeoserver {
//
//    @Autowired
//    GeoserverConfig geoserverConfig;
//
//    @Autowired
//    RestTemplate restTemplate;
//
//
//    @ApiOperation (value = "List all layers", notes = "")
//    @RequestMapping (value = "/layers", method = RequestMethod.GET)
//    public JsonResult listLayers() throws Exception {
//        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/layers.json";
//        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
//        if (responseEntity.getStatusCode() != HttpStatus.OK) {
//            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
//        }
//        return ResultUtils.success(responseEntity.getBody());
//    }
//
//    @ApiOperation (value = "get a certain layer description", notes = "注意这里的layer要加前缀，及workspace:layer,默认workspace是datacontainer")
//    @RequestMapping (value = "/layers/{workspaceAndLauer}", method = RequestMethod.GET)
//    JsonResult listLayer(@PathVariable ("workspaceAndLauer") String workspaceAndLauer) {
//        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/layers/" + workspaceAndLauer + ".json";
//        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
//        if (responseEntity.getStatusCode() != HttpStatus.OK) {
//            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
//        }
//        return ResultUtils.success(responseEntity.getBody());
//    }
//
//    /***************************************/
//    /*********shapefile*********************/
//    @ApiOperation(value = "List all data stores in certain workspaces", notes = "注意我们这里将工作区给定死了为datacontainer")
//    @RequestMapping(value = "/datacontainer/datastores", method = RequestMethod.GET)
//    JsonResult listDataStores(){
//        String url=geoserverConfig.getBasicURL()+"/geoserver/rest/workspaces/datacontainer/datastores.json";
//        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
//        if (responseEntity.getStatusCode() != HttpStatus.OK) {
//            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
//        }
//        return ResultUtils.success(responseEntity.getBody());
//    }
//
//    @ApiOperation(value = "get a certain data store", notes = "注意我们这里将工作区给定死了为datacontainer")
//    @RequestMapping(value = "/datacontainer/datastores/{datastore}", method = RequestMethod.GET)
//    JsonResult listDataStore(@PathVariable("datastore") String datastore) throws Exception {
//        String url=geoserverConfig.getBasicURL()+ "/geoserver/rest/workspaces/datacontainer/datastores/"
//                + datastore
//                + ".json";
//        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
//        if (responseEntity.getStatusCode() != HttpStatus.OK) {
//            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
//        }
//        return ResultUtils.success(responseEntity.getBody());
//    }
//
//    @ApiOperation(value = "get  features list", notes = "注意我们这里将工作区给定死了为datacontainer")
//    @RequestMapping(value = "/datacontainer/datastores/{datastore}/featuretypes", method = RequestMethod.GET)
//    JsonResult listFeatureTypes(@PathVariable("datastore") String datastore) throws Exception {
//        String url=geoserverConfig.getBasicURL()+ "/geoserver/rest/workspaces/datacontainer/datastores/"
//                + datastore
//                +"/featuretypes.json";
//        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
//        if (responseEntity.getStatusCode() != HttpStatus.OK) {
//            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
//        }
//        return ResultUtils.success(responseEntity.getBody());
//    }
//
//    @ApiOperation(value = "get  a features ", notes = "注意我们这里将工作区给定死了为datacontainer")
//    @RequestMapping(value = "/datacontainer/datastores/{datastore}/featuretypes/{featureType}", method = RequestMethod.GET)
//    JsonResult listFeatureTypes(@PathVariable("datastore") String datastore,
//                                @PathVariable("featureType") String featureType) throws Exception {
//        String url=geoserverConfig.getBasicURL()+ "/geoserver/rest/workspaces/datacontainer/datastores/"
//                + datastore
//                +"/featuretypes"+featureType+".json";
//        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuthHeader(), JSONObject.class);
//        if (responseEntity.getStatusCode() != HttpStatus.OK) {
//            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
//        }
//        return ResultUtils.success(responseEntity.getBody());
//    }
//
//    @RequestMapping(value = "/workspaces/datacontainer/datastores/{datastore}/{method}.shp", method = RequestMethod.POST)
//    JsonResult createDataStoresByUpload( @PathVariable("datastore") String datastore,
//                                                   @PathVariable("method") String method,
//                                                   @RequestParam("ip") String ip,
//                                                   @RequestParam("file") MultipartFile file) throws Exception {
//        String url="http://" + ip + ":" + port + "/geoserver/rest/workspaces/" + workspaceName + "/datastores/"+datastore
//                +"/"+"file.shp";
//        File fileTemp = MyFileUtils.multipartToFile(file);
//        HashMap<String, String> headerMap = new HashMap<>();
//        headerMap.put("connection", "keep-alive");
//        headerMap.put("Content-Type", "application/zip");//也可以通过file.getContentType来获取
//        String responseContet = MyHttpUtils.PUTRawFile(url, "utf-8", headerMap, fileTemp, admin, password);
//        return ResultUtil.success(responseContet);
//    }
//
//    /*********geotiff*********************/
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    public HttpEntity setAuthHeader(){
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(geoserverConfig.getUsername(), geoserverConfig.getPassword());
//        return  new HttpEntity<>(null, headers);
//    }
//
//
//}
