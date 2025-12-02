// types/dict.ts
/**
 * 基础字典节点（接口返回的每个元素）
 */
export interface DictNode {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  parentId: number;
  path: string;
  level: number;
  sort: number;
  children: DictNode[];
  name: string;
  type: string;
  code: string;
  value: string;
  label: string;
  state: number;
}



/**
 * 字典项（用于前端使用的扁平化结构）
 */
export interface DictItem {
  value: string;
  label: string;
  code: string;
  state: number;
  sort?: number;
}

/**
 * 字典类型结构
 */
export interface DictType {
  name: string;     // 字典类型名称（如"性别"）
  type: string;     // 字典类型编码（如"sys_sex"）
  items: DictItem[]; // 字典项列表
  rootNode?: DictNode; // 原始根节点（可选，用于调试）
}

/**
 * 前端使用的字典数据结构
 */
export type DictData = Record<string, DictType>;

/**
 * API响应格式
 */
export type DictResult = DictNode[];