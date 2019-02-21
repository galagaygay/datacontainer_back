package njnu.opengms.container.controller;

import com.alibaba.fastjson.JSONObject;
import com.ngis.udx.Transfer;
import com.ngis.udx.schema.UdxSchema;
import io.swagger.annotations.ApiOperation;
import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.controller.common.BaseController;
import njnu.opengms.container.dto.schemadoc.AddSchemaDocDTO;
import njnu.opengms.container.dto.schemadoc.FindSchemaDocDTO;
import njnu.opengms.container.dto.schemadoc.UpdateSchemaDocDTO;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.pojo.SchemaDoc;
import njnu.opengms.container.repository.MappingMethodRepository;
import njnu.opengms.container.repository.RefactorMethodRepository;
import njnu.opengms.container.service.SchemaDocServiceImp;
import njnu.opengms.container.utils.ResultUtils;
import njnu.opengms.container.vo.MappingMethodVO;
import njnu.opengms.container.vo.RefactorMethodVO;
import njnu.opengms.container.vo.SchemaDocVO;
import org.dom4j.DocumentException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName SchemaDocController
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/17
 * @Version 1.0.0
 */
@RestController
@RequestMapping (value = "/schemaDoc")
public class SchemaDocController implements BaseController<SchemaDoc, AddSchemaDocDTO, UpdateSchemaDocDTO, FindSchemaDocDTO, SchemaDocVO, String, SchemaDocServiceImp> {

    @Autowired
    SchemaDocServiceImp schemaDocServiceImp;
    @Autowired
    MappingMethodRepository mappingMethodRepository;
    @Autowired
    RefactorMethodRepository refactorMethodRepository;

    @Override
    public SchemaDocServiceImp getService() {
        return this.schemaDocServiceImp;
    }

    @ApiOperation (value = "获取与指定id对应的schemaDoc相关的资源", notes = "包括映射、重构资源")
    @RequestMapping (value = "/{id}/relatedResources", method = RequestMethod.GET)
    public JsonResult getRelatedResources(@PathVariable ("id") String id) {
        List<MappingMethodVO> mappingMethodVOList = new ArrayList<>();
        mappingMethodRepository.findBySupportedUdxSchema(id).forEach(el -> {
            MappingMethodVO vo = new MappingMethodVO();
            BeanUtils.copyProperties(el, vo);
            mappingMethodVOList.add(vo);
        });

        List<RefactorMethodVO> refactorMethodVOList = new ArrayList<>();
        refactorMethodRepository.findBySupportedUdxSchemas(id).forEach(el -> {
            RefactorMethodVO vo = new RefactorMethodVO();
            BeanUtils.copyProperties(el, vo);
            refactorMethodVOList.add(vo);
        });
        JSONObject object = new JSONObject();
        object.put("map", mappingMethodVOList);
        object.put("refactor", refactorMethodVOList);
        return ResultUtils.success(object);
    }


    @ApiOperation (value = "获取点击量前10高的SchemaDoc", notes = "目前这里不是用的点击量，是按照创建事件进行排序的")
    @RequestMapping (value = "/getTop10", method = RequestMethod.GET)
    public JsonResult getTop10() {
        //TODO 目前这里不是用的点击量，是按照创建事件进行排序的
        return ResultUtils.success(schemaDocServiceImp.getTop10());
    }

    @ApiOperation (value = "根据name查询，获取SchemaDoc列表")
    @RequestMapping (value = "/findByNameContains", method = RequestMethod.GET)
    public JsonResult getSchemaDocByName(@RequestParam ("name") String name) {
        return ResultUtils.success(schemaDocServiceImp.findByNameContains(name));
    }

    @ApiOperation (value = "由UdxSchema生成对应的同构UdxData")
    @RequestMapping (value = "/generate", method = RequestMethod.POST)
    public JsonResult generateUDXData(@RequestBody UdxSchema udxSchema) {
        return ResultUtils.success(Transfer.generate(udxSchema));
    }

    @ApiOperation (value = "上传Schema文件得到Schema", notes = "Schema文件可以为json或者xml文件")
    @RequestMapping (value = "/getSchema", method = RequestMethod.POST)
    public JsonResult getSchema(@RequestParam ("file") MultipartFile file) throws IOException, DocumentException {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        File fileNormal = File.createTempFile(UUID.randomUUID().toString(), suffix);
        file.transferTo(fileNormal);
        UdxSchema udxSchema;
        if ("json".equals(suffix)) {
            udxSchema = Transfer.loadSchemaFromJsonFile(fileNormal);
        } else if ("xml".equals(suffix)) {
            udxSchema = Transfer.loadSchemaFromXmlFile(fileNormal);
        } else {
            throw new MyException(ResultEnum.TRANSFER_UDX_ERROR);
        }
        return ResultUtils.success(udxSchema);
    }
}
