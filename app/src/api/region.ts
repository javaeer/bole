// api/region.ts
import { request } from "@/utils/request";
import { RegionResult } from "@/types/region";

const REGION_BASE_URL = "/region";

const RegionAPI = {

  getById(id: number) {
    return request.get<RegionResult>(`${REGION_BASE_URL}/${id}`);
  },

  getProvinces() {
    return request.get<RegionResult[]>(`${REGION_BASE_URL}/provinces`);
  },

  getCities(provinceId: number) {
    return request.get<RegionResult[]>(`${REGION_BASE_URL}/cities/${provinceId}`);
  },

  getDistricts(cityId: number) {
    return request.get<RegionResult[]>(`${REGION_BASE_URL}/districts/${cityId}`);
  },

  getHotCity() {
    return request.get<RegionResult[]>(`${REGION_BASE_URL}/hot`);
  },


};

export default RegionAPI;