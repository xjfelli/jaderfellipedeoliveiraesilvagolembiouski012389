/**
 * Interface para os dados de registro de um novo usuário
 */
export interface RegisterData {
  username: string;
  email: string;
  password: string;
  fullname: string;
}

/**
 * Interface para a resposta de validação de disponibilidade
 */
export interface AvailabilityResponse {
  available: boolean;
  message?: string;
}

/**
 * Interface para os erros de registro
 */
export interface RegisterError {
  field?: string;
  message: string;
  code?: string;
}
