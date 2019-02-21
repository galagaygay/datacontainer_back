package njnu.opengms.container.enums;

/**
 * 自定义的异常code，请勿覆盖常见的HttpStatus.*.value()
 */
public enum ResultEnum {
    SUCCESS(0, "成功"),
    DEFAULT_EXCEPTION(-1, "默认的服务器内部异常，我并不想进行处理！！"),

    NO_OBJECT(-2, "没有对应的对象"),
    EXIST_OBJECT(-3, "同名对象已存在，请更换名称"),

    NO_TOKEN(-4, "Missing Token"),
    TOKEN_NOT_MATCH(-5, "Token not match"),
    TOKEN_WRONG(-6, "Token Wrong"),
    USER_PASSWORD_NOT_MATCH(-7, "账户名和密码不匹配"),

    UPLOAD_TYPE_ERROR(-8, "上传Type存在问题"),
    TRANSFER_UDX_ERROR(-9, "UDX转换时，支持的文件类型仅仅有json与xml"),


    REMOTE_SERVICE_ERROR(-10, "远程服务调用出错");

    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
