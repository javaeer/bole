export interface JobIntentionForm {
  position?: string;
  salary?: string;
  jobType?: string;
  city?: string;
}

export interface JobIntentionQuery extends PageQuery {

}


export interface JobIntentionItem {
  id: number
  createdAt: string
  updatedAt: string
  deleted: number
  userId: number
  position: string
  city: string
  salary: string
  jobType: string
}