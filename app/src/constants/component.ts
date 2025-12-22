export const COMPONENT_LIBRARY = [
  {
    id: 1,
    key: "UserBasicInfo",
    name: "基本信息",
    icon: "person",
    description: "个人基本信息和联系方式",
    category: "basic",
    defaultConfig: {
      showAvatar: true,
      showContact: true,
    },
  },
  {
    id: 2,
    key: "JobIntention",
    name: "求职意向",
    icon: "target",
    description: "求职目标和期望",
    category: "job",
    defaultConfig: {
      showSalary: false,
      showLocation: true,
    },
  },
  {
    id: 3,
    key: "WorkExperience",
    name: "工作经历",
    icon: "work",
    description: "工作经历和职位信息",
    category: "work",
    defaultConfig: {
      showDuration: true,
      showCompany: true,
    },
  },
  {
    id: 4,
    key: "EducationExperience",
    name: "教育背景",
    icon: "school",
    description: "学历和教育经历",
    category: "education",
    defaultConfig: {
      showTime: true,
      showDegree: true,
    },
  },
  {
    id: 5,
    key: "SelfEvaluation",
    name: "自我评价",
    icon: "rate-review",
    description: "自我评价和总结",
    category: "evaluation",
    defaultConfig: {
      maxLength: 500,
    },
  },
  {
    id: 6,
    key: "Skills",
    name: "技能专长",
    icon: "star",
    description: "专业技能和能力",
    category: "skill",
    defaultConfig: {
      skillLevel: true,
      showTags: true,
    },
  },
  {
    id: 7,
    key: "ProjectExperience",
    name: "项目经验",
    icon: "assignment",
    description: "项目经历和成果",
    category: "project",
    defaultConfig: {
      showRole: true,
      showTechnologies: true,
    },

  },
  {
    id: 8,
    key: "CompanyExperience",
    name: "公司经历",
    icon: "assignment",
    description: "公司经历",
    category: "project",
    defaultConfig: {
      showRole: true,
      showTechnologies: true,
    },
  },
];

