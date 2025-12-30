import { request } from "@/utils/request";
import { WorkExperienceForm, WorkExperienceQuery, WorkExperienceResult } from "@/types/work-experience";

const WORK_EXPERIENCE_BASE_URL = "/work/experience";

const WorkExperienceAPI = {

  getById(id: number) {
    return request.get<WorkExperienceResult>(`${WORK_EXPERIENCE_BASE_URL}/${id}`);
  },
  delete(id: number) {
    return request.delete<boolean>(`${WORK_EXPERIENCE_BASE_URL}/${id}`);
  },

  add(form: WorkExperienceForm) {
    return request.post(`${WORK_EXPERIENCE_BASE_URL}/add`, form);
  },

  update(form: WorkExperienceForm) {
    return request.put(`${WORK_EXPERIENCE_BASE_URL}/edit`, form);
  },

  page(params: PageParam, query ?: WorkExperienceQuery) {
    return request.page<PageResult<WorkExperienceResult>>(`${WORK_EXPERIENCE_BASE_URL}/page`, params, query);
  },

};

export default WorkExperienceAPI;