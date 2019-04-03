package njnu.opengms.container.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.component.GeoserverConfig;
import njnu.opengms.container.component.PathConfig;
import njnu.opengms.container.dto.dataresource.AddDataResourceDTO;
import njnu.opengms.container.dto.dataresource.FindDataResourceDTO;
import njnu.opengms.container.dto.dataresource.UpdateDataResourceDTO;
import njnu.opengms.container.enums.DataResourceTypeEnum;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.getmeta.DataStoreMetaGet;
import njnu.opengms.container.getmeta.impl.ShapefileMeta;
import njnu.opengms.container.pojo.DataResource;
import njnu.opengms.container.service.DataResourceService;
import njnu.opengms.container.utils.ResultUtils;
import njnu.opengms.container.utils.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName DataResourceController
 * @Description todo
 * @Author sun_liber
 * @Date 2019/2/13
 * @Version 1.0.0
 */
@RestController
@RequestMapping (value = "/dataResource")
public class DataResourceController {

    @Autowired
    DataResourceService dataResourceService;

    @Autowired
    GeoserverConfig geoserverConfig;

    @Autowired
    PathConfig pathConfig;

    @RequestMapping (value = "", method = RequestMethod.GET)
    JsonResult list(FindDataResourceDTO findDataResourceDTO) {
        return ResultUtils.success(dataResourceService.list(findDataResourceDTO));
    }

    @ApiOperation (value = "上传DataReource", notes = "注意fileName请不要添加后缀")
    @RequestMapping (value = "", method = RequestMethod.POST)
    JsonResult add(@RequestBody AddDataResourceDTO addDataResourceDTO) {
        return ResultUtils.success(dataResourceService.add(addDataResourceDTO));
    }

    @RequestMapping (value = "/count", method = RequestMethod.GET)
    JsonResult count() {
        return ResultUtils.success(dataResourceService.count());
    }

    @RequestMapping (value = "/{id}", method = RequestMethod.GET)
    JsonResult get(@PathVariable ("id") String id) {
        return ResultUtils.success(dataResourceService.getById(id));
    }

    @RequestMapping (value = "/{id}", method = RequestMethod.DELETE)
    JsonResult delete(@PathVariable ("id") String id) throws IOException {
        DataResource dataResource = dataResourceService.getById(id);
        if (dataResource == null) {
            return ResultUtils.success("删除成功");
        }
        //删除对应的数据实体
        FileUtils.forceDelete(new File(pathConfig.getStoreFiles() + File.separator + dataResource.getSourceStoreId()));
        //TODO  之后应该完成对应的geoserver服务删除
        dataResourceService.delete(id);
        return ResultUtils.success("删除成功");
    }

    @RequestMapping (value = "/{id}", method = RequestMethod.PUT)
    JsonResult update(@PathVariable ("id") String id, @RequestBody UpdateDataResourceDTO updateDataResourceDTO) {
        dataResourceService.save(id, updateDataResourceDTO);
        return ResultUtils.success("更新成功");
    }


    /**
     * 根据用户返回所有存储在数据容器的数据
     *
     * @param author
     * @param page
     * @param pageSize
     *
     * @return
     */
    @RequestMapping (value = "/listByAuthor/{author}", method = RequestMethod.GET)
    JsonResult listByAuthor(@PathVariable ("author") String author,
                            @RequestParam (value = "page", required = false) Integer page,
                            @RequestParam (value = "pageSize", required = false) Integer pageSize) {
        if (page == null || pageSize == null) {
            return ResultUtils.success(dataResourceService.listByAuthor(author));
        }
        return ResultUtils.success(dataResourceService.listByAuthor(author, page, pageSize));
    }

    /**
     * 根据门户的数据条目的Id返回存储在数据容器的数据
     *
     * @param dataItemId
     *
     * @return
     */
    @RequestMapping (value = "/listByDataItemId/{dataItemId}", method = RequestMethod.GET)
    JsonResult listByDataItemId(@PathVariable ("dataItemId") String dataItemId) {
        return ResultUtils.success(dataResourceService.listByDataItemId(dataItemId));
    }

    /**
     * 根据数据存储的文件名，进行模糊查询
     *
     * @param value
     *
     * @return
     */
    @RequestMapping (value = "/listByFileNameContains/{value}", method = RequestMethod.GET)
    JsonResult listByFileNameContains(@PathVariable ("value") String value) {
        return ResultUtils.success(dataResourceService.listByFileNameContains(value));
    }

    /**
     * 根据MDL，对数据存储进行查询
     *
     * @param mdlId
     *
     * @return
     */
    @RequestMapping (value = "/listByMdlId/{mdlId}", method = RequestMethod.GET)
    JsonResult listByMdlId(@PathVariable ("mdlId") String mdlId) {
        return ResultUtils.success(dataResourceService.listByMdlId(mdlId));
    }

    /**
     * 将与门户的数据条目相关的所有数据存储打包返回
     * 为了避免数据重名，对其进行名称添加 前缀
     * @param dataItemId
     * @return
     * @throws IOException
     */
    @RequestMapping (value = "/downloadAll/{dataItemId}", method = RequestMethod.GET)
    ResponseEntity<InputStreamResource> downloadAllRelatedDataItem(@PathVariable ("dataItemId") String dataItemId) throws IOException {
        List<DataResource> list = dataResourceService.listByDataItemId(dataItemId);
        List<File> fileList = new ArrayList<>();
        List<String> renameList = new ArrayList<>();
        list.forEach(el -> {
            fileList.add(new File(pathConfig.getStoreFiles() + File.separator + el.getSourceStoreId()));
            renameList.add(new String(el.getFileName() + "." + el.getSuffix()));
        });
        File temp = File.createTempFile("zipFiles", "zip");
        filesToZipWithRename(temp, fileList, renameList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment;filename=zipFiles.zip");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(temp.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(FileUtils.openInputStream(temp)));
    }

    /**
     * @param zip        目标压缩文件
     * @param srcFiles   压缩文件列表
     * @param renameList 重命名列表
     *
     * @throws IOException
     */
    public void filesToZipWithRename(File zip, List<File> srcFiles, List<String> renameList) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
        byte[] buf = new byte[1024];
        try {
            for (int i = 0; i < srcFiles.size(); i++) {
                FileInputStream in = new FileInputStream(srcFiles.get(i));
                out.putNextEntry(new ZipEntry(renameList.get(i)));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.close();
    }

    /**
     * 将指定的多个数据存储打包，需要指定数据存储的文件名和后缀，
     * @param sourceStoreIdList 数据存储的ID列表
     * @param fileNameList 注意文件名请不要重复
     * @param suffixList
     * @return
     * @throws IOException
     */
    @RequestMapping (value = "/zipDataStoreList", method = RequestMethod.GET)
    ResponseEntity<InputStreamResource> zipDataStoreList(@RequestParam ("sourceStoreId") List<String> sourceStoreIdList,
                                                         @RequestParam ("fileName") List<String> fileNameList,
                                                         @RequestParam ("suffix") List<String> suffixList
    ) throws IOException {
        List<File> fileList = new ArrayList<>();
        List<String> renameList = new ArrayList<>();
        for (int i = 0; i < sourceStoreIdList.size(); i++) {
            fileList.add(new File(pathConfig.getStoreFiles() + File.separator + sourceStoreIdList.get(i)));
            renameList.add(fileNameList.get(i) + "." + suffixList.get(i));
        }
        File temp = File.createTempFile("zipFiles", "zip");
        filesToZipWithRename(temp, fileList, renameList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment;filename=zipFiles.zip");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(temp.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(FileUtils.openInputStream(temp)));
    }

    /**
     * 将指定的数据存储，先全部解压缩到特定文件夹，在压缩打包
     * @param sourceStoreIdList 数据存储的ID列表
     * @return
     * @throws IOException
     */
    @RequestMapping (value = "/zipDataStoreList/songjie", method = RequestMethod.GET)
    ResponseEntity<InputStreamResource> zipDataStoreList(@RequestParam ("sourceStoreId") List<String> sourceStoreIdList
    ) throws IOException {
        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < sourceStoreIdList.size(); i++) {
            fileList.add(new File(pathConfig.getStoreFiles() + File.separator + sourceStoreIdList.get(i)));
        }
        File temp = File.createTempFile("zipFiles", "zip");
        filesToZipWithPrefix(temp, fileList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment;filename=zipFiles.zip");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(temp.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(FileUtils.openInputStream(temp)));
    }

    /**
     * @param zip  目标压缩文件
     * @param srcFiles   压缩文件列表
     *将压缩文件列表中的所有数据先解压，这里的解压会在解压的文件中添加<b>前缀</b>，然后全部压缩到目标压缩文件
     * @throws IOException
     */
    private void filesToZipWithPrefix(File zip, List<File> srcFiles) throws IOException {
        String uid = UUID.randomUUID().toString();
        for (File srcFile : srcFiles) {
            unZipFilesWithPrefixFilterSuffix(srcFile, pathConfig.getDataProcess() + File.separator + uid, uid,null);
        }
        File dir = new File(pathConfig.getDataProcess() + File.separator + uid);
        File[] fileArray = dir.listFiles();
        ZipUtils.zipFiles(zip, "", fileArray);
    }


    /**
     * @param zipFile 待解压缩的文件
     * @param descDir 目标文件夹
     * @param prefix  添加的前缀
     * @param suffix  忽略的后缀文件
     *
     * @throws IOException
     */
    public static void unZipFilesWithPrefixFilterSuffix(File zipFile, String descDir, String prefix, String suffix) throws IOException {
        if (!descDir.endsWith("/")) {
            descDir += "/";
        }
        File dir = new File(descDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);

        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            if (suffix != null && FilenameUtils.getExtension(zipEntryName).equals(suffix)) {
                break;
            }
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + prefix + "_" + zipEntryName).replaceAll("\\*", "/");
            //判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }

            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        zip.close();
    }

    @RequestMapping (value = "/toGeoserver/{id}", method = RequestMethod.GET)
    @ApiOperation (value = "将shapefile或者geotiff文件发布到geoserver中", notes = "")
    void toGeoserverDataStores(@PathVariable ("id") String id, HttpServletResponse response) throws IOException {
        DataResource dataResource = dataResourceService.getById(id);
        if (dataResource.isToGeoserver()) {
            //已发布服务
            return ;
        }
        if (dataResource.getType() == DataResourceTypeEnum.SHAPEFILE) {

            unZipFilesWithPrefixFilterSuffix(new File(pathConfig.getStoreFiles() + File.separator + dataResource.getSourceStoreId()),
                        pathConfig.getShapefiles(),
                    id,
                    "mshp");
            response.sendRedirect("/custom_geoserver/datacontainer/datastores/shapefileList?id=" + id);
        } else if (dataResource.getType() == DataResourceTypeEnum.GEOTIFF) {
            File src = new File(pathConfig.getStoreFiles() + File.separator
                    + dataResource.getSourceStoreId());
            File des = new File(pathConfig.getGeotiffes() + File.separator + id + "_" + dataResource.getFileName() + ".tif");
            if (!des.exists()) {
                FileUtils.copyFile(src, des);
                response.sendRedirect("/custom_geoserver/datacontainer/coverageStores/" + id + "?id=" + id + "&fileName=" + id + "_" + dataResource.getFileName() + ".tif");
            }
            response.sendRedirect("/custom_geoserver/datacontainer/coverageStores/" + id + "?id=" + id + "&fileName=" + id + "_" + dataResource.getFileName() + ".tif");
        } else {
            throw new MyException(ResultEnum.NOTSUPPORT_GEOSERVER_ERROR);
        }
    }

    @RequestMapping (value = "/getMeta/{id}", method = RequestMethod.GET)
    @ApiOperation (value = "获取shapefile或者geotiff文件的meta", notes = "")
    JsonResult getMeta(@PathVariable ("id") String id) throws IOException {
        DataResource dataResource = dataResourceService.getById(id);
        if (dataResource.getMeta() != null) {
            return ResultUtils.success(dataResource.getMeta());
        }

        if (dataResource.getType() == DataResourceTypeEnum.SHAPEFILE) {
            ZipUtils.unZipFiles(new File(pathConfig.getStoreFiles() + File.separator + dataResource.getSourceStoreId()),
                    pathConfig.getGetMeta() + File.separator + dataResource.getSourceStoreId());
            File dir = new File(pathConfig.getGetMeta() + File.separator + dataResource.getSourceStoreId());
            Collection<File> fileCollection = FileUtils.listFiles(dir, new SuffixFileFilter(".shp"), null);
            File real_file = fileCollection.iterator().next();
            DataStoreMetaGet metaGet = new ShapefileMeta();
            String jsonString = JSONObject.toJSONString(metaGet.getMeta(real_file));
            UpdateDataResourceDTO updateDataResourceDTO = new UpdateDataResourceDTO();
            updateDataResourceDTO.setMeta(jsonString);
            dataResourceService.save(id, updateDataResourceDTO);
            return ResultUtils.success(jsonString);
        } else if (dataResource.getType() == DataResourceTypeEnum.GEOTIFF) {
            //TODO
            return ResultUtils.success("");
        } else {
            throw new MyException(ResultEnum.NOTSUPPORT_GETMETA_ERROR);
        }
    }

    @RequestMapping (value = "/getdbf/{id}", method = RequestMethod.GET)
    @ApiOperation (value = "获取shapefile的pdf", notes = "")
    JsonResult getMeta(@RequestParam (value = "from", required = false) Integer from,
                       @RequestParam (value = "to", required = false) Integer to,
                       @PathVariable ("id") String id
    ) throws IOException {
        DataResource dataResource = dataResourceService.getById(id);
        if (dataResource.getMeta() != null && dataResource.getType().equals(DataResourceTypeEnum.SHAPEFILE)) {
            File dir = new File(pathConfig.getGetMeta() + File.separator + dataResource.getSourceStoreId());
            Collection<File> fileCollection = FileUtils.listFiles(dir, new SuffixFileFilter(".dbf"), null);
            File real_file = fileCollection.iterator().next();
            DataStoreMetaGet metaGet = new ShapefileMeta();
            return ResultUtils.success(((ShapefileMeta) metaGet).readDBF(real_file, from, to));
        } else {
            throw new MyException("该数据的Meta未获取或者该数据不是shapefile格式");
        }
    }
}
