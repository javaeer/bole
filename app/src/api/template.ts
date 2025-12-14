import { request } from "@/utils/request";

const TEMPLATE_BASE_URL = "/template";

const TemplateAPI = {

  getTemplateById(id: number) {
    return request.get({ url: `${TEMPLATE_BASE_URL}/${id}` });
  },

  getPreview(code: string) {
    return request.get({ url: `${TEMPLATE_BASE_URL}/preview/${code}` });
  },

};

export default TemplateAPI;