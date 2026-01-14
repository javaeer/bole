import { request } from "@/utils/request";
import { LogPageQuery, LogVO, VisitStatsVO, VisitTrendQuery, VisitTrendVO } from "@/types/log";

const LOG_BASE_URL = "/logs";

const LogAPI = {
  /**
   * 获取日志分页列表
   *
   * @param queryParams 查询参数
   */
  getPage(queryParams: LogPageQuery) {
    return request.get<PageResult<LogVO[]>>(`${LOG_BASE_URL}/page`, queryParams);
  },

  /**
   * 获取访问趋势
   *
   * @param queryParams
   * @returns
   */
  getVisitTrend(queryParams: VisitTrendQuery) {
    return request.get<VisitTrendVO>(`${LOG_BASE_URL}/visit/trend`, queryParams);
  },

  /**
   * 获取访问趋势
   *
   * @param queryParams
   * @returns
   */
  getVisitStats() {
    return request.get<VisitStatsVO>(`${LOG_BASE_URL}/visit/stats`);
  },
};

export default LogAPI;