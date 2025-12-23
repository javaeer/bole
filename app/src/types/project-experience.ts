export interface ProjectExperienceForm {
  company?: string;
  position?: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  skills?: string[];
  achievements?: string[];
}

export interface ProjectExperienceQuery extends BodyQuery {
  keyField?: "name";
  sortBy?: "createdAt" | "updatedAt" | "status" | "name";
  sortOrder?: "asc" | "desc";
}


export interface ProjectExperienceItem {
  id: number
  createdAt: string
  updatedAt: string
  deleted: number
  userId: number
  name: string
  status: number  // 0: 未开始, 1: 进行中, 2: 已完成, 3: 已暂停
  startDate: string
  endDate: string
  description: string
  achievements: string | null
  sort: number
}