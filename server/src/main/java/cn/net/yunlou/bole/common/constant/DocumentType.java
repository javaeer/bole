package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: DocumentType
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 21:33
 * Modified By
 * Modified At
 */
@Getter
@AllArgsConstructor
public enum DocumentType implements IEnum<String> {

    PDF("pdf", "pdf"),
    WORD("word", "word"),
    HTML("html", "html"),
    TXT("txt", "txt");

    private final String value;

    private final String label;
}
