export const COMPONENT_LIBRARY = [
  {
    code: 'UserBasicInfo',
    name: '基本信息',
    icon: 'person',
    description: '个人基本信息和联系方式',
    category: 'basic',
    defaultConfig: {
      showAvatar: true,
      showContact: true
    }
  },
  {
    code: 'EducationExperience',
    name: '教育背景',
    icon: 'school',
    description: '学历和教育经历',
    category: 'education',
    defaultConfig: {
      showTime: true,
      showDegree: true
    }
  },
  {
    code: 'WorkExperience',
    name: '工作经历',
    icon: 'work',
    description: '工作经历和职位信息',
    category: 'work',
    defaultConfig: {
      showDuration: true,
      showCompany: true
    }
  },
  {
    code: 'Skills',
    name: '技能专长',
    icon: 'star',
    description: '专业技能和能力',
    category: 'skill',
    defaultConfig: {
      skillLevel: true,
      showTags: true
    }
  },
  {
    code: 'ProjectExperience',
    name: '项目经验',
    icon: 'assignment',
    description: '项目经历和成果',
    category: 'project',
    defaultConfig: {
      showRole: true,
      showTechnologies: true
    }
  },
  {
    code: 'SelfEvaluation',
    name: '自我评价',
    icon: 'rate-review',
    description: '自我评价和总结',
    category: 'evaluation',
    defaultConfig: {
      maxLength: 500
    }
  },
  {
    code: 'JobIntention',
    name: '求职意向',
    icon: 'target',
    description: '求职目标和期望',
    category: 'job',
    defaultConfig: {
      showSalary: false,
      showLocation: true
    }
  }
];

export const LAYOUT_TYPES =[
  { value: "single-column", label: "单栏", icon: "single" },
  { value: "two-column", label: "双栏", icon: "double" },
  { value: "three-column", label: "三栏", icon: "triple" },
  { value: "creative", label: "创意", icon: "creative" },
]