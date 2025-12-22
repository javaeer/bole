export interface SkillForm {
  id?: number
  name?: string
  category?: string
  proficiencyPercent?: number
  level?: string
  experienceYears?: number
  description?: string
  tags?: string
  isCertified?: boolean
  certificateName?: string
  certificateDate?: string
}

export interface SkillQuery extends PageQuery {

}


export interface SkillItem {

}