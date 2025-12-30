import { request } from "@/utils/request";
import { CompanyForm, CompanyQuery, CompanyResult } from "@/types/company";

const COMPANY_BASE_URL = "/company";

const CompanyAPI = {

	getById(id : number) {
		return request.get<CompanyResult>(`${COMPANY_BASE_URL}/${id}`);
	},

	unfollow(id : number) {
		return request.put<CompanyResult>(`${COMPANY_BASE_URL}/unfollow/${id}`);
	},

	follow(id : number) {
		return request.put<CompanyResult>(`${COMPANY_BASE_URL}/follow/${id}`);
	},

	add(form : CompanyForm) {
		return request.post(`${COMPANY_BASE_URL}/add`, form);
	},

	update(form : CompanyForm) {
		return request.put(`${COMPANY_BASE_URL}/update`, form);
	},

	page(params : PageParam, query ?: CompanyQuery) {
		return request.page<PageResult<CompanyResult>>(`${COMPANY_BASE_URL}/page`, params, query);
	},

};

export default CompanyAPI;