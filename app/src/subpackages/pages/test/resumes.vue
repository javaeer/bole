<!-- pages/resume/preview.vue -->
<template>
  <view class="resume-preview-page">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 错误状态 -->
    <view v-else-if="error" class="error-state">
      <text class="error-icon">⚠️</text>
      <text class="error-text">{{ error }}</text>
      <button @click="loadResumeData" class="retry-button">重试</button>
    </view>

    <!-- 简历预览 -->
    <view v-else-if="resumeData" class="resume-container">
      <DynamicRenderer
        :resume-data="resumeData"
        :preview-mode="true"
      />
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { ResumesResult } from "@/types/resumes";
import DynamicRenderer from "@/components/resumes/DynamicRenderer.vue";

// 状态
const resumeData = ref<ResumesResult | null>(null);
const loading = ref(false);
const error = ref<string | null>(null);

// 加载简历数据
const loadResumeData = async () => {
  try {
    loading.value = true;
    error.value = null;

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 800));

    // 使用你的接口数据
    resumeData.value = {
      id: 14,
      createdAt: "2026-01-05 23:02:39",
      updatedAt: "2026-01-05 23:02:39",
      deleted: 0,
      userId: 1,
      templateId: 3,
      name: "张三的个人简历",
      status: "published",
      viewCount: 123,
      downloadCount: 45,
      globalStyle: {
        theme: "tech",
        fontSizes: { h1: "30", body: "13" },
        fontFamily: "'Roboto', 'Helvetica Neue', Arial, sans-serif",
        headerColor: null,
        primaryColor: "#5ac8fa",
        accentColor: "#5ac8fa",
        secondaryColor: "#5ac8fa",
        backgroundColor: "#f0f8ff",
        spacing: {
          sectionMargin: "16px",
          padding: "12px",
          lineHeight: "1.5"
        }
      },
      globalLayout: {
        type: "timeline",
        columns: { left: 50, right: 50 },
        orientation: "portrait",
        componentOrder: [
          "UserBasicInfo",
          "JobIntention",
          "WorkExperience",
          "Skills",
          "SelfEvaluation"
        ]
      },
      components: [
        {
          key: "UserBasicInfo",
          name: "基本信息",
          props: {
            id: 1,
            name: "张三",
            email: "zhangsan@tencent.com",
            phone: "18610880038",
            title: "高级工程师",
            avatar: "https://example.com/avatar1.jpg",
            gender: 0,
            github: "zhangsan",
            wechat: "zhangsan_wx",
            deleted: 0,
            website: "https://zhangsan.dev",
            location: "深圳",
            createdAt: "2026-01-01T17:07:50",
            updatedAt: "2026-01-05T22:08:38",
            workYears: 5,
            showAvatar: true,
            showContact: true
          },
          styles: {},
          templateId: null,
          componentId: 1,
          defaultConfig: {
            props: {
              title: "基本信息",
              fields: [
                "name",
                "gender",
                "birthday",
                "phone",
                "email",
                "location",
                "workYears"
              ],
              showName: true,
              showEmail: true,
              showPhone: true,
              avatarSize: "medium",
              showAvatar: true,
              showGender: true,
              showBirthday: true,
              showLocation: true,
              showWorkYears: true
            },
            styles: {
              padding: "20px",
              fontSize: "16px",
              fieldColor: "#666666",
              titleColor: "#333333",
              avatarBorder: "2px solid #e8e8e8",
              borderRadius: "8px",
              backgroundColor: "#FFFFFF"
            }
          }
        },
        {
          key: "JobIntention",
          name: "求职意向",
          props: {
            intentions: [
              {
                id: 1,
                city: "北京市",
                salary: "1000000",
                deleted: 0,
                jobType: "全职",
                position: "CTO",
                createdAt: "2026-01-04T12:33:23",
                updatedAt: "2026-01-04T12:33:23"
              }
            ],
            showSalary: false,
            showLocation: true
          },
          styles: {},
          templateId: null,
          componentId: 2,
          defaultConfig: {
            props: {
              title: "求职意向",
              salaryUnit: "K",
              showJobType: true,
              locationType: "city",
              showWorkLocation: true,
              showCurrentStatus: true,
              showExpectedSalary: true,
              showOnboardingTime: true,
              showExpectedIndustry: true,
              showExpectedPosition: true
            },
            styles: {
              padding: "20px",
              fontSize: "16px",
              boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
              titleColor: "#333333",
              borderRadius: "8px",
              highlightColor: "#1890ff",
              backgroundColor: "#FFFFFF"
            }
          }
        },
        {
          key: "WorkExperience",
          name: "工作经历",
          props: {
            experiences: [
              {
                id: 1,
                sort: 1,
                company: "伯乐科技",
                deleted: 0,
                endDate: "2023-06-30",
                position: "软件工程师",
                createdAt: "2026-01-01T17:07:50",
                isCurrent: false,
                startDate: "2021-07-01",
                updatedAt: "2026-01-01T17:07:50",
                description: "负责核心业务功能开发",
                achievements: [
                  "完成3个重大项目",
                  "获得年度优秀员工"
                ]
              },
              {
                id: 2,
                sort: 2,
                company: "伯乐科技",
                deleted: 0,
                position: "高级软件工程师",
                createdAt: "2026-01-01T17:07:50",
                isCurrent: true,
                startDate: "2023-07-01",
                updatedAt: "2026-01-01T17:07:50",
                description: "负责系统架构设计和团队指导",
                achievements: [
                  "主导系统重构",
                  "培养3名初级工程师"
                ]
              }
            ],
            showCompany: true,
            showDuration: true
          },
          styles: {},
          templateId: null,
          componentId: 3,
          defaultConfig: {
            props: {
              title: "工作经历",
              orderBy: "startDate",
              maxItems: 5,
              showSkills: true,
              showJobTitle: true,
              orderDirection: "desc",
              showDepartment: true,
              showWorkPeriod: true,
              showCompanyLogo: true,
              showCompanyName: true,
              showWorkContent: true,
              showAchievements: true
            },
            styles: {
              padding: "20px",
              fontSize: "14px",
              titleColor: "#333333",
              itemSpacing: "16px",
              periodColor: "#999999",
              borderRadius: "8px",
              companyColor: "#1890ff",
              timelineColor: "#e8e8e8",
              backgroundColor: "#FFFFFF"
            }
          }
        },
        {
          key: "Skills",
          name: "技能专长",
          props: {
            skills: [
              {
                id: 3,
                name: "MySQL",
                sort: 3,
                tags: [
                  "sss",
                  "okook"
                ],
                level: "中级",
                deleted: 0,
                category: "数据库",
                isPublic: true,
                createdAt: "2026-01-01T17:07:50",
                updatedAt: "2026-01-01T18:21:07",
                description: "熟悉MySQL数据库设计、优化和SQL调优",
                isCertified: false,
                experienceYears: 3,
                proficiencyPercent: 75
              },
              {
                id: 2,
                name: "Spring框架",
                sort: 2,
                tags: [],
                level: "高级",
                deleted: 0,
                category: "框架",
                isPublic: true,
                createdAt: "2026-01-01T17:07:50",
                updatedAt: "2026-01-01T18:20:17",
                description: "精通Spring、Spring Boot、Spring Cloud等框架",
                isCertified: true,
                certificateDate: "2023-01-20",
                certificateName: "Spring Professional Certification",
                experienceYears: 4,
                proficiencyPercent: 90
              },
              {
                id: 1,
                name: "Java编程",
                sort: 1,
                tags: [],
                level: "高级",
                deleted: 0,
                category: "编程语言",
                isPublic: true,
                createdAt: "2026-01-01T17:07:50",
                updatedAt: "2026-01-01T18:20:17",
                description: "熟练掌握Java语言特性，包括集合、多线程、IO等",
                isCertified: true,
                certificateDate: "2022-03-15",
                certificateName: "Oracle Certified Professional",
                experienceYears: 5.5,
                proficiencyPercent: 85
              }
            ],
            showTags: true,
            skillLevel: true
          },
          styles: {},
          templateId: null,
          componentId: 6,
          defaultConfig: {
            props: {
              title: "技能专长",
              showSkillLevel: true,
              skillLevelType: "progress",
              groupByCategory: true,
              skillCategories: [
                "编程语言",
                "框架工具",
                "数据库",
                "其他技能"
              ],
              showExperienceYears: true,
              maxSkillsPerCategory: 8
            },
            styles: {
              padding: "20px",
              fontSize: "14px",
              titleColor: "#333333",
              borderRadius: "8px",
              skillSpacing: "12px",
              progressColor: "#1890ff",
              skillNameColor: "#555555",
              backgroundColor: "#FFFFFF",
              categorySpacing: "24px"
            }
          }
        },
        {
          key: "SelfEvaluation",
          name: "自我评价",
          props: {
            maxLength: 500,
            evaluations: [
              {
                id: 1,
                content: "6666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666",
                deleted: 0,
                createdAt: "2026-01-05T15:54:45",
                updatedAt: "2026-01-05T15:54:45",
                highlights: [
                  "6",
                  "7",
                  "8",
                  "9",
                  "0",
                  "777"
                ]
              }
            ]
          },
          styles: {},
          templateId: null,
          componentId: 5,
          defaultConfig: {
            props: {
              title: "自我评价",
              format: "paragraph",
              maxLength: 500,
              showHobbies: true,
              allowRichText: true,
              showStrengths: true,
              characterTraits: [
                "责任心强",
                "学习能力强",
                "团队协作"
              ],
              showCareerGoals: true,
              showSkillsSummary: true,
              showCharacterTraits: true
            },
            styles: {
              border: "1px solid #f0f0f0",
              padding: "20px",
              fontSize: "14px",
              lineHeight: "1.8",
              titleColor: "#333333",
              borderRadius: "8px",
              contentColor: "#555555",
              backgroundColor: "#fafafa",
              highlightBackground: "#fff7e6"
            }
          }
        }
      ]
    };

  } catch (err: any) {
    console.error('加载简历数据失败:', err);
    error.value = err.message || '加载简历数据失败，请稍后重试';
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadResumeData();
});
</script>

<style scoped>
.resume-preview-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.loading-state,
.error-state {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 60vh;
  gap: 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #5ac8fa;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 16px;
  color: #666;
}

.error-icon {
  font-size: 48px;
}

.error-text {
  font-size: 16px;
  color: #f56c6c;
  max-width: 400px;
  text-align: center;
  line-height: 1.5;
}

.retry-button {
  background: #5ac8fa;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.resume-container {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>