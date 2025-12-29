// 简历查询条件
import { TemplateComponentItem } from "@/types/template-component";
import { TemplateGlobalLayout, TemplateGlobalStyle } from "@/types/template";

export interface ResumesQuery extends BodyQuery {
  name?: string;
  status?: number;
  userId?: number;
  templateId?: number;
  createAt?: [string, string]; // 创建时间范围
}

// 简历结果
export interface ResumesResult {
  id: number | null;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  userId: number;
  templateId: number;
  status: number;
  viewCount: number;
  downloadCount: number;
  globalStyle: TemplateGlobalStyle;
  globalLayout: TemplateGlobalLayout;
  components: TemplateComponentItem[];
}

// 简历表单
export interface ResumesForm {
  id?: number;
  userId?: number;
  templateId: number;
  status?: number;
  globalStyle?: TemplateGlobalStyle;
  globalLayout?: TemplateGlobalLayout;
  components?: Array<{
    id?: number
    componentId: number
    key: string
    name: string
    defaultConfig?: any
    props: {
      // 通用字段
      title?: string
      // 经历类字段
      experiences?: Array<{
        id?: number
        company?: string
        position?: string
        description?: string
        startDate?: string
        endDate?: string
        skills?: string[]
        achievements?: string[]
        [key: string]: any
      }>
      // 意向类字段
      intentions?: Array<{
        id?: number
        position?: string
        salary?: string
        jobType?: string
        city?: string
        [key: string]: any
      }>
      // 评价类字段
      evaluations?: Array<{
        id?: number
        content?: string
        createdAt?: string
        [key: string]: any
      }>
      // 技能类字段
      skills?: Array<{
        id?: number
        name?: string
        category?: string
        proficiencyPercent?: number
        level?: string
        experienceYears?: number
        description?: string
        tags?: string
        isCertified?: boolean
        certificateName?: string
        certificateDate?: string
        [key: string]: any
      }>
      // 基础信息字段
      name?: string
      email?: string
      phone?: string
      location?: string
      avatar?: string
      workYears?: number
      [key: string]: any
    }
    styles?: Record<string, any>
  }>;
}
