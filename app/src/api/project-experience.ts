import { request } from "@/utils/request";
import {
	ProjectExperienceForm,
	ProjectExperienceResult,
	ProjectExperienceQuery,
} from "@/types/project-experience";

const PROJECT_EXPERIENCE_BASE_URL = "/project/experience";

const ProjectExperienceAPI = {

	getById(id : number) {
		return request.get<ProjectExperienceResult>(`${PROJECT_EXPERIENCE_BASE_URL}/${id}`);
	},

	delete(id : number) {
		return request.delete<boolean>(`${PROJECT_EXPERIENCE_BASE_URL}/del`,{id});
	},

	update(form : ProjectExperienceForm) {
		return request.put(`${PROJECT_EXPERIENCE_BASE_URL}/edit`, form);
	},

	add(form : ProjectExperienceForm) {
		return request.post(`${PROJECT_EXPERIENCE_BASE_URL}/add`, form);
	},

	page(params : PageParam, query ?: ProjectExperienceQuery) {
		return request.page<PageResult<ProjectExperienceResult>>(`${PROJECT_EXPERIENCE_BASE_URL}/page`, params, query);
	},

};

export default ProjectExperienceAPI;