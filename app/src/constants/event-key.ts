export const EventKey = {
  USER_INFO_UPDATED_EVENT: "userInfoUpdated",
} as const;

export type EventKey = typeof EventKey[ keyof typeof EventKey]
