export interface CompanyCommentResult {
  id: number;
  content: string;
  createdAt: string;
  updatedAt: string;
  userId: number;
  userName: string;
  canDelete?: boolean;
}


export interface CompanyCommentQuery extends BodyQuery {
  companyId: number;
}

export interface CompanyCommentForm {
  companyId: number;
  content: string;
}
