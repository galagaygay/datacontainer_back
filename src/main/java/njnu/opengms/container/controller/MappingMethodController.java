package njnu.opengms.container.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.bean.ProcessResponse;
import njnu.opengms.container.controller.common.BaseController;
import njnu.opengms.container.dto.mappingmethod.AddMappingMethodDTO;
import njnu.opengms.container.dto.mappingmethod.FindMappingMethodDTO;
import njnu.opengms.container.dto.mappingmethod.UpdateMappingMethodDTO;
import njnu.opengms.container.pojo.MappingMethod;
import njnu.opengms.container.service.MappingMethodServiceImp;
import njnu.opengms.container.utils.MethodInvokeUtils;
import njnu.opengms.container.utils.ResultUtils;
import njnu.opengms.container.vo.MappingMethodVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName MappingMethodController
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/17
 * @Version 1.0.0
 */
@RestController
@RequestMapping (value = "/map")
public class MappingMethodController implements BaseController<MappingMethod, AddMappingMethodDTO, UpdateMappingMethodDTO, FindMappingMethodDTO, MappingMethodVO, String, MappingMethodServiceImp> {
    @Autowired
    MappingMethodServiceImp mappingMethodServiceImp;

    @Value ("${web.upload-path}")
    String upload;

    @Override
    public MappingMethodServiceImp getService() {
        return this.mappingMethodServiceImp;
    }

    @ApiImplicitParams ({
            @ApiImplicitParam (name = "id", value = "数据映射对应的id"),
            @ApiImplicitParam (name = "callType", value = "可选值为src2udx,udx2src"),
            @ApiImplicitParam (name = "input", value = "上传文件的路径"),
            @ApiImplicitParam (name = "output", value = "下载文件的名称"),
    })
    @RequestMapping (value = "/invoke", method = RequestMethod.GET)
    public JsonResult invoke(@RequestParam ("id") String id,
                             @RequestParam ("callType") String callType,
                             @RequestParam ("input") String input,
                             @RequestParam ("output") String output
    ) throws IOException {
        //根据映射方法ID查找映射方法存储的路径
        MappingMethod mappingMethod = mappingMethodServiceImp.get(id);
        String position = mappingMethod.getPosition();
        //调用方法
        String uid = UUID.randomUUID().toString();
        String basePath = upload + File.separator + position;
        String inputLocal = upload + File.separator + input;
        String outputLocal = upload + File.separator + "online_call_files" + File.separator + uid + File.separator + output;
        ProcessResponse processResponse = MethodInvokeUtils.computeMap(basePath, callType, inputLocal, outputLocal);
        //返回cmd命令执行情况，以及输出数据路径
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("processResponse", processResponse);
        if (processResponse.getFlag()) {
            jsonObject.put("output", "online_call_files" + File.separator + uid + File.separator + output);
        } else {
            jsonObject.put("output", null);
        }
        return ResultUtils.success(jsonObject);
    }
}
