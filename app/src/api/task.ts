import { request } from "@/utils/request";
import { TaskForm, TaskQuery, TaskResult } from "@/types/task";

const TASK_BASE_URL = "/task";

const TaskAPI = {

  getById(id: number) {
    return request.get<TaskResult>(`${TASK_BASE_URL}/${id}`);
  },
  /**
   * 添加转换任务
   * @param param
   */
  addTask(param: TaskForm) {
    return request.post<boolean>(`${TASK_BASE_URL}/add`, param);
  },

  deleteTask(id : number) {
    return request.delete<boolean>(`${TASK_BASE_URL}/del`,{id});
  },

  clearCompletedTasks() {
    return request.delete<boolean>(`${TASK_BASE_URL}/clear`);
  },
  /**
   * 获取分页列表
   *
   * @param pageQuery 分页参数
   * @param queryParams 查询参数
   * @returns 分页结果
   */
  getPage(pageQuery: PageParam, queryParams: TaskQuery) {
    return request.page<PageResult<TaskResult>>(`${TASK_BASE_URL}/page`, pageQuery, queryParams);
  },
};

export default TaskAPI;