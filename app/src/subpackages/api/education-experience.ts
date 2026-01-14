import { request } from "@/utils/request";
import {
  EducationExperienceForm,
  EducationExperienceQuery,
  EducationExperienceResult,
} from "@/types/education-experience";

const EDUCATION_EXPERIENCE_BASE_URL = "/education/experience";

const EducationExperienceAPI = {

  getById(id: number) {
    return request.get<EducationExperienceResult>(`${EDUCATION_EXPERIENCE_BASE_URL}/${id}`);
  },
  delete(id: number) {
    return request.delete<boolean>(`${EDUCATION_EXPERIENCE_BASE_URL}/del`,{id});
  },

  add(form: EducationExperienceForm) {
    return request.post(`${EDUCATION_EXPERIENCE_BASE_URL}/add`, form);
  },

  update(form: EducationExperienceForm) {
    return request.put(`${EDUCATION_EXPERIENCE_BASE_URL}/edit`, form);
  },

  page(params: PageParam, query ?: EducationExperienceQuery) {
    return request.page<PageResult<EducationExperienceResult>>(`${EDUCATION_EXPERIENCE_BASE_URL}/page`, params, query);
  },

};

export default EducationExperienceAPI;