import { request } from "@/utils/request";
import { NoticeDetailVO, NoticeForm, NoticePageQuery, NoticePageVO } from "@/types/notice";

const NOTICE_BASE_URL = "/notices";

const NoticeAPI = {
  /** 获取通知公告分页数据 */
  getPage(queryParams?: NoticePageQuery) {
    return request.post<PageResult<NoticePageVO[]>>(`${NOTICE_BASE_URL}/page`, queryParams);
  },

  /**
   * 获取通知公告表单数据
   *
   * @param id NoticeID
   * @returns Notice表单数据
   */
  getFormData(id: number) {
    return request.get<NoticeForm>( `${NOTICE_BASE_URL}/${id}/form`);
  },

  /**
   * 添加通知公告
   *
   * @param data Notice表单数据
   * @returns
   */
  add(data: NoticeForm) {
    return request.post(`${NOTICE_BASE_URL}`, data);
  },

  /**
   * 更新通知公告
   *
   * @param id NoticeID
   * @param data Notice表单数据
   */
  update(id: number, data: NoticeForm) {
    return request.put(`${NOTICE_BASE_URL}/${id}`, data);
  },

  /**
   * 批量删除通知公告，多个以英文逗号(,)分割
   *
   * @param ids 通知公告ID字符串，多个以英文逗号(,)分割
   */
  deleteByIds(ids: string) {
    return request.delete(`${NOTICE_BASE_URL}/${ids}`);
  },

  /**
   * 发布通知
   *
   * @param id 被发布的通知公告id
   * @returns
   */
  publish(id: number) {
    return request.put( `${NOTICE_BASE_URL}/${id}/publish`);
  },

  /**
   * 撤回通知
   *
   * @param id 撤回的通知id
   * @returns
   */
  revoke(id: number) {
    return request.put( `${NOTICE_BASE_URL}/${id}/revoke`);
  },
  /**
   * 查看通知
   *
   * @param id
   */
  getDetail(id: string) {
    return request.get<NoticeDetailVO>( `${NOTICE_BASE_URL}/${id}/detail`);
  },

  /* 全部已读 */
  readAll() {
    return request.put( `${NOTICE_BASE_URL}/read/all`);
  },

  /** 获取我的通知分页列表 */
  getMyNoticePage(queryParams?: PageParam, queryData?: any) {
    return request.page<PageResult<NoticePageVO[]>>( `${NOTICE_BASE_URL}/my/page`, queryData, queryParams);
  },
};

export default NoticeAPI;
