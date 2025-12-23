export interface CompanyComment {
  id: number
  content: string
  createdAt: string
  updatedAt: string
  userId: number
  userName: string
  canDelete?: boolean
}