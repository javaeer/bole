package cn.net.yunlou.bole.handler.document;

import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.handler.IDocumentGeneratorStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * FileName: TextDocumentGeneratorStrategy
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 22:25
 * Modified By
 * Modified At
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TextDocumentGeneratorStrategy implements IDocumentGeneratorStrategy {
    @Override
    public DocumentType getType() {
        return DocumentType.TXT;
    }

    @Override
    public File generate(Long resumesId) {

        return null;
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType==DocumentType.TXT;
    }
}
