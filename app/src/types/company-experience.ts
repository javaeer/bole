export interface CompanyExperienceResult {
  company?: string
  position?: string
  description?: string
  startDate?: string
  endDate?: string
  skills?: string[]
  achievements?: string[]
}


export interface CompanyExperienceForm {
  company?: string
  position?: string
  description?: string
  startDate?: string
  endDate?: string
  skills?: string[]
  achievements?: string[]
}

export interface CompanyExperienceQuery extends BodyQuery {

}


export interface CompanyExperienceItem {

}