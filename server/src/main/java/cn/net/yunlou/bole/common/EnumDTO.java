package cn.net.yunlou.bole.common;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnumDTO implements Serializable {
    private Object value;
    private String label;
}
