export interface TemplateComponentResult {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  name: string;
  component: string;
  props: string;
  styles: string;
}

export type TemplateComponentResults = TemplateComponentResult[];