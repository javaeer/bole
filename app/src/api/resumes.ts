import { request } from "@/utils/request";
import { ResumesForm, ResumesQuery, ResumesResult } from "@/types/resumes";

const RESUMES_BASE_URL = "/resumes";

const ResumesAPI = {

  getById(id: number) {
    return request.get<ResumesResult>({ url: `${RESUMES_BASE_URL}/${id}` });
  },

  getPreview(templateId: number) {
    return request.get<ResumesResult>(`${RESUMES_BASE_URL}/preview/${templateId}`);
  },

  addResumes(resumesForm: ResumesForm) {
    return request.post(`${RESUMES_BASE_URL}/add`, resumesForm);
  },

  page(params: PageQuery, query?: ResumesQuery) {
    return request.page<PageResult<ResumesResult>>(`${RESUMES_BASE_URL}/page`, params, query);
  },

};

export default ResumesAPI;