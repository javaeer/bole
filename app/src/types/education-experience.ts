export interface EducationExperienceForm {
  company?: string;
  position?: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  skills?: string[];
  achievements?: string[];
}

export interface EducationExperienceQuery extends PageQuery {

}


export interface EducationExperienceItem {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  userId: number;
  school: string;
  major: string;
  degree: string;
  startDate: string;
  endDate: string;
  isHighest: number;
  description: string | null;
  achievements: string | null;
  sort: number;
}