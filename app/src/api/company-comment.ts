import { request } from "@/utils/request";

const COMPANY_COMMENT_BASE_URL = "/company-comment";

const CompanyCommentAPI = {

	getById(id : number) {
		return request.get<CompanyCommentResult>(`${COMPANY_COMMENT_BASE_URL}/${id}`);
	},

	add(form : CompanyCommentForm) {
		return request.post(`${COMPANY_COMMENT_BASE_URL}/add`, form);
	},

	page(params : PageQuery, query ?: CompanyCommentQuery) {
		return request.page<PageResult<CompanyCommentResult>>(`${COMPANY_COMMENT_BASE_URL}/page`, params, query);
	},

};

export default CompanyCommentAPI;