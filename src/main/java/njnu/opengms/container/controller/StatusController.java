package njnu.opengms.container.controller;

import njnu.opengms.container.bean.JsonResult;
import njnu.opengms.container.utils.ResultUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by SongJie on 2019/4/12 11:43
 */
@RestController
public class StatusController {

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public JsonResult ping() {
        return ResultUtils.success("OK");
    }
}
