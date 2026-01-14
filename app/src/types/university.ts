// 查询条件
import { UniversityComment } from "@/types/university-comment";

export interface UniversityQuery extends BodyQuery {
  name?: string;
  status?: number;
  createAt?: [string, string]; // 创建时间范围
}

// 结果
export interface UniversityResult {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  name: string;
  email: string;
  holder: string;
  location: string;
  website: string;
  github: string | null;
  wechat: string | null;
  bio: string | null;
  followers: number;
  fans: number;
  likes: number;
  companyComments: UniversityComment[] | null;
  isFollowed?: boolean;
}

// 表单
export interface UniversityForm {
  name: string;
  email?: string;
  holder?: string;
  location: string;
  website?: string;
  github?: string;
  wechat?: string;
  bio?: string;
}
