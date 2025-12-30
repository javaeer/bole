import { request } from "@/utils/request";
import { JobIntentionForm, JobIntentionQuery, JobIntentionResult } from "@/types/job-intention";

const JOB_INTENTION_BASE_URL = "/job/intention";

const JobIntentionAPI = {

  getById(id: number) {
    return request.get<JobIntentionResult>(`${JOB_INTENTION_BASE_URL}/${id}`);
  },
  delete(id: number) {
    return request.delete<boolean>(`${JOB_INTENTION_BASE_URL}/${id}`);
  },

  add(form: JobIntentionForm) {
    return request.post(`${JOB_INTENTION_BASE_URL}/add`, form);
  },

  update(form: JobIntentionForm) {
    return request.put(`${JOB_INTENTION_BASE_URL}/edit`, form);
  },

  page(params: PageParam, query ?: JobIntentionQuery) {
    return request.page<PageResult<JobIntentionResult>>(`${JOB_INTENTION_BASE_URL}/page`, params, query);
  },

};

export default JobIntentionAPI;