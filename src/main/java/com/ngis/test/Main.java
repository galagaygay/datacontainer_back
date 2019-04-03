package com.ngis.test;

import njnu.opengms.container.getmeta.impl.ShapefileMeta;

import java.io.File;
import java.io.IOException;

/**
 * @ClassName Main
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/6
 * @Version 1.0.0
 */

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("F:/sunlingzhi/datacontainer_store/geoserver_files/shapefiles/24a6b80a-e15b-49f2-bebc-d03e427a5051_QXJM.dbf");
        ShapefileMeta shapefileMeta = new ShapefileMeta();
        shapefileMeta.readDBF(file, null, null);
    }
}
