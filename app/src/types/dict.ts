/**
 * 字典数据
 *
 * @description 字典数据
 */
export interface DictItem {
  /** 字典数据值 */
  value: string;

  /** 字典数据标签 */
  label: string;

  /** 标签类型 */
  tagType: string;
}

/**
 * 字典数据项分页VO
 *
 * @description 字典数据分页对象
 */
export interface Dict {
  /** 字典名称 */
  name: string;

  /** 字典编码 */
  dictCode: string;

  /** 字典数据集合 */
  dictDataList: DictItem[];
}


/**
 * 字典查询参数
 */
export interface DictQuery{
  /**
   * 关键字(字典名称/编码)
   */
  keywords?: string;

  /**
   * 字典状态（1:启用，0:禁用）
   */
  status?: number;
}

/**
 * 字典分页对象
 */
export interface DictPageResult extends PageResult<Dict>{
}

/**
 * 字典
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


export type DictResult = Dict[]