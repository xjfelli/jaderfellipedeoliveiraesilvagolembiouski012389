/**
 * Interface genérica para respostas da API
 * Todas as respostas do backend seguem este formato
 */
export interface ApiResponse<T = any> {
  success: boolean;
  data: T;
}
