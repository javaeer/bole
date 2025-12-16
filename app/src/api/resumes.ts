import { request } from "@/utils/request";

const RESUMES_BASE_URL = "/resumes";

const ResumesAPI = {

  getById(id: number) {
    return request.get({ url: `${RESUMES_BASE_URL}/${id}` });
  },

  getPreview(templateId: number) {
    return request.get(`${RESUMES_BASE_URL}/preview/${templateId}`);
  },

};

export default ResumesAPI;