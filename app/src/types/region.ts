// types/region.ts
export interface RegionResult {
  id: number;
  parentId: number;
  path: string;
  level: number;  // 1:省, 2:市, 3:区县
  children: RegionResult[] | null;
  parent: RegionResult | null;
  name: string;
  shortName: string;
  initial: string | null;
  pinyin: string | null;
  jianpin: string | null;
  longitude: number;
  latitude: number;
  telCode: string | null;
  zipCode: string | null;
  carCode: string | null;
  cnwStationCode: string | null;
  nmcStationCode: string;
  nmcProvinceCode: string;
  cmaStationCode: string | null;
  parentName: string | null;
  childrenCount: number | null;
}

export interface Region {
  id: number;
  parentId: number;
  name: string;
  shortName: string;
  pinyin: string;
  level: number;
  path: string;
  longitude?: number;
  latitude?: number;
}

// 分级的区域类型
export interface Province extends Region {
  level: 1;
}

export interface City extends Region {
  level: 2;
  provinceId: number;
}

export interface District extends Region {
  level: 3;
  cityId: number;
  provinceId: number;
}

// 热门城市类型
export interface HotCity {
  id: number;
  name: string;
  shortName: string;
  pinyin: string;
  level: number;
  parentId?: number;
  // 可能包含完整路径信息
  province?: {
    id: number
    name: string
  };
  city?: {
    id: number
    name: string
  };
  sortOrder?: number;
}

// 选择的区域
export interface SelectedRegion {
  province?: Province;
  city?: City;
  district?: District;
}