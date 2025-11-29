import request from "@/utils/request";
import { DictForm, DictPageResult, DictQuery, DictResult } from "@/types/dict";

const DICT_BASE_URL = "/dict";

const DictAPI = {
  /**
   * 获取字典分页列表
   *
   * @param queryParams 查询参数
   * @returns 字典分页结果
   */
  getPage(queryParams: DictQuery) {
    return request<DictPageResult>({
      url: `${DICT_BASE_URL}/page`,
      method: "GET",
      data: queryParams,
    });
  },

  /**
   * 获取字典表单数据
   *
   * @param id 字典ID
   * @returns 字典表单数据
   */
  getFormData(id: number) {
    return request<DictForm>({
      url: `${DICT_BASE_URL}/${id}/form`,
      method: "GET",
    });
  },

  /**
   * 新增字典
   *
   * @param data 字典表单数据
   */
  add(data: DictForm) {
    return request({
      url: `${DICT_BASE_URL}`,
      method: "POST",
      data: data,
    });
  },

  /**
   * 修改字典
   *
   * @param id 字典ID
   * @param data 字典表单数据
   */
  update(id: number, data: DictForm) {
    return request({
      url: `${DICT_BASE_URL}/${id}`,
      method: "PUT",
      data: data,
    });
  },

  /**
   * 删除字典
   *
   * @param ids 字典ID，多个以英文逗号(,)分隔
   */
  deleteByIds(ids: string) {
    return request({
      url: `${DICT_BASE_URL}/${ids}`,
      method: "delete",
    });
  },

  /**
   * 获取字典列表
   *
   * @returns 字典列表
   */
  getList() {
    return request<DictResult>({
      url: `${DICT_BASE_URL}/list`,
      method: "GET",
    });
  },
};

export default DictAPI;


