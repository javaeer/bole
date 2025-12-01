/**
 * 字典项数据
 */
export interface DictItem {
  patentId: number;
  /** 字典编码 */
  code: string;
  /** 字典数据值 */
  value: string;
  /** 字典数据标签 */
  label: string;
  /** 字典数据状态 */
  state: number;
}

/**
 * 字典类型
 */
export interface Dict {
  /** 字典名称 */
  name: string;
  /** 字典类型 */
  type: string;
  /** 标签类型 */
  tagType: string;
  /** 字典数据集合 */
  children: DictItem[];
}

/**
 * 字典查询参数
 */
export interface DictQuery {
  /**
   * 字典状态（1:启用，0:禁用）
   */
  status?: number;
}

/**
 * 字典表单
 */
export interface DictForm {
  /**
   * 字典ID
   */
  id?: number;
  /**
   * 字典名称
   */
  name?: string;
  /**
   * 字典编码
   */
  dictCode?: string;
  /**
   * 字典状态（1-启用，0-禁用）
   */
  status?: number;
  /**
   * 备注
   */
  remark?: string;
}

/**
 * 字典结果类型
 */
export type DictResult = Dict[];