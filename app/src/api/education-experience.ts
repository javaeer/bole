import { request } from "@/utils/request";
import {
	EducationExperienceForm,
	EducationExperienceItem,
	EducationExperienceQuery,
} from "@/types/education-experience";

const EDUCATION_EXPERIENCE_BASE_URL = "/education-experience";

const EducationExperienceAPI = {

	getById(id : number) {
		return request.get<EducationExperienceItem>(`${EDUCATION_EXPERIENCE_BASE_URL}/${id}`);
	},

	add(form : EducationExperienceForm) {
		return request.post(`${EDUCATION_EXPERIENCE_BASE_URL}/add`, form);
	},

	page(params : PageQuery, query ?: EducationExperienceQuery) {
		return request.page<PageResult<EducationExperienceItem>>(`${EDUCATION_EXPERIENCE_BASE_URL}/page`, params, query);
	},

};

export default EducationExperienceAPI;