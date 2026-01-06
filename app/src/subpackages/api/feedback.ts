import { request } from "@/utils/request";
import { FeedbackForm, FeedbackQuery, FeedbackResult } from "@/types/feedback";

const FEEDBACK_BASE_URL = "/feedback";

const FeedbackAPI = {

	getById(id : number) {
		return request.get<FeedbackResult>(`${FEEDBACK_BASE_URL}/${id}`);
	},

	add(form : FeedbackForm) {
		return request.post(`${FEEDBACK_BASE_URL}/add`, form);
	},

	edit(form : FeedbackForm) {
		return request.put(`${FEEDBACK_BASE_URL}/edit`, form);
	},

	page(params : PageParam, query ?: FeedbackQuery) {
		return request.page<PageResult<FeedbackResult>>(`${FEEDBACK_BASE_URL}/page`, params, query);
	},

};

export default FeedbackAPI;