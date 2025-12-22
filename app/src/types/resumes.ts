// 简历查询条件
import { TemplateComponentItem } from "@/types/template-component";
import { TemplateGlobalLayout, TemplateGlobalStyle } from "@/types/template";

export interface ResumesQuery extends PageQuery {
  name?: string
  status?: number
  userId?: number
  templateId?: number
  createAt?: [string, string] // 创建时间范围
}

// 简历结果
export interface ResumesResult {
  id: number | null
  createdAt: string
  updatedAt: string
  deleted: number
  userId: number
  templateId: number
  status: number
  viewCount: number
  downloadCount: number
  globalStyle: TemplateGlobalStyle
  globalLayout: TemplateGlobalLayout
  components: TemplateComponentItem[]
}

// 简历表单
export interface ResumesForm {
  id?: number
  userId?: number
  templateId: number
  status?: number
  globalStyle?: TemplateGlobalStyle
  globalLayout?: TemplateGlobalLayout
  components?: TemplateComponentItem[]
}
