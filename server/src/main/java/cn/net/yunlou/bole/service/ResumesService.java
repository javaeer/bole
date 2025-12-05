package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.Resumes;
import cn.net.yunlou.bole.model.ResumesCreate;
import cn.net.yunlou.bole.model.ResumesEdit;
import cn.net.yunlou.bole.model.ResumesQuery;
import cn.net.yunlou.bole.model.ResumesView;

/**
 * FileName: ResumesService Description: Created By MR. WANG Created At 2025/11/24 21:59 Modified By
 * Modified At
 */
public interface ResumesService
        extends IBaseService<Resumes, ResumesCreate, ResumesView, ResumesEdit, ResumesQuery> {}
