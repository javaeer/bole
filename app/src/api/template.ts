import { request } from "@/utils/request";
import { TemplateForm, TemplateQuery, TemplateResult } from "@/types/template";

const TEMPLATE_BASE_URL = "/template";

const TemplateAPI = {

  getById(id: number) {
    return request.get<TemplateResult>(`${TEMPLATE_BASE_URL}/${id}`);
  },

  getPreview(code: string) {
    return request.get<TemplateResult>(`${TEMPLATE_BASE_URL}/preview/${code}`);
  },

  addTemplate(templateForm: TemplateForm) {
    return request.post(`${TEMPLATE_BASE_URL}/add`, templateForm);
  },

  page(params: PageQuery, query?: TemplateQuery) {
    return request.page<PageResult<TemplateResult>>(`${TEMPLATE_BASE_URL}/page`, params, query);
  },

  pageIndex(params: PageQuery, query?: TemplateQuery) {
    return request.page<PageResult<TemplateResult>>(`${TEMPLATE_BASE_URL}/index`, params, query);
  },

};

export default TemplateAPI;