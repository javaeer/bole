// 组件基础配置
export interface BaseComponentConfig {
  title?: string
  [key: string]: any
}

// 用户基本信息组件配置
export interface UserBasicInfoConfig extends BaseComponentConfig {
  fields?: string[]
  showName?: boolean
  showEmail?: boolean
  showPhone?: boolean
  showAvatar?: boolean
  showGender?: boolean
  showBirthday?: boolean
  showLocation?: boolean
  showWorkYears?: boolean
  avatarSize?: 'small' | 'medium' | 'large'
  layout?: 'card' | 'simple'
}

// 工作经历组件配置
export interface WorkExperienceConfig extends BaseComponentConfig {
  experiences?: WorkExperienceItem[]
  orderBy?: 'startDate' | 'endDate' | 'position'
  orderDirection?: 'asc' | 'desc'
  maxItems?: number
  showCompanyLogo?: boolean
  showCompanyName?: boolean
  showJobTitle?: boolean
  showDepartment?: boolean
  showWorkPeriod?: boolean
  showWorkContent?: boolean
  showAchievements?: boolean
  showSkills?: boolean
}

// 工作经历项
export interface WorkExperienceItem {
  id: number
  company: string
  position: string
  department?: string
  startDate: string
  endDate?: string
  description?: string
  achievements?: string[]
  skills?: string[]
  isCurrent?: boolean
  createdAt?: string
  updatedAt?: string
  deleted?: number
}

// 教育经历组件配置
export interface EducationExperienceConfig extends BaseComponentConfig {
  experiences?: EducationExperienceItem[]
  orderBy?: 'startDate' | 'endDate' | 'degree'
  orderDirection?: 'asc' | 'desc'
  maxItems?: number
  showUniversityLogo?: boolean
  showUniversityName?: boolean
  showDegree?: boolean
  showMajor?: boolean
  showEducationPeriod?: boolean
  showGPA?: boolean
  showCourses?: boolean
  showHonors?: boolean
  degreeFormat?: 'full' | 'short'
}

// 教育经历项
export interface EducationExperienceItem {
  id: number
  university: string
  degree: string
  major?: string
  startDate: string
  endDate?: string
  description?: string
  gpa?: string
  ranking?: string
  courses?: string[]
  achievements?: string[]
  isHighest?: number
  createdAt?: string
  updatedAt?: string
  deleted?: number
}

// 技能专长组件配置
export interface SkillsConfig extends BaseComponentConfig {
  skills?: SkillItem[]
  skillCategories?: string[]
  groupByCategory?: boolean
  maxSkillsPerCategory?: number
  showSkillLevel?: boolean
  skillLevelType?: 'progress' | 'text' | 'stars'
  showExperienceYears?: boolean
  showTags?: boolean
}

// 技能项
export interface SkillItem {
  id: number
  name: string
  category?: string
  level?: string
  proficiencyPercent?: number
  experienceYears?: number
  description?: string
  tags?: string
  isCertified?: boolean
  certificateName?: string
  certificateDate?: string
  isPublic?: boolean
  sort?: number
  createdAt?: string
  updatedAt?: string
  deleted?: number
}