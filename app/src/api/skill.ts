import { request } from "@/utils/request";
import {
	SkillForm,
	SkillResult,
	SkillQuery,
} from "@/types/skill";

const SKILL_BASE_URL = "/skill";

const SkillAPI = {

	getById(id : number) {
		return request.get<SkillResult>(`${SKILL_BASE_URL}/${id}`);
	},

	delete(id : number) {
		return request.delete<boolean>(`${SKILL_BASE_URL}/${id}`);
	},

	update(form : SkillForm) {
		return request.put(`${SKILL_BASE_URL}/edit`, form);
	},

	add(form : SkillForm) {
		return request.post(`${SKILL_BASE_URL}/add`, form);
	},

	page(params : PageParam, query ?: SkillQuery) {
		return request.page<PageResult<SkillResult>>(`${SKILL_BASE_URL}/page`, params, query);
	},

};

export default SkillAPI;