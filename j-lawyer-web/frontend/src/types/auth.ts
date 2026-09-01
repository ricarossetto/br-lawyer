export interface LoginRequestV8 {
  username: string;
  password: string;
  otp?: string;
}

export interface TokenResponseV8 {
  accessToken: string;
  expiresIn: number;
  principal: string;
  roles: string[];
}

export interface UserSession {
  principal: string;
  roles: string[];
  accessToken: string;
  expiresAt: number;
}