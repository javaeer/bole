import { request } from "@/utils/request";
import { SelfEvaluationForm, SelfEvaluationQuery, SelfEvaluationResult } from "@/types/self-evaluation";

const SELF_EVALUATION_BASE_URL = "/self/evaluation";

const SelfEvaluationAPI = {

  getById(id: number) {
    return request.get<SelfEvaluationResult>(`${SELF_EVALUATION_BASE_URL}/${id}`);
  },
  delete(id: number) {
    return request.delete<boolean>(`${SELF_EVALUATION_BASE_URL}/${id}`);
  },

  add(form: SelfEvaluationForm) {
    return request.post(`${SELF_EVALUATION_BASE_URL}/add`, form);
  },

  update(form: SelfEvaluationForm) {
    return request.put(`${SELF_EVALUATION_BASE_URL}/edit`, form);
  },

  page(params: PageParam, query ?: SelfEvaluationQuery) {
    return request.page<PageResult<SelfEvaluationResult>>(`${SELF_EVALUATION_BASE_URL}/page`, params, query);
  },

};

export default SelfEvaluationAPI;