// 组件基础配置
import { SkillResult } from "@/types/skill";
import { EducationExperienceResult } from "@/types/education-experience";
import { WorkExperienceResult } from "@/types/work-experience";
import { UserResult } from "@/types/user";
import { ProjectExperienceResult } from "@/types/project-experience";
import { JobIntentionResult } from "@/types/job-intention";
import { SelfEvaluationResult } from "@/types/self-evaluation";


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

// 工作经历组件配置
export interface WorkExperienceConfig {
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
export interface EducationExperienceConfig {
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
export interface SkillsConfig {
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
export interface ProjectExperienceConfig {
  experiences?: ProjectExperienceResult[];
  showTags?: boolean;
}

//求职意向组件配置
export interface JobIntentionConfig {
  intentions?: JobIntentionResult[];
  showTags?: boolean;
}

//求职意向组件配置
export interface SelfEvaluationConfig {
  evaluations?: SelfEvaluationResult[];
  showTags?: boolean;
}