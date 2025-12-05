package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.entity.Config;
import cn.net.yunlou.bole.mapper.ConfigMapper;
import cn.net.yunlou.bole.service.ConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * FileName: SystemServiceImpl Description: Created By laughtiger Created At 2025/11/28 11:17
 * Modified By Modified At
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {}
