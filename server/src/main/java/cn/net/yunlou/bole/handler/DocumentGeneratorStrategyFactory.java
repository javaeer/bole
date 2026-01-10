package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FileName: DocumentGeneratorStrategyFactory
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 22:16
 * Modified By
 * Modified At
 */

@Component
@RequiredArgsConstructor
public class DocumentGeneratorStrategyFactory {

    /** 这个注入了多个实现类对象 */
    // @Autowired
    private final List<IDocumentGeneratorStrategy> strategies;

    /**
     * 根据类型 获取 文档生成策略
     *
     * @param documentType
     * @return
     */
    public IDocumentGeneratorStrategy getDocumentGeneratorStrategy(DocumentType documentType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(documentType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No strategy found for " + documentType));
    }
    
}
