package njnu.opengms.container.enums;

/**
 * @EnumName DataResourceTypeEnum
 * @Description todo
 * @Author sun_liber
 * @Date 2019/3/7
 * @Version 1.0.0
 */
public enum DataResourceTypeEnum {
    SHAPEFILE(0, "shapefile"),
    GEOTIFF(1, "geotiff"),
    UDX(2, "udx"),
    OTHER(3, "other");


    private int code;
    private String type;

    DataResourceTypeEnum(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}
