export interface SkillForm {
  id?: number;
  name?: string;
  category?: string;
  proficiencyPercent?: number;
  level?: string;
  experienceYears?: number;
  description?: string;
  tags?: string;
  isCertified?: boolean;
  certificateName?: string;
  certificateDate?: string;
}

export interface SkillQuery extends BodyQuery {

}


export interface SkillItem {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  userId: number;
  name: string;
  level: string;
  category: string;
  description: string;
  proficiencyPercent: number;
  experienceYears: number;
  isCertified: boolean;
  certificateName: string | null;
  certificateDate: string | null;
  tags: string;
  isPublic: boolean;
  sort: number;
}