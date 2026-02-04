export interface AuthResponse {
  accessToken: string;
  email: string;
  expiresIn: string;
  refreshToken: string;
  tokenType: string;
  username: string;
}

export interface User {
  id: string;
  username: string;
  email?: string;
  fullname: string;
}
