
//模板组件响应类型
export interface TemplateComponentResult {
  name: string;
  key: string;
  defaultConfig?: string; //组件默认配置
  templateId?: number; //模板ID
  componentId: number; //组件ID
  props: Record<string, any>;
  styles: Record<string, any>;
}

//模板组件 提交 类型
export interface TemplateComponentForm {
  templateId?: number; //新建模板时，可为空
  componentId: number; //组件ID
  props: Record<string, any>;
  styles: Record<string, any>;
}

// 基础组件props类型
export interface ComponentCommonProps {
  title?: string;
  showTitle?: boolean;
  [key: string]: any;
}

// 用户基本信息组件配置
export interface UserBasicInfoConfig extends UserResult {
  showName?: boolean;
  showEmail?: boolean;
  showPhone?: boolean;
  showAvatar?: boolean;
  showGender?: boolean;
  showBirthday?: boolean;
  showLocation?: boolean;
  showWorkYears?: boolean;
  avatarSize?: "small" | "medium" | "large";
  layout?: "card" | "simple";
}