// 简历结果
export interface FeedbackResult {
  id: number | null;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  userId: number;
  type: string;
  content: string;
  images: string[];
  contact: Contact;
}



export interface FeedbackQuery extends BodyQuery {
  userId?: number;
}


export interface FeedbackForm {
  userId?: number;
  type: string;
  content?: string;
  images?: string[];
  contact?: Contact;
}

export interface Contact {
  phone?: string;
  email?: string;
}