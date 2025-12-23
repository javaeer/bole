import { request } from "@/utils/request";
import { ResumesForm, ResumesQuery, ResumesResult } from "@/types/resumes";
import {
	ProjectExperienceForm,
	ProjectExperienceItem,
	ProjectExperienceQuery,
} from "@/types/education-experience";

const PROJECT_EXPERIENCE_BASE_URL = "/project-experience";

const ProjectExperienceAPI = {

	getById(id : number) {
		return request.get<ProjectExperienceItem>(`${PROJECT_EXPERIENCE_BASE_URL}/${id}`);
	},

	add(form : ProjectExperienceForm) {
		return request.post(`${PROJECT_EXPERIENCE_BASE_URL}/add`, form);
	},

	page(params : PageQuery, query ?: ProjectExperienceQuery) {
		return request.page<PageResult<ProjectExperienceItem>>(`${PROJECT_EXPERIENCE_BASE_URL}/page`, params, query);
	},

};

export default ProjectExperienceAPI;