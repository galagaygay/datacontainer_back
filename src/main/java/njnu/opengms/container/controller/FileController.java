package njnu.opengms.container.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.utils.ResultUtils;
import njnu.opengms.container.utils.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value ("${web.upload-path}")
    String upload;

    @ApiOperation (value = "上传实体的Zip文件", notes = "上传映射、重构服务实体")
    @ApiImplicitParam (name = "type", value = "上传实体的类型,可以为map、refactor", dataType = "string", paramType = "path", required = true)
    @RequestMapping (value = "/upload/{type}", method = RequestMethod.POST)
    JsonResult upload(@RequestParam ("file") MultipartFile file,
                      @PathVariable ("type") String type) throws IOException {
        String path = "";
        if (("map").equals(type)) {
            path += "services" + File.separator + "map" + File.separator;
        } else if (("refactor").equals(type)) {
            path += "services" + File.separator + "refactor" + File.separator;
        } else if ("run".equals(type)) {
            path += "run" + File.separator;
        } else {
            throw new MyException(ResultEnum.UPLOAD_TYPE_ERROR);
        }
        path += (UUID.randomUUID().toString());

        FileUtils.copyInputStreamToFile(file.getInputStream(), new File(upload + File.separator + path + File.separator + file.getOriginalFilename()));
        // 针对映射和重构需要进行解压
        // 注意解压路径是同级目录下的invoke文件夹
        if (("map").equals(type) || ("refactor").equals(type)) {
            ZipUtils.unZipFiles(new File(upload + File.separator + path + File.separator + file.getOriginalFilename()), upload + File.separator + path + File.separator + "invoke");
        }
        return ResultUtils.success(path + File.separator + file.getOriginalFilename());
    }


    @ApiOperation (value = "下载文件", notes = "根据映射、重构的路径下载文件,这里用post方法是因为数据库存储的路径是\\,get请求拼接字符串需要对\\进行编码")
    @RequestMapping (value = "/download", method = RequestMethod.POST)
    ResponseEntity<InputStreamResource> download(@RequestParam ("path") String path) throws IOException {
        File file = new File(upload + File.separator + path);
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
}
