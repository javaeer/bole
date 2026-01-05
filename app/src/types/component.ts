// 组件基础配置
import { SkillResult } from "@/types/skill";
import { EducationExperienceResult } from "@/types/education-experience";
import { WorkExperienceResult } from "@/types/work-experience";
import { UserBasicInfo } from "@/types/user";


export interface ComponentMetadata {
  key: string;
  name: string;
  description?: string;
  category: string;
  icon: string;
  defaultProps: Record<string, any>;
  defaultStyles: Record<string, any>;
  version?: string;
}

export interface ComponentConfig {
  id: number;
  name: string;
  key: string;
  icon: string;
  description: string;
  category: string;
  props: Record<string, any>;
  styles: Record<string, any>;
  defaultConfig?: {
    props?: Record<string, any>;
    styles?: Record<string, any>;
  };
  metadata?: Record<string, any>;
}

export interface BaseComponentConfig {
  title?: string;

  [key: string]: any;
}


// 用户基本信息组件配置
export interface UserBasicInfoConfig extends BaseComponentConfig {
  info?: UserBasicInfo;
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

// 工作经历组件配置
export interface WorkExperienceConfig extends BaseComponentConfig {
  experiences?: WorkExperienceResult[];
  orderBy?: "startDate" | "endDate" | "position";
  orderDirection?: "asc" | "desc";
  maxItems?: number;
  showCompanyLogo?: boolean;
  showCompanyName?: boolean;
  showJobTitle?: boolean;
  showDepartment?: boolean;
  showWorkPeriod?: boolean;
  showWorkContent?: boolean;
  showAchievements?: boolean;
  showSkills?: boolean;
}

// 教育经历组件配置
export interface EducationExperienceConfig extends BaseComponentConfig {
  experiences?: EducationExperienceResult[];
  orderBy?: "startDate" | "endDate" | "degree";
  orderDirection?: "asc" | "desc";
  maxItems?: number;
  showUniversityLogo?: boolean;
  showUniversityName?: boolean;
  showDegree?: boolean;
  showMajor?: boolean;
  showEducationPeriod?: boolean;
  showGPA?: boolean;
  showCourses?: boolean;
  showHonors?: boolean;
  degreeFormat?: "full" | "short";
}

// 技能专长组件配置
export interface SkillsConfig extends BaseComponentConfig {
  skills?: SkillResult[];
  skillCategories?: string[];
  groupByCategory?: boolean;
  maxSkillsPerCategory?: number;
  showSkillLevel?: boolean;
  skillLevelType?: "progress" | "text" | "stars";
  showExperienceYears?: boolean;
  showTags?: boolean;
}

//项目经历组件配置
export interface ProjectExperienceConfig extends BaseComponentConfig {

}

//求职意向组件配置
export interface JobIntentionConfig extends BaseComponentConfig {

}

//求职意向组件配置
export interface SelfEvaluationConfig extends BaseComponentConfig {

}