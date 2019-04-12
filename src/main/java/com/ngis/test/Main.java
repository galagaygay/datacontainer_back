package com.ngis.test;

/**
 * @ClassName Main
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/6
 * @Version 1.0.0
 */

public class Main {
    public static void main(String[] args) throws Exception {
        File rasterFile = new File("F:\\dem30m.tif");
        DataStoreMetaGet metaGet = new GeotiffMetaGet();
        GeotiffMeta geotiffMeta = (GeotiffMeta) metaGet.getMeta(rasterFile);
        System.out.println(geotiffMeta.getName());


    }
}
