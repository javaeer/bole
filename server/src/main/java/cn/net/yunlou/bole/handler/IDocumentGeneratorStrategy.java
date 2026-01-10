package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.DocumentType;

import java.io.File;

/**
 * FileName: IDocumentGeneratorStrategy
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 22:14
 * Modified By
 * Modified At
 */
public interface IDocumentGeneratorStrategy {


    DocumentType getType();

    File generate(Long resumesId);

    boolean supports(DocumentType documentType);
}
