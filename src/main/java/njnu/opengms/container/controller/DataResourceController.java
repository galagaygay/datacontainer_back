package njnu.opengms.container.controller;

import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.dto.dataresource.AddDataResourceDTO;
import njnu.opengms.container.dto.dataresource.FindDataResourceDTO;
import njnu.opengms.container.dto.dataresource.UpdateDataResourceDTO;
import njnu.opengms.container.service.DataResourceService;
import njnu.opengms.container.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /******/
    // Above code is for Application

    /*****/
    JsonResult listByMdlId(@PathVariable ("mdlId") String mdlId) {
        return ResultUtils.success(dataResourceService.listByMdlId(mdlId));
    }

}
