export interface SelfEvaluationForm {
  id?: number;
  content: string;
  highlights?: string[];
}

export interface SelfEvaluationQuery extends BodyQuery {

}


export interface SelfEvaluationResult {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  content: string;
  highlights: string[] | null;
}