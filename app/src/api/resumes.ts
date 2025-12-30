import { request } from "@/utils/request";
import { ResumesForm, ResumesQuery, ResumesResult } from "@/types/resumes";

const RESUMES_BASE_URL = "/resumes";

const ResumesAPI = {

	getById(id : number) {
		return request.get<ResumesResult>(`${RESUMES_BASE_URL}/${id}`);
	},

	getPreview(templateId : number) {
		return request.get<ResumesResult>(`${RESUMES_BASE_URL}/preview/${templateId}`);
	},

	add(form : ResumesForm) {
		return request.post(`${RESUMES_BASE_URL}/add`, form);
	},

	edit(form : ResumesForm) {
		return request.put(`${RESUMES_BASE_URL}/edit`, form);
	},

	page(params : PageParam, query ?: ResumesQuery) {
		return request.page<PageResult<ResumesResult>>(`${RESUMES_BASE_URL}/page`, params, query);
	},

};

export default ResumesAPI;