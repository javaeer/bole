export interface SkillForm {
  id?: number;
  name: string;
  level: string;
  category: string;
  description: string;
  proficiencyPercent: number;
  experienceYears: number;
  isCertified: boolean;
  certificateName: string | null;
  certificateDate: string | null;
  tags: string[]; // 改为数组类型
  isPublic: boolean;
}

export interface SkillQuery extends BodyQuery {

}


export interface SkillResult {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  name: string;
  level: string;
  category: string;
  description: string;
  proficiencyPercent: number;
  experienceYears: number;
  isCertified: boolean;
  certificateName: string | null;
  certificateDate: string | null;
  tags: string[];
  isPublic: boolean;
  sort: number;
}