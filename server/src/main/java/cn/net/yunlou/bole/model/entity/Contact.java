package cn.net.yunlou.bole.model.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * FileName: Contact Description: Created By laughtiger Created At 2025/12/29 16:50 Modified By
 * Modified At
 */
@Data
public class Contact implements Serializable {

    private String user;

    private String phone;

    private String email;
}
