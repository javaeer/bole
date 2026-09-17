export const useValidation = () => {
  // 邮箱验证
  const validateEmail = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return emailRegex.test(email)
  }

  // 手机号验证
  const validatePhone = (phone: string): boolean => {
    const phoneRegex = /^1[3-9]\d{9}$/
    return phoneRegex.test(phone)
  }

  // 密码强度验证
  const validatePassword = (password: string): { valid: boolean; strength: 'weak' | 'medium' | 'strong' } => {
    if (password.length < 6) {
      return { valid: false, strength: 'weak' }
    }

    let score = 0
    if (/[a-z]/.test(password)) score++
    if (/[A-Z]/.test(password)) score++
    if (/[0-9]/.test(password)) score++
    if (/[^a-zA-Z0-9]/.test(password)) score++

    const strength = score < 2 ? 'weak' : score < 3 ? 'medium' : 'strong'
    return { valid: password.length >= 8, strength }
  }

  // 必填验证
  const validateRequired = (value: any): boolean => {
    if (value === null || value === undefined) return false
    if (typeof value === 'string') return value.trim().length > 0
    if (Array.isArray(value)) return value.length > 0
    return true
  }

  // 长度验证
  const validateLength = (value: string, min: number, max?: number): boolean => {
    if (!value) return false
    if (value.length < min) return false
    if (max && value.length > max) return false
    return true
  }

  // 数字范围验证
  const validateNumberRange = (value: number, min: number, max: number): boolean => {
    return value >= min && value <= max
  }

  // URL验证
  const validateUrl = (url: string): boolean => {
    try {
      new URL(url)
      return true
    } catch {
      return false
    }
  }

  // 身份证验证
  const validateIdCard = (idCard: string): boolean => {
    const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
    return idCardRegex.test(idCard)
  }

  // 统一验证方法
  const validate = (rules: Record<string, any>, values: Record<string, any>) => {
    const errors: Record<string, string> = {}
    
    for (const [field, rule] of Object.entries(rules)) {
      const value = values[field]
      
      if (rule.required && !validateRequired(value)) {
        errors[field] = rule.message || `${field}是必填项`
        continue
      }
      
      if (rule.minLength && !validateLength(value, rule.minLength)) {
        errors[field] = rule.message || `${field}长度不能少于${rule.minLength}个字符`
        continue
      }
      
      if (rule.maxLength && !validateLength(value, rule.maxLength, rule.maxLength)) {
        errors[field] = rule.message || `${field}长度不能超过${rule.maxLength}个字符`
        continue
      }
      
      if (rule.email && !validateEmail(value)) {
        errors[field] = rule.message || '邮箱格式不正确'
        continue
      }
      
      if (rule.phone && !validatePhone(value)) {
        errors[field] = rule.message || '手机号格式不正确'
        continue
      }
    }
    
    return errors
  }

  return {
    validateEmail,
    validatePhone,
    validatePassword,
    validateRequired,
    validateLength,
    validateNumberRange,
    validateUrl,
    validateIdCard,
    validate
  }
}