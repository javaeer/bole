export interface SelfEvaluationForm {
  content?: string
}

export interface SelfEvaluationQuery extends PageQuery {

}


export interface SelfEvaluationItem {
  id: number
  createdAt: string
  updatedAt: string
  deleted: number
  userId: number
  content: string
  keywords: string | null
}