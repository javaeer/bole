export const EventKey = {
  USER_INFO_UPDATED_EVENT: "userInfoUpdated",
  TEMPLATE_UPDATED_EVENT: "templateCollectionUpdated",
} as const;

export type EventKey = typeof EventKey[ keyof typeof EventKey]
