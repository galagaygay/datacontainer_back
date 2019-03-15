package njnu.opengms.container.controller;

import io.swagger.annotations.ApiOperation;
import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.component.GeoserverConfig;
import njnu.opengms.container.component.PathConfig;
import njnu.opengms.container.dto.dataresource.AddDataResourceDTO;
import njnu.opengms.container.dto.dataresource.FindDataResourceDTO;
import njnu.opengms.container.dto.dataresource.UpdateDataResourceDTO;
import njnu.opengms.container.enums.DataResourceTypeEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.pojo.DataResource;
import njnu.opengms.container.service.DataResourceService;
import njnu.opengms.container.utils.ResultUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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


    /*******/
    //   Above code is for portal

    /*******/
    @RequestMapping (value = "/listByAuthor/{author}", method = RequestMethod.GET)
    JsonResult listByAuthor(@PathVariable ("author") String author,
                            @RequestParam (value = "page", required = false) Integer page,
                            @RequestParam (value = "pageSize", required = false) Integer pageSize) {
        if (page == null || pageSize == null) {
            return ResultUtils.success(dataResourceService.listByAuthor(author));
        }
        return ResultUtils.success(dataResourceService.listByAuthor(author, page, pageSize));
    }

    @RequestMapping (value = "/listByDataItemId/{dataItemId}", method = RequestMethod.GET)
    JsonResult listByDataItemId(@PathVariable ("dataItemId") String dataItemId) {
        return ResultUtils.success(dataResourceService.listByDataItemId(dataItemId));
    }

    @RequestMapping (value = "/listByFileNameContains/{dataItemFileName}", method = RequestMethod.GET)
    JsonResult listByFileNameContains(@PathVariable ("dataItemFileName") String dataItemFileName) {
        return ResultUtils.success(dataResourceService.listByFileNameContains(dataItemFileName));
    }

    @RequestMapping (value = "/downloadAll/{dataItemId}", method = RequestMethod.GET)
    ResponseEntity<InputStreamResource> downloadAll(@PathVariable ("dataItemId") String dataItemId) throws IOException {
        List<DataResource> list = dataResourceService.listByDataItemId(dataItemId);
        List<File> fileList = new ArrayList<>();
        List<String> renameList = new ArrayList<>();
        list.forEach(el -> {
            fileList.add(new File(pathConfig.getStoreFiles() + File.separator + el.getSourceStoreId()));
            renameList.add(new String(el.getFileName() + "." + el.getSuffix()));
        });
        File temp = File.createTempFile("zipFiles", "zip");
        zipFiles(temp, fileList, renameList);
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


    /******/
    // Above code is for Application

    /*****/
    @RequestMapping (value = "/listByMdlId/{mdlId}", method = RequestMethod.GET)
    JsonResult listByMdlId(@PathVariable ("mdlId") String mdlId) {
        return ResultUtils.success(dataResourceService.listByMdlId(mdlId));
    }


    /*******/
    //   Above code is for geoserver

    /*******/
    @RequestMapping (value = "/toGeoserver/{id}", method = RequestMethod.GET)
    @ApiOperation (value = "将shapefile或者geotiff文件发布到geoserver中", notes = "")
    void toGeoserverDataStores(@PathVariable ("id") String id, HttpServletResponse response) throws IOException {
        DataResource dataResource = dataResourceService.getById(id);
        if (dataResource.getType() == DataResourceTypeEnum.SHAPEFILE) {
            File file = new File(geoserverConfig.getShapefiles() + File.separator + id + "_" + dataResource.getFileName() + ".shp");
            if (!file.exists()) {
                unZipFiles(new File(pathConfig.getStoreFiles() + File.separator + dataResource.getSourceStoreId()),
                        geoserverConfig.getShapefiles(),
                        id);
                response.sendRedirect("/custom_geoserver/datacontainer/datastores/shapefileList?fileName=" + id + "_" + dataResource.getFileName() + ".shp");
            }
            return;
        } else if (dataResource.getType() == DataResourceTypeEnum.GEOTIFF) {
            File src = new File(pathConfig.getStoreFiles() + File.separator
                    + dataResource.getSourceStoreId());
            File des = new File(geoserverConfig.getGeotiffes() + File.separator + id + "_" + dataResource.getFileName() + ".tif");
            if (!des.exists()) {
                FileUtils.copyFile(src, des);
                response.sendRedirect("/custom_geoserver/datacontainer/coverageStores/" + id + "?fileName=" + id + "_" + dataResource.getFileName() + ".tif");
            }
            return;
        } else {
            throw new MyException("Geoserver 目前仅支持shapefile与geotiff数据");
        }
    }

    public static void unZipFiles(File zipFile, String descDir, String fileHeader) throws IOException {
        if (!descDir.endsWith("/")) {
            descDir += "/";
        }
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);

        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + fileHeader + "_" + zipEntryName).replaceAll("\\*", "/");
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

    public void zipFiles(File zip, List<File> srcFiles, List<String> renameList) throws IOException {
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
}
