package njnu.opengms.container.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.bean.ProcessResponse;
import njnu.opengms.container.controller.common.BaseController;
import njnu.opengms.container.dto.refactormethod.AddRefactorMethodDTO;
import njnu.opengms.container.dto.refactormethod.FindRefactorMethodDTO;
import njnu.opengms.container.dto.refactormethod.UpdateRefactorMethodDTO;
import njnu.opengms.container.pojo.RefactorMethod;
import njnu.opengms.container.service.RefactorMethodServiceImp;
import njnu.opengms.container.utils.MethodInvokeUtils;
import njnu.opengms.container.utils.ResultUtils;
import njnu.opengms.container.vo.RefactorMethodVO;
import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName RefactorMethodController
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/17
 * @Version 1.0.0
 */
@RestController
@RequestMapping (value = "/refactor")
public class RefactorMethodController implements BaseController<RefactorMethod, AddRefactorMethodDTO, UpdateRefactorMethodDTO, FindRefactorMethodDTO, RefactorMethodVO, String, RefactorMethodServiceImp> {
    @Autowired
    RefactorMethodServiceImp refactorMethodServiceImp;

    @Value ("${web.upload-path}")
    String upload;

    @Override
    public RefactorMethodServiceImp getService() {
        return this.refactorMethodServiceImp;
    }


    @ApiImplicitParams ({
            @ApiImplicitParam (name = "id", value = "数据重构对应的id"),
            @ApiImplicitParam (name = "method", value = "数据重构中调用的方法"),
            @ApiImplicitParam (name = "input", value = "上传文件的路径数组"),
            @ApiImplicitParam (name = "output", value = "下载文件的名字数组"),
    })
    @RequestMapping (value = "/invoke", method = RequestMethod.GET)
    public JsonResult invoke(@RequestParam ("id") String id,
                             @RequestParam ("method") String method,
                             @RequestParam ("input") List<String> input,
                             @RequestParam ("output") List<String> output
    ) throws IOException {
        RefactorMethod refactorMethod = refactorMethodServiceImp.get(id);
        String position = refactorMethod.getPosition();

        List<String> inputList = new ArrayList<>();
        for (String s : input) {
            inputList.add(upload + File.separator + s);
        }
        List<String> outputList = new ArrayList<>();
        List<String> out = new ArrayList<>();
        for (String s : output) {
            String uuid = UUID.randomUUID().toString();
            outputList.add(upload + File.separator + "run" + File.separator + uuid + File.separator + s);
            out.add("run" + File.separator + uuid + File.separator + s);

        }
        ProcessResponse processResponse = MethodInvokeUtils.computeRefactor(
                upload + File.separator + position,
                method,
                inputList,
                outputList);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("processResponse", processResponse);
        if (processResponse.getFlag()) {
            JSONArray jsonArray = new JSONArray();
            for (String s : out) {
                jsonArray.add(s);
            }
            jsonObject.put("outputs", jsonArray);
        } else {
            jsonObject.put("output", null);
        }
        return ResultUtils.success(jsonObject);

    }

    @ApiOperation (value = "获取重构库中包含的方法", notes = "注意这里返回的是xml字符串在前端可以考虑xml2json或者直接用dom解析")
    @RequestMapping (value = "/{id}/getMethod", method = RequestMethod.GET)
    public JsonResult invoke(@PathVariable ("id") String id) throws IOException, DocumentException {
        RefactorMethod refactorMethod = refactorMethodServiceImp.get(id);
        String position = refactorMethod.getPosition();
        return ResultUtils.success(FileUtils.readFileToString(new File(upload + File.separator + position + File.separator + "methods.xml"), "utf-8"));
    }
}
