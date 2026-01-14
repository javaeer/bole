export interface SmsSendForm {
  areaCode: string;
  phone: string;
  templateId: number;
}

export interface EmailSendForm {
  email: string;
  templateId: number;
}