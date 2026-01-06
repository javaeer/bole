import { request } from "@/utils/request";
import { UniversityForm, UniversityQuery, UniversityResult } from "@/types/company";

const UNIVERSITY_BASE_URL = "/university";

const UniversityAPI = {

	getById(id : number) {
		return request.get<UniversityResult>(`${UNIVERSITY_BASE_URL}/${id}`);
	},

	unfollow(id : number) {
		return request.put<UniversityResult>(`${UNIVERSITY_BASE_URL}/unfollow/${id}`);
	},

	follow(id : number) {
		return request.put<UniversityResult>(`${UNIVERSITY_BASE_URL}/follow/${id}`);
	},

	add(form : UniversityForm) {
		return request.post(`${UNIVERSITY_BASE_URL}/add`, form);
	},

	update(form : UniversityForm) {
		return request.put(`${UNIVERSITY_BASE_URL}/update`, form);
	},

	page(params : PageParam, query ?: UniversityQuery) {
		return request.page<PageResult<UniversityResult>>(`${UNIVERSITY_BASE_URL}/page`, params, query);
	},

};

export default UniversityAPI;