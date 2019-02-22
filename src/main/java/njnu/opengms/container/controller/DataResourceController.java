package njnu.opengms.container.controller;

import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.dto.dataresource.AddDataResourceDTO;
import njnu.opengms.container.dto.dataresource.FindDataResourceDTO;
import njnu.opengms.container.dto.dataresource.UpdateDataResourceDTO;
import njnu.opengms.container.pojo.DataResource;
import njnu.opengms.container.service.DataResourceService;
import njnu.opengms.container.utils.ResultUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
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

    @Value ("${web.upload-path}")
    String staticPath;

    @RequestMapping (value = "", method = RequestMethod.GET)
    JsonResult list(FindDataResourceDTO findDataResourceDTO) {
        return ResultUtils.success(dataResourceService.list(findDataResourceDTO));
    }

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
    JsonResult delete(@PathVariable ("id") String id) {
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
    JsonResult listByAuthor(@PathVariable ("author") String author) {
        return ResultUtils.success(dataResourceService.listByAuthor(author));
    }

    @RequestMapping (value = "/listByDataItemId/{dataItemId}", method = RequestMethod.GET)
    JsonResult listByDataItemId(@PathVariable ("dataItemId") String dataItemId) {
        return ResultUtils.success(dataResourceService.listByDataItemId(dataItemId));
    }

    @RequestMapping (value = "/listContainsDataItemFileName/{dataItemFileName}", method = RequestMethod.GET)
    JsonResult listByDataItemName(@PathVariable ("dataItemFileName") String dataItemFileName) {
        return ResultUtils.success(dataResourceService.listContainsDataItemFileName(dataItemFileName));
    }

    @RequestMapping (value = "/downloadAll/{dataItemId}", method = RequestMethod.GET)
    ResponseEntity<InputStreamResource> downloadAll(@PathVariable ("dataItemId") String dataItemId) throws IOException {
        List<DataResource> list = dataResourceService.listByDataItemId(dataItemId);
        List<File> fileList = new ArrayList<>();
        List<String> renameList = new ArrayList<>();
        list.forEach(el -> {
            fileList.add(new File(staticPath + File.separator + "store_dataResource_files" + File.separator + el.getSourceStoreId()));
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
    JsonResult listByMdlId(@PathVariable ("mdlId") String mdlId) {
        return ResultUtils.success(dataResourceService.listByMdlId(mdlId));
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
        System.out.println("*****************压缩完毕*******************");
    }

}
