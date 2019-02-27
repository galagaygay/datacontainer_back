package njnu.opengms.container.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.component.PathConfig;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.utils.ResultUtils;
import njnu.opengms.container.utils.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName FileController
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/18
 * @Version 1.0.0
 */
@RestController
@RequestMapping (value = "/file")
public class FileController {


    @Autowired
    PathConfig pathConfig;

    @ApiOperation (value = "上传文件", notes = "上传映射、重构服务实体、在线调用的输入文件、数据资源")
    @ApiImplicitParam (name = "type", value = "上传实体的类型,可以为map、refactor、online_call_files、store_dataResource_files", dataType = "string", paramType = "path", required = true)
    @RequestMapping (value = "/upload/{type}", method = RequestMethod.POST)
    JsonResult upload(@RequestParam ("file") MultipartFile file,
                      @PathVariable ("type") String type) throws IOException {
        String path;
        if (("map").equals(type)) {
            path = pathConfig.getServicesMap();
        } else if (("refactor").equals(type)) {
            path = pathConfig.getServicesRefactor();
        } else if ("online_call_files".equals(type)) {
            path = pathConfig.getOnlineCallFiles();
        } else if ("store_dataResource_files".equals(type)) {
            path = pathConfig.getStoreFiles();
            String uuid = UUID.randomUUID().toString();
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(path + File.separator + uuid));
            return ResultUtils.success(uuid);
        } else {
            throw new MyException(ResultEnum.UPLOAD_TYPE_ERROR);
        }
        path += File.separator + (UUID.randomUUID().toString());
        FileUtils.copyInputStreamToFile(file.getInputStream(), new File(path + File.separator + file.getOriginalFilename()));
        // 针对映射和重构需要进行解压
        // 注意解压路径是同级目录下的invoke文件夹
        if (("map").equals(type) || ("refactor").equals(type)) {
            ZipUtils.unZipFiles(new File(path + File.separator + file.getOriginalFilename()), path + File.separator + "invoke");
        }
        int index = path.indexOf(pathConfig.getBase()) + pathConfig.getBase().length() + 1;
        return ResultUtils.success(path.substring(index, path.length()) + File.separator + file.getOriginalFilename());
    }


    @ApiOperation (value = "下载文件", notes = "根据文件的路径下载文件,这里用post方法是因为数据库存储的路径是\\,get请求拼接字符串需要对\\进行编码")
    @RequestMapping (value = "/download", method = RequestMethod.POST)
    ResponseEntity<InputStreamResource> download(@RequestParam ("path") String path) throws IOException {
        File file = new File(pathConfig.getBase() + File.separator + path);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment;filename=" + file.getName());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(FileUtils.openInputStream(file)));
    }

    @ApiOperation (value = "下载数据资源", notes = "sourceStoreId存储了文件的位置，其fileName文件名和suffix后缀也需要传递给后台")
    @RequestMapping (value = "/download_data_resource", method = RequestMethod.GET)
    ResponseEntity<InputStreamResource> download(@RequestParam ("sourceStoreId") String sourceStoreId,
                                                 @RequestParam ("fileName") String fileName,
                                                 @RequestParam ("suffix") String suffix) throws IOException {
        File file = new File(pathConfig.getStoreFiles() + File.separator + sourceStoreId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment;filename=" + fileName + "." + suffix);
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(FileUtils.openInputStream(file)));
    }
}

