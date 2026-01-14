export interface EducationExperienceForm {
  id?: number;
  universityId: number;
  university: string;
  major: string;
  degree: string;
  startDate: string;
  endDate: string;
  isHighest: number;
  description?: string;
  achievements?: string[] | null;
}

export interface EducationExperienceQuery extends BodyQuery {
  university?: string;
}


export interface EducationExperienceResult {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  userId: number;
  universityId: number;
  university: string;
  major: string;
  degree: string;
  startDate: string;
  endDate: string;
  isHighest: number;
  description: string | null;
  achievements: string[] | null;
  sort: number;
}