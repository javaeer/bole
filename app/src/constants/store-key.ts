export const StoreKey = {
  TOKEN_KEY: "access_token",
  REFRESH_TOKEN_KEY: "refresh_token",
  TOKEN_EXPIRE_KEY: "token_expire",
  USER_INFO_KEY: "user_info",
  DICT_KEY: "dict",
  SYSTEM_CONFIG_LIST: "system_config_list",
} as const;

export type StoreKey = typeof StoreKey[keyof typeof StoreKey];

