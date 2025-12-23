// 查询条件
import { CompanyComment } from "@/types/company-comment";

export interface CompanyQuery extends BodyQuery {
  name?: string;
  status?: number;
  createAt?: [string, string]; // 创建时间范围
}

// 结果
export interface CompanyResult {
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
  companyComments: CompanyComment[] | null;
  isFollowed?: boolean;
}

// 表单
export interface CompanyForm {
  name: string;
  email?: string;
  holder?: string;
  location: string;
  website?: string;
  github?: string;
  wechat?: string;
  bio?: string;
}
