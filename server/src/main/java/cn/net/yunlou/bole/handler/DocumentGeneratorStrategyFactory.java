package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.handler.document.DocumentDirectoryManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FileName: DocumentGeneratorStrategyFactory
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 22:16
 * Modified By
 * Modified At
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentGeneratorStrategyFactory {

    private final DocumentDirectoryManager directoryManager;

    /** 这个注入了多个实现类对象 */
    // @Autowired
    private final List<IDocumentGeneratorStrategy> strategies;

    private final Map<DocumentType, IDocumentGeneratorStrategy> strategyMap =
            new ConcurrentHashMap<>();


    /**
     * 初始化所有策略
     */
    @PostConstruct
    public void init() {
        for (IDocumentGeneratorStrategy strategy : strategies) {
            strategyMap.put(strategy.getType(), strategy);
            log.info("注册文档生成策略: {}", strategy.getType());
        }

        log.info("文档生成器工厂初始化完成，已注册 {} 个策略", strategyMap.size());
    }


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

    /**
     * 获取支持的文档类型
     */
    public List<DocumentType> getSupportedTypes() {
        return List.copyOf(strategyMap.keySet());
    }

    /**
     * 检查是否支持指定类型
     */
    public boolean supports(DocumentType documentType) {
        return strategyMap.containsKey(documentType);
    }
    
}
