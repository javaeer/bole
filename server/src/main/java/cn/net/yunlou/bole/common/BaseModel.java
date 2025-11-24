package cn.net.yunlou.bole.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *     前端请求参数祖类 是处理参数的基石
 * </p>
 *
 * @Author javaeer(javaeer @ aliyun.com)
 * @Date 2020/3/30 12:06
 * @Version 1.0
 */
@Data
public class BaseModel implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    protected Date startAt;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    protected Date stopAt;
}
