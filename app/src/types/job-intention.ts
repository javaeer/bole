export interface JobIntentionForm {
  id?: number;
  position?: string;
  salary?: string;
  jobType?: string;
  city?: string;
}

export interface JobIntentionQuery extends BodyQuery {

}


export interface JobIntentionResult {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  userId: number;
  position: string;
  city: string;
  salary: string;
  jobType: string;
}