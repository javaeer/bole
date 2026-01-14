export interface ProjectExperienceForm {
  id?: number;
  name: string;
  companyId?: number;
  company?: string;
  position?: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  skills?: string[];
  status?: number;
  achievements?: string[];
}

export interface ProjectExperienceQuery extends BodyQuery {
  keyField?: "name";
  sortBy?: "createdAt" | "updatedAt" | "status" | "name";
  sortOrder?: "asc" | "desc";
  status?: 1;
}


export interface ProjectExperienceResult {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  userId: number;
  name: string;
  status: number;  // 0: 未开始, 1: 进行中, 2: 已完成, 3: 已暂停
  startDate: string;
  endDate: string;
  description: string;
  achievements: string | null;
  sort: number;
}