export interface WorkExperienceForm {
  id?: number;
  companyId?: number;
  company?: string;
  position?: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  skills?: string[];
  achievements?: string[];
}

export interface WorkExperienceQuery extends BodyQuery {

}


export interface WorkExperienceResult {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  userId: number;
  companyId: number;
  position: string;
  startDate: string;
  endDate: string;
  isCurrent: boolean;
  description: string;
  achievements: string[] | null;
  sort: number;
}