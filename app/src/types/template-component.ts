//模板组件响应类型
export interface TemplateComponentItem {
  id: number;//此乃多对多 关联关系主键 非 组件ID
  deleted: number;
  name: string;
  key: string;
  defaultConfig?: string; //组件默认配置
  templateId?: number; //模板ID
  componentId: number; //组件ID
  props: Record<string, any>;
  styles: Record<string, any>;
  createdAt?: string;
  updatedAt?: string;
}

//模板组件 提交 类型
export interface TemplateComponentForm {
  templateId?: number; //新建模板时，可为空
  componentId: number; //组件ID
  props: Record<string, any>;
  styles: Record<string, any>;
}