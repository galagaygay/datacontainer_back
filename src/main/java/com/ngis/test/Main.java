package com.ngis.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import njnu.opengms.container.getmeta.DataStoreMetaGet;
import njnu.opengms.container.getmeta.impl.ShapefileMetaGet;
import njnu.opengms.container.getmeta.meta.ShapefileMeta;

import java.io.File;

/**
 * @ClassName Main
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/6
 * @Version 1.0.0
 */

public class Main {
    public static void main(String[] args) throws Exception {
//        File rasterFile = new File("F:\\sunlingzhi\\datacontainer_store\\meta\\b8bc64a2-5130-4c4f-b244-b4752c0c9da0\\dem30m.tif");
//        DataStoreMetaGet metaGet = new GeotiffMetaGet();
//        GeotiffMeta geotiffMeta= (GeotiffMeta) metaGet.getMeta(rasterFile);
//        System.out.println(geotiffMeta.getName());


        File shapeFile = new File("E:\\qqDoc\\523769144\\FileRecv\\DaoLu\\DL.shp");
        DataStoreMetaGet metaGet = new ShapefileMetaGet();
        ShapefileMeta shapefileMeta = (ShapefileMeta) metaGet.getMeta(shapeFile);


        String s = JSONObject.toJSONString(shapefileMeta);
        String z = JSON.toJSONString(shapefileMeta);

        System.out.println(shapefileMeta.getLowerCorner());
        System.out.println(z);
    }
}
