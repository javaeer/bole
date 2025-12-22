package cn.net.yunlou.bole.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用API响应")
public class BusinessResponse<T> {

    @Schema(description = "状态码", example = "200")
    private Integer code;

    @Schema(description = "消息", example = "操作成功")
    private String message;

    @Schema(description = "数据")
    private T data;

    @Schema(description = "时间戳", example = "1640995200000")
    private Long timestamp;

    public static <T> BusinessResponse<T> success(T data) {
        return BusinessResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> BusinessResponse<T> error(String message) {
        return BusinessResponse.<T>builder()
                .code(500)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> BusinessResponse<T> error(Integer code, String message) {
        return BusinessResponse.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> BusinessResponse<T> error(BusinessStatus businessStatus) {
        return BusinessResponse.<T>builder()
                .code(businessStatus.getValue())
                .message(businessStatus.getLabel())
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
