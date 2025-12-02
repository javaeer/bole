import { del, get, page, post, put } from "@/utils/request";
import { DictForm, DictPageResult, DictQuery, DictResult } from "@/types/dict";

const DICT_BASE_URL = "/dict";

const DictAPI = {
  /**
   * 获取字典分页列表
   *
   * @param queryParams 查询参数
   * @returns 字典分页结果
   */
  getPage(pageQuery: PageQuery, queryParams: DictQuery) {
    return page<DictPageResult>(`${DICT_BASE_URL}/page`, pageQuery, queryParams);
  },

  /**
   * 获取字典表单数据
   *
   * @param id 字典ID
   * @returns 字典表单数据
   */
  getFormData(id: number) {
    return get<DictForm>(`${DICT_BASE_URL}/${id}/form`);
  },

  /**
   * 新增字典
   *
   * @param data 字典表单数据
   */
  add(data: DictForm) {
    return post(`${DICT_BASE_URL}`, data);
  },

  /**
   * 修改字典
   *
   * @param id 字典ID
   * @param data 字典表单数据
   */
  update(id: number, data: DictForm) {
    return put(`${DICT_BASE_URL}/${id}`, data);
  },

  /**
   * 删除字典
   *
   * @param ids 字典ID，多个以英文逗号(,)分隔
   */
  deleteByIds(ids: string) {
    return del(`${DICT_BASE_URL}`, ids);
  },

  /**
   * 获取字典列表
   *
   * @returns 字典列表
   */
  postList(queryParams: DictQuery) {
    return post<DictResult>(`${DICT_BASE_URL}/list`,queryParams);
  },

  /**
   * 获取整个字典树结构
   */
  getWholeTree(){
    return get<DictResult>(`${DICT_BASE_URL}/children`);
  },
};

export default DictAPI;


